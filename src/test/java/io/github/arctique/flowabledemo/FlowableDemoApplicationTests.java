package io.github.arctique.flowabledemo;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class FlowableDemoApplicationTests {

    @Autowired
    ProcessEngine processEngine;
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;

    /**
     * Spring环境下的应用, 部署流程到数据库
     */
    @Test
    void deployFlow() {
        // 可以使用容器注入
        // RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment()
                // 可以同时部署多个流程
                .addClasspathResource("process/base/leave_process.bpmn20.xml")
                .name("请假流程")
                .deploy();
        System.out.println("id: " + deploy.getId());
    }

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
        String processId = "leave_process:1:8156d66a-dbd6-11ee-8606-c8e265d125f9";
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processId);
    }

    /**
     * 根据用户查看代办信息
     */
    @Test
    void findFlow() {
        // 任务实例操作是通过TaskService来实现的
        // TaskService taskService = processEngine.getTaskService();

        // 获取到act_ru_task中assignee字段是zhangsan的记录
        List<Task> list = taskService.createTaskQuery().taskAssignee("zhangsan").list();
        for (Task task : list) {
            System.out.println(task.getId());
        }
    }

    /**
     * 任务审批
     */
    @Test
    void completeTask() {
        taskService.complete("9d5e6f24-dbd6-11ee-bc78-c8e265d125f9");
    }
}
