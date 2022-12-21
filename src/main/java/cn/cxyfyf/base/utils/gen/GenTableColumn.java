package cn.cxyfyf.base.utils.gen;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 代码生成业务字段表 hc_gen_table_column
 * 
 */
@Getter
@Setter
public class GenTableColumn implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 列名称 */
    private String columnName;

    /** 列描述 */
    private String columnComment;

    /** 列类型 */
    private String columnType;

    /** JAVA类型 */
    private String javaType;

    /** JAVA字段名 */
    private String javaField;

    /** JAVA字段名首字母大写 */
    private String javaFirstField;

    /** 是否主键（1是） */
    private String isPk;

    /** sql查询类型 */
    private String queryType;
}