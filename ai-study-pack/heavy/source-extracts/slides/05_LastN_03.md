# SIM 模型

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/05_LastN_03.pdf

## Page 1

SIM模型
王树森
http://wangshusen.github.io/

## Page 2

DIN模型
⋯
⽤户LastN交互记录
𝐱!
𝐱"
𝐱#
𝐱$
𝐪
候选物品

## Page 3

DIN模型
⋯
⽤户LastN交互记录
𝐱!
𝐱"
𝐱#
𝐱$
𝐪
候选物品
注意⼒层

## Page 4

DIN模型
• 计算⽤户LastN 向量的加权平均。
• 权重是候选物品与LastN 物品的相似度。
参考⽂献：
• Zhou et al. Deep interest network for click-through rate prediction. In
KDD, 2018.

## Page 5

DIN模型的缺点
• 注意⼒层的计算量∝𝑛（⽤户⾏为序列的长度）。
• 只能记录最近⼏百个物品，否则计算量太⼤。
• 缺点：关注短期兴趣，遗忘长期兴趣。
参考⽂献：
• Zhou et al. Deep interest network for click-through rate prediction. In
KDD, 2018.

## Page 6

如何改进DIN？
• ⽬标：保留⽤户长期⾏为序列（𝑛很⼤），⽽且计算量
不会过⼤。
• 改进DIN：
• DIN 对LastN 向量做加权平均，权重是相似度。
• 如果某LastN 物品与候选物品差异很⼤，则权重接近零。
• 快速排除掉与候选物品无关的LastN 物品，降低注意⼒
层的计算量。

## Page 7

SIM模型
• 保留⽤户长期⾏为记录，𝑛的⼤⼩可以是⼏千。
• 对于每个候选物品，在⽤户LastN 记录中做快速查
找，找到𝑘个相似物品。
• 把LastN 变成TopK，然后输⼊到注意⼒层。
• SIM 模型减⼩计算量（从𝑛降到𝑘）。
参考⽂献：
• Qi et al. Search-based User Interest Modeling with Lifelong Sequential
Behavior Data for Click-Through Rate Prediction. In CIKM, 2020.

## Page 8

第一步：查找
• ⽅法⼀：Hard Search
• 根据候选物品的类⽬，保留LastN 物品中类⽬相同的。
• 简单，快速，无需训练。

## Page 9

第一步：查找
• ⽅法⼀：Hard Search
• 根据候选物品的类⽬，保留LastN 物品中类⽬相同的。
• 简单，快速，无需训练。
• ⽅法⼆：Soft Search
• 把物品做embedding，变成向量。
• 把候选物品向量作为query，做𝑘近邻查找，保留LastN
物品中最接近的𝑘个。
• 效果更好，编程实现更复杂。

## Page 10

⽤户TopK交互记录
第二步：注意力机制
⋯
𝐱!
𝐱"
𝐱#
𝐱%
𝐪
候选物品
注意⼒层
作为⽤户⾏为特征，输⼊
模型，预估点击率等指标。

## Page 11

第二步：注意力机制
使⽤时间信息
• ⽤户与某个LastN 物品的交互时刻距今为𝛿。
• 对𝛿做离散化，再做embedding，变成向量𝐝。
• 把两个向量做concatenation，表征⼀个LastN 物品。
• 向量𝐱是物品embedding 。
• 向量𝐝是时间的embedding。

## Page 12

第二步：注意力机制
𝐱!
𝐝!
𝐱"
𝐝"
𝐱#
𝐝#
⋯
𝐪
⽤户TopK交互记录
候选物品

## Page 13

第二步：注意力机制
𝐱!
注意⼒层
𝐝!
𝐱"
𝐝"
𝐱#
𝐝#
⋯
𝐪
⽤户TopK交互记录
候选物品

## Page 14

第二步：注意力机制
为什么SIM 使⽤时间信息？
• DIN 的序列短，记录⽤户近期⾏为。
• SIM 的序列长，记录⽤户长期⾏为。
• 时间越久远，重要性越低。

## Page 15

结论
• 长序列（长期兴趣）优于短序列（近期兴趣）。
• 注意⼒机制优于简单平均。
• Soft search 还是hard search？取决于⼯程基建。
• 使⽤时间信息有提升。

## Page 16

Thank You!
http://wangshusen.github.io/
