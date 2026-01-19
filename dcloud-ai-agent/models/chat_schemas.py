from pydantic import BaseModel
from typing import List, Optional

class ChatMessage(BaseModel):
    role:str
    content:str
    timestamp:str
    
    
class ChatHistoryResponse(BaseModel):
    messages:List[ChatMessage]


class ChatRequest(BaseModel):
    message:str
    