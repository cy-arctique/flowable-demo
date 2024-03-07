package io.github.arctique.flowabledemo.flowable.not_spring;

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
                .addClasspathResource("processes/base/first_flow.bpmn20.xml")
                .name("第一个流程图")
                .deploy();
        System.out.println("deploy.getId(): " + deploy.getId());
    }
}
