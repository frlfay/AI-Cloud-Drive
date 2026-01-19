from fastapi import APIRouter, Depends
from models.doc_schemas import DocumentRequest
from models.json_response import JsonData
from services.doc_service import DocumentService
from fastapi.responses import StreamingResponse
import asyncio

router = APIRouter(prefix="/api/document", tags=["文档助手"])


async def generate_stream_response(request: DocumentRequest, doc_service: DocumentService):
    """生成流式响应"""
    async for chunk in doc_service.process_document_stream(request):
        response = JsonData.stream_data(chunk)
        yield f"data: {response.model_dump_json()}\n\n"
        await asyncio.sleep(0.01)
    
    yield "data: [DONE]\n\n"


@router.post("/stream")
async def process_document_stream(request: DocumentRequest,
                                  doc_service: DocumentService = Depends(DocumentService)):
    """
    处理文档
    """
    return StreamingResponse(
        generate_stream_response(request, doc_service),
        media_type="text/event-stream"
    )