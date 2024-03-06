package io.github.arctique.flowabledemo;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FlowableDemoApplicationTests {

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

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
