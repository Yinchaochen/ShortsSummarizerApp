# 排序

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/08_Improvement_03.pdf

## Page 1

涨指标的方法：排序模型
王树森
http://wangshusen.github.io/

## Page 2

涨指标的方法有哪些？
• 改进召回模型，添加新的召回模型。
• 改进粗排和精排模型。
• 提升召回、粗排、精排中的多样性。
• 特殊对待新⽤户、低活⽤户等特殊⼈群。
• 利⽤关注、转发、评论这三种交互⾏为。

## Page 3

排序模型
1. 精排模型的改进
2. 粗排模型的改进
3. ⽤户⾏为序列建模
4. 在线学习
5. ⽼汤模型

## Page 4

精排模型的改进

## Page 5

Embedding + 全连接⺴络
全连接⺴络
⋯
预估点击率
预估点赞率
预估转发率
预估评论率
concatenation
离散特征
连续特征

## Page 6

Embedding + 全连接⺴络
全连接⺴络
⋯
预估点击率
预估点赞率
预估转发率
预估评论率
离散特征
连续特征
concatenation
基座

## Page 7

Embedding + 全连接⺴络
全连接⺴络
⋯
预估点击率
预估点赞率
预估转发率
预估评论率
离散特征
连续特征
concatenation

## Page 8

精排模型：基座
• 基座的输⼊包括离散特征和连续特征，输出⼀个向量，作为
多⽬标预估的输⼊。
• 改进1：基座加宽加深，计算量更⼤，预测更准确。

## Page 9

精排模型：基座
• 基座的输⼊包括离散特征和连续特征，输出⼀个向量，作为
多⽬标预估的输⼊。
• 改进1：基座加宽加深，计算量更⼤，预测更准确。
• 改进2：做⾃动的特征交叉，⽐如bilinear [1] 和LHUC [2]。
• 改进3：特征⼯程，⽐如添加统计特征、多模态内容特征。
参考⽂献
1. Huang et al. FiBiNET: combining feature importance and bilinear feature interaction
for click-through rate prediction. In RecSys, 2019.
2. Swietojanski et al. Learning hidden unit contributions for unsupervised acoustic model
adaptation. In WSDM, 2016.

## Page 10

Embedding + 全连接⺴络
全连接⺴络
⋯
预估点击率
预估点赞率
预估转发率
预估评论率
离散特征
连续特征
concatenation

## Page 11

精排模型：多目标预估
• 基于基座输出的向量，同时预估点击率等多个⽬标。
• 改进1：增加新的预估⽬标，并把预估结果加⼊融合公式。
• 最标准的⽬标包括点击率、点赞率、收藏率、转发率、评论率、
关注率、完播率……
• 寻找更多⽬标，⽐如进⼊评论区、给他⼈写的评论点赞……
• 把新的预估⽬标加⼊融合公式。

## Page 12

精排模型：多目标预估
• 基于基座输出的向量，同时预估点击率等多个⽬标。
• 改进1：增加新的预估⽬标，并把预估结果加⼊融合公式。
• 改进2：MMoE [1]、PLE [2] 等结构可能有效，但往往无效。
• 改进3：纠正position bias [3] 可能有效，也可能无效。
参考⽂献
1. Ma et al. Modeling task relationships in multi-task learning with multi-gate mixture-of-
experts. In KDD, 2018.
2. Tang et al. Progressive layered extraction (PLE): A novel multi-task learning (MTL)
model for personalized recommendations. In RecSys, 2020.
3. Zhou et al. Recommending what video to watch next: a multitask ranking system. In
RecSys, 2019.

## Page 13

粗排模型的改进

## Page 14

粗排模型
• 粗排的打分量⽐精排⼤10 倍，因此粗排模型必须够快。
• 简单模型：多向量双塔模型，同时预估点击率等多个⽬标。
• 复杂模型：三塔模型[1] 效果好，但⼯程实现难度较⼤。
参考⽂献
1. Wang et al. COLD: towards the next generation of pre-ranking system. arXiv, 2020.

