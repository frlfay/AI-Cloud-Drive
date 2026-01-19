from langchain_openai import ChatOpenAI
from core.config import settings

def get_default_llm():
    return ChatOpenAI(
       model = settings.LLM_MODEL_NAME,
       base_url = settings.LLM_BASE_URL,
       api_key = settings.LLM_API_KEY,
       temperature = settings.LLM_TEMPERATURE,
       streaming = settings.LLM_STREAMING
    )