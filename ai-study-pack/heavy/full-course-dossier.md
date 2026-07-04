# Full Course Dossier

This is the heavyweight, single-file dossier for another AI.

Primary source basis:

- Official course repo: https://github.com/wangshusen/RecommenderSystem
- User playlist root: https://www.bilibili.com/video/BV1FwXrBmEp4

## Course Coverage Facts

- Official topic count: `45`
- User playlist topic count: `42`
- Missing official topics from the user's playlist: `3`

Missing official topics:

- `Multi-gate Mixture-of-Experts (MMoE)`
- `多样性的度量`
- `聚类召回`

## Recommended Learning Order

1. Internalize the project-oriented summary.
2. Study the detailed course notes end to end.
3. Memorize the algorithm index.
4. Use the official topic map and playlist gap analysis for source navigation.
5. Read section bundles when exact source grounding is needed.
6. Use the mastery checklist as a self-test before claiming mastery.

## Project Summary

# Project-Oriented Summary

This summary is written for AI assistants or engineers who need to use the course in a real product context.

## One-sentence view

Industrial recommender systems are not about finding one best model. They are about building a layered decision system around the right business goals: `retrieval -> ranking -> reranking -> experimentation`.

## The central ideas

### 1. Optimize business goals, not only CTR

The course treats `DAU`, `retention`, `time spent`, and platform-specific value as the true north-star metrics. CTR and interaction rates matter, but they are often only supporting metrics.

Practical takeaway:

- Never define a recommender project only around click prediction.
- At minimum, track retention and time-related metrics together with click and interaction metrics.

### 2. Retrieval is a multi-channel system

Retrieval is not one model. Industrial systems combine many retrieval channels, such as:

- `item-to-item` methods like `ItemCF` and `Swing`
- `user-to-item` embedding retrieval such as `two-tower`
- tree or path based methods such as `Deep Retrieval`
- rule-driven channels such as freshness pools, geo pools, new-user pools, and author pools

Practical takeaway:

- Design retrieval as a portfolio of channels.
- Each channel should have a purpose, target population, and quota.

### 3. Ranking is multi-objective

Ranking should estimate several outcomes jointly, such as:

- click-through rate
- like rate
- collect/favorite rate
- share rate
- watch time or completion-related signals

These are later fused into a final score.

Practical takeaway:

- Use multi-task prediction or equivalent multi-objective scoring.
- For video products, do not rely only on click. Watch-time modeling is essential.

### 4. Features often matter more than fancy models

The course repeatedly highlights the value of:

- user profile features
- item profile features
- recent aggregated user statistics
- recent aggregated item statistics
- feature crosses
- sequence features

Practical takeaway:

- Before reaching for a more complex architecture, improve feature coverage and feature freshness.
- Keep feature definitions aligned with product behavior and business logic.

### 5. Sequence modeling captures current intent

Static user embedding is not enough. Recent behavior often reveals short-term intent better than long-term profile.

Key models:

- `DIN`: attention over recent interactions
- `SIM`: scalable retrieval of the most relevant historical behaviors before attention

Practical takeaway:

- If your project has session-like or feed-like behavior, sequence features should be in v1 or very early v2.

### 6. Diversity is not optional

If the system only optimizes interest score, users often get repetitive content. Short-term metrics may look good while long-term satisfaction drops.

Common strategies in the course:

- `MMR`
- `DPP`
- rule-based spacing and category breakup
- sliding-window reranking

Practical takeaway:

- Add diversity explicitly, especially in final reranking.
- Evaluate diversity as part of retention strategy, not as decoration.

### 7. Cold start needs separate logic

New items lack interaction statistics and stable embeddings. On UGC platforms, cold start also affects creator willingness to keep publishing.

Practical takeaway:

- Treat new-item distribution as a dedicated subsystem.
- Separate user-side and creator-side metrics.
- Consider traffic allocation, freshness pools, and early exposure protection.

### 8. Special populations need special handling

New users and low-activity users do not behave like established users. Models trained on the majority population often underperform badly on them.

Practical takeaway:

- Build special pools, special ranking strategies, or even special models for fragile populations.
- Protect retention-heavy groups instead of forcing one global strategy.

### 9. Interaction behaviors can be strategic signals

The course does not treat `follow`, `share`, and `comment` as just extra labels. They are strategic behaviors that affect future retention and ecosystem growth.

Practical takeaway:

- If a downstream action improves long-term product value, consider optimizing for it directly.
- Example: if following authors raises retention, the system can intentionally encourage follows for users with low follow counts.

## A reusable architecture template

For many real projects, a strong first architecture inspired by this course is:

1. Multi-channel retrieval
2. Lightweight coarse ranker
3. Multi-objective fine ranker
4. Final reranking for diversity, freshness, and policy constraints
5. Online A/B testing with retention-aware evaluation

## What to remember when building your own project

- Define the north-star metrics first.
- Start with clear retrieval channel design.
- Build multi-objective ranking early.
- Model recent behavior, not only user profile.
- Add diversity and cold-start handling on purpose.
- Protect new and weakly observed users.
- Trust online experiments more than pretty offline metrics.

## The most important sentence from the whole course

Industrial recommendation is a system optimization problem shaped by product goals, not a single-model leaderboard problem.

## Detailed Course Notes

# 详细课程笔记

这份文档的目标不是复述标题，而是把整套课压缩成另一个 AI 可以真正吸收的知识结构。

