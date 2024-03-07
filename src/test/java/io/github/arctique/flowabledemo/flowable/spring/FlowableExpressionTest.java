package io.github.arctique.flowabledemo.flowable.spring;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arctique
 * @date 2024/3/7 23:21
 */
@SpringBootTest
public class FlowableExpressionTest {

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
                .addClasspathResource("processes/expression/expression00.bpmn20.xml")
                .name("表达式任务分配案例")
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
         String processKey = "expression00";
         runtimeService.startProcessInstanceByKey(processKey);
//        String processId = "leave_process:1:8156d66a-dbd6-11ee-8606-c8e265d125f9";
//        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processId);
    }

    /**
     * 根据用户查看代办信息
     */
    @Test
    void findFlow() {
        // 任务实例操作是通过TaskService来实现的
        // TaskService taskService = processEngine.getTaskService();

        // 获取到act_ru_task中assignee字段是zhangsan的记录
        List<Task> list = taskService.createTaskQuery().taskAssignee("lisi").list();
        for (Task task : list) {
            System.out.println(task.getId());
        }
    }

    /**
     * 任务审批
     */
    @Test
    void completeTask() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("employeeName", "lisi");
        // lisi审核
//        taskService.complete("716a3725-dc98-11ee-8fc3-c8e265d125f9", variables);
        // 最后方法表达式
        taskService.complete("62549a1e-dc9a-11ee-b5c4-c8e265d125f9");
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
}
