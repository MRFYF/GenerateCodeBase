package cn.cxyfyf.base;

import cn.cxyfyf.base.utils.gen.GenEnum;
import cn.cxyfyf.base.utils.gen.GenUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenTest {
    public static void main(String[] args) {
        String[] tables = {"sys_user", "sys_dept"}; // 对应数据库中的表名，多个逗号分隔 "sys_user", "sys_dept"
        for (String table : tables) {
            GenUtils.generatorCode(table, GenEnum.COMPANY); // GenEnum.COMPANY 使用公司模板、 GenEnum.OWNER 使用默认模板
        }
        log.info("代码生成后路径：" + GenUtils.genPath);
    }
}
