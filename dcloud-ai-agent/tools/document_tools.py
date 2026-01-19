from typing import List, Optional, Dict, Any, Generator
import requests
from bs4 import BeautifulSoup
from langchain.tools import Tool
import re
from urllib.parse import urlparse
import time
import io
from PyPDF2 import PdfReader
from tqdm import tqdm
import gc
import logging
from core.exceptions import ApiException

logger = logging.getLogger(__name__)



class DocumentTools:
    """文档处理工具集"""
    @staticmethod
    def fetch_document(url:str)->Dict[str,Any]:
        """获取文档"""
        try:
            response = requests.get(url,stream=True,timeout=30)
            response.raise_for_status()
            
            #获取文档类型
            content_type = response.headers.get("content-type",'')
            if "text/html" in content_type:
                logger.debug("解析HTML")
                return DocumentTools._parse_html(response)
            elif "application/pdf" in content_type:
                logger.debug("解析PDF")
                return DocumentTools._parse_pdf_stream(response)
            else:
                logger.debug("未知文档类型")
                return {
                    "title":  urlparse(url).path.split("/")[-1],
                    "content": response.text,
                    "type":"text"
                }
            
        except Exception as e:
            #logger.exception("获取文档失败")
            raise ApiException(msg="无法获取文档")
        
        
    @staticmethod
    def _parse_html(response:requests.Response)->Dict[str,Any]:
        logger.debug("解析HTML")
        soup = BeautifulSoup(response.text, "html.parser")
        
        #提取获取标题和内容
        title = soup.title.string if soup.title else "未命名文档"
        
        #提取正文
        content = ""
        
        for p in soup.find_all(['p','h1','h2','h3','h4','h5','h6']):
            content += p.get_text() + "\n"
        
        return {
            "title":title,
            "content":content,
            "type":"html"
        }
    
    
    @staticmethod
    def _parse_pdf_stream(response: requests.Response )->Dict[str,Any]:
        """解析pdf流，加载在线内容"""
        logger.debug("解析pdf流")
        
        #创建内存缓冲区
        buffer = io.BytesIO()
        
        #获取文件大小
        total_size = int(response.headers.get("content-length",0))
        
        #使用tqdm显示进度条
        with tqdm(total=total_size, unit="B", unit_scale=True, desc="下载PDF") as pbar:
            for chunk in response.iter_content(chunk_size=8192):
                if chunk:
                    buffer.write(chunk)
                    pbar.update(len(chunk))
                    
        #重置缓冲区
        buffer.seek(0)
        
        #分块读取PDF内容
        content = ""
        try:
            pdf_reader = PdfReader(buffer)
            #获取文档信息
            info = pdf_reader.metadata
            title = info.title if info and info.title else "未命名文档"
            
            #分块处理
            total_pages = len(pdf_reader.pages)
            with tqdm(total=total_pages, desc="处理PDF") as pbar:
                for page_num in range(total_pages):
                    page = pdf_reader.pages[page_num]
                    content += page.extract_text()+"\n"
                    
                    #移除相关的空行或者空格,清理一下内存
                    if (page_num+1)%10 == 0:
                        content = DocumentTools._clean_text(content)
                        gc.collect()
                    pbar.update(1)
                    
        except Exception as e:
            raise ApiException(msg="无法解析PDF")
        finally:
            buffer.close()
            
        return {
            "title":title,
            "content":content,
            "type":"pdf"
        }
            
    @staticmethod
    def _clean_text(text: str) -> str:
        """清理文本内容
        
        Args:
            text (str): 待清理的文本内容
        
        Returns:
            str: 清理后的文本内容
        """
        # 移除多余的空行
        text = re.sub(r'\n\s*\n', '\n\n', text)
        # 移除多余的空格
        text = re.sub(r' +', ' ', text)
        return text.strip()
    
    @staticmethod
    def create_tools()->List[Tool]:
        """创建文档处理工具集
        Returns:
            list[Tool]: 文档处理工具列表
        """
        return[
            Tool(
                name="fetch_document",
                func=DocumentTools.fetch_document,
                description="用于获取文档内容，输入url，返回标题和内容"
            )
        ]