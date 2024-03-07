

#### 表结构

##### ACT_RE_MODEL

> 流程模型表，存储流程模型相关信息

| 字段                          | 名称         | 备注 |
| ----------------------------- | ------------ | ---- |
| ID_                           | 主键         |      |
| REV_                          | 版本号       |      |
| NAME_                         | 名称         |      |
| KEY_                          | 标识         |      |
| CATEGORY_                     | 分类         |      |
| CREATE_TIME_                  | 创建时间     |      |
| LAST_UPDATE_TIME_             | 最后更新时间 |      |
| VERSION_                      | 版本         |      |
| META_INFO_                    | 元数据       |      |
| DEPLOYMENT_ID_                | 部署ID       |      |
| EDITOR_SOURCE_VALUE_ID_       | 二进制文件ID |      |
| EDITOR_SOURCE_EXTRA_VALUE_ID_ | 二进制文件ID |      |
| TENANT_ID_                    | 租户ID       |      |

##### ACT_RE_DEPLOYMENT

> 部署表，部署流程定义时需要被持久化保存下来的信息

| 字段                  | 名称         | 备注 |
| --------------------- | ------------ | ---- |
| ID_                   | 主键         |      |
| NAME_                 | 名称         |      |
| CATEGORY_             | 分类         |      |
| KEY_                  |              |      |
| TENANT_ID_            | 租户ID       |      |
| DEPLOY_TIME_          | 部署时间     |      |
| DERIVED_FROM_         | 来源于       |      |
| DERIVED_FROM_ROOT_    | 来源于       |      |
| PARENT_DEPLOYMENT_ID_ |              |      |
| ENGINE_VERSION_       | 流程引擎版本 |      |

##### ACT_RE_PROCDEF

> 流程定义表

| 字段                    | 名称             | 备注                                  |
| ----------------------- | ---------------- | ------------------------------------- |
| ID_                     | 主键             |                                       |
| REV_                    | 版本号           |                                       |
| CATEGORY_               | 分类             | 流程定义XML文件的targetNamespaces属性 |
| NAME_                   | 名称             |                                       |
| KEY_                    | 标识             |                                       |
| VERSION_                | 版本             |                                       |
| DEPLOYMENT_ID_          | 部署ID           |                                       |
| RESOURCE_NAME_          | 资源名称         | 流程bpmn文件名称                      |
| DGRM_RESOURCE_NAME_     | 图片资源名称     |                                       |
| DESCRIPTION_            | 描述             |                                       |
| HAS_START_FORM_KEY_     | 拥有开始表单标识 | start节点是否存在formKey 0否1是       |
| HAS_GRAPHICAL_NOTATION_ | 拥有图形信息     |                                       |
| SUSPENSION_STATE_       | 挂起状态         | 暂停状态1激活2暂停                    |
| TENANT_ID_              | 租户ID           |                                       |
| ENGINE_VERSION_         |                  |                                       |
| DERIVED_FROM_           |                  |                                       |
| DERIVED_FROM_ROOT_      |                  |                                       |
| DERIVED_VERSION_        |                  |                                       |

##### ACT_GE_BYTEARRAY

> 资源表

| 字段           | 名称               | 备注                                                         |
| -------------- | ------------------ | ------------------------------------------------------------ |
| ID_            | 主键               |                                                              |
| REV_           | 版本号             |                                                              |
| NAME_          | 名称               | 部署的文件名称，<br/>如：holiday-request-new.bpmn20.xml、 <br/>holiday-request-new.bpmn20.png |
| DEPLOYMENT_ID_ | 部署ID             | ACT_RE_DEPLOYMENT表ID                                        |
| BYTES_         | 字节（二进制数据） |                                                              |
| GENERATED_     | 是否系统生成       | 0为用户上传<br/>1为系统自动生成，比如系统会自动根据xml生成png |

##### ACT_RU_EXECUTION

> 运行时流程执行实例

