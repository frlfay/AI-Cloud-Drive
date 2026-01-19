from fastapi import HTTPException, status
from fastapi.responses import JSONResponse
from typing import Any
from models.json_response import JsonData

class ApiException(Exception):
    """API异常基类"""
    def __init__(
        self,
        msg: str = "操作失败",
        code: int = -1,
        data: Any = None
    ):
        self.msg = msg
        self.code = code
        self.data = data
        super().__init__(msg)


async def api_exception_handler(request, exc: Exception) -> JSONResponse:
    """统一异常处理器"""
    print(f"❌ API异常: {str(exc)}")
    if isinstance(exc, ApiException):
        response = JsonData.error(msg=exc.msg, code=exc.code)
    elif isinstance(exc, HTTPException):
        response = JsonData.error(msg=str(exc.detail), code=exc.status_code)
    else:
        # 处理其他所有异常，包括验证错误
        response = JsonData.error(msg=str(exc), code=-1)
        
    return JSONResponse(
        status_code=status.HTTP_200_OK,
        content=response.model_dump()
    )