建议阅读方式：

1. 先读 `project-summary.md`，建立全局框架。
2. 再读本文，按章节吃透概念、链路、算法和工程含义。
3. 再读 `algorithm-index.md`，把关键模型和方法单独记牢。
4. 最后用 `mastery-checklist.md` 检查是否真的掌握。

## 0. 整门课的主线

这套课最核心的主线是：

- 推荐系统首先是一个业务系统，而不是一个模型竞赛。
- 推荐的基本链路是 `召回 -> 粗排/精排 -> 重排 -> 实验验证`。
- 工业系统的核心目标是长期收益，尤其是 `DAU`、`留存`、`时长`、`消费`、`内容生态`。
- 因此，任何模型、特征、策略都必须回到业务指标上评价。

如果另一个 AI 学完后只记住了 `双塔`、`DIN`、`DPP` 这些模型名，但没有理解“为什么工业界这样拆链路、为什么多样性和冷启动必须单独处理”，那么它其实还没有学会这门课。

## 1. 概要

### 1.1 推荐系统的基本概念

推荐系统本质上是在海量候选内容中，为某个用户选择最合适的一小批内容。这里的“最合适”不是抽象意义上的相关性，而是具体业务目标下的最优。

这门课强调几个工业界的现实：

- 用户看到的只是最后几十个曝光位，但系统面对的是几万、几百万甚至更大的内容池。
- 推荐系统并不是一次排序，而是多阶段筛选。
- 不同平台的目标不同。UGC 平台除了消费指标，还关心发布和生态；PGC 平台则更偏向消费时长和付费价值。

推荐系统的输入通常包括：

- 用户信息：ID、画像、历史行为、近期状态。
- 物品信息：ID、内容特征、标签、统计特征、发布时间、作者信息。
- 上下文信息：时间、地点、设备、页面位置、会话状态。

输出不是单一分数，而是最终曝光给用户的一组内容。

### 1.2 推荐系统的链路

工业推荐链路分层的原因不是形式主义，而是算力和业务约束共同决定的：

- 全量内容太多，不可能直接用复杂模型逐个打分。
- 不同阶段的目标不同，允许的计算量也不同。

典型链路：

1. 召回：从海量内容中找出几百到几千个候选。
2. 粗排：用较轻量模型快速筛掉不优候选。
3. 精排：用更强模型细致估计多种行为目标。
4. 重排：加入多样性、规则约束、冷启动保护、业务策略。
5. 实验：用在线 A/B 测试验证真正收益。

这条链路里最容易被误解的是：每一层都不是“低配版上一层”，而是承担不同职责。召回负责覆盖，排序负责准确，多样性和规则更多在重排体现。

### 1.3 A/B 测试与指标体系

课程反复强调：如果指标定义错了，后面的模型优化几乎都没有意义。

工业推荐常见的核心指标：

- `DAU`
- `留存`，例如 `LT7`、`LT30`
- `总时长`
- `总阅读/播放`
- 平台特有指标，例如发布、关注、转发、互动生态

非核心但常见的观测指标：

- `CTR`
- 点赞率
- 收藏率
- 转发率
- 评论率

关键认知：

- 不能只拿点击率当最终标准。
- 某个策略提升点击，不一定提升留存。
- 某个策略提升时长，也可能压低阅读数或曝光数。
- 工业优化要看指标兑换关系，而不是单指标胜负。

## 2. 召回

召回的目标是“高覆盖、低延迟、较强相关性”。它不是最终排序，而是把巨大的全量库压缩成可排序的候选集。

### 2.1 ItemCF

核心思想：

- 如果喜欢物品 A 的用户也经常喜欢物品 B，那么 A 和 B 相似。
- 当用户对 A 表现出兴趣时，可以给他召回与 A 相似的 B。

适用场景：

- 行为共现强的内容平台。
- 没有太多复杂特征时的强基线。
- “看了这个还看什么”的相似推荐。

优点：

- 简单、稳定、解释性强。
- 对热门内容和成熟物品有效。

缺点：

- 容易受到热门物品和热点群体干扰。
- 对新物品不友好。
- 只能利用已有共现，不擅长泛化。

工程含义：

- ItemCF 常常是工业系统中最可靠的召回通道之一。
- 但它通常只是多路召回中的一条，而不是全部。

### 2.2 Swing

Swing 可以看成对传统 ItemCF 的改良，重点解决“共现用户其实是个小圈子”时的误判问题。

直觉上：

- 单纯用户重合并不一定说明两个物品真的广泛相似。
- 如果重合来自某个非常小、非常特殊的群体，ItemCF 可能会高估相似度。
- Swing 会对这种情况做惩罚，让相似度更稳健。

工程意义：

- 比传统 ItemCF 更适合复杂兴趣分布。
- 对一些细分圈层内容更鲁棒。

### 2.3 UserCF

UserCF 的核心是找和目标用户相似的用户，再把这些相似用户喜欢的内容召回给目标用户。

它的优势在于：

- 适合“相似人群推荐相似内容”的场景。
- 在用户侧关系比较稳定时有价值。

但它在大规模系统中的问题也明显：

- 用户数往往远大于物品数，找相似用户代价高。
- 用户兴趣变化快，用户相似关系不够稳定。
- 在线计算和存储都更重。