| 字段                       | 名称                 | 备注 |
| -------------------------- | -------------------- | ---- |
| ID_                        | 主键                 |      |
| REV_                       | 版本号               |      |
| PROC_INST_ID_              | 流程实例ID           |      |
| BUSINESS_KEY_              | 业务主键ID           |      |
| PARENT_ID_                 | 父执行流ID           |      |
| PROC_DEF_ID_               | 流程定义的数据ID     |      |
| SUPER_EXEC_                |                      |      |
| ROOT_PROC_INST_ID_         | 流程实例的root流程ID |      |
| ACT_ID_                    | 节点实例ID           |      |
| IS_ACTIVE_                 | 是否存活             |      |
| IS_CONCURRENT_             | 执行流是否正在并行   |      |
| IS_SCOPE_                  |                      |      |
| IS_EVENT_SCOPE_            |                      |      |
| IS_MI_ROOT_                |                      |      |
| SUSPENSION_STATE_          | 流程终端状态         |      |
| CACHED_ENT_STATE_          |                      |      |
| TENANT_ID_                 | 租户ID               |      |
| NAME_                      |                      |      |
| START_ACT_ID_              |                      |      |
| START_TIME_                | 开始时间             |      |
| START_USER_ID_             | 开始用户 ID          |      |
| LOCK_TIME_                 | 锁定时间             |      |
| LOCK_OWNER_                |                      |      |
| IS_COUNT_ENABLED_          |                      |      |
| EVT_SUBSCR_COUNT_          |                      |      |
| TASK_COUNT_                |                      |      |
| JOB_COUNT_                 |                      |      |
| TIMER_JOB_COUNT_           |                      |      |
| SUSP_JOB_COUNT_            |                      |      |
| DEADLETTER_JOB_COUNT_      |                      |      |
| EXTERNAL_WORKER_JOB_COUNT_ |                      |      |
| VAR_COUNT_                 |                      |      |
| ID_LINK_COUNT_             |                      |      |
| CALLBACK_ID_               |                      |      |
| CALLBACK_TYPE_             |                      |      |
| REFERENCE_ID_              |                      |      |
| REFERENCE_TYPE_            |                      |      |
| PROPAGATED_STAGE_INST_ID_  |                      |      |
| BUSINESS_STATUS_           |                      |      |

##### ACT_RU_ACTINST

>活动实例表

| 字段               | 名称         | 备注                 |
| ------------------ | ------------ | -------------------- |
| ID_                | 主键         |                      |
| REV_               | 版本号       |                      |
| PROC_DEF_ID_       | 流程定义ID   |                      |
| PROC_INST_ID_      | 流程实例ID   |                      |
| EXECUTION_ID_      | 执行实例ID   |                      |
| ACT_ID_            | 活动节点ID   |                      |
| TASK_ID_           |              |                      |
| CALL_PROC_INST_ID_ |              |                      |
| ACT_NAME_          | 活动节点名称 |                      |
| ACT_TYPE_          | 活动节点类型 | startEvent、userTask |
| ASSIGNEE_          |              |                      |
| START_TIME_        |              |                      |
| END_TIME_          |              |                      |
| DURATION_          |              |                      |
| TRANSACTION_ORDER_ |              |                      |
| DELETE_REASON_     |              |                      |
| TENANT_ID_         | 租户ID       |                      |

##### ACT_RU_TASK

> 运行时任务表

| 字段                      | 名称               | 备注                          |
| ------------------------- | ------------------ | ----------------------------- |
| ID_                       | 主键               |                               |
| REV_                      | 版本号             |                               |
| EXECUTION_ID_             | 任务所在的执行流ID |                               |
| PROC_INST_ID_             | 流程实例ID         |                               |
| PROC_DEF_ID_              | 流程定义数据ID     |                               |
| TASK_DEF_ID_              |                    |                               |
| SCOPE_ID_                 |                    |                               |
| SUB_SCOPE_ID_             |                    |                               |
| SCOPE_TYPE_               |                    |                               |
| SCOPE_DEFINITION_ID_      |                    |                               |
| PROPAGATED_STAGE_INST_ID_ |                    |                               |
| NAME_                     | 任务名称           |                               |
| PARENT_TASK_ID_           | 父任务ID           |                               |
| DESCRIPTION_              | 说明               |                               |
| TASK_DEF_KEY_             | 任务定义ID         |                               |
| OWNER_                    | 任务拥有人         | 一般为空，只有在委托时才有值  |
| ASSIGNEE_                 | 被指派执行任务的人 | 签收人或委托人                |
| DELEGATION_               | 委托状态           | 委托中PENDING，已处理RESOLVED |
| PRIORITY_                 | 优先级             |                               |
| CREATE_TIME_              | 创建时间           |                               |
| DUE_DATE_                 | 耗时               |                               |
| CATEGORY_                 | 类别               |                               |
| SUSPENSION_STATE_         | 是否挂起           | 1激活2挂起                    |
| TENANT_ID_                | 租户ID             |                               |
| FORM_KEY_                 | 表单标识           |                               |
| CLAIM_TIME_               | 拾取时间           |                               |
| IS_COUNT_ENABLED_         |                    |                               |
| VAR_COUNT_                |                    |                               |
| ID_LINK_COUNT_            |                    |                               |
| SUB_TASK_COUNT_           |                    |                               |

##### ACT_RU_VARIABLE

> 运行时变量表

