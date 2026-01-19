import json
from typing import List, Dict
import redis
from datetime import datetime
from core.config import settings
from core.llm import get_default_llm
from models.json_response import JsonData
import logging

logger = logging.getLogger(__name__)

class ChatService:
    
    def __init__(self):
        self.redis_client = redis.Redis(
            host=settings.REDIS_HOST,
            port=settings.REDIS_PORT,
            password=settings.REDIS_PASSWORD,
            db=settings.REDIS_DB,
            max_connections=settings.REDIS_MAX_CONNECTIONS,
            decode_responses=True
        )
        self.llm = get_default_llm()
        
    def _get_chat_key(self, account_id: str)->str:
        """获取用户对话历史key"""
        return f"chat_history:{account_id}"
    
    def save_chat_history(self, account_id:str, messages:List[Dict]):
        """保存用户对话历史"""
        key = self._get_chat_key(account_id)
        self.redis_client.set(key, json.dumps(messages))
        
    def get_chat_history(self, account_id:str)->List[Dict]:
        """获取用户对话历史"""
        key = self._get_chat_key(account_id)
        messages = self.redis_client.get(key)
        if messages:
            return json.loads(messages)
        return []
    
    def add_message(self,account_id:str, role:str, content:str):
        """添加一条消息到聊天历史"""
        messages = self.get_chat_history(account_id)
        messages.append({"role":role,"content":content,"timestamp":datetime.now().isoformat()})
        #增加更多逻辑 TODO
        self.save_chat_history(account_id, messages)
    
    def save_chat_messages(self,account_id:str,user_message:str,assistant_message:str):
        """保存用户对话消息"""
        self.add_message(account_id, "user", user_message)
        self.add_message(account_id, "assistant", assistant_message)
        
    def clear_chat_history(self, account_id:str):
        """清空用户对话历史"""
        key = self._get_chat_key(account_id)
        self.redis_client.delete(key)
    
    def _get_summary_key(self, account_id:str)->str:
        """获取摘要key"""
        return f"chat_summary:{account_id}"
        
    async def generate_summary(self, account_id:str)->str:
        """生成摘要"""
        try:
            #获取最新聊天记录
            messages = self.get_chat_history(account_id)
            print(f"messages:{messages}")
            if not messages:
                print("没有聊天记录")
                return ""
            
            #构建提示词
            prompt = f"""请根据以下对话历史生成一个简洁的核心摘要，突出主要话题和关键信息：
            
            {json.dumps(messages, ensure_ascii=False, indent=2)}
            
            摘要要求：
            1. 突出对话的主要话题和关键信息
            2. 使用第三人称描述，提取重要数据/时间节点/待办事项
            3. 保留原始对话中的重要细节
            4. 确保包含最新的对话内容
            """
            
            #生成摘要
            reponse = await self.llm.ainvoke(prompt)
            new_summary = reponse.content
            
            #获取历史摘要
            summary_key = self._get_summary_key(account_id)
            #old_summary = await self.redis_client.get(summary_key)
            old_summary = self.redis_client.get(summary_key)
            final_summary = ""
            if old_summary:
                # 如果存在旧摘要，生成一个合并的提示词
                merge_prompt = f"""请将以下两个摘要合并成一个新的摘要：
                旧摘要：
                {old_summary}
                
                新摘要：
                {new_summary}
                
                合并要求：
                1. 保留两个摘要中的重要信息
                2. 突出对话的主要话题和关键信息
                3. 使用第三人称描述，提取重要数据/时间节点/待办事项
                4. 保留原始对话中的重要细节
                5. 确保包含最新的对话内容
                """
                
                merge_response = await self.llm.ainvoke(merge_prompt)
                final_summary = merge_response.content
            else:
                final_summary = new_summary
            
            #更新缓存
            self.redis_client.set(summary_key,final_summary)
            return final_summary
    
        except Exception as e:
            logger.error(f"生成摘要失败:{e}")
            return ""