因此工业界一般更偏向 item-to-item 或 embedding 召回，而不是把 UserCF 当主力。

### 2.4 离散特征处理

这一讲虽然不是独立召回模型，但非常重要，因为后面大部分模型都依赖正确的离散特征处理。

核心问题：

- 用户 ID、物品 ID、类目、品牌、关键词等都是高维稀疏离散特征。
- 这些特征不能直接用原始取值喂给神经网络。

常见方法：

- 分桶
- one-hot / multi-hot
- embedding
- 归一化和哈希化

工程含义：

- 很多召回和排序问题，根源并不是模型太弱，而是离散特征表达得不好。

### 2.5 矩阵补全

矩阵补全可以看成推荐中的经典 embedding 思想：

- 每个用户学一个向量。
- 每个物品学一个向量。
- 用户对物品的兴趣，用两个向量的内积表示。

它的重要性在于：

- 它是更复杂神经召回模型的思想起点。
- 双塔模型可以视作“带丰富特征版本”的矩阵补全。

优点：

- 结构清晰。
- 对 ID 级别关系建模直接。

缺点：

- 对新用户、新物品泛化差。
- 严重依赖足够多的交互数据。

### 2.6 双塔模型：模型和训练

双塔模型是这门课里最重要的召回模型之一。

基本结构：

- 用户塔：输入用户 ID、用户画像、用户行为等特征，输出用户向量。
- 物品塔：输入物品 ID、内容特征、统计特征等，输出物品向量。
- 用户向量和物品向量做内积或相似度，得到兴趣分数。

为什么双塔工业上重要：

- 物品向量可以离线预计算。
- 用户向量可以在线快速计算。
- 再配合 ANN 检索，就可以在大规模物品库里快速召回。

它比矩阵补全更强的原因：

- 不再只依赖 ID。
- 可以把各种特征都吸进用户塔和物品塔。
- 泛化能力更强。

### 2.7 双塔模型：正负样本

双塔的效果很大程度取决于训练样本设计，而不是只有网络结构。

正样本常见来源：

- 点击
- 播放
- 点赞
- 收藏
- 转发

负样本常见来源：

- 随机负样本
- 曝光未点击
- 困难负样本

关键工程经验：

- 负样本设计直接决定召回的区分能力。
- 只用非常简单的随机负样本，模型往往学不出真正细的偏好边界。
- 工业界会越来越强调 hard negative。

### 2.8 双塔模型：线上服务

这一讲的核心不是算法，而是“怎么真正上线”。

典型线上流程：

1. 物品塔离线算向量。
2. 把物品向量建 ANN 索引。
3. 用户请求到来时，在线算用户向量。
4. 在 ANN 索引里查最相近的物品。

工程重点：

- 索引更新频率
- 新物品向量的插入
- 向量版本兼容
- 用户侧实时特征更新
- 延迟与召回质量平衡

如果另一个 AI 只会讲双塔结构，却不会讲上线方式和索引维护，它对这门课的掌握还是不够工业化。

### 2.9 双塔模型 + 自监督学习

这部分的核心思想是：不只用显式监督信号，还利用更广泛的结构信号或序列信号去学习更好的表征。

作用：

- 丰富用户和物品向量表示。
- 在监督数据有限或噪声较强时提升鲁棒性。
- 让 embedding 更能吸收共现结构和上下文语义。

工程意义：

- 这是“只靠纯监督点击标签不够”的一个明确信号。
- 在内容平台中，表征学习往往决定召回上限。

### 2.10 Deep Retrieval

Deep Retrieval 的关键思想不是“再做一个双塔”，而是改变物品索引方式：

- 不把物品只表示成一个向量。
- 而是把物品表示成路径或树上的位置。
- 在线时先匹配路径，再拿到路径下的物品。

它的意义：

- 在非常大规模场景下，索引结构和召回结构深度耦合。
- 可以得到更高效的分层召回。

与双塔的区别：

- 双塔更像“向量检索”。
- Deep Retrieval 更像“结构化检索”。

### 2.11 其它召回通道

工业推荐不会只靠 CF 和双塔。常见还有：

- 地理位置召回
- 作者召回
- 缓存召回
- 新鲜内容召回
- 内容池召回

这类通道的价值在于：

- 它们经常不是最智能的，但在某些特定场景特别有效。
- 它们能弥补主召回模型在冷启动、人群细分、内容时效性上的不足。

### 2.12 曝光过滤

如果用户刚看过、刚刷过、刚跳过的内容还不停被召回出来，系统体验会很差。

因此工业系统需要做曝光过滤：

- 过滤最近曝光过的物品
- 控制重复出现频率
- 常见实现包括 Bloom Filter 等结构

它不属于“高大上的模型”，但对真实产品非常关键。

## 3. 排序

排序的任务是从召回来的候选里，选出最有价值的最终曝光结果。

### 3.1 多目标排序模型

推荐排序通常不是只预测一个目标，而是同时预测多个目标：

- CTR
- 点赞率
- 收藏率
- 转发率
- 评论率
- 播放时长
- 完播率

为什么要多目标：

- 用户价值不是单一行为。
- 单一点击目标容易导致标题党、短期刺激、低留存。
- 不同行为对平台长期价值贡献不同。

因此精排常见做法是：

1. 同时预测多个行为概率或收益。
2. 再把这些分数融合成最终排序分数。

### 3.2 MMoE

MMoE 是多任务学习在排序中的代表模型。