| 字段          | 名称                           | 备注                                 |
| ------------- | ------------------------------ | ------------------------------------ |
| ID_           | 主键                           |                                      |
| REV_          | 版本号                         |                                      |
| TYPE_         | 参数类型                       | 可以是基本的类型，也可以用户自行扩展 |
| NAME_         | 参数名称                       |                                      |
| EXECUTION_ID_ | 参数执行ID                     |                                      |
| PROC_INST_ID_ | 流程实例ID                     |                                      |
| TASK_ID_      | 任务ID                         |                                      |
| SCOPE_ID_     |                                |                                      |
| SUB_SCOPE_ID_ |                                |                                      |
| SCOPE_TYPE_   |                                |                                      |
| BYTEARRAY_ID_ | 资源ID                         |                                      |
| DOUBLE_       | 参数为double，则保存在该字段中 |                                      |
| LONG_         | 参数为long，则保存在该字段中   |                                      |
| TEXT_         | 用户保存文本类型的参数值       |                                      |
| TEXT2_        | 用户保存文本类型的参数值       |                                      |

##### ACT_RU_IDENTITYLINK

> 运行时流程人员表，主要存储当前节点参与者的信息

| 字段                 | 名称         | 备注                                                         |
| -------------------- | ------------ | ------------------------------------------------------------ |
| ID_                  | 主键         |                                                              |
| REV_                 | 版本号       |                                                              |
| GROUP_ID_            | 用户组ID     |                                                              |
| TYPE_                | 关系数据类型 | assignee支配人(组)<br/>candidate候选人(组)<br/>ower拥有人<br/>participant参与者<br/> |
| USER_ID_             | 用户ID       |                                                              |
| TASK_ID_             | 任务ID       |                                                              |
| PROC_INST_ID_        | 流程实例ID   |                                                              |
| PROC_DEF_ID_         | 流程定义ID   |                                                              |
| SCOPE_ID_            |              |                                                              |
| SUB_SCOPE_ID_        |              |                                                              |
| SCOPE_TYPE_          |              |                                                              |
| SCOPE_DEFINITION_ID_ |              |                                                              |

##### ACT_HI_VARINST

> 历史变量表

| 字段               | 名称                           | 备注 |
| ------------------ | ------------------------------ | ---- |
| ID_                | 主键                           |      |
| REV_               | 版本号                         |      |
| PROC_INST_ID_      | 流程实例ID                     |      |
| EXECUTION_ID_      | 参数执行ID                     |      |
| TASK_ID_           | 任务ID                         |      |
| NAME_              | 参数名                         |      |
| VAR_TYPE_          | 参数类型                       |      |
| SCOPE_ID_          |                                |      |
| SUB_SCOPE_ID_      |                                |      |
| SCOPE_TYPE_        |                                |      |
| BYTEARRAY_ID_      | 资源ID                         |      |
| DOUBLE_            | 参数为double，则保存在该字段中 |      |
| LONG_              | 参数为long，则保存在该字段中   |      |
| TEXT_              | 用户保存文本类型的参数值       |      |
| TEXT2_             | 用户保存文本类型的参数值       |      |
| CREATE_TIME_       | 创建时间                       |      |
| LAST_UPDATED_TIME_ | 最后更新时间                   |      |

##### ACT_HI_IDENTITYLINK

> 历史流程人员表，主要存储当前节点参与者的信息

| 字段                 | 名称       | 备注                                            |
| -------------------- | ---------- | ----------------------------------------------- |
| ID_                  | 主键       |                                                 |
| GROUP_ID_            | 组ID       |                                                 |
| TYPE_                | 类型       | assignee、candidate、ower、starter、participant |
| USER_ID_             | 用户ID     |                                                 |
| TASK_ID_             | 任务ID     |                                                 |
| CREATE_TIME_         | 创建时间   |                                                 |
| PROC_INST_ID_        | 流程实例ID |                                                 |
| SCOPE_ID_            |            |                                                 |
| SUB_SCOPE_ID_        |            |                                                 |
| SCOPE_TYPE_          |            |                                                 |
| SCOPE_DEFINITION_ID_ |            |                                                 |

##### ACT_HI_TASKINST

> 历史任务表，存放已经办理的任务，注意：只记录userTask内容

