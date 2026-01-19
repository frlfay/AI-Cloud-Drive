from fastapi import Depends, Header
from jose import JWTError, jwt
from typing import Dict, Any, Optional
import logging
from datetime import datetime, timedelta, UTC
import os
import sys
from core.config import settings
from core.exceptions import ApiException

logger = logging.getLogger(__name__)

def remove_prefix(token: str) -> str:
    """移除token的前缀"""
    return token.replace(settings.JWT_LOGIN_SUBJECT, "").strip() if token.startswith(settings.JWT_LOGIN_SUBJECT) else token

async def get_current_user(token: Optional[str] = Header(None)) -> Dict[str, Any]:
    """
    获取当前用户信息
    
    Args:
        token: JWT token字符串，从请求头中获取
        
    Returns:
        Dict[str, Any]: 包含用户信息的字典，格式为 {"account_id": int, "username": str}
        
    Raises:
        ApiException: 当token无效、过期或解析失败时抛出异常
    """
    try:
        # 验证token是否存在
        if not token or not token.strip():
            raise ApiException(msg="Token不能为空", code=-1)
            
        # 移除前缀并解析token
        token = remove_prefix(token.strip())
        payload = jwt.decode(token, settings.JWT_SECRET_KEY, algorithms=[settings.JWT_ALGORITHM])
        
        # 验证token内容
        if payload.get("sub") != settings.JWT_LOGIN_SUBJECT or not payload.get("accountId"):
            raise ApiException(msg="无效的认证凭据", code=-1)
            
        # 记录日志并返回用户信息
        logger.info(f"JWT解密成功，accountId: {payload.get('accountId')}, username: {payload.get('username')}")
        return {"account_id": payload.get("accountId"), "username": payload.get("username")}
        
    except Exception as e:
        logger.error(f"JWT处理失败: {str(e)}")
        raise ApiException(msg="无效的认证凭据", code=-1)