核心思想：

- 多个任务共享一组 experts。
- 每个任务通过自己的 gate 选择不同 experts 的组合。

它适合的场景：

- 多个任务之间既相关，又不完全一致。
- 希望共享底层信息，但又不想所有任务完全绑死。

工程意义：

- 多目标排序不是简单把多个头挂在一个底座上。
- 任务之间存在冲突和共享，MMoE 是处理这种张力的经典方法。

### 3.3 预估分数融合

当系统已经预测出多个行为分数后，需要一个融合策略。

简单方法：

- 加权和
- 分段策略
- 规则修正

更关键的是理解：

- 融合系数本身就是业务策略的一部分。
- 不同平台、不同频道、不同人群，融合权重可能不一样。
- “点得多但不留存”和“点击没那么高但提升时长留存”的目标，往往需要平衡。

### 3.4 播放时长建模

课程特别指出，视频场景不能简单用回归直接拟合时长。

更稳健的做法是：

- 对时长目标做适合分类式学习的变换
- 用更稳定的损失训练
- 把时长作为排序重要目标之一

工业意义：

- 视频推荐如果只盯点击，会过度偏向容易点开的内容。
- 真正的视频平台核心价值经常和观看深度强相关。

### 3.5 排序模型的特征

这部分是整门课最有工程价值的内容之一。

课程把排序特征拆成几类：

- 用户画像特征
- 物品画像特征
- 用户统计特征
- 物品统计特征
- 交叉特征
- 序列特征

重点不是背分类，而是理解特征系统设计原则：

- 特征要覆盖长期偏好和短期意图。
- 特征要覆盖用户、物品、上下文三端。
- 特征要按时间窗统计，例如近 30 天、7 天、1 天、1 小时。
- 特征要做分桶，例如按类目、内容形态、性别、年龄段分桶。

### 3.6 粗排三塔模型

粗排的目标是在精排前进一步压缩候选，同时尽量保持高价值内容不被误杀。

三塔的直觉通常是：

- 用户塔
- 物品塔
- 上下文或额外信息塔

它体现的思想是：

- 粗排比召回更准，但比精排更轻。
- 粗排也可以引入丰富特征，但必须控制计算开销。

## 4. 交叉结构

交叉结构处理的是“特征如何组合”这个问题。

### 4.1 FM

FM 适合高维稀疏特征场景，核心能力是有效建模二阶特征交互。

它的意义：

- 在 ID、类目、品牌、关键词等稀疏特征很多时，FM 是经典基线。
- 训练和表达都相对高效。

### 4.2 DCN

DCN 强调显式特征交叉，适合做更结构化的组合。

它的工业意义：

- 当你怀疑“重要信息藏在某些特征组合里”时，DCN 往往比纯 MLP 更合适。
- 在召回塔、排序底座中都可以使用。

### 4.3 LHUC

LHUC 更像是一种个性化调制思路，用来让不同用户群对网络内部表达产生不同影响。

它的价值：

- 让同一个主模型对不同人群有不同响应。
- 对个性化推荐尤其有意义。

### 4.4 SENet / FiBiNET

这一类方法强调对特征重要性和双线性交叉的更精细建模。

可以理解为：

- 不是所有特征都同样重要。
- 不是所有交叉都应该一视同仁。

工程意义：

- 当你有大量结构化特征时，这类结构能提升有效交叉能力。

## 5. 用户行为序列建模

### 5.1 用户行为序列特征

用户不是静态的。最近看了什么、点了什么、收藏了什么，往往比长期画像更能反映当前兴趣。

LastN 序列的基本价值：

- 表达短期兴趣
- 表达兴趣迁移
- 弥补用户画像滞后

### 5.2 DIN

DIN 的关键是 attention：

- 不再对历史行为简单平均。
- 而是根据候选物品，给历史行为不同权重。
- 与候选更相关的历史行为权重大。

好处：

- 个性化更细。
- 对当前候选更敏感。

问题：

- 序列越长，计算越贵。
- 很难直接保留特别长的历史。

### 5.3 SIM

SIM 可以理解为对 DIN 的工业级扩展。

它的思路是：

- 先从很长历史里快速找出与当前候选最相关的一小部分。
- 再对这一小部分做更精细建模。

因此它解决了 DIN 的关键痛点：

- 既想保留长期兴趣
- 又不想让注意力层计算爆炸

工业意义非常强：

- 对长行为序列用户特别重要。
- 对内容流和商品流都实用。

## 6. 多样性

课程认为多样性不是锦上添花，而是会显著影响长期指标的核心能力。

### 6.1 多样性的度量

多样性衡量的不是单个物品好不好，而是一组物品之间是否过于相似。

相似度来源可以包括：

- 类目
- 品牌
- 标签
- 多模态向量
- embedding

判断一组结果是否多样，本质上是在看：

- 是否过分集中在相似主题
- 是否给用户留下探索空间

### 6.2 MMR

MMR 是最经典的多样性方法之一。

它的本质是平衡两件事：

- 当前物品本身的价值高不高
- 当前物品和已经选中物品相似不相似

因此 MMR 适合做逐步选择式的重排。

### 6.3 规则约束

工业系统里，多样性往往不只靠数学模型，也靠规则：

- 相邻几个位置不能同类目
- 相邻若干位置不能同作者
- 某些内容形态不能过密

