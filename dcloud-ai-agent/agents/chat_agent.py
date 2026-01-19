from langchain.agents import AgentExecutor, create_openai_functions_agent
from langchain.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain.tools import Tool
from core.exceptions import ApiException
from core.llm import get_default_llm
from typing import List, AsyncIterator, Dict, AsyncGenerator
import asyncio
from services.chat_service import ChatService
from tools.chat_tools import get_chat_tools
from models.json_response import JsonData
import logging
logger = logging.getLogger(__name__)

def create_chat_agent(tools:List[Tool]):
    """创建聊天智能体"""
    system_prompt = """你是一个智能聊天助手。你可以:
    1. 进行日常对话和问答
    2. 使用搜索工具获取最新信息
    3. 记住与用户的对话历史
    请保持回答专业、友好且准确。如果用户的问题需要最新信息,请使用搜索工具。"""
    
    prompt = ChatPromptTemplate.from_messages(
        [
            ("system", system_prompt),
            ("system","以下是之前的对话摘要：{summary}"),
            ("human", "{input}"),
            MessagesPlaceholder(variable_name="agent_scratchpad")
        ]
    )
    
    llm = get_default_llm()
    
    agent = create_openai_functions_agent(llm,tools,prompt)
    
    agent_excutor = AgentExecutor(
        agent=agent,
        tools=tools,
        verbose=True,
        max_iterations=3,
        handle_parsing_errors=True
    )
    
    return agent_excutor


async def chat_with_agent(agent_excutor:AgentExecutor,
                          chat_service: ChatService,
                          account_id:str,
                          inpput_text:str)->AsyncIterator:
    """和智能体对话"""
    try:
        #获取历史摘要
        summary = await chat_service.generate_summary(account_id)
        
        #执行对话
        async for chunk in agent_excutor.astream({
            "input": inpput_text,
            "summary": summary,
        }):
            logger.info(f"用户{account_id}对话结果:{chunk}")
            if "output" in chunk:
                response = chunk["output"]
                
                #保持对话消息
                #await chat_service.save_chat_messages(account_id, inpput_text, response)
                chat_service.save_chat_messages(account_id, inpput_text, response)
                #流式响应
                for token in response:
                    yield token
                    await asyncio.sleep(0.01)
        
    except Exception as e:
        logger.error(f"用户{account_id}对话失败：{e}")
        raise ApiException(msg="对话失败，请稍后再试")
    
    

    
async def generate_stream_response(
                                   chat_service:ChatService,
                                   account_id:str,
                                   message:str)->AsyncIterator:
    """生成流式响应"""
    agent = create_chat_agent(get_chat_tools())
    current_chunk = ""
    async for token in chat_with_agent(agent,chat_service,account_id,message):
        current_chunk += token
        #当遇到标点符号或者长度达到一定时，就发送chunk一次
        if token in ["。","？","！","；","，"] or len(current_chunk)>=10:
            response = JsonData.stream_data(current_chunk)
            yield f"data: {response.model_dump_json()}\n\n"
            current_chunk = ""
            await asyncio.sleep(0.01)
    
    #发送剩余的chunk
    if current_chunk:
        response = JsonData.stream_data(current_chunk)
        yield f"data: {response.model_dump_json()}\n\n"
        
    #发送结束标记
    yield "data: [DONE]\n\n"