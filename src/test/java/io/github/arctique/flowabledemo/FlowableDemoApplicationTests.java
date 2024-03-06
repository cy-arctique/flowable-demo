package io.github.arctique.flowabledemo;

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