这类规则很工程，但很有效。

### 6.4 DPP：数学基础

DPP 把“多样性”用集合体积或行列式思想表达出来。

直觉上：

- 如果一组向量彼此很像，它们张成的体积小。
- 如果一组向量方向差异更大，张成的体积大。

于是就可以同时考虑：

- 单个物品的 reward
- 整个集合的多样性

### 6.5 DPP：多样性算法

DPP 的优点：

- 理论表达优雅
- 能直接优化“集合级”的多样性

缺点：

- 实现和计算都更重
- 工程落地复杂度高于 MMR

因此工程上常见情况是：

- 简单场景先用 MMR 和规则
- 更高要求场景再考虑 DPP 类方法

## 7. 物品冷启动

冷启动不是“推荐系统的一个角落问题”，而是 UGC 平台生态问题。

### 7.1 为什么新物品难

新物品通常缺两类最重要信息：

- 足够稳定的统计特征
- 足够成熟的 ID embedding

因此：

- 召回不准
- 排序不准
- 优质新内容可能被埋没

在 UGC 平台上，这还会进一步影响作者发布意愿。

### 7.2 冷启动评价指标

冷启动不能只看用户侧指标，还要看：

- 作者侧指标
- 内容侧指标
- 用户侧指标

关键理念：

- 新内容被更早、更多地曝光，可能提升作者继续创作的动力。
- 但如果为冷启牺牲太多用户体验，也是不对的。

### 7.3 简单召回通道

冷启动阶段常常先依赖一些更朴素但可控的通道：

- 新内容池
- 基于内容标签的召回
- 类目池
- 时效性池

### 7.4 聚类召回

聚类召回的本质是把相似内容或用户分簇，再把新内容通过簇结构接入分发。

优势：

- 对缺交互的新物品更友好
- 可以利用内容相似性先找到早期适配用户

### 7.5 Look-Alike 人群扩散

这部分强调：

- 如果已经有一批适合这类内容的人群
- 可以从中扩展出更大的相似人群

它体现的是“先找到核心受众，再往外扩散”的思想。

### 7.6 流量调控

冷启动不能完全靠自然排序，因为新内容先天弱势。

所以需要流量调控：

- 给新内容一定保护流量
- 控制试探节奏
- 根据反馈逐步加量或减量

本质上这是探索和利用的平衡。

### 7.7 冷启动 A/B 测试

冷启动实验比普通推荐实验更难，原因是：

- 既要看用户侧反馈
- 又要看作者侧和内容侧反馈
- 指标延迟更长
- 分流方案更复杂

课程明确指出：工业界并没有完美的作者侧实验方案。

## 8. 涨指标的方法

这一部分可以看作整门课的“策略层总结”。

### 8.1 概述

现代推荐系统涨指标的常见方向：

- 改进召回
- 改进粗排和精排
- 提升多样性
- 特殊对待特殊人群
- 利用高价值交互行为

这部分最重要的观点是：

- 涨指标不是只加更深的模型
- 而是对系统不同层次逐项改造

### 8.2 涨指标的方法：召回

常见方向：

- 加新的召回通道
- 优化双塔样本
- 优化 hard negative
- 强化用户行为序列进入召回
- 在召回塔里用更强交叉结构，如 DCN
- 用多向量表示代替单向量表示

### 8.3 涨指标的方法：排序

排序侧常见改进：

- 更强的底座
- 更好的多任务结构
- 更好的特征系统
- 更好的序列建模
- 在线学习

工业含义：

- 很多增益其实来自特征刷新、样本改进、任务定义改进，而不是只是“换个大模型名词”。

### 8.4 涨指标的方法：多样性

课程强调：

- 多样性可以在召回、粗排、精排、重排多层处理
- 但精排后的最终重排尤其关键
- 还可以结合打散规则和滑动窗口策略

### 8.5 涨指标的方法：特殊人群

新用户、低活用户、特定行为用户，需要单独看待。

常见做法：

- 建特殊内容池
- 建特殊排序策略
- 建特殊模型

这是因为：

- 主流人群训练出来的模型对这些群体往往偏差较大
- 而这些人群又往往最容易流失

### 8.6 涨指标的方法：利用交互行为

课程在这里给出了很重要的产品观：

- 关注、评论、转发，不只是额外标签
- 它们可能会影响用户长期留存和平台生态

因此：

- 排序模型可以把这些行为作为目标
- 系统也可以策略性地促进高价值交互

例如：

- 如果用户关注作者数量过少，系统可以更积极地给他推荐容易关注的作者内容

## 9. 把整门课翻译成项目设计语言

如果另一个 AI 真正掌握了这门课，它应该能把课程内容翻译成项目架构，而不是只会解释算法。

一个合格的项目设计输出，至少应当包含：

1. 北极星指标与观测指标
2. 多路召回设计
3. 粗排与精排分工
4. 多目标排序目标定义
5. 特征体系
6. 序列建模方案
7. 多样性策略
8. 冷启动策略
9. 特殊人群保护策略
10. A/B 测试计划

## 10. 学完后必须形成的认知

另一个 AI 如果真的吸收了这门课，应当具备以下认知：

- 推荐系统首先是目标设计问题，其次才是建模问题。
- 召回和排序不是一个问题的两个实现，而是职责不同的两个层次。
- 多目标优化、长周期指标、多样性、冷启动、特殊人群，这些都不是附属功能，而是工业推荐的主战场。
- 好的推荐系统不是“某个模型最高分”，而是“整个系统在业务上稳定赚钱或稳定提升生态”。

