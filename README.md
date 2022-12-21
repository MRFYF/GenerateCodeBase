# GenerateCodeBase
代码生成基础项目，配套模板，可自己改造



## 项目版本

项目默认使用jdk17、springboot 2.7.5 版本，可自行在pom文件进行版本降级，支持jdk8+ 

![image](https://user-images.githubusercontent.com/52645077/208847542-0e2c778c-2d98-424b-b936-df3f9a1ef980.png)

![image](https://user-images.githubusercontent.com/52645077/208847564-b7c9aabc-3f16-42fb-b8fb-84397fb9a177.png)

## 使用方式

![image](https://user-images.githubusercontent.com/52645077/208847580-1c1b8048-4aca-404d-9f28-da5857cfbf98.png)

打开cn.cxyfyf.base.GenTest，在此，按住Ctrl点击GenUtils进入此类

![image](https://user-images.githubusercontent.com/52645077/208847594-e7d84e3a-c312-49ff-807f-0f3681560f3f.png)

```java
	// 公司模板包名
    public final static String hjhpackageName = "com.leesky.ezframework.decorate";
	// 默认模板包名
    public final static String packageName = "cn.cxyfyf.base";
    // 作者
    public final static String author = "fengyingfeng";
    // 自动去除表前缀，默认是false
    public final static boolean autoRemovePre = false;
    // 表前缀(暂无用)
    public final static String tablePrefix = "hc_";
    // 代码生成路径 （本地绝对路径）
    public final static String genPath = "D:\\software\\tem\\code";
    /**
     * 数据连接相关，需要手动设置
     */
    private static final String USERNAME = "root"; // mysql 链接用户名
    private static final String PASSWORD = "123456"; // mysql 链接用户名对应密码
    private static final String DRIVER_CLASSNAME = "com.mysql.cj.jdbc.Driver";
	// 这里将test替换为数据库名称
    private static final String URL = "jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8&characterEncoding=utf-8";
```

然后再回到cn.cxyfyf.base.GenTest，配置相关表名，即可运行main方法，控制台输出路径即可生成成功。



## 其他事项

想要更改模板配置可以进入resource目录下 两个目录分别是两套模板，可自行改造

