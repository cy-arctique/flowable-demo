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

当启动一个流程实例后, 会在`act_ru_` 对应的表结构中操作, 运行实例涉及的表结构共10张:
+ `act_ru_deadletter_job` 正在运行的任务表
+ `act_ru_event_subscr` 运行时事件
+ `act_ru_execution` 运行时流程执行实例
+ `act_ru_history_job` 历史作业表
+ `act_ru_identitylink` 运行时用户关系信息
+ `act_ru_job` 运行时作业表
+ `act_ru_suspended_job` 暂停作业表
+ `act_ru_task` 运行时任务表
+ `act_ru_timer_job` 定时作业表
+ `act_ru_variable` 运行时变量表

启动一个流程实例的时候涉及到的表有:
+ `act_ru_execution` 运行时流程执行实例
+ `act_ru_identitylink` 运行时用户关系信息
+ `act_ru_task` 运行时任务表
+ `act_ru_variable` 运行时变量表

### 流程完毕

一个具体的流程审批完成后相关的数据如下:

首先, 以下四张表对应的数据都没有了, 也就是这个流程已经不是运行中的流程了

+ `act_ru_execution` 运行时流程执行实例
+ `act_ru_identifylink` 运行时用户关系信息
+ `act_ru_task` 运行时任务表
+ `act_ru_variable` 运行时变量表

然后在对应的历史表中可以看到相关的信息

+ `act_hi_actinst` 历史的流程实例
+ `act_hi_attachment` 历史的流程附件
+ `act_hi_comment` 历史的说明性信息
+ `act_hi_detail` 历史的流程运行中的细节信息
+ `act_hi_identifylink` 历史的流程运行过程中用户关系
+ `act_hi_procinst` 历史的流程实例
+ `act_hi_taskinst` 历史的任务实例
+ `act_hi_variable` 历史的流程运行中的变量信息

## 流程的挂起与激活 & 流程实例的挂起与激活

```java
/**
 * 流程的挂起与激活
 * 针对的是流程定义
 */
@Test
void suspendedActivity() {
    String processId = "first_flow:1:4";
    // 查看流程是否是挂起状态
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionId(processId)
            .singleResult();
    boolean suspended = processDefinition.isSuspended();
    System.out.println(suspended);
    // SUSPENSION_STATE_ 1激活 2挂起
    // 挂起之后的流程不能启动流程`startFlow()`
    if (suspended) {
        repositoryService.activateProcessDefinitionById(processId);
    } else {
        repositoryService.suspendProcessDefinitionById(processId);
    }
}

/**
 * 流程实例的挂起与激活
 */
@Test
void suspendedProcessInstance() {
    String processInstanceId = "";
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .singleResult();
    boolean suspended = processInstance.isSuspended();
    if (suspended) {
        runtimeService.activateProcessInstanceById(processInstanceId);
    } else {
        runtimeService.suspendProcessInstanceById(processInstanceId);
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

## 表结构

支持的数据库有:

| Activi数据库类型 | 示例JDBC URL                                                                                                                                                                                                          | 备注                           |
|-------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------|
| h2          | jdbc:h2:tcp://localhost/activiti                                                                                                                                                                                    | 默认配置的数据库                     |
| mysql       | jdbc:mysql://localhost:3306/activiti?autoReconnect=true                                                                                                                                                             | 已使用mysql-connect-java数据库驱动测试 |
| oracle      | jdbc:oracle:thin:@localhost:1521/activiti                                                                                                                                                                           |                              |
| postgres    | jdbc:postgresql://localhost:5432/activiti                                                                                                                                                                           |                              |
| db2         | jdbc:db2://localhost:5000/activiti                                                                                                                                                                                  |                              |
| mssql       | jdbc:sqlserver://localhost:1433;databaseName=activiti(jdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver)<br/> OR jdbc:jtds:sqlserver://localhost:1433/activiti(jdbc.driver=net.sourceforge.jtds.jdbc.Driver) |                              |

工作流程的相关操作都是操作存储在对应的表结构中, 在Flowable中的表结构在初始化的时候会创建五类表结构, 具体如下:

+ `ACT_RE` RE表示repository. 这个前缀的表包含了流程定义和流程静态资源(图片规则等)
+ `ACT_RU` RU表示runtime. 这些是运行时的表, 包含流程实例, 任务, 变量, 异步任务等运行中的数据. Flowable只在流程实例执行过程中保存这些数据, 在流程结束时就会删除这些记录. 这样运行时表可以一直很小速度很快
+ `ACT_HI` HI表示history. 这些表包含历史数据, 比如历史流程实例, 变量, 任务等
+ `ACT_GE` GE表示general. 通用数据, 用于不同场景下
+ `ACT_ID` ID表示identify(组织机构). 这些表包含表示的信息, 如用户, 用户组等等

### 具体表结构含义

| 表分类    | 表名                    | 解释                         |
|--------|-----------------------|----------------------------|
| 一般数据   |                       |                            |
|        | `ACT_GE_BYTEARRAY`    | 通用的流程定义和流程资源               |
|        | `ACT_GE_PROPERTY`     | 系统相关属性                     |
| 流程历史记录 |                       |                            |
|        | `ACT_HI_ACTINST`      | 历史的流程实例                    |
|        | `ACT_HI_ATTACHMENT`   | 历史流程附件                     |
|        | `ACT_HI_COMMENT`      | 历史的说明性信息                   |
|        | `ACT_HI_DETAIL`       | 历史的流程运行中的细节信息              |
|        | `ACT_HI_IDENTIFYLINK` | 历史的流程运行过程中用户关系             |
|        | `ACT_HI_PROCINST`     | 历史的流程实例                    |
|        | `ACT_HI_TASKINST`     | 历史的任务实例                    |
|        | `ACT_HI_VARINST`      | 历史的流程运行中的变量信息              |
| 流程定义表  |                       |                            |
|        | `ACT_RE_DEPLOYMENT`   | 部署单元信息                     |
|        | `ACT_RE_MODEL`        | 模型信息                       |
|        | `ACT_RE_PROCDEF`      | 已部署的流程定义                   |
| 运行实例表  |                       |                            |
|        | `ACT_RU_EVENT_SUBSCR` | 运行时事件                      |
|        | `ACT_RU_EXECUTION`    | 运行时流程执行实例                  |
|        | `ACT_RU_IDENTIFYLINK` | 运行时用户关系信息, 存储任务节点与参与者的相关信息 |
|        | `ACT_RU_JOB`          | 运行时作业                      |
|        | `ACT_RU_TASK`         | 运行时任务                      |
|        | `ACT_RU_VARIABLE`     | 运行时变量                      |
| 用户用户组表 |                       |                            |
|        | `ACT_ID_BYTEARRAY`    | 二进制数据                      |
|        | `ACT_ID_GROUP`        | 用户组信息                      |
|        | `ACT_ID_INFO`         | 用户信息详情                     |
|        | `ACT_ID_MEMBERSHIP`   | 人与组关系表                     |
|        | `ACT_ID_PRIV`         | 权限表                        |
|        | `ACT_ID_PRIV_MAPPING` | 用户或组权限关系表                  |
|        | `ACT_ID_PROPERTY`     | 属性表                        |
|        | `ACT_ID_TOKEN`        | 记录用户的token信息               |
|        | `ACT_ID_USER`         | 用户表                        |

### 部署流程

部署资源表: `act_ge_bytearray`

```sql
CREATE TABLE `act_ge_bytearray` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int DEFAULT NULL COMMENT '版本号',
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
  `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '部署id',
  `BYTES_` longblob COMMENT '字节(二进制数据)',
  `GENERATED_` tinyint DEFAULT NULL COMMENT '是否系统生成(0用户上传 1系统自动生成; 比如系统会自动根据xml生成png)',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`),
  CONSTRAINT `ACT_FK_BYTEARR_DEPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
```