## Algorithm Index

# 算法索引卡

这份索引卡的目标是让另一个 AI 快速建立“问题 -> 方法 -> 优缺点 -> 工程含义”的映射。

## ItemCF

- 解决的问题：基于物品共现做相似召回。
- 核心直觉：喜欢 A 的用户也喜欢 B，则 A 和 B 相似。
- 主要输入：用户-物品交互日志。
- 主要优点：简单、稳定、解释性强。
- 主要缺点：对新物品不友好，容易受热门物品干扰。
- 典型位置：召回通道。

## Swing

- 解决的问题：修正传统 ItemCF 对小圈子共现的误判。
- 核心直觉：不是所有共现都同样可信，应惩罚特殊小群体造成的虚高相似度。
- 主要优点：比纯 ItemCF 更鲁棒。
- 主要缺点：仍然依赖已有行为共现。
- 典型位置：召回通道。

## UserCF

- 解决的问题：基于相似用户做召回。
- 核心直觉：相似用户喜欢相似物品。
- 主要优点：用户关系明显时有价值。
- 主要缺点：大规模部署重，用户相似关系不稳定。
- 典型位置：早期系统或辅助通道。

## 矩阵补全

- 解决的问题：学习用户和物品的 latent embedding。
- 核心直觉：用户向量与物品向量的内积表示兴趣。
- 主要优点：概念清晰，是 embedding 推荐的基础。
- 主要缺点：对新用户、新物品泛化弱。
- 典型位置：经典推荐基线，双塔的思想前身。

## 双塔模型

- 解决的问题：大规模个性化召回。
- 核心直觉：用户塔和物品塔分别编码，在线做相似检索。
- 主要输入：用户特征、物品特征、正负样本。
- 主要优点：可离线建物品索引，线上高效，工业可落地性强。
- 主要缺点：单向量表达能力有限，训练高度依赖样本设计。
- 典型位置：核心召回模型。

## Hard Negative

- 解决的问题：随机负样本过于简单，模型难以学到细粒度边界。
- 核心直觉：让模型区分“看起来像正样本但其实不是”的难例。
- 主要优点：通常显著提高召回/排序判别能力。
- 主要缺点：构造不当可能引入噪声或训练不稳定。

## 自监督表征学习

- 解决的问题：纯监督信号有限，表征能力不足。
- 核心直觉：利用更广泛的共现、序列、上下文结构学习更好的 embedding。
- 主要优点：增强泛化和鲁棒性。
- 主要缺点：训练设计更复杂。

## Deep Retrieval

- 解决的问题：超大规模内容库下的高效结构化召回。
- 核心直觉：把物品表示为路径或树节点，在线先匹配路径再拿物品。
- 主要优点：检索结构更强，适合极大规模。
- 主要缺点：建模和索引实现复杂。

## 曝光过滤

- 解决的问题：重复推荐、刷屏式重复曝光。
- 核心直觉：把最近曝光或消费过的内容做抑制。
- 主要优点：直接改善体验。
- 主要缺点：过滤过强会损伤高价值重复内容。

## 多目标排序

- 解决的问题：单一 CTR 目标无法代表真实平台价值。
- 核心直觉：同时预测点击、点赞、收藏、转发、时长等目标，再融合。
- 主要优点：更贴近业务收益。
- 主要缺点：任务冲突、权重设计复杂。

## MMoE

- 解决的问题：多任务学习中共享与冲突并存。
- 核心直觉：共享 experts + 任务专属 gates。
- 主要优点：适合多目标排序。
- 主要缺点：结构更复杂，训练调参成本更高。

## 分数融合

- 解决的问题：多个目标如何变成一个最终排序分数。
- 核心直觉：业务目标决定融合方式。
- 主要优点：灵活、直接体现策略。
- 主要缺点：权重很容易被调成“看起来合理但长期收益差”。

## 播放时长建模

- 解决的问题：视频排序不能只依赖点击。
- 核心直觉：把观看深度纳入训练和排序目标。
- 主要优点：更贴近视频产品真实价值。
- 主要缺点：目标设计和损失设计更复杂。

## FM

- 解决的问题：高维稀疏特征的二阶交叉。
- 核心直觉：用低维向量高效表达二阶交互。
- 主要优点：经典、便宜、稳定。
- 主要缺点：表达高阶复杂关系能力有限。

## DCN

- 解决的问题：显式建模特征交叉。
- 核心直觉：通过 cross layers 结构化构建交互。
- 主要优点：适合推荐场景中的高价值组合特征。
- 主要缺点：结构与输入设计要谨慎。

## LHUC

- 解决的问题：同一主模型对不同人群适配不足。
- 核心直觉：通过可学习缩放让隐层对不同用户更个性化。
- 主要优点：提升个性化调制能力。
- 主要缺点：解释和调试比标准 MLP 更难。

## SENet / FiBiNET

- 解决的问题：不同特征和交叉的重要性不一样。
- 核心直觉：做特征重标定与更细的交叉建模。
- 主要优点：特征丰富时常有收益。
- 主要缺点：收益依赖特征质量。

## LastN 序列特征