## Page 15

粗精排一致性建模
• 蒸馏精排训练粗排，让粗排与精排更⼀致。
• ⽅法1：pointwise 蒸馏。
• 设𝑦是⽤户真实⾏为，设𝑝是精排的预估。
• ⽤!"#
$ 作为粗排拟合的⽬标。
• 例：
• 对于点击率⽬标，⽤户有点击（𝑦= 1），精排预估𝑝= 0.6。
• ⽤!"#
$ = 0.8 作为粗排拟合的点击率⽬标。

## Page 16

粗精排一致性建模
• 蒸馏精排训练粗排，让粗排与精排更⼀致。
• ⽅法1：pointwise 蒸馏。
• ⽅法2：pairwise 或listwise 蒸馏。
• 给定𝑘个候选物品，按照精排预估做排序。
• 做learning to rank (LTR)，让粗排拟合物品的序（⽽⾮值）。
• 例：
• 对于物品𝑖和𝑗，精排预估点击率为𝑝% > 𝑝&。
• LTR ⿎励粗排预估点击率满⾜𝑞% > 𝑞&，否则有惩罚。
• LTR 通常使⽤pairwise logistic loss。

## Page 17

粗精排一致性建模
• 蒸馏精排训练粗排，让粗排与精排更⼀致。
• ⽅法1：pointwise 蒸馏。
• ⽅法2：pairwise 或listwise 蒸馏。
• 优点：粗精排⼀致性建模可以提升核⼼指标。
• 缺点：如果精排出bug，精排预估值𝑝有偏，会污染粗排训练
数据。

## Page 18

⽤户⾏为序列建模

## Page 19

⋯
向量：
⋯
物品ID：
平均
Embedding

## Page 20

用户行为序列建模
• 最简单的⽅法是对物品向量取平均，作为⼀种⽤户特征[1]。
• DIN [2] 使⽤注意⼒机制，对物品向量做加权平均。
• ⼯业界⽬前沿着SIM [3] 的⽅向发展。先⽤类⽬等属性筛选物
品，然后⽤DIN 对物品向量做加权平均。
参考⽂献
1. Covington, Adams, and Sargin. Deep neural networks for YouTube recommendations. In
RecSys, 2016.
2. Zhou et al. Deep interest network for click-through rate prediction. In KDD, 2018.
3. Qi et al. Search-based User Interest Modeling with Lifelong Sequential Behavior Data
for Click-Through Rate Prediction. In CIKM, 2020.

## Page 21

用户行为序列建模
• 改进1：增加序列长度，让预测更准确，但是会增加计算成本
和推理时间。
• 改进2：筛选的⽅法，⽐如⽤类⽬、物品向量表征聚类。
• 离线⽤多模态神经⽹络提取物品内容特征，将物品表征为向量。
• 离线将物品向量聚为1000 类，每个物品有⼀个聚类序号。
• 线上排序时，⽤户⾏为序列中有𝑛= 1,000,000 个物品。某候
选物品的聚类序号是70，对𝑛个物品做筛选，只保留聚类序号
为70 的物品。𝑛个物品中只有数千个被保留下来。
• 同时有好⼏种筛选⽅法，取筛选结果的并集。

## Page 22

用户行为序列建模
• 改进1：增加序列长度，让预测更准确，但是会增加计算成本
和推理时间。
• 改进2：筛选的⽅法，⽐如⽤类⽬、物品向量表征聚类。
• 改进3：对⽤户⾏为序列中的物品，使⽤ID 以外的⼀些特征。
• 概括：沿着SIM 的⽅向发展，让原始的序列尽量长，然后做
筛选降低序列长度，最后将筛选结果输⼊DIN。

## Page 23

在线学习

## Page 24

全量更新vs 增量更新
前天的数据
昨天凌晨
⋯
基于前天的全量模型，⽤
前天的数据，做全量更新。
做增量更新

