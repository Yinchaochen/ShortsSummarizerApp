# Section 5: 用户行为序列建模

## Why This Section Matters

- Understand why recent user behavior often captures current intent better than static profile features.
- Learn the motivation and tradeoff between DIN and SIM.

## Common Mistakes To Avoid

- Do not ignore sequence length and compute cost.
- Do not lose long-term interest while modeling short-term intent.

## Topic List

- Official #26: `用户行为序列特征` (user playlist part `25`)
- Official #27: `DIN 模型` (user playlist part `26`)
- Official #28: `SIM 模型` (user playlist part `27`)

## Official Source Links

- `用户行为序列特征`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/05_LastN_01.pdf`, Bilibili `https://www.bilibili.com/video/BV1GG4y1B7Yh`, YouTube `https://youtu.be/Stbc9goPKXQ`
- `DIN 模型`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/05_LastN_02.pdf`, Bilibili `https://www.bilibili.com/video/BV1bT411T7u4`, YouTube `https://youtu.be/0hPep80Oy6k`
- `SIM 模型`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/05_LastN_03.pdf`, Bilibili `https://www.bilibili.com/video/BV1Ze4y1B7JL`, YouTube `https://youtu.be/_4J9aF5KR84`

## Local Extract Files

- `用户行为序列特征`: `../source-extracts/slides/05_LastN_01.md`
- `DIN 模型`: `../source-extracts/slides/05_LastN_02.md`
- `SIM 模型`: `../source-extracts/slides/05_LastN_03.md`
## Inline Slide Extracts

### 用户行为序列特征

# 用户行为序列特征

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/05_LastN_01.pdf

## Page 1

用户行为序列建模
王树森
http://wangshusen.github.io/

## Page 2

点击率
点赞率
收藏率
转发率
神经⽹络
全连接层+Sigmoid
物品特征
Concatenation
统计特征
场景特征
用户特征

## Page 3

⋯
向量：
⋯
物品ID：
平均
Embedding

## Page 4

LastN特征
• LastN：⽤户最近的𝑛次交互（点击、点赞等）的
物品ID。
• 对LastN 物品ID 做embedding，得到𝑛个向量。
• 把𝑛个向量取平均，作为⽤户的⼀种特征。
• 适⽤于召回双塔模型、粗排三塔模型、精排模型。
参考⽂献：
• Covington, Adams, and Sargin. Deep neural networks for YouTube
recommendations. In ACM Conference on Recommender Systems, 2016.

## Page 5

小红书的实践
⋯
平均
点击的LastN
Embedding

## Page 6

小红书的实践
⋯
平均
点击的LastN
⋯
点赞的LastN
⋯
平均
收藏的LastN
⋯
Embedding

## Page 7

Thank You!
http://wangshusen.github.io/

### DIN 模型

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

### SIM 模型

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
