# DIN 模型

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/05_LastN_02.pdf

## Page 1

DIN模型
王树森
http://wangshusen.github.io/

## Page 2

⋯
向量：
⋯
物品ID：
平均
Embedding

## Page 3

DIN模型
• DIN ⽤加权平均代替平均，即注意⼒机制
（attention）。
• 权重：候选物品与⽤户LastN 物品的相似度。
参考⽂献：
• Zhou et al. Deep interest network for click-through rate prediction. In
KDD, 2018.

## Page 4

⋯
LastN向量：
𝐱!
𝐱"
𝐱#
𝐱$
𝐪
候选物品
向量：

## Page 5

⋯
相似度：
LastN向量：
𝐱!
𝐱"
𝐱#
𝐱$
𝐪
𝛼!
候选物品
向量：

## Page 6

⋯
相似度：
LastN向量：
𝐱!
𝐱"
𝐱#
𝐱$
𝐪
𝛼!
𝛼"
候选物品
向量：

## Page 7

⋯
相似度：
LastN向量：
𝐱!
𝐱"
𝐱#
𝐱$
𝐪
𝛼!
𝛼"
𝛼#
候选物品
向量：

## Page 8

⋯
LastN向量：
𝐱!
𝐱"
𝐱#
𝐱$
𝐪
⋯
相似度：
𝛼!
𝛼"
𝛼#
𝛼$
候选物品
向量：

## Page 9

⋯
相似度：
LastN向量：
𝐱!
𝐱"
𝐱#
𝐱$
𝐪
𝛼!
𝛼"
𝛼#
𝛼$
×
加权和
候选物品
向量：

## Page 10

DIN模型
• 对于某候选物品，计算它与⽤户LastN 物品的相似
度。
• 以相似度为权重，求⽤户LastN 物品向量的加权和，
结果是⼀个向量。
• 把得到的向量作为⼀种⽤户特征，输⼊排序模型，
预估（⽤户，候选物品）的点击率、点赞率等指标。
• 本质是注意⼒机制（attention）。

## Page 11

DIN的本质是注意力机制
⋯
作为key 和value
𝐱!
𝐱"
𝐱#
𝐱$
𝐪
作为query

## Page 12

DIN的本质是注意力机制
⋯
作为key 和value
𝐱!
𝐱"
𝐱#
𝐱$
𝐪
作为query
单头注意⼒层

## Page 13

DIN有效的原因
美⾷
美妆
汽车
候选物品（美⾷）
简单平均

## Page 14

DIN有效的原因
候选物品（美⾷）
汽车
美妆
美⾷

## Page 15

候选物品（美⾷）
汽车
美妆
DIN有效的原因
美⾷
加权平均
作为⽤户特征

## Page 16

DIN有效的原因
候选物品（新闻）
汽车
美妆
美⾷

## Page 17

美⾷
汽车
美妆
DIN有效的原因
加权平均
候选物品（新闻）

## Page 18

简单平均v.s. 注意力机制
• 简单平均和注意⼒机制都适⽤于精排模型。
• 简单平均适⽤于双塔模型、三塔模型。
• 简单平均只需要⽤到LastN，属于⽤户⾃⾝的特征。
• 把LastN 向量的平均作为⽤户塔的输⼊。
• 注意⼒机制不适⽤于双塔模型、三塔模型。
• 注意⼒机制需要⽤到LastN + 候选物品。
• ⽤户塔看不到候选物品，不能把注意⼒机制⽤在⽤户塔。

## Page 19

Thank You!
http://wangshusen.github.io/
