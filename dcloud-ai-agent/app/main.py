import uvicorn
from fastapi import FastAPI
from core.config import settings
from core.exceptions import ApiException, api_exception_handler
from routers import chat,doc,pan
import logging
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(
    title=settings.APP_NAME,
    description="AI智能体中心API服务",
    version="1.0.0"
)
#开启跨域配置
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 添加异常处理器
app.add_exception_handler(ApiException, api_exception_handler)

#日志配置
logging.basicConfig(
    filename="app.log",
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S"
    )
logging.info("AI智能体中心API服务启动成功info")


app.include_router(chat.router)
app.include_router(doc.router)
app.include_router(pan.router)

@app.get("/")
async def root():
    return {
        "message": "欢迎使用AI智能体中心API",
        "version": "1.0.0",
        "available_agents": ["chat","doc","pan"] # 列出可用的智能体
    }

# 启动服务器的命令
if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)