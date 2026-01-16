# JsonToBpmnDemo

## 项目简介

JsonToBpmnDemo 是一个 Java 项目，旨在将特定格式的 JSON 流程定义数据转换为 Flowable/Activiti 支持的 BPMN 2.0 XML 格式。该项目主要适配前端工作流设计器导出的 JSON 数据，通过解析、重组节点关系，最终生成标准的 BPMN 文件，方便直接部署到工作流引擎中运行。

## 功能特性

*   **JSON 转 BPMN**: 核心功能，支持将复杂的嵌套 JSON 流程结构转换为扁平化的 BPMN 模型。
*   **节点自动连接**: 内置 `NodeConnector` 工具，能够自动处理 JSON 中节点的父子关系（parentId/childId），包括复杂的网关分支逻辑。
*   **适配性强**: 专门针对主流的 Vue3 工作流设计器数据结构进行适配。

## 支持节点

目前项目已接入并支持以下核心节点转换：

*   **StartNode (开始节点)**: 流程的发起起点。
*   **ApprovalNode (审批节点)**: 支持用户审批配置。
*   **GatewayNode (网关节点)**: 支持互斥网关（ExclusiveGateway），处理条件分支。
*   **ConditionNode (条件节点)**: 配合网关使用，解析并生成条件表达式（如 `var:eq`, `var:gt` 等）。
*   **EndNode (结束节点)**: 流程的终点。

### 后续更新计划

我们将持续完善项目，陆续接入更多类型的节点，以支持更丰富的业务场景：

*   **CcNode (抄送节点)**: 计划中...
*   **NotifyNode (消息通知节点)**: 计划中...
*   **TimerNode (定时节点)**: 计划中...

## 致谢与参考

本项目在开发过程中参考并基于以下优秀的开源项目和资源进行了修改与优化，特此感谢：

1.  **原逻辑参考**: 本项目的部分核心逻辑参考自 [lowflow-design](https://github.com/tsai996/lowflow-design) (GitHub) / [lowflow-design](https://gitee.com/cai_xiao_feng/lowflow-design) (Gitee)。
    *   **作者**: 蔡晓峰 (tsai996)
    *   **相关贡献**: 特别感谢其关于节点的设计思路。

    本项目在此基础上对节点连接处理逻辑进行了深度优化和重构。

2.  **前端适配**: 本项目的数据结构适配了 **wflow-web-next** 设计器。
    *   **项目地址**: [wflow-web-next](https://gitee.com/willianfu/wflow-web-next)
    *   **作者**: willianfu

3.  **教程资源**: 关于详细的节点配置和实现原理，可以参考我的 CSDN 文章：
    *   [JSON 转 BPMN 实战：如何用 Flowable 把前端流程图转成 XML](https://blog.csdn.net/weixin_45076035/article/details/155607435?spm=1001.2014.3001.5502)

## 测试数据

您可以使用以下 JSON 数据进行测试。该数据描述了一个包含“发起人 -> 用户申请 -> 网关（大于1000/1000以内） -> 审批/结束”的简单请假流程。

```json
{
  "name": "请假流程",
  "process": [
    {
      "id": "node_root",
      "type": "Start",
      "name": "发起人",
      "parentId": null,
      "childId": null,
      "props": {}
    },
    {
      "id": "node_17628242120246176",
      "type": "Approval",
      "name": "用户申请",
      "parentId": null,
      "childId": null,
      "props": {
        "mode": "USER",
        "ruleType": "ROOT_SELF",
        "taskMode": {
          "type": "AND",
          "percentage": 100
        },
        "needSign": false,
        "assignUser": [],
        "rootSelect": {
          "multiple": false
        },
        "leader": {
          "level": 1,
          "emptySkip": false
        },
        "leaderTop": {
          "level": 0,
          "toEnd": false,
          "emptySkip": false
        },
        "assignDept": {
          "dept": [],
          "type": "LEADER"
        },
        "assignRole": [],
        "noUserHandler": {
          "type": "TO_NEXT",
          "assigned": []
        },
        "sameRoot": {
          "type": "TO_SELF",
          "assigned": []
        },
        "timeout": {
          "enable": false,
          "time": 1,
          "timeUnit": "M",
          "type": "TO_PASS"
        }
      }
    },
    {
      "id": "node_17685476393335780_fork",
      "type": "Gateway",
      "name": "网关节点",
      "parentId": null,
      "childId": null,
      "props": {
        "type": "Exclusive",
        "branch": [
          {
            "id": "node_17685476393333937",
            "type": "Exclusive",
            "name": "大于1000",
            "parentId": null,
            "childId": null,
            "props": {
              "logic": true,
              "groups": [
                {
                  "logic": true,
                  "conditions": [
                    {
                      "group": "FORM",
                      "type": "NumberInput",
                      "symbol": "NumberInput_hvccvk7m",
                      "name": [
                        "表单",
                        "金额"
                      ],
                      "valueType": null,
                      "compare": "GT",
                      "compareVal": [
                        "1000"
                      ]
                    }
                  ]
                }
              ]
            }
          },
          {
            "id": "node_17685476393339974",
            "type": "Exclusive",
            "name": "1000以内（默认）",
            "parentId": null,
            "childId": null,
            "props": {
              "logic": true,
              "groups": [
                {
                  "logic": true,
                  "conditions": []
                }
              ]
            }
          }
        ]
      },
      "branch": [
        [
          {
            "id": "node_17685476615368380",
            "type": "Approval",
            "name": "校长",
            "parentId": null,
            "childId": null,
            "props": {
              "mode": "USER",
              "ruleType": "LEADER",
              "taskMode": {
                "type": "AND",
                "percentage": 100
              },
              "needSign": false,
              "assignUser": [],
              "rootSelect": {
                "multiple": false
              },
              "leader": {
                "level": 2,
                "emptySkip": false
              },
              "leaderTop": {
                "level": 1,
                "toEnd": false,
                "emptySkip": false
              },
              "assignDept": {
                "dept": [],
                "type": "LEADER"
              },
              "assignRole": [],
              "noUserHandler": {
                "type": "TO_NEXT",
                "assigned": []
              },
              "sameRoot": {
                "type": "TO_SELF",
                "assigned": []
              },
              "timeout": {
                "enable": false,
                "time": 1,
                "timeUnit": "M",
                "type": "TO_PASS"
              }
            }
          }
        ],
        []
      ]
    }
  ],
  "remark": ""
}
```