- 解决的问题：静态画像无法表达短期兴趣。
- 核心直觉：最近行为更能代表当前意图。
- 主要优点：简单且有效。
- 主要缺点：平均化处理会损失候选相关性。

## DIN

- 解决的问题：不同历史行为对当前候选的重要性不同。
- 核心直觉：对历史序列做候选相关的 attention。
- 主要优点：比简单平均更精准。
- 主要缺点：长序列计算贵。

## SIM

- 解决的问题：DIN 难以处理很长行为序列。
- 核心直觉：先从长序列里筛相关子集，再做精细注意力。
- 主要优点：兼顾长期兴趣与计算效率。
- 主要缺点：实现复杂度更高。

## MMR

- 解决的问题：排序结果过于相似。
- 核心直觉：平衡兴趣分数和与已选结果的差异性。
- 主要优点：简单、直观、好部署。
- 主要缺点：是贪心近似，集合级最优性有限。

## DPP

- 解决的问题：从集合层面优化多样性。
- 核心直觉：让被选结果在向量空间中张成更大体积。
- 主要优点：理论优雅，集合多样性表达更强。
- 主要缺点：数学和工程复杂度高。

## 规则打散

- 解决的问题：纯模型多样性仍可能不够。
- 核心直觉：直接施加业务可解释规则。
- 主要优点：稳定、可控。
- 主要缺点：规则过多会压制模型收益。

## 聚类召回

- 解决的问题：新物品缺行为数据，难以被精确召回。
- 核心直觉：先通过内容相似性或簇结构找到可分发人群。
- 主要优点：对冷启动友好。
- 主要缺点：簇的质量决定效果。

## Look-Alike

- 解决的问题：如何把内容从核心受众扩散到更大人群。
- 核心直觉：从已知适配人群外推相似人群。
- 主要优点：适合冷启放量。
- 主要缺点：扩散过猛会损伤精准度。

## 流量调控

- 解决的问题：新物品天然弱势，完全靠自然排序很难起量。
- 核心直觉：通过策略性配额和节奏控制进行探索。
- 主要优点：能保护优质新内容。
- 主要缺点：控制不好会伤害用户体验。

## Official Topic Inventory

| Official # | Section | Topic | Playlist Part |
|---|---|---|---|
| 1 | 概要 | 推荐系统的基本概念 | 1 |
| 2 | 概要 | 推荐系统的链路 | 2 |
| 3 | 概要 | AB测试 | 3 |
| 4 | 召回 | 基于物品的协同过滤（ItemCF） | 4 |
| 5 | 召回 | Swing模型 | 5 |
| 6 | 召回 | 基于用户的协同过滤（UserCF） | 6 |
| 7 | 召回 | 离散特征处理 | 7 |
| 8 | 召回 | 矩阵补充 | 8 |
| 9 | 召回 | 双塔模型：模型和训练 | 9 |
| 10 | 召回 | 双塔模型：正负样本 | 10 |
| 11 | 召回 | 双塔模型：线上服务 | 11 |
| 12 | 召回 | 双塔模型+自监督学习 | 12 |
| 13 | 召回 | Deep Retrieval 召回 | 13 |
| 14 | 召回 | 其它召回通道 | 14 |
| 15 | 召回 | 曝光过滤 | 15 |
| 16 | 排序 | 多目标排序模型 | 16 |
| 17 | 排序 | Multi-gate Mixture-of-Experts (MMoE) | missing |
| 18 | 排序 | 预估分数融合 | 17 |
| 19 | 排序 | 播放时长建模 | 18 |
| 20 | 排序 | 推荐系统的特征 | 19 |
| 21 | 排序 | 粗排三塔模型 | 20 |
| 22 | 交叉结构 | Factorized Machine (FM) | 21 |
| 23 | 交叉结构 | Deep & Cross Network (深度交叉网络) | 22 |
| 24 | 交叉结构 | LHUC | 23 |
| 25 | 交叉结构 | SENet & FiBiNET | 24 |
| 26 | 用户行为序列建模 | 用户行为序列特征 | 25 |
| 27 | 用户行为序列建模 | DIN 模型 | 26 |
| 28 | 用户行为序列建模 | SIM 模型 | 27 |
| 29 | 多样性 | 多样性的度量 | missing |
| 30 | 多样性 | MMR 算法 | 28 |
| 31 | 多样性 | 规则约束 | 29 |
| 32 | 多样性 | DPP：数学基础 | 30 |
| 33 | 多样性 | DPP：多样性算法 | 31 |
| 34 | 物品冷启动 | 评价指标 | 32 |
| 35 | 物品冷启动 | 简单的召回通道 | 33 |
| 36 | 物品冷启动 | 聚类召回 | missing |
| 37 | 物品冷启动 | Look-Alike人群扩散 | 34 |
| 38 | 物品冷启动 | 流量调控 | 35 |
| 39 | 物品冷启动 | 冷启动的AB测试 | 36 |
| 40 | 涨指标的方法 | 概述 | 37 |
| 41 | 涨指标的方法 | 召回 | 38 |
| 42 | 涨指标的方法 | 排序 | 39 |
| 43 | 涨指标的方法 | 多样性 | 40 |
| 44 | 涨指标的方法 | 特殊人群 | 41 |
| 45 | 涨指标的方法 | 交互行为 | 42 |

## Mastery Checklist

# 掌握度检查表

这份清单的目的不是让另一个 AI “看起来懂”，而是检查它是否真的吸收了课程。

