from typing import  Any, AsyncIterator
from agents.doc_agent import create_document_agent, process_document
from models.doc_schemas import DocumentRequest
from tools.document_tools import DocumentTools
import gc
import logging

logger = logging.getLogger(__name__)

class DocumentService:
    def __init__(self):
        #初始化工具
        self.tools = DocumentTools.create_tools()
        #创建智能体
        self.agent_excutor = create_document_agent(self.tools)
        
    async def process_document_stream(self,request:DocumentRequest)->AsyncIterator[str]:
        """处理文档"""
        try:
            logger.info(f"开始处理文档: {request.url}")
            #获取文档内容
            doc_content = DocumentTools.fetch_document(str(request.url))
            
            #根据文档长度进行分隔
            content = doc_content["content"]
            chunks = []
            
            if len(content) > 100000:
                logger.info(f"文档长度超过10000，开始分割")
                current_chunk = ""
                for paragraph in content.split('\n\n'):
                    if len(current_chunk) + len(paragraph) > 100000 and current_chunk:
                        chunks.append(current_chunk)
                        current_chunk = paragraph
                    else:
                        current_chunk = f"{current_chunk}\n\n{paragraph}" if current_chunk else paragraph
                if current_chunk:
                    chunks.append(current_chunk)
            
            else:
                chunks = [content]
            
            #处理每个文档块
            for i,chunk in enumerate(chunks):
                #构建输入文本
                input_text = self._build_input_text(
                    doc_content["title"], 
                    chunk, 
                    request.summary_type ,
                    request.language, 
                    request.length or "无限制", 
                    request.additional_instructions or "无")
                #异步处理文档块
                async for chunk in process_document(self.agent_excutor,input_text):
                    yield chunk
                
                #如果不是最后一个片段，插入分割符
                if i<len(chunks)-1:
                    yield "\n\n-----下一部分-----\n\n"
                    
                #清理内存
                gc.collect()
            
            
            
        except Exception as e:
            logger.error(f"处理文档失败: {str(e)}")
            yield f"处理文档失败: {str(e)}"
            
    
    def _build_input_text(self,title:str, content:str,summary_type:str,
                          language:str,length:str,additional_instructions:str)->str:
        
       
        """构建输入文本"""
        return f"""
        文档标题: {title}
        文档内容: {content}
        总结类型: {summary_type}
        输出语言: {language}
        最大长度: {length}
        额外要求: {additional_instructions}
        """ 
        