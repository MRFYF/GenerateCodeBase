package cn.cxyfyf.base.utils.gen;

import cn.cxyfyf.base.utils.CustomException;
import cn.cxyfyf.base.utils.StringUtils;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 代码生成器 工具类
 *
 */
public class GenUtils
{
    // 包名
    public final static String hjhpackageName = "com.leesky.ezframework.decorate";
    public final static String packageName = "cn.cxyfyf.base";
    // 作者
    public final static String author = "fengyingfeng";
    // 自动去除表前缀，默认是false
    public final static boolean autoRemovePre = false;
    // 表前缀(暂无用)
    public final static String tablePrefix = "hc_";
    // 代码生成路径
    public final static String genPath = "D:\\software\\tem\\code";
    /**
     * 数据连接相关，需要手动设置
     */
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";
    private static final String DRIVER_CLASSNAME = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8&characterEncoding=utf-8";


    /**
     * 生成代码（自定义路径）
     *
     * @param tableName 表名称
     */
    public static void generatorCode(String tableName, GenEnum genEnum) {

        // 查询表信息 初始化 字段属性
        GenTable table = getTableInfo(tableName);
        // 初始化表配置信息
        initTable(table, genEnum);

        // 模板引擎 初始化
        VelocityInitializer.initVelocity();
        VelocityContext context = VelocityUtils.prepareContext(table);

        // 获取模板列表
        List<String> templates = GenEnum.OWNER.equals(genEnum) ? VelocityUtils.getTemplateList() : VelocityUtils.getHjhTemplateList();

        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, StandardCharsets.UTF_8.toString());
            tpl.merge(context, sw);
            try {
                String path = getGenPath(table, template, genEnum);
                FileUtils.writeStringToFile(new File(path), sw.toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new CustomException("渲染模板失败，表名：" + table.getTableName());
            } finally {
                try {
                    sw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化表信息
     */
    public static void initTable(GenTable genTable, GenEnum genEnum)
    {
        genTable.setClassName(convertClassName(genTable.getTableName()));
        genTable.setClassNameFirst(StringUtils.uncapitalize(genTable.getClassName()));
        genTable.setPackageName(GenEnum.OWNER.equals(genEnum) ? packageName : hjhpackageName);
        genTable.setModuleName(getModuleName(GenEnum.OWNER.equals(genEnum) ? packageName : hjhpackageName));
        genTable.setBusinessName(getBusinessName(genTable.getTableName()));
        genTable.setFunctionName(genTable.getTableComment());
        genTable.setFunctionAuthor(author);
        if (StringUtils.isEmpty(genPath))
            throw new CustomException("自定义代码生成路径不能为空！");
        genTable.setGenPath(genPath);
    }

    /**
     * 初始化列属性字段
     */
    public static void initColumnField(GenTableColumn column)
    {
        String dataType = getDbType(column.getColumnType());
        String columnName = column.getColumnName();
        // 设置java字段名
        column.setJavaField(StringUtils.toCamelCase(columnName));
        column.setJavaFirstField(StringUtils.capitalize(column.getJavaField()));
        // 设置默认类型
        column.setJavaType(GenConstants.TYPE_STRING);
        column.setQueryType("EQ");

        if (arraysContains(GenConstants.COLUMNTYPE_TIME, dataType))
        {
            column.setJavaType(GenConstants.TYPE_DATE);
            column.setQueryType("BETWEEN");
        }
        else if (arraysContains(GenConstants.COLUMNTYPE_NUMBER, dataType))
        {
            // 如果是浮点型 统一用BigDecimal
            String[] str = StringUtils.split(StringUtils.substringBetween(column.getColumnType(), "(", ")"), ",");
            if (str != null && str.length == 2 && Integer.parseInt(str[1]) > 0)
            {
                column.setJavaType(GenConstants.TYPE_BIGDECIMAL);
                column.setQueryType("LTE");
            }
            // 如果是tinyint
            else if (column.getColumnType().contains("tinyint"))
            {
                column.setJavaType(GenConstants.TYPE_INTEGER);
                column.setQueryType("EQ");
            }
            // 如果是整形
            else if (str != null && str.length == 1 && Integer.parseInt(str[0]) <= 11)
            {
                column.setJavaType(GenConstants.TYPE_INTEGER);
                column.setQueryType("EQ");
            }
            // 长整形
            else
            {
                column.setJavaType(GenConstants.TYPE_LONG);
                column.setQueryType("EQ");
            }
        }

        for (String likeField : GenConstants.likeFields) {
            if (column.getJavaField().toLowerCase().contains(likeField)) {
                column.setQueryType("LIKE");
            }
        }

    }

    /**
     * 校验数组是否包含指定值
     *
     * @param arr 数组
     * @param targetValue 值
     * @return 是否包含
     */
    public static boolean arraysContains(String[] arr, String targetValue)
    {
        return Arrays.asList(arr).contains(targetValue);
    }

    /**
     * 获取模块名
     *
     * @param packageName 包名
     * @return 模块名
     */
    public static String getModuleName(String packageName)
    {
        int lastIndex = packageName.lastIndexOf(".");
        int nameLength = packageName.length();
        return org.apache.commons.lang.StringUtils.substring(packageName, lastIndex + 1, nameLength);
    }

    /**
     * 获取业务名
     *
     * @param tableName 表名
     * @return 业务名
     */
    public static String getBusinessName(String tableName)
    {
        int lastIndex = tableName.lastIndexOf("_");
        int nameLength = tableName.length();
        return org.apache.commons.lang.StringUtils.substring(tableName, lastIndex + 1, nameLength);
    }

    /**
     * 表名转换成Java类名
     *
     * @param tableName 表名称
     * @return 类名
     */
    public static String convertClassName(String tableName)
    {
        if (autoRemovePre && StringUtils.isNotEmpty(tablePrefix))
        {
            String[] searchList = StringUtils.split(tablePrefix, ",");
            tableName = replaceFirst(tableName, searchList);
        }
        return StringUtils.convertToCamelCase(tableName);
    }

    /**
     * 批量替换前缀
     *
     * @param replacementm 替换值
     * @param searchList 替换列表
     * @return
     */
    public static String replaceFirst(String replacementm, String[] searchList)
    {
        String text = replacementm;
        for (String searchString : searchList)
        {
            if (replacementm.startsWith(searchString))
            {
                text = replacementm.replaceFirst(searchString, "");
                break;
            }
        }
        return text;
    }

    /**
     * 关键字替换
     *
     * @param text 需要被替换的名字
     * @return 替换后的名字
     */
    public static String replaceText(String text)
    {
        return RegExUtils.replaceAll(text, "(?:表|若依)", "");
    }

    /**
     * 获取数据库类型字段
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static String getDbType(String columnType)
    {
        if (StringUtils.indexOf(columnType, "(") > 0)
        {
            return org.apache.commons.lang.StringUtils.substringBefore(columnType, "(");
        }
        else
        {
            return columnType;
        }
    }

    /**
     * 获取字段长度
     *
     * @param columnType 列类型
     * @return 截取后的列类型
     */
    public static Integer getColumnLength(String columnType)
    {
        if (StringUtils.indexOf(columnType, "(") > 0)
        {
            String length = StringUtils.substringBetween(columnType, "(", ")");
            return Integer.valueOf(length);
        }
        else
        {
            return 0;
        }
    }

    /**
     * 获取表结构信息
     * 目前仅支持mysql
     */
    private static GenTable getTableInfo(String tableName) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        GenTable tableInfo = new GenTable();
        tableInfo.setTableName(tableName);
        List<GenTableColumn> columns = new ArrayList<>();
        try {
            conn = DBConnectionUtil.getConnection();

            //表字段信息
            String sql = "select column_name,data_type,column_type,column_comment,column_key,extra from information_schema.columns where table_name=? order by ordinal_position";
            ps = conn.prepareStatement(sql);
            ps.setString(1, tableName);
            rs = ps.executeQuery();
            while (rs.next()) {
                GenTableColumn column = new GenTableColumn();
                //列名，全部转为小写
                column.setColumnName(rs.getString("column_name").toLowerCase());
                //列类型
                column.setColumnType(rs.getString("column_type"));
                //列注释
                column.setColumnComment(rs.getString("column_comment"));
                //主键
                if (rs.getString("column_key").equals("PRI")) {
                    column.setIsPk("1");
                    // 设置主键列
                    tableInfo.setPkColumn(column);
                } else {
                    column.setIsPk("0");
                }

                initColumnField(column);

                columns.add(column);
            }

            tableInfo.setColumns(columns);

            //表注释
            sql = "select table_comment from information_schema.tables where table_name=?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, tableName);
            rs = ps.executeQuery();
            while (rs.next()) {
                //表注释
                tableInfo.setTableComment(rs.getString("table_comment"));
            }
        } catch (SQLException e) {

            e.printStackTrace();
        } finally {
            if(rs != null){
                DBConnectionUtil.close(conn, ps, rs);
            }
        }
        return tableInfo;
    }

    /**
     * JDBC连接数据库工具类
     */
    private static class DBConnectionUtil {

        static {
            // 1、加载驱动
            try {
                Class.forName(DRIVER_CLASSNAME);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * 返回一个Connection连接
         */
        @SneakyThrows
        static Connection getConnection() {
            // 2、连接数据库
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }

        /**
         * 关闭Connection，Statement连接
         */
        @SneakyThrows
        public static void close(Connection conn, Statement stmt) {
            conn.close();
            stmt.close();
        }

        /**
         * 关闭Connection，Statement，ResultSet连接
         */
        @SneakyThrows
        public static void close(Connection conn, Statement stmt, ResultSet rs) {
            close(conn, stmt);
            rs.close();
        }

    }

    /**
     * 获取代码生成地址
     *
     * @param table    业务表信息
     * @param template 模板文件路径
     * @return 生成地址
     */
    public static String getGenPath(GenTable table, String template, GenEnum genEnum) {
        String genPath = table.getGenPath();
        if (StringUtils.equals(genPath, "/")) {
            return System.getProperty("user.dir") + File.separator + "src" + File.separator + VelocityUtils.getFileName(template, table, genEnum);
        }
        return genPath + File.separator + VelocityUtils.getFileName(template, table, genEnum);
    }
}