## Page 25

全量更新vs 增量更新
前天的数据
昨天凌晨
⋯
昨天的数据
今天凌晨
基于昨天的全量模型，⽤
昨天的数据，做全量更新。
⋯
做增量更新

## Page 26

在线学习的资源消耗
• 既需要在凌晨做全量更新，也需要全天不间断做增量更新。
• 设在线学习需要10,000 CPU core 的算⼒增量更新⼀个精排模
型。推荐系统⼀共需要多少额外的算⼒给在线学习？
• 为了做AB 测试，线上同时运⾏多个不同的模型。
• 如果线上有𝑚个模型，则需要𝑚套在线学习的机器。
• 线上有𝑚个模型，其中1 个是holdout，1 个是推全的模型，
𝑚−2 个测试的新模型。

## Page 27

在线学习的资源消耗
holdout
召回
粗排
精排
重排
10%⽤户
90%⽤户

## Page 28

在线学习的资源消耗
holdout
召回
粗排
精排
重排
10%⽤户
90%⽤户
新模型#1
新模型#2
推全模型

## Page 29

在线学习的资源消耗
• 线上有𝑚个模型，其中1 个是holdout，1 个是推全的
模型，𝑚−2 个测试的新模型。
• 每套在线学习的机器成本都很⼤，因此𝑚数量很⼩，制
约模型开发迭代的效率。
• 在线学习对指标的提升巨⼤，但是会制约模型开发迭代
的效率。

## Page 30

⽼汤模型

## Page 31

老汤模型
• ⽤每天新产⽣的数据对模型做1 epoch 的训练。
• 久⽽久之，⽼模型训练得⾮常好，很难被超过。
• 对模型做改进，重新训练，很难追上⽼模型……
• 问题1：如何快速判断新模型结构是否优于⽼模型？（不需要
追上线上的⽼模型，只需要判断新⽼模型谁的结构更优。）
• 问题2：如何更快追平、超过线上的⽼模型？（只有⼏⼗天的
数据，新模型就能追上训练上百天的⽼模型。）

## Page 32

老汤模型
• 对于新、⽼模型结构，都随机初始化模型全连接层。
• Embedding 层可以是随机初始化，也可以是复⽤⽼模型训练
好的参数。
• ⽤𝑛天的数据训练新⽼模型。（从旧到新，训练1 epoch）
• 如果新模型显著优于⽼模型，新模型很可能更优。
• 只是⽐较新⽼模型结构谁更好，⽽⾮真正追平⽼模型。
问题1：如何快速判断新模型结构是否优于⽼模型？

## Page 33

老汤模型
• 已经得出初步结论，认为新模型很可能优于⽼模型。⽤⼏⼗
天的数据训练新模型，早⽇追平⽼模型。
• ⽅法1：尽可能多地复⽤⽼模型训练好的embedding 层，避免
随机初始化embedding 层。（Embedding 层是对⽤户、物品
特点的“记忆”，⽐全连接层学得慢。）
• ⽅法2：⽤⽼模型做teacher，蒸馏新模型。（⽤户真实⾏为
是𝑦，⽼模型的预测是𝑝，⽤
!"#
$ 作为训练新模型的⽬标。）
问题2：如何更快追平线上的⽼模型？

## Page 34

总结：改进排序模型
• 精排模型：改进模型基座（加宽加深、特征交叉、特征⼯程），
改进多⽬标预估（增加新⽬标、MMoE、position bias）。
• 粗排模型：三塔模型（取代多向量双塔模型），粗精排⼀致性
建模。
• ⽤户⾏为序列建模：沿着SIM 的⽅向迭代升级，加长序列长度，
改进筛选物品的⽅法。
• 在线学习：对指标提升⼤，但是会降低模型迭代升级效率。
• ⽼汤模型制约模型迭代升级效率，需要特殊技巧。

## Page 35

Thank You!
http://wangshusen.github.io/
