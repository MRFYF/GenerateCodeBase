package cn.cxyfyf.base.utils.gen;

import cn.cxyfyf.base.utils.DateUtils;
import cn.cxyfyf.base.utils.StringUtils;
import org.apache.velocity.VelocityContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模板工具类
 */
public class VelocityUtils {
    /**
     * 项目空间路径
     */
    private static final String PROJECT_PATH = "main/java";

    /**
     * mybatis空间路径
     */
    private static final String MYBATIS_PATH = "main/resources/mapper";

    /**
     * 设置模板变量信息
     *
     * @return 模板列表
     */
    public static VelocityContext prepareContext(GenTable genTable) {

        String packageName = genTable.getPackageName();
        String functionName = genTable.getFunctionName();

        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("tableName" , genTable.getTableName());
        velocityContext.put("tableComment" , genTable.getTableComment());
        velocityContext.put("functionName" , StringUtils.isNotEmpty(functionName) ? functionName : "【请填写功能名称】");
        velocityContext.put("className" , genTable.getClassName());
        velocityContext.put("classNameFirst" , genTable.getClassNameFirst());
        velocityContext.put("moduleName" , genTable.getModuleName());
        velocityContext.put("BusinessName" , StringUtils.capitalize(genTable.getBusinessName()));
        velocityContext.put("businessName" , genTable.getBusinessName());
        velocityContext.put("basePackage" , getPackagePrefix(packageName));
        velocityContext.put("packageName" , packageName);
        velocityContext.put("author" , genTable.getFunctionAuthor());
        velocityContext.put("datetime" , DateUtils.getDate());
        velocityContext.put("pkColumn" , genTable.getPkColumn());
        velocityContext.put("importList" , getImportList(genTable));
        velocityContext.put("columns" , genTable.getColumns());
        velocityContext.put("table" , genTable);
        velocityContext.put("fieldsStr" , genTable.getColumns().stream().map(GenTableColumn::getJavaField).collect(Collectors.joining(",")));
        return velocityContext;
    }

    /**
     * 合家和模板信息
     *
     * @return 模板列表
     */
    public static List<String> getHjhTemplateList() {
        List<String> templates = new ArrayList<>();
        templates.add("hjh/model.java.vm");
        templates.add("hjh/dto.java.vm");
        templates.add("hjh/exceldto.java.vm");
        templates.add("hjh/mapper.java.vm");
        templates.add("hjh/service.java.vm");
        templates.add("hjh/serviceimpl.java.vm");
        templates.add("hjh/controller.java.vm");
        return templates;
    }

    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<String> getTemplateList() {
        List<String> templates = new ArrayList<>();
        templates.add("vm/java/domain.java.vm");
        templates.add("vm/java/to.java.vm");
        templates.add("vm/java/vo.java.vm");
        templates.add("vm/java/mapper.java.vm");
        templates.add("vm/java/service.java.vm");
        templates.add("vm/java/serviceImpl.java.vm");
        templates.add("vm/java/controller.java.vm");
        templates.add("vm/xml/mapper.xml.vm");
        return templates;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, GenTable genTable, GenEnum genEnum) {
        // 文件名称
        String fileName = "";
        // 包路径
        String packageName = genTable.getPackageName();
        // 模块名
        String moduleName = genTable.getModuleName();
        // 大写类名
        String className = genTable.getClassName();
        String classNameFirst = genTable.getClassNameFirst();
        String javaPath = PROJECT_PATH + "/" + StringUtils.replace(packageName, "." , "/");
        String mybatisPath = MYBATIS_PATH + "/" + moduleName;

        // 获取模板名称，去掉路径名
        if (template.contains("/")) {
            String[] split = template.split("/");
            template = split[split.length-1];
        }

        switch (template) {
            case "domain.java.vm":
                fileName = StringUtils.format("{}/domain/{}.java", javaPath, className);
                break;
            case "mapper.java.vm":
                if (genEnum.equals(GenEnum.COMPANY)) {
                    fileName = StringUtils.format("{}/mapper/I{}Mapper.java", javaPath, classNameFirst);
                } else {
                    fileName = StringUtils.format("{}/mapper/{}Mapper.java", javaPath, className);
                }
                break;
            case "service.java.vm":
                fileName = StringUtils.format("{}/service/I{}Service.java", javaPath, GenEnum.OWNER.equals(genEnum) ? className : classNameFirst);
                break;
            case "serviceImpl.java.vm":
            case "serviceimpl.java.vm":
                fileName = StringUtils.format("{}/service/impl/{}ServiceImpl.java", javaPath, className);
                break;
            case "controller.java.vm":
                fileName = StringUtils.format("{}/controller/{}Controller.java", javaPath, className);
                break;
            case "to.java.vm":
                fileName = StringUtils.format("{}/controller/to/{}To.java", javaPath, className);
                break;
            case "vo.java.vm":
                fileName = StringUtils.format("{}/controller/vo/{}Vo.java", javaPath, className);
                break;
            case "mapper.xml.vm":
                fileName = StringUtils.format("{}/{}Mapper.xml", mybatisPath, className);
                break;
            case "model.java.vm":
                fileName = StringUtils.format("{}/model/{}Model.java", javaPath, className);
                break;
            case "dto.java.vm":
                fileName = StringUtils.format("{}/dto/{}DTO.java", javaPath, className);
                break;
            case "exceldto.java.vm":
                fileName = StringUtils.format("{}/dto/{}ExcelDTO.java", javaPath, className);
                break;
        }
        return fileName;
    }

    /**
     * 获取包前缀
     *
     * @param packageName 包名称
     * @return 包前缀名称
     */
    public static String getPackagePrefix(String packageName) {
        int lastIndex = packageName.lastIndexOf(".");
        return StringUtils.substring(packageName, 0, lastIndex);
    }

    /**
     * 根据列类型获取导入包
     *
     * @param genTable 业务表对象
     * @return 返回需要导入的包列表
     */
    public static HashSet<String> getImportList(GenTable genTable) {
        List<GenTableColumn> columns = genTable.getColumns();
        HashSet<String> importList = new HashSet<>();
        for (GenTableColumn column : columns) {
            if (GenConstants.TYPE_DATE.equals(column.getJavaType())) {
                importList.add("java.util.Date");
                importList.add("com.fasterxml.jackson.annotation.JsonFormat");
            } else if (GenConstants.TYPE_BIGDECIMAL.equals(column.getJavaType())) {
                importList.add("java.math.BigDecimal");
            } else if (GenConstants.TYPE_STRING.equals(column.getJavaType())) {
                importList.add("java.lang.String");
            } else if (GenConstants.TYPE_INTEGER.equals(column.getJavaType())) {
                importList.add("java.lang.Integer");
            } else if (GenConstants.TYPE_LONG.equals(column.getJavaType())) {
                importList.add("java.lang.Long");
            }
        }
        return importList;
    }

}
