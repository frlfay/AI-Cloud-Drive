from pydantic import BaseModel, HttpUrl
from typing import Optional

class DocumentRequest(BaseModel):
    """文档处理请求"""
    url: HttpUrl  # 文档URL
    summary_type: str = "brief"  # 总结类型：brief(简要), detailed(详细), key_points(要点)
    language: str = "zh"  # 输出语言
    length: Optional[str] = None  # 长度限制
    additional_instructions: Optional[str] = None  # 额外要求