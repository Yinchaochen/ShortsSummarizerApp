# Playlist Gap Analysis

User playlist root: https://www.bilibili.com/video/BV1FwXrBmEp4
Official course source: https://github.com/wangshusen/RecommenderSystem

- Official topic count: `45`
- User playlist topic count: `42`
- Missing official topics from the user's playlist: `3`

## Missing Topics

- Official #17: `Multi-gate Mixture-of-Experts (MMoE)` (section `排序`)
- Official #29: `多样性的度量` (section `多样性`)
- Official #36: `聚类召回` (section `物品冷启动`)

## Official To Playlist Mapping

| Official # | Section | Topic | In 42-part Playlist | Playlist Part |
|---|---|---|---|---|
| 1 | 概要 | 推荐系统的基本概念 | yes | 1 |
| 2 | 概要 | 推荐系统的链路 | yes | 2 |
| 3 | 概要 | AB测试 | yes | 3 |
| 4 | 召回 | 基于物品的协同过滤（ItemCF） | yes | 4 |
| 5 | 召回 | Swing模型 | yes | 5 |
| 6 | 召回 | 基于用户的协同过滤（UserCF） | yes | 6 |
| 7 | 召回 | 离散特征处理 | yes | 7 |
| 8 | 召回 | 矩阵补充 | yes | 8 |
| 9 | 召回 | 双塔模型：模型和训练 | yes | 9 |
| 10 | 召回 | 双塔模型：正负样本 | yes | 10 |
| 11 | 召回 | 双塔模型：线上服务 | yes | 11 |
| 12 | 召回 | 双塔模型+自监督学习 | yes | 12 |
| 13 | 召回 | Deep Retrieval 召回 | yes | 13 |
| 14 | 召回 | 其它召回通道 | yes | 14 |
| 15 | 召回 | 曝光过滤 | yes | 15 |
| 16 | 排序 | 多目标排序模型 | yes | 16 |
| 17 | 排序 | Multi-gate Mixture-of-Experts (MMoE) | no | - |
| 18 | 排序 | 预估分数融合 | yes | 17 |
| 19 | 排序 | 播放时长建模 | yes | 18 |
| 20 | 排序 | 推荐系统的特征 | yes | 19 |
| 21 | 排序 | 粗排三塔模型 | yes | 20 |
| 22 | 交叉结构 | Factorized Machine (FM) | yes | 21 |
| 23 | 交叉结构 | Deep & Cross Network (深度交叉网络) | yes | 22 |
| 24 | 交叉结构 | LHUC | yes | 23 |
| 25 | 交叉结构 | SENet & FiBiNET | yes | 24 |
| 26 | 用户行为序列建模 | 用户行为序列特征 | yes | 25 |
| 27 | 用户行为序列建模 | DIN 模型 | yes | 26 |
| 28 | 用户行为序列建模 | SIM 模型 | yes | 27 |
| 29 | 多样性 | 多样性的度量 | no | - |
| 30 | 多样性 | MMR 算法 | yes | 28 |
| 31 | 多样性 | 规则约束 | yes | 29 |
| 32 | 多样性 | DPP：数学基础 | yes | 30 |
| 33 | 多样性 | DPP：多样性算法 | yes | 31 |
| 34 | 物品冷启动 | 评价指标 | yes | 32 |
| 35 | 物品冷启动 | 简单的召回通道 | yes | 33 |
| 36 | 物品冷启动 | 聚类召回 | no | - |
| 37 | 物品冷启动 | Look-Alike人群扩散 | yes | 34 |
| 38 | 物品冷启动 | 流量调控 | yes | 35 |
| 39 | 物品冷启动 | 冷启动的AB测试 | yes | 36 |
| 40 | 涨指标的方法 | 概述 | yes | 37 |
| 41 | 涨指标的方法 | 召回 | yes | 38 |
| 42 | 涨指标的方法 | 排序 | yes | 39 |
| 43 | 涨指标的方法 | 多样性 | yes | 40 |
| 44 | 涨指标的方法 | 特殊人群 | yes | 41 |
| 45 | 涨指标的方法 | 交互行为 | yes | 42 |

## Interpretation

- The 42-part playlist is a very strong coverage set, but it is not the complete official syllabus.
- Another AI that studies only the 42-part playlist may miss one ranking topic, one diversity topic, and one cold-start topic.
- For full mastery, the AI should study the full official 45-topic syllabus.
