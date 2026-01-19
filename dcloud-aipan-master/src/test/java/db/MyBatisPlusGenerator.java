package db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import org.apache.ibatis.type.JdbcType;

import java.util.Collections;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群,官网 https://xdclass.net
 * @Version 1.0
 **/

public class MyBatisPlusGenerator {

    public static void main(String[] args) {

        String userName = "root";
        String password = "xdclass.net168";
        String serverInfo = "39.108.115.28:3306";
        String targetModuleNamePath = "/";
        String dbName = "dcloud_aipan";

        String[] tables = {
                "account", "file","account_file","file_chunk", "file_suffix","file_type", "share", "share_file", "storage"
        };


        // 使用 FastAutoGenerator 快速配置代码生成器
        FastAutoGenerator.create("jdbc:mysql://"+serverInfo+"/"+dbName+"?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&tinyInt1isBit=true", userName, password)
                .globalConfig(builder -> {
                    builder.author("小滴课堂-二当家小D,")        // 设置作者
                            .commentDate("yyyy-MM-dd")
                            .enableSpringdoc()
                            .disableOpenDir() //禁止打开输出目录
                            .dateType(DateType.ONLY_DATE)   //定义生成的实体类中日期类型 DateType.ONLY_DATE 默认值: DateType.TIME_PACK
                            .outputDir(System.getProperty("user.dir") + targetModuleNamePath + "/src/main/java"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("net.xdclass") // 父包模块名
                            .entity("model")      //Entity 包名 默认值:entity
                            .mapper("mapper")     //Mapper 包名 默认值:mapper
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + targetModuleNamePath + "/src/main/resources/mapper")); // 设置mapperXml生成路,默认存放在mapper的xml下
                })
                .dataSourceConfig(builder -> {//Mysql下tinyint字段转换
                    builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                        if (JdbcType.TINYINT == metaInfo.getJdbcType()) {
                            return DbColumnType.BOOLEAN;
                        }
                        return typeRegistry.getColumnType(metaInfo);
                    });
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables) // 设置需要生成的表名 可变参数
                            .entityBuilder()// Entity策略配置
                            .enableFileOverride() // 开启生成Entity层文件覆盖
                            .idType(IdType.ASSIGN_ID)//主键策略  雪花算法自动生成的id
                            .enableLombok() //开启lombok
                            .logicDeleteColumnName("del")// 说明逻辑删除是哪个字段
                            .enableTableFieldAnnotation()// 属性加上注解说明
                            .formatFileName("%sDO") //格式化生成的文件名称
                            .controllerBuilder().disable()// Controller策略配置,这里不生成Controller层
                            .serviceBuilder().disable()// Service策略配置,这里不生成Service层
                            .mapperBuilder()// Mapper策略配置
                            .enableFileOverride() // 开启生成Mapper层文件覆盖
                            .formatMapperFileName("%sMapper")// 格式化Mapper文件名称
                            .superClass(BaseMapper.class) //继承的父类
                            .enableBaseResultMap() // 开启生成resultMap,
                            .enableBaseColumnList() // 开启生成Sql片段
                            .formatXmlFileName("%sMapper"); // 格式化xml文件名称
                })
                .templateConfig(builder -> {
                    // 不生成Controller
                    builder.disable(TemplateType.CONTROLLER,TemplateType.SERVICE,TemplateType.SERVICE_IMPL);
                })
                .execute(); // 执行生成
    }


}