## A. 基础理解

另一个 AI 至少应该能清楚回答以下问题：

1. 为什么推荐系统不能只优化 CTR？
2. 为什么工业推荐必须拆成召回、排序、重排等多个阶段？
3. `DAU`、`LT7/LT30`、时长、交互率分别属于什么层级的指标？
4. 为什么线上 A/B 测试比离线指标更有决定性？
5. 为什么“模型更复杂”不等于“系统更成功”？

## B. 召回掌握

另一个 AI 应该能解释：

1. `ItemCF` 的基本思想、适用场景和缺陷。
2. `Swing` 相比 `ItemCF` 在解决什么问题。
3. `UserCF` 为什么在工业大规模场景中通常不是主力。
4. 矩阵补全和双塔模型之间的关系。
5. 双塔模型为什么适合大规模线上检索。
6. 双塔训练时为什么负样本设计很关键。
7. 什么是 hard negative，以及为什么它重要。
8. 双塔上线时为什么需要 ANN 索引。
9. `Deep Retrieval` 与向量召回的核心区别是什么。
10. 为什么工业界需要多路召回，而不是单一召回模型。
11. 地理位置、作者、缓存、新鲜内容等通道为什么仍然重要。
12. 曝光过滤为什么属于“很朴素但很关键”的能力。

## C. 排序掌握

另一个 AI 应该能解释：

1. 为什么排序通常要做多目标建模。
2. CTR、点赞、收藏、转发、时长之间为什么会有冲突。
3. 为什么分数融合本身就是一种产品策略。
4. 视频场景为什么必须重视播放时长建模。
5. MMoE 解决了多任务学习中的什么问题。
6. 粗排和精排各自承担什么职责。
7. 为什么排序特征要覆盖用户、物品、上下文三端。
8. 为什么需要多时间窗统计特征。

## D. 特征与交叉结构掌握

另一个 AI 应该能解释：

1. 为什么高维稀疏特征需要 embedding。
2. `FM` 适合解决什么问题。
3. `DCN` 和普通 MLP 的差异在哪里。
4. `LHUC` 想增强什么能力。
5. `SENet / FiBiNET` 为什么强调特征重要性和交叉质量。

## E. 序列建模掌握

另一个 AI 应该能解释：

1. 为什么用户最近行为比静态画像更能表达当前兴趣。
2. `DIN` 的注意力机制在解决什么问题。
3. `DIN` 的主要计算瓶颈是什么。
4. `SIM` 如何缓解长序列建模的计算压力。
5. 为什么长短期兴趣都应该被保留。

## F. 多样性掌握

另一个 AI 应该能解释：

1. 为什么多样性能影响留存和时长，而不仅仅是页面好看。
2. 常见的物品相似性可以如何定义。
3. `MMR` 的平衡目标是什么。
4. `DPP` 为什么是集合级多样性方法。
5. 为什么工业系统中规则打散仍然重要。
6. 为什么精排和重排中的多样性处理方式可能不同。

## G. 冷启动掌握

另一个 AI 应该能解释：

1. 为什么新物品推荐天然更难。
2. 为什么冷启动不仅是用户体验问题，还是作者生态问题。
3. 冷启动为什么需要单独的评价指标。
4. 什么是作者侧、用户侧、内容侧指标。
5. 简单内容池、聚类召回、Look-Alike、流量调控分别解决什么问题。
6. 为什么冷启动 A/B 测试比普通推荐实验更复杂。

## H. 策略层掌握

另一个 AI 应该能解释：

1. 涨指标的主要方向有哪些。
2. 为什么“加更深的模型”不是唯一手段。
3. 为什么新用户和低活用户要特殊处理。
4. 如何通过关注、转发、评论等高价值行为改善长期指标。
5. 什么是“指标兑换关系”，为什么它重要。

## I. 项目翻译能力

另一个 AI 如果真的学会了，应该能独立产出下面这些内容：

1. 一套包含北极星指标和观测指标的项目指标定义。
2. 一套多路召回设计，说明每条通道的职责。
3. 一套粗排和精排的分工设计。
4. 一套多目标排序目标与融合策略。
5. 一套用户、物品、上下文特征清单。
6. 一套序列建模方案。
7. 一套多样性策略。
8. 一套冷启动策略。
9. 一套特殊人群保护策略。
10. 一套 A/B 测试方案。

## J. 不应出现的错误信号

如果另一个 AI 出现以下表现，说明它还没有真正掌握这门课：

- 只会说“用 CTR 做目标”。
- 把推荐问题理解成单次排序问题。
- 只会讲模型结构，不会讲线上索引和部署。
- 不知道为什么要多路召回。
- 忽视多样性、冷启动、特殊人群。
- 完全不谈 A/B 测试。
- 给不出任何业务指标层面的解释。

## K. 通过标准

可以把下面这条作为是否“基本掌握”的标准：

如果另一个 AI 能在不查资料的前提下，完整解释课程的八大模块，并为一个真实推荐项目产出一份自洽的系统设计，且能说明每个模块与业务指标的关系，那么它可以认为已经较为扎实地掌握了这门课。

## Heavy-Pack Navigation

- Machine-readable syllabus: `manifest.json`
- Official-to-playlist mapping: `playlist-gap-analysis.md`
- Chapter bundles with inline extracts: `section-bundles/`
- Raw extracted official texts: `source-extracts/`
