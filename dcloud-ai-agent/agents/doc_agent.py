from langchain.agents import AgentExecutor, create_openai_functions_agent
from langchain.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain.tools import Tool
from core.llm import get_default_llm
from typing import List, AsyncIterator
import asyncio
from datetime import datetime
import logging

logger = logging.getLogger(__name__)


def create_document_agent(tools:List[Tool])->AgentExecutor:
    """创建文档智能体"""
    # 定义系统提示词
    system_prompt = """
    你是一个专业的文档处理助手。你的任务是分析用户提供的文档，生成高质量的总结。
    你需要：
    1. 仔细阅读并理解文档内容
    2. 根据用户要求的总结类型（简要/详细/要点）生成相应的总结
    3. 提取文档的关键要点
    4. 确保总结准确、全面、易读
    如果用户提供了额外的要求，请尽量满足这些要求。
    如果用户的需求需要最新信息,请使用相关的工具。
    """
    
    #创建提示词模版
    prompt = ChatPromptTemplate.from_messages(
        [
            ("system", system_prompt),
            ("human", "{input}"),
            MessagesPlaceholder(variable_name="agent_scratchpad")
        ]
    )
    
    #获取大模型实例
    llm = get_default_llm()
    
    #创建智能体
    agent = create_openai_functions_agent(llm,tools,prompt)
    
    #创建执行器
    agent_excutor = AgentExecutor(
        agent=agent,
        tools=tools,
        verbose=True,
        max_iterations=3,
        handle_parsing_errors=True
    )
    
    return agent_excutor


async def process_document(agent_excutor:AgentExecutor,input_text:str)->AsyncIterator[str]:
    logging.info(f"开始处理文档: {input_text}")
    async for chunk in agent_excutor.astream({"input":input_text}):
        if "output" in chunk:
            for token in chunk["output"]:
                yield token
                await asyncio.sleep(0.01)
            
        
        

