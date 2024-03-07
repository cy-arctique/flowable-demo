# Flowable

工作流程demo

## 项目配置

`SpringBoot版本: 2.6.13`, `JDK: 1.8.0_201`

### 项目依赖项

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- https://mvnrepository.com/artifact/org.flowable/flowable-spring-boot-starter -->
<dependency>
    <groupId>org.flowable</groupId>
    <artifactId>flowable-spring-boot-starter</artifactId>
    <version>6.7.2</version>
</dependency>

<!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.28</version>
</dependency>
```

## 部署流程

在成功部署流程后, 会在如下三个表中记录部署信息:
+ `act_ge_bytearray` 记录流程定义的资源信息. xml和流程图的图片信息
+ `act_re_deployment` 流程部署表, 记录这次部署的行为
+ `act_re_procdef` 流程定义表, 记录这次部署动作对应的流程定义信息

```sql
SELECT * FROM act_ge_bytearray;
SELECT * FROM act_re_deployment;
SELECT * FROM act_re_procdef;
```

### 在非Spring环境下部署

代码演示:
```java
/**
 * @author CY
 * @date 24/3/6 16:12
 */
public class FlowableTest {

    // &nullCatalogMeansCurrent=true
    private static final String URL = "jdbc:mysql://localhost:3306/flowable-learn?serverTimezone=UTC&nullCatalogMeansCurrent=true";

    /**
     * 在非Spring环境下的应用, 部署流程到数据库
     */
    @Test
    public void deployFlow() {
        // 流程引擎的配置对象, 关联相关的数据源
        StandaloneProcessEngineConfiguration cfg = (StandaloneProcessEngineConfiguration) new StandaloneProcessEngineConfiguration()
                .setJdbcUrl(URL)
                .setJdbcDriver("com.mysql.cj.jdbc.Driver")
                .setJdbcUsername("root").setJdbcPassword("123456")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        // 获取流程引擎对象
        ProcessEngine processEngine = cfg.buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                // 可以同时部署多个流程
                .addClasspathResource("process/base/first_flow.bpmn20.xml")
                .name("第一个流程图")
                .deploy();
        System.out.println("deploy.getId(): " + deploy.getId());
    }
}
```

### 在Spring环境下部署

配置:
```yaml
# application.yaml
spring:
  profiles:
    active: dev
```

```yaml
# application-dev.yaml
server:
  port: 8855

spring:
  datasource:
    driverClassName: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/flowable-learn?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false&allowMultiQueries=true&nullCatalogMeansCurrent=true
    username: root
    password: 123456

flowable:
  asyncExecutorActivate: true # 关闭定时任务
  databaseSchemaUpdate: true # 当Flowable发现库与数据库表结构不一致时, 会自动将数据库表结构升级至版本
logging:
  level:
    org:
      flowable: debug
```

代码演示:
```java
@SpringBootTest
class FlowableDemoApplicationTests {

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    RepositoryService repositoryService;

    @Test
    void deployFlow() {
        // 可以使用容器注入
        // RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                // 可以同时部署多个流程
                .addClasspathResource("process/base/first_flow.bpmn20.xml")
                .name("Spring的第一个流程图")
                .deploy();
        System.out.println("id: " + deploy.getId());
    }
}
```

## 启动流程

流程启动后:
+ `act_hi_procinst` 每启动一个流程实例, 就会在这个表中维护一条记录
+ `act_ru_execution` 记录流程的分支
+ `act_ru_task` 表记录的是当前待办的记录信息

```sql
SELECT * FROM act_hi_procinst;
SELECT * FROM act_ru_execution;
SELECT * FROM act_ru_task;
```

```java
@SpringBootTest
class FlowableDemoApplicationTests {

    @Autowired
    RuntimeService runtimeService;

    /**
     * 启动流程
     * 两种方式启动一个流程:
     * 1.根据流程定义id启动流程实例
     * 2.根据流程定义key启动流程实例
     *
     * 需要注意的是:
     * id是在流程定义表中动态维护的, key是创建流程图的时候自定义的. key需要保证唯一
     * 推荐使用id启动流程
     */
    @Test
    void startFlow() {
        // String processKey = "first_flow";
        // runtimeService.startProcessInstanceByKey(processKey);
        String processId = "first_flow:1:f9780755-dbc4-11ee-9811-c8e265d125f9";
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processId);
    }
}
```


## 关于Service

### RepositoryService

是active的资源管理类, 提供了管理和控制流程发布包和流程定义的操作. 使用工作流建模工具设计的业务流程图需要使用此service
将流程定义文件的内容部署到计算机

除了部署流程定义以外还可以:
+ 查询引擎中的发布包和流程定义 
+ 暂停或激活发布包, 对应全部和特定流程定义. 暂停意味着它们不能再执行任何操作了, 激活是对应的反向操作. 获得多种资源, 像是包含在发布包里的文件, 或引擎自动生成的流程图
+ 获得流程定义的pojo版本, 可以用来通过java解析流程, 而不必通过xml

### RuntimeService

Activiti的流程管理类. 可以从这个服务类中获取很多关于流程执行的信息

### TaskService

Activiti的任务管理类. 可以在这个服务类中获取很多关于流程执行相关的信息

### HistoryService

Flowable的历史管理类, 可以查询历史信息, 执行流程时, 引擎会保存很多数据(根据配置), 比如流程实例启动时间, 任务参与者, 完成任务的时间, 每个流程实例的执行路径,
等等, 这个服务主要是通过查询来获得这些数据

### ManagementService

Activiti的引擎管理类, 提供了对Flowable流程引擎的管理和维护功能, 这些功能不在工作流驱动的应用程序中使用, 主要用于Flowable系统的日常维护
