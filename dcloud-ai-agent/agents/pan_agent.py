from langchain_community.agent_toolkits.sql.base import create_sql_agent
from langchain_community.agent_toolkits.sql.toolkit import SQLDatabaseToolkit
from langchain_community.utilities import SQLDatabase
from langchain.prompts import ChatPromptTemplate, MessagesPlaceholder
from typing import Dict, Any, List, Union
import json
from core.llm import get_default_llm
from core.config import settings
from models.json_response import JsonData
from models.pan_schemas import PanQueryRequest
import logging
from langchain.agents.agent_types import AgentType

logger = logging.getLogger(__name__)

def create_pan_agent() -> Any:
    """创建网盘查询agent"""
     # 创建数据库连接，只读模式
    db = SQLDatabase.from_uri(
        f"mysql+pymysql://{settings.MYSQL_USER}:{settings.MYSQL_PASSWORD}@{settings.MYSQL_HOST}:{settings.MYSQL_PORT}/{settings.MYSQL_DATABASE}",
        include_tables=['account_file', 'storage']    
        )
    
    #创建大模型
    llm = get_default_llm()
    
    #创建数据库工具包
    tookit = SQLDatabaseToolkit(db=db, llm=llm)
    
    #创建提示词
   # 创建提示模板
    prompt = ChatPromptTemplate.from_messages([
        ("system", """你是一个智能网盘助手，专门用于查询用户的网盘文件信息。你只能执行查询操作，不能执行任何修改数据的操作。

        重要警告：
        1. 你绝对不能生成或编造任何数据
        2. 你只能返回实际查询到的数据
        3. 如果查询没有结果，必须返回空结果
        4. 任何生成或编造数据的行为都是严重错误
        5. 你只能使用数据库中的实际数据
        6. 不能对查询结果进行任何修改或补充
        7. 不能生成示例数据或占位数据
        8. 不能假设或推测数据
        9. 不能使用模板或示例数据
        10. 不能对数据进行任何形式的加工或美化

        数据库表结构说明：
        - account_file: 用户文件表
          - id: 文件ID（account_file表的主键）
          - account_id: 用户ID
          - is_dir: 是否为文件夹（0不是，1是）
          - parent_id: 上层文件夹ID（顶层为0）
          - file_id: 实际存储的文件ID
          - file_name: 文件名称
          - file_type: 文件类型（common/compress/excel/word/pdf/txt/img/audio/video/ppt/code/csv）
          - file_suffix: 文件后缀名
          - file_size: 文件大小（字节）
          - del: 是否删除（0未删除，1已删除）
          - del_time: 删除时间
          - gmt_create: 创建时间
          - gmt_modified: 更新时间
        
        - storage: 存储信息表
          - id: 记录ID
          - account_id: 用户ID
          - used_size: 已使用空间（字节）
          - total_size: 总空间（字节）
          - gmt_create: 创建时间
          - gmt_modified: 更新时间

        你可以处理以下类型的查询请求：
        1. 文件查询
           - 查看我的文件列表
           - 搜索特定文件
           - 查看文件详细信息
           - 查看文件夹内容
           - 查看最近修改的文件
        
        2. 文件统计
           - 统计文件数量
           - 统计文件类型分布
           - 统计存储空间使用情况
           - 查看最近上传的文件
        
        3. 存储空间
           - 查看已使用空间
           - 查看剩余空间
           - 查看空间使用率
        

        重要限制：
        1. 你只能执行 SELECT 查询，不能执行任何修改数据的操作
        2. 所有查询必须包含 account_id 条件，确保数据安全
        3. 不能执行以下操作：
           - 删除文件
           - 修改文件
           - 创建文件
           - 移动文件
           - 重命名文件
           - 清空回收站
           - 修改存储空间
        4. 如果用户请求执行任何修改操作，请礼貌地拒绝并说明原因
        5. 如果查询没有结果，必须返回空结果，不能生成示例数据
        6. 绝对不能生成或编造任何数据
        7. 只能返回实际查询到的数据
        8. 不能对数据进行任何形式的加工或美化

        处理请求时请注意：
        1. 必须使用 account_id 过滤用户数据，确保数据安全
        2. 对于文件夹查询，使用 is_dir=1 和 parent_id
        3. 对于文件类型查询，使用 file_type 字段
        4. 对于模糊查询，使用 LIKE 和通配符
        5. 对于时间相关的查询，使用 gmt_create 和 gmt_modified
        6. 对于空间统计，使用 storage 表
        7. 结果要简洁明了，突出重点
        8. 所有查询必须包含 account_id 条件
        9. 查询文件信息时，必须返回 account_file 表的 id 和 file_id
        10. 所有响应必须返回 JSON 格式的数据，包含完整的文件信息
        11. 如果查询没有结果，返回空数组或空对象，不要生成示例数据
        12. 绝对不能生成或编造任何数据

        响应格式必须符合以下模型结构：
        1. 文件列表响应：
           {{
               "type": "file_list",
               "data": List[FileInfo]  # FileInfo包含id, file_id, file_name, file_type, file_suffix, file_size, gmt_create, gmt_modified
           }}
        
        2. 存储空间信息响应：
           {{
               "type": "storage_info",
               "data": StorageInfo  # StorageInfo包含used_size, total_size, used_percentage
           }}
        
        3. 文件统计信息响应：
           {{
               "type": "file_statistics",
               "data": FileStatistics  # FileStatistics包含total_files, total_size, file_types, recent_files
           }}
        
        请根据用户的问题，使用 SQL 查询来获取信息，并返回符合上述格式的 JSON 数据。
        重要警告：你绝对不能生成或编造任何数据，只能返回实际查询到的数据。任何生成或编造数据的行为都是严重错误。"""),
        ("human", "{input}"),
        MessagesPlaceholder(variable_name="agent_scratchpad"),
    ])
    
    #创建SQLAgent
    agent = create_sql_agent(
        llm=llm,
        toolkit=tookit,
        agent_type="openai-tools",
        verbose=True,
        return_intermediate_steps=True,
        max_iterations=15,
        handle_parsing_errors=True,
        prompt=prompt
    )
    
    return agent


async def process_pan_query(request: PanQueryRequest)->JsonData:
    agent = create_pan_agent()
    
    #构建查询输入，可以做更多事情，包括检查过滤用户的输入
    query_input = f"用户ID为{request.account_id}的{request.query}"
    
    #获取代理的输出
    response = await agent.ainvoke({"input":query_input})
    
    if "output" not in response:
        return JsonData.error("查询失败,请换种方式再重试下")
    
    output = response["output"]
    
    #解析数据
    try:
        data = json.loads(output) if isinstance(output, str) else output
        logger.info(f"解析数据成功: {data}")
        return JsonData.success({"type": data.get("type"),"data": data.get("data")})
    
    except json.JSONDecodeError as e:
        logger.error(f"解析数据失败: {str(e)}")
        return JsonData.success(data={"content":str(output)})