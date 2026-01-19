
from fastapi import APIRouter, Depends
from models.pan_schemas import PanQueryRequest
from agents.pan_agent import process_pan_query
from core.auth import get_current_user
import logging


logger = logging.getLogger(__name__)
#创建路由
router = APIRouter(
    prefix="/api/pan",
    tags=["网盘查询"]
)


@router.post("/query")
async def pan_query(request: PanQueryRequest,
                    current_user: dict = Depends(get_current_user)):
    
    request.account_id = current_user['account_id']
    logger.info(f"用户{request.account_id}开始查询网盘")
    #调用智能体进行查询
    return await process_pan_query(request)