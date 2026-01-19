from langchain_core.output_parsers import StrOutputParser
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.runnables import RunnablePassthrough
from langchain_community.document_loaders import WebBaseLoader
from langchain_community.document_loaders import TextLoader
from langchain_community.vectorstores import Chroma
from langchain_openai import ChatOpenAI
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_community.embeddings import DashScopeEmbeddings

# 1. 加载文档
# loader = WebBaseLoader("https://xdclass.net")  # 示例网页
# docs = loader.load()

# 文本加载
loader = TextLoader("data/qa.txt")
docs = loader.load()

# 2. 分割文档
text_splitter = RecursiveCharacterTextSplitter(
    chunk_size=1000,
    chunk_overlap=200
)
splits = text_splitter.split_documents(docs)

# 3. 初始化嵌入模型
embedding_model = DashScopeEmbeddings(
    model="text-embedding-v2",  # 第二代通用模型
    max_retries=3,
    dashscope_api_key="sk-005c3c25f6d042848b29d75f2f020f08"
)

# 4. 创建向量存储
vectorstore = Chroma.from_documents(
    documents=splits,
    embedding=embedding_model,
    persist_directory="./rag_chroma_db"
)
retriever = vectorstore.as_retriever(search_kwargs={"k": 3})

# 5. 定义模型
model = ChatOpenAI(
    model_name="qwen-plus",
    base_url="https://dashscope.aliyuncs.com/compatible-mode/v1",
    api_key="sk-005c3c25f6d042848b29d75f2f020f08",
    temperature=0.7
)

# 6. 创建提示模板
template = """[INST]<<SYS>>
你是一个有用的AI助手，请根据以下上下文回答问题：
{context}
<</SYS>>
问题：{question} [/INST]"""
rag_prompt = ChatPromptTemplate.from_template(template)

# 7. 构建RAG链
rag_chain = (
    {"context": retriever, "question": RunnablePassthrough()}
    | rag_prompt
    | model
    | StrOutputParser()
)

# 使用示例
# result = rag_chain.invoke("你的问题")
# print(result)
