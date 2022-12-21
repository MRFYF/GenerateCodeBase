package cn.cxyfyf.base.utils.gen;

/**
 * @author ：fengyingfeng
 * @date ：Created in 2021/8/27 10:21
 * @description：代码生成模块常量类
 * @modified By：
 * @version: $
 */
public class GenConstants {

    /** 数据库时间类型 */
    public static final String[] COLUMNTYPE_TIME = { "datetime", "time", "date", "timestamp" };

    /** 数据库数字类型 */
    public static final String[] COLUMNTYPE_NUMBER = { "tinyint", "smallint", "mediumint", "int", "number", "integer",
            "bigint", "float", "double", "decimal" };

    /** 模糊查询字段 */
    public static final String[] likeFields = { "name", "memo", "remark", "describe", "info", "mail", "phone"};

    /** 字符串类型 */
    public static final String TYPE_STRING = "String";

    /** 整型 */
    public static final String TYPE_INTEGER = "Integer";

    /** 长整型 */
    public static final String TYPE_LONG = "Long";

    /** 高精度计算类型 */
    public static final String TYPE_BIGDECIMAL = "BigDecimal";

    /** 时间类型 */
    public static final String TYPE_DATE = "Date";


}
