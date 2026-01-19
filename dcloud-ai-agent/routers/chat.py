from fastapi import APIRouter, Depends
from fastapi.responses import StreamingResponse
from services.chat_service import ChatService
from models.chat_schemas import ChatRequest, ChatHistoryResponse
from models.json_response import JsonData
from agents.chat_agent import generate_stream_response
from core.auth import get_current_user
from core.exceptions import ApiException
from typing import Dict, Any
import logging

logger = logging.getLogger(__name__)

router = APIRouter(
    prefix="/api/chat",
    tags=["AI聊天助手"],
)

@router.post("/stream")
async def chat_stream(request: ChatRequest,
                      chat_service: ChatService = Depends(ChatService),
                      current_user: Dict[str, Any] = Depends(get_current_user)):
    account_id = current_user['account_id']
    logger.info(f"用户{account_id}开始对话")
    
    return StreamingResponse(
        generate_stream_response(chat_service,account_id,request.message),
        media_type="text/event-stream"
    )
    
    
    