部署表: `act_re_deployment`

```sql
CREATE TABLE `act_re_deployment` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '分类',
  `KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '' COMMENT '租户id',
  `DEPLOY_TIME_` timestamp(3) NULL DEFAULT NULL COMMENT '部署时间',
  `DERIVED_FROM_` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '来源于',
  `DERIVED_FROM_ROOT_` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '来源于',
  `PARENT_DEPLOYMENT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ENGINE_VERSION_` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '流程引擎版本',
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
```

流程表: `act_re_procdef`

```sql
CREATE TABLE `act_re_procdef` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int DEFAULT NULL COMMENT '版本',
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '分类(流程定义的namespace就是类别)',
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '名称',
  `KEY_` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '标识',
  `VERSION_` int NOT NULL COMMENT '版本',
  `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '部署id',
  `RESOURCE_NAME_` varchar(4000) COLLATE utf8_bin DEFAULT NULL COMMENT '资源名称(流程bpmn文件名称)',
  `DGRM_RESOURCE_NAME_` varchar(4000) COLLATE utf8_bin DEFAULT NULL COMMENT '图片资源名称',
  `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL COMMENT '描述',
  `HAS_START_FORM_KEY_` tinyint DEFAULT NULL COMMENT '拥有开始表单表示(start节点是否存在formkey 0否 1是)',
  `HAS_GRAPHICAL_NOTATION_` tinyint DEFAULT NULL COMMENT '拥有图形信息',
  `SUSPENSION_STATE_` int DEFAULT NULL COMMENT '挂起状态(暂停转态 1激活 2暂停)',
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '' COMMENT '租户id',
  `ENGINE_VERSION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DERIVED_FROM_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DERIVED_FROM_ROOT_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DERIVED_VERSION_` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID_`),
  UNIQUE KEY `ACT_UNIQ_PROCDEF` (`KEY_`,`VERSION_`,`DERIVED_VERSION_`,`TENANT_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;
```

注意:

业务流程定义数据表. 此表和`act_re_deployment` 是多对一的关系, 即一个部署的bar包里可能包含多个流程定义文件, 每个
流程定义文件都会有一条记录在`act_re_procdef` 表内, 每个流程定义的数据, 都会对于`act_ge_bytearray` 表内的一个资源文件和PNG
图片文件. `act_ge_bytearray`, `act_re_deployment`, `act_re_procdef`这三个表的关联关系为`act_re_deployment` 表的主键, 
其他两个表的关联字段对应是`DEPLOYMENT_ID_`