| 字段                      | 名称                     | 备注                                   |
| ------------------------- | ------------------------ | -------------------------------------- |
| ID_                       | 主键                     |                                        |
| REV_                      | 版本号                   |                                        |
| PROC_DEF_ID_              | 流程定义ID               | ACT_RE_PRODEF表ID                      |
| TASK_DEF_ID_              | 任务定义ID               |                                        |
| TASK_DEF_KEY_             | 任务节点定义ID           | xml任务节点的ID                        |
| PROC_INST_ID_             | 流程实例ID               |                                        |
| EXECUTION_ID_             | 执行ID                   |                                        |
| SCOPE_ID_                 |                          |                                        |
| SUB_SCOPE_ID_             |                          |                                        |
| SCOPE_TYPE_               |                          |                                        |
| SCOPE_DEFINITION_ID_      |                          |                                        |
| PROPAGATED_STAGE_INST_ID_ |                          |                                        |
| NAME_                     | 名称                     |                                        |
| PARENT_TASK_ID_           | 父任务ID                 |                                        |
| DESCRIPTION_              | 说明                     |                                        |
| OWNER_                    | 实际签收人，任务的拥有者 | 签收人（默认为空，只有在委托时才有值） |
| ASSIGNEE_                 | 被指派执行该任务的人     |                                        |
| START_TIME_               | 开始时间                 |                                        |
| CLAIM_TIME_               | 提醒时间                 |                                        |
| END_TIME_                 | 结束时间                 |                                        |
| DURATION_                 | 耗时                     |                                        |
| DELETE_REASON_            | 删除原因                 |                                        |
| PRIORITY_                 | 优先级别                 |                                        |
| DUE_DATE_                 | 过期时间                 |                                        |
| FORM_KEY_                 | 节点定义的formkey        |                                        |
| CATEGORY_                 | 类别                     |                                        |
| TENANT_ID_                | 租户ID                   |                                        |
| LAST_UPDATED_TIME_        | 最后更新时间             |                                        |

##### ACT_HI_PROCINST

> 历史流程实例表

| 字段                       | 名称         | 备注 |
| -------------------------- | ------------ | ---- |
| ID_                        | 主键         |      |
| REV_                       | 版本号       |      |
| PROC_INST_ID_              | 流程实例ID   |      |
| BUSINESS_KEY_              | 业务主键     |      |
| PROC_DEF_ID_               | 属性ID       |      |
| START_TIME_                | 开始时间     |      |
| END_TIME_                  | 结束时间     |      |
| DURATION_                  | 耗时         |      |
| START_USER_ID_             | 开始人       |      |
| START_ACT_ID_              | 起始节点     |      |
| END_ACT_ID_                | 结束节点     |      |
| SUPER_PROCESS_INSTANCE_ID_ | 父流程实例ID |      |
| DELETE_REASON_             | 删除原因     |      |
| TENANT_ID_                 | 租户ID       |      |
| NAME_                      | 名称         |      |
| CALLBACK_ID_               |              |      |
| CALLBACK_TYPE_             |              |      |
| REFERENCE_ID_              |              |      |
| REFERENCE_TYPE_            |              |      |
| PROPAGATED_STAGE_INST_ID_  |              |      |
| BUSINESS_STATUS_           |              |      |

##### ACT_HI_ACTINST

> 历史节点表，记录流程流转过的所有节点，与HI_TASKINST不同的是，TASKINT只记录usertask内容

| 字段               | 名称                 | 备注                   |
| ------------------ | -------------------- | ---------------------- |
| ID_                | 主键                 |                        |
| REV_               | 版本号               |                        |
| PROC_DEF_ID_       | 流程定义ID           | ACT_RE_DEPLOYMENT表ID  |
| PROC_INST_ID_      | 流程实例ID           |                        |
| EXECUTION_ID_      | 执行实例ID           |                        |
| ACT_ID_            | 节点ID               | xml文件中活动的id      |
| TASK_ID_           | 任务ID               |                        |
| CALL_PROC_INST_ID_ | 调用外部的流程实例ID |                        |
| ACT_NAME_          | 节点名称             |                        |
| ACT_TYPE_          | 节点类型             | 如startEvent、userTask |
| ASSIGNEE_          | 签收人               |                        |
| START_TIME_        | 开始时间             |                        |
| END_TIME_          | 结束时间             |                        |
| TRANSACTION_ORDER_ |                      |                        |
| DURATION_          | 耗时                 |                        |
| DELETE_REASON_     | 删除原因             |                        |
| TENANT_ID_         | 租户ID               |                        |





#### 用户分配

人工任务可分配给三种人

- assignee：办理人、受让人，任务的是实际办理人，任务只能同时有一个办理人
- candidate：候选人，任务可以有多个候选人，每个候选人都能看到该任务，候选人需要claim(拾取)任务成为assignee后，才能进行任务的办理，任务被拾取后其他候选人就看不到该任务了。候选人在拾取任务后可以unclaim，将任务归还，此时其他候选人可以看到并claim任务
- candidateGroup：候选人组，不想单独指定多个候选人，可以指定一个候选人组，一般为角色ID