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

### 在非Spring环境下部署

代码演示:
```java
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.junit.jupiter.api.Test;

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
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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