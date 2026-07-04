# Section 2: 召回

## Why This Section Matters

- Learn retrieval as a multi-channel system instead of a single model.
- Understand the tradeoffs among ItemCF, Swing, UserCF, two-tower, and Deep Retrieval.
- Understand why online indexing and filtering are part of retrieval design.

## Common Mistakes To Avoid

- Do not assume a single retrieval model is enough.
- Do not forget hard negatives, ANN indexing, and exposure filtering.

## Topic List

- Official #4: `基于物品的协同过滤（ItemCF）` (user playlist part `4`)
- Official #5: `Swing模型` (user playlist part `5`)
- Official #6: `基于用户的协同过滤（UserCF）` (user playlist part `6`)
- Official #7: `离散特征处理` (user playlist part `7`)
- Official #8: `矩阵补充` (user playlist part `8`)
- Official #9: `双塔模型：模型和训练` (user playlist part `9`)
- Official #10: `双塔模型：正负样本` (user playlist part `10`)
- Official #11: `双塔模型：线上服务` (user playlist part `11`)
- Official #12: `双塔模型+自监督学习` (user playlist part `12`)
- Official #13: `Deep Retrieval 召回` (user playlist part `13`)
- Official #14: `其它召回通道` (user playlist part `14`)
- Official #15: `曝光过滤` (user playlist part `15`)

## Official Source Links

- `基于物品的协同过滤（ItemCF）`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_01.pdf`, Bilibili `https://www.bilibili.com/video/BV1mA4y1Q7RN`, YouTube `https://youtu.be/QtmunNLeDvo`
- `Swing模型`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_02.pdf`, Bilibili `https://www.bilibili.com/video/BV1DA4y1Q7rB`, YouTube `https://youtu.be/DUUMNTDuJ3Q`
- `基于用户的协同过滤（UserCF）`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_03.pdf`, Bilibili `https://www.bilibili.com/video/BV1HY4y1Y7P1`, YouTube `https://youtu.be/7O9zFMNdrZ8`
- `离散特征处理`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_04.pdf`, Bilibili `https://www.bilibili.com/video/BV1pS4y1a7QT`, YouTube `https://youtu.be/Wiqfn0BIcJs`
- `矩阵补充`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_05.pdf`, Bilibili `https://www.bilibili.com/video/BV1b34y1e7En`, YouTube `https://youtu.be/phpIjr8_C7g`
- `双塔模型：模型和训练`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_06.pdf`, Bilibili `https://www.bilibili.com/video/BV1YA4y1D75Q`, YouTube `https://youtu.be/2Mc10LZ-DB0`
- `双塔模型：正负样本`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_07.pdf`, Bilibili `https://www.bilibili.com/video/BV133411T7ue`, YouTube `https://youtu.be/KOpl2cJyKOg`
- `双塔模型：线上服务`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_08.pdf`, Bilibili `https://www.bilibili.com/video/BV1KY4y1h73Y`, YouTube `https://youtu.be/3qOvHfW1A-8`
- `双塔模型+自监督学习`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_09.pdf`, Bilibili `https://www.bilibili.com/video/BV1v24y1B7JH`, YouTube `https://youtu.be/Ra3MVhneR9E`
- `Deep Retrieval 召回`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_10.pdf`, Bilibili `https://www.bilibili.com/video/BV1Fu4y1b7PL`, YouTube `https://youtu.be/BYtzZ48hRFM`
- `其它召回通道`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_11.pdf`, Bilibili `https://www.bilibili.com/video/BV1m5411R7nd`, YouTube `https://youtu.be/7CKBjx7bw7k`
- `曝光过滤`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_12.pdf`, Bilibili `https://www.bilibili.com/video/BV1sa4y137LF`, YouTube `https://youtu.be/cM76ZbkqrFU`

## Local Extract Files

- `基于物品的协同过滤（ItemCF）`: `../source-extracts/slides/02_Retrieval_01.md`
- `Swing模型`: `../source-extracts/slides/02_Retrieval_02.md`
- `基于用户的协同过滤（UserCF）`: `../source-extracts/slides/02_Retrieval_03.md`
- `离散特征处理`: `../source-extracts/slides/02_Retrieval_04.md`
- `矩阵补充`: `../source-extracts/slides/02_Retrieval_05.md`
- `双塔模型：模型和训练`: `../source-extracts/slides/02_Retrieval_06.md`
- `双塔模型：正负样本`: `../source-extracts/slides/02_Retrieval_07.md`
- `双塔模型：线上服务`: `../source-extracts/slides/02_Retrieval_08.md`
- `双塔模型+自监督学习`: `../source-extracts/slides/02_Retrieval_09.md`
- `Deep Retrieval 召回`: `../source-extracts/slides/02_Retrieval_10.md`
- `其它召回通道`: `../source-extracts/slides/02_Retrieval_11.md`
- `曝光过滤`: `../source-extracts/slides/02_Retrieval_12.md`
## Inline Slide Extracts

### 基于物品的协同过滤（ItemCF）

# 基于物品的协同过滤（ItemCF）

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_01.pdf

## Page 1

基于物品的协同过滤（ItemCF）
王树森
http://wangshusen.github.io/
ShusenWang@xiaohongshu.com

## Page 2

ItemCF的原理
我喜欢看《笑傲江湖》
《笑傲江湖》与《鹿鼎记》相似
我没看过《鹿鼎记》
给我推荐《鹿鼎记》

## Page 3

ItemCF的原理
推荐系统如何知道《笑傲江湖》与《鹿鼎记》相似？
• 看过《笑傲江湖》的⽤户也看过《⿅⿍记》。
• 给《笑傲江湖》好评的⽤户也给《⿅⿍记》好评。
我喜欢看《笑傲江湖》
我没看过《鹿鼎记》
给我推荐《鹿鼎记》
《笑傲江湖》与《鹿鼎记》相似

## Page 4

ItemCF 的实现

## Page 5

User
用户对物品的兴趣：
𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟, 𝑖𝑡𝑒𝑚!
用户交互过的物品

## Page 6

User
用户对物品的兴趣：
𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟, 𝑖𝑡𝑒𝑚!
用户交互过的物品
Item

## Page 7

User
用户对物品的兴趣：
𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟, 𝑖𝑡𝑒𝑚!
物品之间的相似度：
𝑠𝑖𝑚𝑖𝑡𝑒𝑚!, 𝑖𝑡𝑒𝑚
0.1
0.4
0.2
0.6
用户交互过的物品
Item

## Page 8

User
用户对物品的兴趣：
𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟, 𝑖𝑡𝑒𝑚!
物品之间的相似度：
𝑠𝑖𝑚𝑖𝑡𝑒𝑚!, 𝑖𝑡𝑒𝑚
0.1
0.4
0.2
0.6
预估⽤户对候选物品的兴趣：∑! 𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟, 𝑖𝑡𝑒𝑚! × 𝑠𝑖𝑚𝑖𝑡𝑒𝑚!, 𝑖𝑡𝑒𝑚
Item

## Page 9

预估⽤户对候选物品的兴趣：2×0.1 + 1×0.4 + 4×0.2 + 3×0.6 = 3.2
User
用户对物品的兴趣：
𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟, 𝑖𝑡𝑒𝑚!
物品之间的相似度：
𝑠𝑖𝑚𝑖𝑡𝑒𝑚!, 𝑖𝑡𝑒𝑚
0.1
0.4
0.2
0.6
Item

## Page 10

物品的相似度

## Page 11

物品相似度
• 两个物品的受众重合度越⾼，两个物品越相似。
• 例如：
• 喜欢《射雕英雄传》和《神雕侠侣》的读者重合度很⾼。
• 可以认为《射雕英雄传》和《神雕侠侣》相似。

## Page 12

两个物品不相似

## Page 13

两个物品相似

## Page 14

计算物品相似度
• 喜欢物品𝑖+ 的⽤户记作集合𝒲+。
• 喜欢物品𝑖, 的⽤户记作集合𝒲,。
• 定义交集𝒱= 𝒲+ ∩𝒲,。

## Page 15

计算物品相似度
• 喜欢物品𝑖+ 的⽤户记作集合𝒲+。
• 喜欢物品𝑖, 的⽤户记作集合𝒲,。
• 定义交集𝒱= 𝒲+ ∩𝒲,。
• 两个物品的相似度：
𝑠𝑖𝑚𝑖+, 𝑖,
=
𝒱
𝒲+
⋅𝒲,
.
注：公式没有考虑喜欢的程度𝑙𝑖𝑘𝑒(𝑢𝑠𝑒𝑟, 𝑖𝑡𝑒𝑚)

## Page 16

计算物品相似度
• 喜欢物品𝑖+ 的⽤户记作集合𝒲+。
• 喜欢物品𝑖, 的⽤户记作集合𝒲,。
• 定义交集𝒱= 𝒲+ ∩𝒲,。
• 两个物品的相似度：
𝑠𝑖𝑚𝑖+, 𝑖,
=
∑-∈𝒱𝑙𝑖𝑘𝑒𝑣, 𝑖+ ⋅𝑙𝑖𝑘𝑒𝑣, 𝑖,
∑0!∈𝒲! 𝑙𝑖𝑘𝑒, 𝑢+, 𝑖+
⋅
∑0"∈𝒲" 𝑙𝑖𝑘𝑒, 𝑢,, 𝑖,
.
余弦相似度（cosine similarity）

## Page 17

小结
• ItemCF 的基本思想：
• 如果⽤户喜欢物品𝑖𝑡𝑒𝑚"，⽽且物品𝑖𝑡𝑒𝑚" 与𝑖𝑡𝑒𝑚# 相似，
• 那么⽤户很可能喜欢物品𝑖𝑡𝑒𝑚#。

## Page 18

小结
• ItemCF 的基本思想：
• 如果⽤户喜欢物品𝑖𝑡𝑒𝑚"，⽽且物品𝑖𝑡𝑒𝑚" 与𝑖𝑡𝑒𝑚# 相似，
• 那么⽤户很可能喜欢物品𝑖𝑡𝑒𝑚#。
• 预估⽤户对候选物品的兴趣：
∑2 𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟, 𝑖𝑡𝑒𝑚2 × 𝑠𝑖𝑚𝑖𝑡𝑒𝑚2, 𝑖𝑡𝑒𝑚.

## Page 19

小结
• ItemCF 的基本思想：
• 如果⽤户喜欢物品𝑖𝑡𝑒𝑚"，⽽且物品𝑖𝑡𝑒𝑚" 与𝑖𝑡𝑒𝑚# 相似，
• 那么⽤户很可能喜欢物品𝑖𝑡𝑒𝑚#。
• 预估⽤户对候选物品的兴趣：
∑2 𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟, 𝑖𝑡𝑒𝑚2 × 𝑠𝑖𝑚𝑖𝑡𝑒𝑚2, 𝑖𝑡𝑒𝑚.
• 计算两个物品的相似度：
• 把每个物品表⽰为⼀个稀疏向量，向量每个元素对应⼀个⽤户。
• 相似度𝑠𝑖𝑚就是两个向量夹⾓的余弦。

## Page 20

ItemCF 召回的完整流程

## Page 21

事先做离线计算
• 记录每个⽤户最近点击、交互过的物品ID。
• 给定任意⽤户ID，可以找到他近期感兴趣的物品列表。
建⽴“⽤户à 物品”的索引

## Page 22

事先做离线计算
• 记录每个⽤户最近点击、交互过的物品ID。
• 给定任意⽤户ID，可以找到他近期感兴趣的物品列表。
建⽴“⽤户à 物品”的索引
建⽴“物品à物品”的索引
• 计算物品之间两两相似度。
• 对于每个物品，索引它最相似的k 个物品。
• 给定任意物品ID，可以快速找到它最相似的k 个物品。

## Page 23

“用户à物品”的索引
⋮
⽤户：

## Page 24

“用户à物品”的索引
, 2
, 1
, 4
, 3
⋯
, 3
, 1
⋯
⋮
⽤户：
(物品ID，兴趣分数）的列表：

## Page 25

“物品à物品”的索引
⋮
物品：

## Page 26

“物品à物品”的索引
, 0.7
, 0.6
, 0.3
, 0.9
, 0.6
, 0.5
, 0.4
⋮
物品：
最相似的𝑘个物品的（ID，相似度）：

## Page 27

线上做召回
1. 给定⽤户ID，通过“⽤户à物品”索引，找到⽤户近期感
兴趣的物品列表（last-n）。
2. 对于last-n列表中每个物品，通过“物品à物品”的索引，
找到top-k 相似物品。

## Page 28

线上做召回
1. 给定⽤户ID，通过“⽤户à物品”索引，找到⽤户近期感
兴趣的物品列表（last-n）。
2. 对于last-n列表中每个物品，通过“物品à物品”的索引，
找到top-k 相似物品。
3. 对于取回的相似物品（最多有𝑛𝑘个），⽤公式预估⽤户
对物品的兴趣分数。
4. 返回分数最⾼的100个物品，作为推荐结果。

## Page 29

线上做召回
1. 记录⽤户最近感兴趣的𝑛= 200 个物品。
2. 取回每个物品最相似的𝑘= 10 个物品。
3. 给取回的𝑛𝑘= 2000 个物品打分（⽤户对物品的兴趣）。
4. 返回分数最⾼的100 个物品作为ItemCF 通道的输出。
索引的意义在于避免枚举所有的物品。
⽤索引，离线计算量⼤，线上计算量⼩。

## Page 30

线上做召回
, 2
, 1
, 4
, 3
⋯
⽤户感兴趣的物品（ID，兴趣分数）

## Page 31

线上做召回
, 2
, 1
, 4
, 3
⋯
, 0.7
, 0.6
⽤户感兴趣的物品（ID，兴趣分数）
Top-k 相似

## Page 32

线上做召回
, 2
, 1
, 4
, 3
⋯
, 0.7
, 0.6
, 0.8
, 0.5
, 0.6
, 0.9
, 0.4
⋮
⽤户感兴趣的物品（ID，兴趣分数）
Top-k 相似

## Page 33

线上做召回
, 2
, 1
, 4
, 3
⋯
, 0.7
, 0.6
, 0.8
, 0.5
, 0.6
, 0.9
, 0.4
⋮
⽤户感兴趣的物品（ID，兴趣分数）
Top-k 相似

## Page 34

线上做召回
, 2
, 1
, 4
, 3
⋯
, 0.7
, 0.6
, 0.8
, 0.5
, 0.6
, 0.9
, 0.4
⋮
⽤户感兴趣的物品（ID，兴趣分数）
Top-k 相似

## Page 35

总结

## Page 36

ItemCF的原理
• ⽤户喜欢物品𝑖+，那么⽤户喜欢与物品𝑖+ 相似的物
品𝑖,。
• 物品相似度：
• 如果喜欢𝑖"、𝑖#的⽤户有很⼤的重叠，那么𝑖"与𝑖# 相似。
• 公式：𝑠𝑖𝑚𝑖", 𝑖#
=
𝒲! ∩𝒲"
𝒲! ⋅𝒲" 。

## Page 37

ItemCF召回通道
• 维护两个索引：
• ⽤户à物品列表：⽤户最近交互过的𝑛个物品。
• 物品à物品列表：相似度最⾼的𝑘个物品。
• 线上做召回：
• 利⽤两个索引，每次取回𝑛𝑘个物品。
• 预估⽤户对每个物品的兴趣分数：
∑! 𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟, 𝑖𝑡𝑒𝑚! × 𝑠𝑖𝑚𝑖𝑡𝑒𝑚!, 𝑖𝑡𝑒𝑚.
• 返回分数最⾼的100个物品，作为召回结果。

## Page 38

长期招聘优秀的算法工程师
• 部门：⼩红书社区技术部。
• ⽅向：搜索、推荐。
• 职位：校招、社招、实习。
• 地点：上海、北京。
• 联系⽅式：ShusenWang@xiaohongshu.com

## Page 39

Thank You!
http://wangshusen.github.io/

### Swing模型

# Swing模型

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_02.pdf

## Page 1

Swing召回通道
王树森
http://wangshusen.github.io/
ShusenWang@xiaohongshu.com

## Page 2

ItemCF的原理
• 物品相似度：如果喜欢𝑖!、𝑖"的⽤户有很⼤的重叠，
那么𝑖!与𝑖" 相似。
• ⽤户喜欢物品𝑖!
• 物品𝑖!与𝑖" 相似
⽤户很可能喜欢物品𝑖"

## Page 3

ItemCF的物品相似度
• 喜欢物品𝑖! 的⽤户记作集合𝒲!。
• 喜欢物品𝑖" 的⽤户记作集合𝒲"。
• 定义交集𝒱= 𝒲! ∩𝒲"。

## Page 4

ItemCF的物品相似度
• 喜欢物品𝑖! 的⽤户记作集合𝒲!。
• 喜欢物品𝑖" 的⽤户记作集合𝒲"。
• 定义交集𝒱= 𝒲! ∩𝒲"。
• 两个物品的相似度：
𝑠𝑖𝑚𝑖!, 𝑖"
=
𝒱
𝒲!
⋅𝒲"
.

## Page 5

ItemCF的物品相似度

## Page 6

ItemCF的物品相似度
交集𝒱= 𝒲! ∩𝒲"
骂川普
⽀持绿⾊能源

## Page 7

假如重合的用户是一个小圈子⋯⋯
某个微信群
《某⽹站护肤品打折》
《字节裁员了》

## Page 8

Swing模型
• ⽤户𝑢! 喜欢的物品记作集合𝒥!。
• ⽤户𝑢" 喜欢的物品记作集合𝒥"。
• 定义两个⽤户的重合度：
overlap 𝑢!, 𝑢" =
𝒥! ∩𝒥" 。
• ⽤户𝑢! 和𝑢" 的重合度⾼，则他们可能来⾃⼀个⼩
圈⼦，要降低他们的权重。

## Page 9

Swing模型
• 喜欢物品𝑖! 的⽤户记作集合𝒲!。
• 喜欢物品𝑖" 的⽤户记作集合𝒲"。
• 定义交集𝒱= 𝒲! ∩𝒲"。
• 两个物品的相似度：
𝑠𝑖𝑚𝑖!, 𝑖"
= 4
#!∈𝒱
#"∈𝒱
𝛼+ overlap 𝑢!, 𝑢"
.

## Page 10

总结
• Swing 与ItemCF 唯⼀的区别在于物品相似度。
• ItemCF：两个物品重合的⽤户⽐例⾼，则判定两个
物品相似。
• Swing：额外考虑重合的⽤户是否来⾃⼀个⼩圈⼦。
• 同时喜欢两个物品的⽤户记作集合𝒱。
• 对于𝒱中的⽤户𝑢! 和𝑢"，重合度记作overlap 𝑢!, 𝑢" 。
• 两个⽤户重合度⼤，则可能来⾃⼀个⼩圈⼦，权重降低。

## Page 11

长期招聘优秀的算法工程师
• 部门：⼩红书社区技术部。
• ⽅向：搜索、推荐。
• 职位：校招、社招、实习。
• 地点：上海、北京。
• 联系⽅式：ShusenWang@xiaohongshu.com

## Page 12

Thank You!
http://wangshusen.github.io/

### 基于用户的协同过滤（UserCF）

# 基于用户的协同过滤（UserCF）

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_03.pdf

## Page 1

基于用户的协同过滤（UserCF）
王树森
http://wangshusen.github.io/
ShusenWang@xiaohongshu.com

## Page 2

UserCF的原理
有很多跟我兴趣非常相似的网友
其中某个网友对某笔记点赞、转发
我没看过这篇笔记
给我推荐这篇笔记

## Page 3

UserCF的原理
推荐系统如何找到跟我兴趣非常相似的网友呢？
• ⽅法⼀：点击、点赞、收藏、转发的笔记有很⼤的重合。
• ⽅法⼆：关注的作者有很⼤的重合。
有很多跟我兴趣非常相似的网友
其中某个网友对某笔记点赞、转发
我没看过这篇笔记
给我推荐这篇笔记

## Page 4

UserCF 的实现

## Page 5

兴趣相似的用户
User
0.9
0.7
0.4
用户之间的相似度：
𝑠𝑖𝑚𝑢𝑠𝑒𝑟, 𝑢𝑠𝑒𝑟!

## Page 6

User
兴趣相似的用户
Item
0.9
0.7
0.4
用户之间的相似度：
𝑠𝑖𝑚𝑢𝑠𝑒𝑟, 𝑢𝑠𝑒𝑟!
用户对物品的兴趣：
𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟!, 𝑖𝑡𝑒𝑚

## Page 7

User
Item
0.9
0.7
0.4
用户之间的相似度：
𝑠𝑖𝑚𝑢𝑠𝑒𝑟, 𝑢𝑠𝑒𝑟!
用户对物品的兴趣：
𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟!, 𝑖𝑡𝑒𝑚
预估⽤户对候选物品的兴趣：∑! 𝑠𝑖𝑚𝑢𝑠𝑒𝑟, 𝑢𝑠𝑒𝑟!
× 𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟!, 𝑖𝑡𝑒𝑚

## Page 8

预估⽤户对候选物品的兴趣：0.9×0 + 0.7×1 + 0.7×3 + 0.4×0 = 2.8
User
Item
0.9
0.7
0.4
用户之间的相似度：
𝑠𝑖𝑚𝑢𝑠𝑒𝑟, 𝑢𝑠𝑒𝑟!
用户对物品的兴趣：
𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟!, 𝑖𝑡𝑒𝑚
预估⽤户对候选物品的兴趣：0.9×0 + 0.7×1 + 0.7×3 + 0.4×0 = 2.8

## Page 9

⽤户的相似度

## Page 10

两个用户不相似

## Page 11

两个用户相似

## Page 12

计算用户相似度
• ⽤户𝑢+ 喜欢的物品记作集合𝒥+。
• ⽤户𝑢, 喜欢的物品记作集合𝒥, 。
• 定义交集𝐼= 𝒥+ ∩𝒥,。

## Page 13

计算用户相似度
• ⽤户𝑢+ 喜欢的物品记作集合𝒥+。
• ⽤户𝑢, 喜欢的物品记作集合𝒥, 。
• 定义交集𝐼= 𝒥+ ∩𝒥,。
• 两个⽤户的相似度：
𝑠𝑖𝑚𝑢+, 𝑢,
=
𝐼
𝒥+
⋅𝒥,
.

## Page 14

降低热门物品权重

## Page 15

降低热门物品权重
• ⽤户𝑢+ 喜欢的物品记作集合𝒥+。
• ⽤户𝑢, 喜欢的物品记作集合𝒥, 。
• 定义交集𝐼= 𝒥+ ∩𝒥,。
• 两个⽤户的相似度：
𝑠𝑖𝑚𝑢+, 𝑢,
=
∑-∈/ 1
𝒥+
⋅𝒥,
.
不论冷门、热门，
物品权重都是1。
= 𝐼

## Page 16

降低热门物品权重
• ⽤户𝑢+ 喜欢的物品记作集合𝒥+。
• ⽤户𝑢, 喜欢的物品记作集合𝒥, 。
• 定义交集𝐼= 𝒥+ ∩𝒥,。
• 两个⽤户的相似度：
𝑠𝑖𝑚𝑢+, 𝑢,
=
∑-∈/
log 1 + 𝑛-
𝒥+
⋅𝒥,
.
𝑛- ：喜欢物品𝑙的用户数量，反映物品的热门程度

## Page 17

小结
• UserCF 的基本思想：
• 如果⽤户𝑢𝑠𝑒𝑟" 跟⽤户𝑢𝑠𝑒𝑟# 相似，⽽且𝑢𝑠𝑒𝑟# 喜欢某物品，
• 那么⽤户𝑢𝑠𝑒𝑟" 也很可能喜欢该物品。

## Page 18

小结
• UserCF 的基本思想：
• 如果⽤户𝑢𝑠𝑒𝑟" 跟⽤户𝑢𝑠𝑒𝑟# 相似，⽽且𝑢𝑠𝑒𝑟# 喜欢某物品，
• 那么⽤户𝑢𝑠𝑒𝑟" 也很可能喜欢该物品。
• 预估⽤户𝑢𝑠𝑒𝑟对候选物品𝑖𝑡𝑒𝑚的兴趣：
∑0 𝑠𝑖𝑚𝑢𝑠𝑒𝑟, 𝑢𝑠𝑒𝑟0 × 𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟0, 𝑖𝑡𝑒𝑚.

## Page 19

小结
• UserCF 的基本思想：
• 如果⽤户𝑢𝑠𝑒𝑟" 跟⽤户𝑢𝑠𝑒𝑟# 相似，⽽且𝑢𝑠𝑒𝑟# 喜欢某物品，
• 那么⽤户𝑢𝑠𝑒𝑟" 也很可能喜欢该物品。
• 预估⽤户𝑢𝑠𝑒𝑟对候选物品𝑖𝑡𝑒𝑚的兴趣：
∑0 𝑠𝑖𝑚𝑢𝑠𝑒𝑟, 𝑢𝑠𝑒𝑟0 × 𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟0, 𝑖𝑡𝑒𝑚.
• 计算两个⽤户的相似度：
• 把每个⽤户表⽰为⼀个稀疏向量，向量每个元素对应⼀个物品。
• 相似度𝑠𝑖𝑚就是两个向量夹⾓的余弦。

## Page 20

UserCF 召回的完整流程

## Page 21

事先做离线计算
• 记录每个⽤户最近点击、交互过的物品ID。
• 给定任意⽤户ID，可以找到他近期感兴趣的物品列表。
建⽴“⽤户à 物品”的索引

## Page 22

事先做离线计算
• 记录每个⽤户最近点击、交互过的物品ID。
• 给定任意⽤户ID，可以找到他近期感兴趣的物品列表。
建⽴“⽤户à 物品”的索引
建⽴“⽤户à⽤户”的索引
• 对于每个⽤户，索引他最相似的k 个⽤户。
• 给定任意⽤户ID，可以快速找到他最相似的k 个⽤户。

## Page 23

“用户à物品”的索引
, 2
, 1
, 4
, 3
⋯
, 3
, 1
⋯
⋮
⽤户：
(物品ID，兴趣分数）的列表：

## Page 24

“用户à用户”的索引
⋮
⽤户：

## Page 25

“用户à用户”的索引
, 0.7
, 0.6
, 0.3
, 0.9
, 0.6
, 0.5
, 0.4
⋮
⽤户：
最相似的𝑘个⽤户的（ID，相似度）：

## Page 26

线上做召回
1. 给定⽤户ID，通过“⽤户à⽤户”索引，找到top-k 相似
⽤户。
2. 对于每个top-k 相似⽤户，通过“⽤户à物品”索引，找
到⽤户近期感兴趣的物品列表（last-n）。

## Page 27

线上做召回
1. 给定⽤户ID，通过“⽤户à⽤户”索引，找到top-k 相似
⽤户。
2. 对于每个top-k 相似⽤户，通过“⽤户à物品”索引，找
到⽤户近期感兴趣的物品列表（last-n）。
3. 对于取回的𝑛𝑘个相似物品，⽤公式预估⽤户对每个物品
的兴趣分数。
4. 返回分数最⾼的100个物品，作为召回结果。

## Page 28

线上做召回
Top-k 相似的⽤户（ID，相似度）
, 0.7
, 0.6
, 0.3

## Page 29

线上做召回
, 1
, 3
Top-k 相似的⽤户（ID，相似度）
⽤户感兴趣
的n个物品
, 0.7
, 0.6
, 0.3

## Page 30

线上做召回
, 1
, 3
, 4
, 1
, 3
, 1
, 2
Top-k 相似的⽤户（ID，相似度）
⽤户感兴趣
的n个物品
, 0.7
, 0.6
, 0.3
, 4

## Page 31

线上做召回
, 1
, 3
, 4
, 1
, 3
, 1
, 2
Top-k 相似的⽤户（ID，相似度）
⽤户感兴趣
的n个物品
, 0.7
, 0.6
, 0.3
, 4

## Page 32

线上做召回
, 1
, 3
, 4
, 1
, 3
, 1
, 2
Top-k 相似的⽤户（ID，相似度）
⽤户感兴趣
的n个物品
, 0.7
, 0.6
, 0.3
, 4

## Page 33

总结

## Page 34

UserCF的原理
• ⽤户𝑢+ 跟⽤户𝑢, 相似，⽽且𝑢, 喜欢某物品，那么
𝑢+ 也可能喜欢该物品。
• ⽤户相似度：
• 如果⽤户𝑢" 和𝑢# 喜欢的物品有很⼤的重叠，那么𝑢" 和
𝑢# 相似。
• 公式：𝑠𝑖𝑚𝑢", 𝑢#
=
𝒥! ∩𝒥"
𝒥! ⋅𝒥" 。

## Page 35

UserCF召回通道
• 维护两个索引：
• ⽤户à物品列表：⽤户近期交互过的𝑛个物品。
• ⽤户à⽤户列表：相似度最⾼的𝑘个⽤户。
• 线上做召回：
• 利⽤两个索引，每次取回𝑛𝑘个物品。
• 预估⽤户user 对每个物品item 的兴趣分数：
∑! 𝑠𝑖𝑚𝑢𝑠𝑒𝑟, 𝑢𝑠𝑒𝑟!
× 𝑙𝑖𝑘𝑒𝑢𝑠𝑒𝑟!, 𝑖𝑡𝑒𝑚.
• 返回分数最⾼的100个物品，作为召回结果。

## Page 36

长期招聘优秀的算法工程师
• 部门：⼩红书社区技术部。
• ⽅向：搜索、推荐。
• 职位：校招、社招、实习。
• 地点：上海、北京。
• 联系⽅式：ShusenWang@xiaohongshu.com

## Page 37

Thank You!
http://wangshusen.github.io/

### 离散特征处理

# 离散特征处理

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_04.pdf

## Page 1

离散特征处理
王树森
http://wangshusen.github.io/
ShusenWang@xiaohongshu.com

## Page 2

离散特征
• 性别：男、⼥两种类别。
• 国籍：中国、美国、印度等200个国家。
• 英⽂单词：常见的英⽂单词有⼏万个。
• 物品ID：⼩红书有⼏亿篇笔记，每篇笔记有⼀个ID。
• ⽤户ID：⼩红书有⼏亿个⽤户，每个⽤户有⼀个ID。

## Page 3

离散特征处理
1. 建⽴字典：把类别映射成序号。
• 中国à 1
• 美国à 2
• 印度à 3
2. 向量化：把序号映射成向量。
• One-hot编码：把序号映射成⾼维稀疏向量。
• Embedding：把序号映射成低维稠密向量。

## Page 4

One-Hot 编码

## Page 5

例1：性别特征
• 性别：男、⼥两种类别。
• 字典：男à 1，⼥à 2。
• One-hot编码：⽤2 维向量表⽰性别。
• 未知à 0 à
0, 0
•
男
à 1 à
1, 0
•
⼥
à 2 à
0, 1

## Page 6

例2：国籍特征
• 国籍：中国、美国、印度等200 种类别。
• 字典：中国à 1，美国à 2，印度à 3，…
• One-hot编码：⽤200 维稀疏向量表⽰国籍。
• 未知à 0 à
0, 0, 0, 0, ⋯, 0
• 中国à 1 à
1, 0, 0, 0, ⋯, 0
• 美国à 2 à
0, 1, 0, 0, ⋯, 0
• 印度à 3 à
0, 0, 1, 0, ⋯, 0

## Page 7

One-Hot编码的局限
• 例1：⾃然语⾔处理中，对单词做编码。
• 英⽂有⼏万个常见单词。
• 那么one-hot向量的维度是⼏万。
• 例2：推荐系统中，对物品ID做编码。
• ⼩红书有⼏亿篇笔记。
• 那么one-hot向量的维度是⼏亿。
类别数量太⼤时，通常不⽤one-hot 编码。

## Page 8

Embedding（嵌⼊）

## Page 9

例1：国籍的Embedding
中国
美国
印度
⽇本
德国
⋯
冰岛
⋯
国籍：
序号：

## Page 10

⋯
例1：国籍的Embedding
中国
美国
印度
⽇本
德国
⋯
冰岛
⋯
国籍：
序号：
向量：

## Page 11

例1：国籍的Embedding
• 参数数量：向量维度× 类别数量。
• 设embedding 得到的向量都是4 维的。
• ⼀共有200 个国籍。
• 参数数量= 4 × 200 = 800。

## Page 12

例1：国籍的Embedding
• 参数数量：向量维度× 类别数量。
• 设embedding 得到的向量都是4 维的。
• ⼀共有200 个国籍。
• 参数数量= 4 × 200 = 800。
• 编程实现：TensorFlow、PyTorch 提供embedding 层。
• 参数以矩阵的形式保存，矩阵⼤⼩是向量维度× 类别数量。
• 输⼊是序号，⽐如“美国”的序号是2。
• 输出是向量，⽐如“美国”对应参数矩阵的第2 列。

## Page 13

例2：物品ID的Embedding
• 数据库⾥⼀共有10,000 部电影。
• 任务是给⽤户推荐电影。
• 设embedding 向量的维度是16。
Embedding 层有多少参数？
• 参数数量= 向量维度× 类别数量= 160,000

## Page 14

例2：物品ID的Embedding
狮⼦王
海底总动员
冰雪奇缘
疯狂动物城
⾓⽃⼠
天国王朝
特洛伊
碟中谍2
谍影重重3
王牌特⼯
雷神
绿巨⼈
钢铁侠
蝙蝠侠

## Page 15

=
×
Embedding = 参数矩阵× One-Hot向量

## Page 16

总结
• 离散特征处理：one-hot 编码、embedding。
• 类别数量很⼤时，⽤embedding。
• Word embedding。
• ⽤户ID embedding。
• 物品ID embedding。

## Page 17

Thank You!
http://wangshusen.github.io/

### 矩阵补充

# 矩阵补充

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_05.pdf

## Page 1

矩阵补充
(Matrix Completion)
王树森
http://wangshusen.github.io/

## Page 2

Embedding Layer
用户ID
Embedding Layer
物品ID
內积：𝐚, 𝐛
𝐚
𝐛

## Page 3

Embedding Layer
用户ID
Embedding Layer
物品ID
內积：𝐚, 𝐛
𝐚
𝐛

## Page 4

用户ID
Embedding Layer
物品ID
內积：𝐚, 𝐛
𝐚
𝐛
不共享
参数

## Page 5

训练

## Page 6

基本想法
• ⽤户embedding 参数矩阵记作𝐀。第𝑢号⽤户对应矩阵
第𝑢列，记作向量𝐚! 。
• 物品embedding 参数矩阵记作𝐁。第𝑖号物品对应矩阵
第𝑖列，记作向量𝐛" 。
𝐚!
𝐀
𝐛"
𝐁

## Page 7

基本想法
• ⽤户embedding 参数矩阵记作𝐀。第𝑢号⽤户对应矩阵
第𝑢列，记作向量𝐚! 。
• 物品embedding 参数矩阵记作𝐁。第𝑖号物品对应矩阵
第𝑖列，记作向量𝐛" 。
• 內积𝐚!, 𝐛" 是第𝑢号⽤户对第𝑖号物品兴趣的预估值。
• 训练模型的⽬的是学习矩阵𝐀和𝐁，使得预估值拟合真
实观测的兴趣分数。

## Page 8

数据集
• 数据集：（⽤户ID，物品ID，兴趣分数）的集合，记作
Ω =
𝑢, 𝑖, 𝑦
。
• 数据集中的兴趣分数是系统记录的，⽐如：
• 曝光但是没有点击à 0 分
• 点击、点赞、收藏、转发à 各算1 分
• 分数最低是0，最⾼是4。

## Page 9

训练
• 把⽤户ID、物品ID映射成向量。
• 第𝑢号⽤户à 向量𝐚!。
• 第𝑖号物品à 向量𝐛"。

## Page 10

训练
• 把⽤户ID、物品ID映射成向量。
• 第𝑢号⽤户à 向量𝐚!。
• 第𝑖号物品à 向量𝐛"。
• 求解优化问题，得到参数𝐀和𝐁。
min
𝐀,𝐁
∑!,",& ∈(
𝑦−
𝐚!, 𝐛"
) .

## Page 11

矩阵补充
每⾏对应
⼀个⽤户
每列对应⼀个物品

## Page 12

矩阵补充
每⾏对应
⼀个⽤户
每列对应⼀个物品
绿⾊位置表⽰曝光给⽤户的物品；灰⾊位置表⽰没有曝光。
第3号⽤户
对
第2号物品
的兴趣分数
等于4

## Page 13

在实践中效果不好⋯⋯
• 物品属性：类⽬、关键词、地理位置、作者信息。
• ⽤户属性：性别、年龄、地理定位、感兴趣的类⽬。
• 双塔模型可以看做矩阵补充的升级版。
缺点1：仅⽤ID embedding，没利⽤物品、⽤户属性。

## Page 14

在实践中效果不好⋯⋯
• 样本：⽤户—物品的⼆元组，记作𝑢, 𝑖。
• 正样本：曝光之后，有点击、交互。（正确的做法）
• 负样本：曝光之后，没有点击、交互。（错误的做法）
缺点1：仅⽤ID embedding，没利⽤物品、⽤户属性。
缺点2：负样本的选取⽅式不对。

## Page 15

在实践中效果不好⋯⋯
• 內积𝐚!, 𝐛" 不如余弦相似度。
• ⽤平⽅损失（回归），不如⽤交叉熵损失（分类）。
缺点1：仅⽤ID embedding，没利⽤物品、⽤户属性。
缺点2：负样本的选取⽅式不对。
缺点3：做训练的⽅法不好。

## Page 16

线上服务

## Page 17

模型存储
1. 训练得到矩阵𝐀和𝐁。
• 𝐀的每⼀列对应⼀个⽤户。
• 𝐁的每⼀列对应⼀个物品。

## Page 18

模型存储
1. 训练得到矩阵𝐀和𝐁。
• 𝐀的每⼀列对应⼀个⽤户。
• 𝐁的每⼀列对应⼀个物品。
2. 把矩阵𝐀的列存储到key-value 表。
• key 是⽤户ID，value 是𝐀的⼀列。
• 给定⽤户ID，返回⼀个向量（⽤户的embedding）。
3. 矩阵𝐁的存储和索引⽐较复杂。

## Page 19

线上服务
1. 把⽤户ID 作为key，查询key-value 表，得到该⽤户
的向量，记作𝐚。

## Page 20

线上服务
1. 把⽤户ID 作为key，查询key-value 表，得到该⽤户
的向量，记作𝐚。
2. 最近邻查找：查找⽤户最有可能感兴趣的k 个物品，
作为召回结果。
• 第𝑖号物品的embedding 向量记作𝐛"。
• 內积𝐚, 𝐛" 是⽤户对第𝑖号物品兴趣的预估。
• 返回內积最⼤的k 个物品。

## Page 21

线上服务
1. 把⽤户ID 作为key，查询key-value 表，得到该⽤户
的向量，记作𝐚。
2. 最近邻查找：查找⽤户最有可能感兴趣的k 个物品，
作为召回结果。
• 第𝑖号物品的embedding 向量记作𝐛"。
• 內积𝐚, 𝐛" 是⽤户对第𝑖号物品兴趣的预估。
• 返回內积最⼤的k 个物品。
如果枚举所有物品，时间复杂度正⽐于物品数量。

## Page 22

近似最近邻查找
(Approximate Nearest Neighbor Search)

## Page 23

支持最近邻查找的系统
• 系统：Milvus、Faiss、HnswLib、等等。
• 衡量最近邻的标准：
• 欧式距离最⼩（L2 距离）
• 向量內积最⼤（內积相似度）
• 向量夹⾓余弦最⼤（cosine相似度）

## Page 24

[No extractable text on this page]

## Page 25

𝐚

## Page 26

[No extractable text on this page]

## Page 27

[No extractable text on this page]

## Page 28

𝐚

## Page 29

𝐚

## Page 30

𝐚

## Page 31

总结

## Page 32

矩阵补充
• 把物品ID、⽤户ID做embedding，映射成向量。
• 两个向量的內积𝐚!, 𝐛" 作为⽤户𝑢对物品𝑖兴趣
的预估。
• 让𝐚!, 𝐛" 拟合真实观测的兴趣分数，学习模型的
embedding 层参数。
• 矩阵补充模型有很多缺点，效果不好。

## Page 33

线上召回
• 把⽤户向量𝐚作为query，查找使得𝐚, 𝐛" 最⼤化
的物品𝑖。
• 暴⼒枚举速度太慢。实践中⽤近似最近邻查找。
• Milvus、Faiss、HnswLib 等向量数据库⽀持近似
最近邻查找。

## Page 34

Thank You!
http://wangshusen.github.io/

### 双塔模型：模型和训练

# 双塔模型：模型和训练

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_06.pdf

## Page 1

双塔模型：模型和训练
王树森
http://wangshusen.github.io/
ShusenWang@xiaohongshu.com

## Page 2

Embedding Layer
用户ID
Embedding Layer
物品ID
內积：𝐚, 𝐛
𝐚
𝐛
矩阵补充模型

## Page 3

双塔模型

## Page 4

Embedding Layer
用户ID
Embedding Layers
用户离散特征
归一化、分桶等处理
用户连续特征

## Page 5

Embedding Layer
用户ID
Embedding Layers
用户离散特征
归一化、分桶等处理
用户连续特征
神经网络
Concatenate
用户的表征

## Page 6

Embedding Layer
物品ID
Embedding Layers
物品离散特征
归一化、分桶等处理
物品连续特征
Concatenate
物品的表征
神经网络

## Page 7

双塔模型
用户ID、离散特征、连续特征
特征变换
神经网络
物品ID、离散特征、连续特征
特征变换
神经网络
內积：𝐚, 𝐛
𝐚
𝐛

## Page 8

双塔模型
用户ID、离散特征、连续特征
物品ID、离散特征、连续特征
余弦相似度：cos 𝐚, 𝐛=
𝐚, 𝐛
𝐚𝟐⋅
𝐛𝟐
𝐚
𝐛
特征变换
神经网络
特征变换
神经网络

## Page 9

双塔模型的训练
• Pointwise：独⽴看待每个正样本、负样本，做简单的
⼆元分类。
• Pairwise：每次取⼀个正样本、⼀个负样本[1]。
• Listwise：每次取⼀个正样本、多个负样本[2]。
参考⽂献：
1.
Jui-Ting Huang et al. Embedding-based Retrieval in Facebook Search. In KDD, 2020.
2.
Xinyang Yi et al. Sampling-Bias-Corrected Neural Modeling for Large Corpus Item
Recommendations. In RecSys, 2019.

## Page 10

正负样本的选择
• 正样本：⽤户点击的物品。
• 负样本[1, 2] ：
• 没有被召回的？
• 召回但是被粗排、精排淘汰的？
• 曝光但是未点击的？
参考⽂献：
1.
Jui-Ting Huang et al. Embedding-based Retrieval in Facebook Search. In KDD, 2020.
2.
Xinyang Yi et al. Sampling-Bias-Corrected Neural Modeling for Large Corpus Item
Recommendations. In RecSys, 2019.

## Page 11

Pointwise训练

## Page 12

Pointwise训练
• 把召回看做⼆元分类任务。
• 对于正样本，⿎励cos 𝐚, 𝐛接近+1。
• 对于负样本，⿎励cos 𝐚, 𝐛接近−1。
• 控制正负样本数量为1: 2 或者1: 3。

## Page 13

Pairwise训练

## Page 14

Pairwise训练
𝐚
𝐛!
用户
物品负样本
物品正样本
𝐛"
特征变换
神经网络

## Page 15

Pairwise训练
用户
物品负样本
𝐚
𝐛!
特征变换
神经网络
特征变换
神经网络
物品正样本
𝐛"
特征变换
神经网络
共享
参数

## Page 16

Pairwise训练
用户
物品负样本
cos 𝐚, 𝐛%
𝐚
𝐛!
特征变换
神经网络
特征变换
神经网络
物品正样本
𝐛"
特征变换
神经网络
cos 𝐚, 𝐛&

## Page 17

Pairwise训练
基本想法：⿎励cos 𝐚, 𝐛! ⼤于cos 𝐚, 𝐛"
• 如果cos 𝐚, 𝐛! ⼤于cos 𝐚, 𝐛" + 𝑚，则没有损失。
• 否则，损失等于cos 𝐚, 𝐛" + 𝑚−cos 𝐚, 𝐛! 。

## Page 18

Pairwise训练
基本想法：⿎励cos 𝐚, 𝐛! ⼤于cos 𝐚, 𝐛"
• 如果cos 𝐚, 𝐛! ⼤于cos 𝐚, 𝐛" + 𝑚，则没有损失。
• 否则，损失等于cos 𝐚, 𝐛" + 𝑚−cos 𝐚, 𝐛! 。
Triplet hinge loss:
𝐿𝐚, 𝐛!, 𝐛"
= max 0, cos 𝐚, 𝐛" + 𝑚−cos 𝐚, 𝐛!
.

## Page 19

Pairwise训练
基本想法：⿎励cos 𝐚, 𝐛! ⼤于cos 𝐚, 𝐛"
Triplet hinge loss:
𝐿𝐚, 𝐛!, 𝐛"
= max 0, cos 𝐚, 𝐛" + 𝑚−cos 𝐚, 𝐛!
.

## Page 20

Pairwise训练
基本想法：⿎励cos 𝐚, 𝐛! ⼤于cos 𝐚, 𝐛"
Triplet logistic loss:
𝐿𝐚, 𝐛!, 𝐛"
= log 1 + exp 𝜎⋅
cos 𝐚, 𝐛" −cos 𝐚, 𝐛!
.
Triplet hinge loss:
𝐿𝐚, 𝐛!, 𝐛"
= max 0, cos 𝐚, 𝐛" + 𝑚−cos 𝐚, 𝐛!
.

## Page 21

Listwise训练

## Page 22

Listwise训练
• ⼀条数据包含：
• ⼀个⽤户，特征向量记作𝐚。
• ⼀个正样本，特征向量记作𝐛&。
• 多个负样本，特征向量记作𝐛'
%, ⋯, 𝐛(%。
• ⿎励cos 𝐚, 𝐛! 尽量⼤。
• ⿎励cos 𝐚, 𝐛#
" , ⋯, cos 𝐚, 𝐛$
" 尽量⼩。

## Page 23

Listwise训练
cos 𝐚, 𝐛&
cos 𝐚, 𝐛'%
cos 𝐚, 𝐛(%
⋯
正样本
负样本
Softmax激活函数
𝑠!
⋯
𝑠#
"
𝑠$"

## Page 24

Listwise训练
cos 𝐚, 𝐛&
cos 𝐚, 𝐛'%
cos 𝐚, 𝐛(%
⋯
正样本
负样本
Softmax激活函数
𝑦& = 1
⋯
𝑦'
% = ⋯= 𝑦(% = 0
CrossEntropyLoss 𝐲, 𝐬
𝑠!
⋯
𝑠#
"
𝑠$"
正样本
负样本
= −log 𝑠!

## Page 25

总结

## Page 26

双塔模型
• ⽤户塔、物品塔各输出⼀个向量。
• 两个向量的余弦相似度作为兴趣的预估值。
• 三种训练⽅式：
• Pointwise：每次⽤⼀个⽤户、⼀个物品（可正可负）。
• Pairwise：每次⽤⼀个⽤户、⼀个正样本、⼀个负样本。
• Listwise：每次⽤⼀个⽤户、⼀个正样本、多个负样本。

## Page 27

不适用于召回的模型
神经网络
Concatenate
预估用户对物品的兴趣
用户ID、离散特征、连续特征
物品ID、离散特征、连续特征
𝐚
𝐛
特征变换

## Page 28

不适用于召回的模型
Concatenate
用户ID、离散特征、连续特征
物品ID、离散特征、连续特征
𝐚
𝐛
特征变换
神经网络
预估用户对物品的兴趣

## Page 29

不适用于召回的模型
Concatenate
用户ID、离散特征、连续特征
物品ID、离散特征、连续特征
𝐚
𝐛
特征变换
神经网络
预估用户对物品的兴趣

## Page 30

Thank You!
http://wangshusen.github.io/

## Page 31

长期招聘优秀的算法工程师
• 部门：⼩红书社区技术部。
• ⽅向：搜索、推荐。
• 职位：校招、社招、实习。
• 地点：上海、北京。
• 联系⽅式：ShusenWang@xiaohongshu.com

### 双塔模型：正负样本

# 双塔模型：正负样本

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_07.pdf

## Page 1

双塔模型：正负样本
王树森
http://wangshusen.github.io/
ShusenWang@xiaohongshu.com

## Page 2

• 正样本：曝光⽽且有点击的⽤户—物品⼆元组。
（⽤户对物品感兴趣）。
• 问题：少部分物品占据⼤部分点击，导致正样本
⼤多是热门物品。
• 解决⽅案：过采样冷门物品，或降采样热门物品。
• 过采样（up-sampling）：⼀个样本出现多次。
• 降采样（down-sampling）：⼀些样本被抛弃。
正样本

## Page 3

推荐系统的链路
召回
粗排、精排
几千
物品
几百
物品
物品1
物品2
物品80
⋮
重排
几亿
物品

## Page 4

如何选择负样本？
召回
粗排、精排
几千
物品
几百
物品
物品1
物品2
物品80
⋮
重排
几亿
物品
没有
被召回
被召回，但是
没有选中和曝光
被曝光，但是
没有被用户点击
负样本

## Page 5

简单负样本

## Page 6

• 未被召回的物品，⼤概率是⽤户不感兴趣的。
• 未被召回的物品≈全体物品
• 从全体物品中做抽样，作为负样本。
• 均匀抽样or ⾮均匀抽样？
简单负样本：全体物品

## Page 7

• 正样本⼤多是热门物品。
• 如果均匀抽样产⽣负样本，负样本⼤多是冷门物品。
简单负样本：全体物品
• 负样本抽样概率与热门程度（点击次数）正相关。
• 抽样概率∝点击次数!.#$。
均匀抽样：对冷门物品不公平
⾮均抽采样：⽬的是打压热门物品

## Page 8

简单负样本：Batch内负样本
点击
⽤户:
⋮
物品:
⋮

## Page 9

简单负样本：Batch内负样本
⽤户:
物品:
⋮
点击
正样本

## Page 10

负样本
简单负样本：Batch内负样本
⽤户:
物品:
⋮
点击
• ⼀个batch 内有𝑛个正样本。
• ⼀个⽤户和𝑛−1 个物品组成
负样本。
• 这个batch 内⼀共有𝑛𝑛−1
个负样本。
• 都是简单负样本。（因为第⼀
个⽤户不喜欢第⼆个物品。）

## Page 11

简单负样本：Batch内负样本
⽤户:
物品:
⋮
点击
• ⼀个物品出现在batch 内的概
率∝点击次数。
• 物品成为负样本的概率本该是
∝点击次数!.#$，但在这⾥是
∝点击次数。
• 热门物品成为负样本的概率过
⼤。
参考⽂献：
•
Xinyang Yi et al. Sampling-Bias-Corrected Neural
Modeling for Large Corpus Item Recommendations.
In RecSys, 2019.

## Page 12

简单负样本：Batch内负样本
⽤户:
物品:
⋮
点击
• 物品𝑖被抽样到的概率：
𝑝% ∝点击次数
• 预估⽤户对物品𝑖的兴趣：
cos 𝐚, 𝐛%
• 做训练的时候，调整为：
cos 𝐚, 𝐛% −log 𝑝%
参考⽂献：
•
Xinyang Yi et al. Sampling-Bias-Corrected Neural
Modeling for Large Corpus Item Recommendations.
In RecSys, 2019.

## Page 13

困难负样本

## Page 14

• 困难负样本：
• 被粗排淘汰的物品（⽐较困难）。
• 精排分数靠后的物品（⾮常困难）。
• 对正负样本做⼆元分类：
• 全体物品（简单）分类准确率⾼。
• 被粗排淘汰的物品（⽐较困难）容易分错。
• 精排分数靠后的物品（⾮常困难）更容易分错。
困难负样本

## Page 15

• 混合⼏种负样本。
• 50%的负样本是全体物品（简单负样本）。
• 50%的负样本是没通过排序的物品（困难负
样本）。
训练数据

## Page 16

常见的错误

## Page 17

物品80
曝光但是没有点击
物品1
物品2
⋮
物品3
物品4
物品5
物品6
物品7
用户浏览
用户未浏览
用户点击
用户未点击
负样本?

## Page 18

曝光但是没有点击
物品1
物品2
物品30
⋮
物品3
物品4
物品5
物品6
物品7
用户浏览
用户未浏览
用户点击
用户未点击
训练召回模型不能⽤这类负样本
训练排序模型会⽤这类负样本
负样本?

## Page 19

• 全体物品（easy ）：绝⼤多数是⽤户根本不感兴趣的。
• 被排序淘汰（hard ）：⽤户可能感兴趣，但是不够感兴趣。
• 有曝光没点击（没⽤）：⽤户感兴趣，可能碰巧没有点击。
选择负样本的原理
召回的⽬标：快速找到⽤户可能感兴趣的物品。
可以作为排序的负样本，
不能作为召回的负样本

## Page 20

• 正样本：曝光⽽且有点击。
• 简单负样本：
• 全体物品。
• batch内负样本。
• 困难负样本：被召回，但是被排序淘汰。
• 错误：曝光、但是未点击的物品做召回的
负样本。
总结

## Page 21

Thank You!
http://wangshusen.github.io/

## Page 22

长期招聘优秀的算法工程师
• 部门：⼩红书社区技术部。
• ⽅向：搜索、推荐。
• 职位：校招、社招、实习。
• 地点：上海、北京。
• 联系⽅式：ShusenWang@xiaohongshu.com

### 双塔模型：线上服务

# 双塔模型：线上服务

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_08.pdf

## Page 1

双塔模型：线上召回和更新
王树森
http://wangshusen.github.io/
ShusenWang@xiaohongshu.com

## Page 2

线上召回

## Page 3

离线存储
用户ID、离散特征、连续特征
物品ID、离散特征、连续特征
𝐚
𝐛
特征变换
神经网络
特征变换
神经网络
把特征向量𝐛, 物品ID
保存到向量数据库

## Page 4

⋮
向量数据库
离线存储
用户ID、离散特征、连续特征
𝐚
特征变换
神经网络
ID=1
ID=2
ID=3
ID=4
ID=n

## Page 5

⋮
向量数据库
线上召回
用户ID、离散特征、连续特征
𝐚
特征变换
神经网络
给定⽤户ID和特征，
在线上计算向量𝐚。
ID=1
ID=2
ID=3
ID=4
ID=n

## Page 6

双塔模型的召回
离线存储：把物品向量𝐛存⼊向量数据库。
1. 完成训练之后，⽤物品塔计算每个物品的特征向量𝐛。
2. 把⼏亿个物品向量𝐛存⼊向量数据库（⽐如Milvus、
Faiss、HnswLib ）。
3. 向量数据库建索引，以便加速最近邻查找。

## Page 7

双塔模型的召回
1. 给定⽤户ID和画像，线上⽤神经⽹络算⽤户向量𝐚。
2. 最近邻查找：
• 把向量𝐚作为query，调⽤向量数据库做最近邻查找。
• 返回余弦相似度最⼤的k 个物品，作为召回结果。
线上召回：查找⽤户最感兴趣的k 个物品。
离线存储：把物品向量𝐛存⼊向量数据库。

## Page 8

双塔模型的召回
• 每做⼀次召回，⽤到⼀个⽤户向量𝐚，⼏亿物品向量𝐛。
（线上算物品向量的代价过⼤。）
• ⽤户兴趣动态变化，⽽物品特征相对稳定。（可以离线
存储⽤户向量，但不利于推荐效果。）
事先存储物品向量𝐛，线上现算⽤户向量𝐚，why？

## Page 9

模型更新

## Page 10

全量更新vs 增量更新
• 在昨天模型参数的基础上做训练。（不是随机初始化）
• ⽤昨天的数据，训练1 epoch，即每天数据只⽤⼀遍。
• 发布新的⽤户塔神经⽹络和物品向量，供线上召回使⽤。
• 全量更新对数据流、系统的要求⽐较低。
全量更新：今天凌晨，⽤昨天全天的数据训练模型。

## Page 11

全量更新vs 增量更新
• ⽤户兴趣会随时发⽣变化。
• 实时收集线上数据，做流式处理，⽣成TFRecord ⽂件。
• 对模型做online learning，增量更新ID Embedding 参数。
（不更新神经⽹络其他部分的参数。）
• 发布⽤户ID Embedding，供⽤户塔在线上计算⽤户向
量。
增量更新：做online learning 更新模型参数。

## Page 12

全量更新vs 增量更新
前天的数据
昨天凌晨
⋯
基于前天的全量模型，⽤
前天的数据，做全量更新。
做增量更新

## Page 13

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

## Page 14

全量更新vs 增量更新
前天的数据
昨天凌晨
⋯
昨天的数据
今天凌晨
⋯
问题：能否只做增量更新，不做全量更新？

## Page 15

全量更新vs 增量更新
问题：能否只做增量更新，不做全量更新？
• ⼩时级数据有偏；分钟级数据偏差更⼤。
• 全量更新：random shuffle ⼀天的数据，做1 epoch 训练。
• 增量更新：按照数据从早到晚的顺序，做1 epoch 训练。
• 随机打乱优于按顺序排列数据，全量训练优于增量训练。

## Page 16

总结

## Page 17

双塔模型
• ⽤户塔、物品塔各输出⼀个向量，两个向量的余弦
相似度作为兴趣的预估值。
• 三种训练的⽅式：pointwise、pairwise、listwise。
• 正样本：⽤户点击过的物品。
• 负样本：全体物品（简单）、被排序淘汰的物品
（困难）。

## Page 18

召回
• 做完训练，把物品向量存储到向量数据库，供线上
最近邻查找。
• 线上召回时，给定⽤户ID、⽤户画像，调⽤⽤户塔
现算⽤户向量𝐚。
• 把𝐚作为query，查询向量数据库，找到余弦相似度
最⾼的k 个物品向量，返回k 个物品ID。

## Page 19

更新模型
• 全量更新：今天凌晨，⽤昨天的数据训练整个神经
⽹络，做1 epoch 的随机梯度下降。
• 增量更新：⽤实时数据训练神经⽹络，只更新ID
Embedding，锁住全连接层。
• 实际的系统：
• 全量更新& 增量更新相结合。
• 每隔⼏⼗分钟，发布最新的⽤户ID Embedding，供⽤户
塔在线上计算⽤户向量。

## Page 20

Thank You!
http://wangshusen.github.io/

## Page 21

长期招聘优秀的算法工程师
• 部门：⼩红书社区技术部。
• ⽅向：搜索、推荐。
• 职位：校招、社招、实习。
• 地点：上海、北京。
• 联系⽅式：ShusenWang@xiaohongshu.com

### 双塔模型+自监督学习

# 双塔模型+自监督学习

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_09.pdf

## Page 1

双塔模型+自监督学习
王树森
http://wangshusen.github.io/
ShusenWang@xiaohongshu.com

## Page 2

双塔模型
用户特征
物品特征
余弦相似度：cos 𝐚, 𝐛=
𝐚, 𝐛
𝐚𝟐⋅
𝐛𝟐
𝐚
𝐛
用户塔
物品塔

## Page 3

双塔模型的问题
• 推荐系统的头部效应严重：
• 少部分物品占据⼤部分点击。
• ⼤部分物品的点击次数不⾼。
• ⾼点击物品的表征学得好，长尾物品的表征学得不好。
• ⾃监督学习：做data augmentation，更好地学习长尾
物品的向量表征。
参考⽂献：
•
Tiansheng Yao et al. Self-supervised Learning for Large-scale Item Recommendations.
In CIKM, 2021.

## Page 4

复习：双塔模型的训练

## Page 5

Batch内负样本
点击
⽤户:
⋮
物品:
⋮

## Page 6

Batch内负样本
⽤户:
物品:
⋮
点击
正样本

## Page 7

负样本
Batch内负样本
⽤户:
物品:
⋮
点击
• ⼀个batch 内有𝑛对正样本。
• 组成𝑛个list，每个list 中有1
对正样本和𝑛−1 对负样本。

## Page 8

Listwise训练
• ⼀个batch 包含𝑛对正样本（有点击）：
𝐚!, 𝐛! ,
𝐚", 𝐛" , ⋯,
𝐚#, 𝐛# .
• 负样本：
𝐚$, 𝐛%
，对于所有的𝑖≠𝑗。
• ⿎励cos 𝐚$, 𝐛$ 尽量⼤，cos 𝐚$, 𝐛% 尽量⼩。

## Page 9

向量𝐩$ ：
损失函数
Softmax激活函数
cos 𝐚%, 𝐛&
cos 𝐚%, 𝐛'
cos 𝐚%, 𝐛%
cos 𝐚%, 𝐛(
⋯
正样本
𝑝$,!
𝑝$,"
𝑝$,$
𝑝$,#
⋯

## Page 10

向量𝐲$ ：
向量𝐩$ ：
损失函数
Softmax激活函数
cos 𝐚%, 𝐛&
cos 𝐚%, 𝐛'
cos 𝐚%, 𝐛%
cos 𝐚%, 𝐛(
⋯
正样本
𝑝$,!
𝑝$,"
𝑝$,$
𝑝$,#
⋯

## Page 11

向量𝐲$ ：
向量𝐩$ ：
CrossEntropyLoss 𝐲%, 𝐩%
= −log 𝑝%,%
损失函数
cos 𝐚%, 𝐛&
Softmax激活函数
cos 𝐚%, 𝐛'
cos 𝐚%, 𝐛%
cos 𝐚%, 𝐛(
⋯
𝑝$,!
𝑝$,"
𝑝$,$
𝑝$,#
⋯
正样本
= −log
exp cos 𝐚%, 𝐛%
∑)*&
(
exp cos 𝐚%, 𝐛)

## Page 12

纠偏
• 物品𝑗被抽样到的概率：
𝑝% ∝点击次数
• 预估⽤户𝑖对物品𝑗的兴趣：cos 𝐚$, 𝐛%
• 做训练的时候，把cos 𝐚$, 𝐛% 替换为：
cos 𝐚$, 𝐛% −log 𝑝%
参考⽂献：
•
Xinyang Yi et al. Sampling-Bias-Corrected Neural Modeling for Large
Corpus Item Recommendations. In RecSys, 2019.

## Page 13

训练双塔模型
• 从点击数据中随机抽取𝑛个⽤户—物品⼆元组，组成
⼀个batch。
• 双塔模型的损失函数：
𝐿'()* 𝑖
= −log
+,- ./0 𝐚!,𝐛! 34/5 6!
∑"#$
%
+,- ./0 𝐚!,𝐛" 34/5 6"
.
• 做梯度下降，减⼩损失函数：
!
# ∑$8!
#
𝐿'()* 𝑖.
对应用户𝑖

## Page 14

⾃监督学习
参考⽂献：
•
Tiansheng Yao et al. Self-supervised Learning for Large-scale Item Recommendations.
In CIKM, 2021.

## Page 15

特征𝑖:
特征𝑖::
变换2
变换1
物品𝑖
物品塔
特征𝑗:
特征𝑗::
变换2
变换1
物品𝑗
共享参数

## Page 16

特征𝑖:
特征𝑖::
变换2
变换1
物品𝑖
物品塔
特征𝑗:
特征𝑗::
变换2
变换1
物品𝑗
高相似度
共享参数
𝐛$
:
𝐛$
::
𝐛%
:
𝐛%
::

## Page 17

物品塔
特征𝑖:
特征𝑖::
变换2
变换1
物品塔
特征𝑗:
特征𝑗::
变换2
变换1
物品𝑖
物品𝑗
低相似度
𝐛$
:
𝐛$
::
𝐛%
:
𝐛%
::

## Page 18

自监督学习
• 物品𝑖的两个向量表征𝐛$
: 和𝐛$
:: 有较⾼的相似度。
• 物品𝑖和𝑗的向量表征𝐛$
: 和𝐛%
:: 有较低的相似度。
• ⿎励cos 𝐛$
:, 𝐛$
:: 尽量⼤，cos 𝐛$
:, 𝐛%
:: 尽量⼩。

## Page 19

自监督学习
• 随机选⼀些离散特征（⽐如类⽬），把它们遮住。
• 例：
• 某物品的类⽬特征是𝒰= 数码, 摄影。
• Mask 后的类⽬特征是𝒰+ = default 。
特征变换：Random Mask

## Page 20

自监督学习
• ⼀个物品可以有多个类⽬，那么类⽬是⼀个多值离
散特征。
• Dropout：随机丢弃特征中50% 的值。
• 例：
• 某物品的类⽬特征是𝒰= 美妆, 摄影。
• Dropout 后的类⽬特征是𝒰+ = 美妆。
特征变换：Dropout（仅对多值离散特征⽣效）

## Page 21

自监督学习
• 假设物品⼀共有4 种特征：
ID，类⽬，关键词，城市
• 随机分成两组：
{ID，关键词} 和{类⽬，城市}
• { ID，default，关键词，default } à 物品表征
• { default，类⽬，default，城市} à 物品表征
特征变换：互补特征（complementary）
⿎励两个向量相似

## Page 22

自监督学习
特征变换：Mask ⼀组关联的特征
• 受众性别：𝒰= 男, ⼥, 中性
• 类⽬：𝒱= 美妆, 数码, ⾜球, 摄影, 科技, ⋯
• 𝑢= ⼥和𝑣= 美妆同时出现的概率𝑝𝑢, 𝑣⼤。
• 𝑢= ⼥和𝑣= 数码同时出现的概率𝑝𝑢, 𝑣⼩。

## Page 23

自监督学习
特征变换：Mask ⼀组关联的特征
• 𝑝𝑢：某特征取值为𝑢的概率。
• 𝑝男性= 20%
• 𝑝⼥性= 30%
• 𝑝中性= 50%

## Page 24

自监督学习
特征变换：Mask ⼀组关联的特征
• 𝑝𝑢：某特征取值为𝑢的概率。
• 𝑝𝑢, 𝑣：某特征取值为𝑢，另⼀个特征取值为𝑣，
同时发⽣的概率。
• 𝑝⼥性，美妆= 3%
• 𝑝⼥性，数码= 0.1%

## Page 25

自监督学习
特征变换：Mask ⼀组关联的特征
• 𝑝𝑢：某特征取值为𝑢的概率。
• 𝑝𝑢, 𝑣：某特征取值为𝑢，另⼀个特征取值为𝑣，
同时发⽣的概率。
• 离线计算特征两两之间的关联，⽤互信息（mutual
information）衡量：
𝑀𝐼𝒰, 𝒱
= ∑;∈𝒰∑>∈𝒱𝑝𝑢, 𝑣⋅log
6 ;,>
6 ; ⋅6 > .

## Page 26

自监督学习
• 设⼀共有𝑘种特征。离线计算特征两两之间MI，
得到𝑘×𝑘的矩阵。
• 随机选⼀个特征作为种⼦，找到种⼦最相关的𝑘/2
种特征。
• Mask 种⼦及其相关的𝑘/2 种特征，保留其余的
𝑘/2 种特征。
特征变换：Mask ⼀组关联的特征

## Page 27

自监督学习
• 好处：⽐random mask、dropout、互补特征等⽅法
效果更好。
• 坏处：⽅法复杂，实现的难度⼤，不容易维护。
特征变换：Mask ⼀组关联的特征

## Page 28

自监督学习
特征变换：Mask ⼀组关联的特征
特征变换：互补特征（complementary）
特征变换：Dropout（仅对多值离散特征⽣效）
特征变换：Random Mask

## Page 29

训练模型
• 从全体物品中均匀抽样，得到𝑚个物品，作为⼀个
batch。
• 做两类特征变换，物品塔输出两组向量：
𝐛!
: , 𝐛"
: , ⋯, 𝐛A
:
和
𝐛!
::, 𝐛"
::, ⋯, 𝐛A
::
• 第𝑖个物品的损失函数：
𝐿0+4B 𝑖
= −log
+,- ./0 𝐛!
",𝐛!
""
∑#$%
&
+,- ./0 𝐛!
",𝐛#
""
.

## Page 30

训练模型
Softmax激活函数
cos 𝐛%
+, 𝐛&++
cos 𝐛%
+, 𝐛'
++
cos 𝐛%
+, 𝐛,
++
⋯
𝑠$,!
𝑠$,"
𝑠$,A
⋯
正样本
cos 𝐛%
+, 𝐛%
++
𝑠$,$

## Page 31

向量𝐬$ ：
训练模型
Softmax激活函数
cos 𝐛%
+, 𝐛&++
cos 𝐛%
+, 𝐛'
++
cos 𝐛%
+, 𝐛,
++
⋯
𝑠$,!
𝑠$,"
𝑠$,A
⋯
正样本
cos 𝐛%
+, 𝐛%
++
𝑠$,$

## Page 32

向量𝐬$ ：
训练模型
Softmax激活函数
cos 𝐛%
+, 𝐛&++
cos 𝐛%
+, 𝐛'
++
cos 𝐛%
+, 𝐛,
++
⋯
𝑠$,!
𝑠$,"
𝑠$,A
⋯
正样本
cos 𝐛%
+, 𝐛%
++
𝑠$,$
向量𝐲$ ：
⋯

## Page 33

向量𝐲$ ：
向量𝐬$ ：
CrossEntropyLoss 𝐲%, 𝐬%
= −log 𝑠%,%
训练模型
⋯
= −log
exp cos 𝐛%
+, 𝐛%
++
∑)*&
, exp cos 𝐛%
+, 𝐛)
++
Softmax激活函数
cos 𝐛%
+, 𝐛&++
cos 𝐛%
+, 𝐛'
++
cos 𝐛%
+, 𝐛%
++
cos 𝐛%
+, 𝐛,
++
⋯
𝑠$,!
𝑠$,"
𝑠$,$
𝑠$,A
⋯
正样本

## Page 34

训练模型
• ⾃监督学习的损失函数：
𝐿!"#$ 𝑖
= −log
"%& '(! 𝐛!
&, 𝐛!
&&
∑"#$
'
"%& '(! 𝐛!
&, 𝐛"
&&
.
• 做梯度下降，减⼩⾃监督学习的损失：
!
A ∑$8!
A 𝐿0+4B 𝑖.

## Page 35

总结

## Page 36

总结
• 双塔模型学不好低曝光物品的向量表征。
• ⾃监督学习：
• 对物品做随机特征变换。
• 特征向量𝐛,
- 和𝐛,
-- 相似度⾼（相同物品）。
• 特征向量𝐛,
- 和𝐛.
-- 相似度低（不同物品）。
• 实验效果：低曝光物品、新物品的推荐变得更准。

## Page 37

训练模型
• 对点击做随机抽样，得到𝑛对⽤户—物品⼆元组，作
为⼀个batch。
• 从全体物品中均匀抽样，得到𝑚个物品，作为⼀个
batch。
• 做梯度下降，使得损失减⼩：
!
# ∑$8!
#
𝐿'()* 𝑖
+ 𝛼⋅
!
A ∑%8!
A
𝐿0+4B 𝑗.
双塔模型的损失
自监督学习的损失

## Page 38

Thank You!
http://wangshusen.github.io/

### Deep Retrieval 召回

# Deep Retrieval 召回

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_10.pdf

## Page 1

Deep Retrieval
王树森
http://wangshusen.github.io/

## Page 2

Deep Retrieval
• 经典的双塔模型把⽤户、物品表⽰为向量，线上做最近邻
查找。
• Deep Retrieval [1] 把物品表征为路径（path），线上查找
⽤户最匹配的路径。
• Deep Retrieval 类似于阿⾥的TDM [2]。
参考⽂献：
1.
Weihao Gao et al. Learning A Retrievable Structure for Large-Scale Recommendations. In
CIKM, 2021.
2.
Han Zhu et al. Learning Tree-based Deep Model for Recommender Systems. In KDD, 2018.

## Page 3

Outline
1. 索引：
• 路径à List<物品>
• 物品à List<路径>
2. 预估模型：神经⽹络预估⽤户对路径的兴趣。
3. 线上召回：⽤户à 路径à 物品。
4. 训练：
• 学习神经⽹络参数。
• 学习物品表征（物品à 路径）。

## Page 4

索引

## Page 5

物品表征为路径
L1
L2
L3
• 深度: depth = 3。
• 宽度: width = K。
• 把⼀个物品表⽰为⼀条路径
（path），⽐如2, 4, 1 。
⋮
𝐾
⋮
𝐾
⋮
𝐾

## Page 6

物品表征为路径
⋮
𝐾
⋮
𝐾
⋮
𝐾
L1
L2
L3
• 深度: depth = 3。
• 宽度: width = K。
• 把⼀个物品表⽰为⼀条路径
（path），⽐如2, 4, 1 。
• ⼀个物品可以表⽰为多条路径，
⽐如
2, 4, 1 , 4, 1, 1
。

## Page 7

物品到路径的索引
• ⼀个物品对应多条路径。
• ⽤3 个节点表⽰⼀条路径：path = [𝑎, 𝑏, 𝑐]。
索引：item à List path
索引：path à List item
• ⼀条路径对应多个物品。

## Page 8

预估模型

## Page 9

预估用户对路径的兴趣
• ⽤3 个节点表⽰⼀条路径：path =
𝑎, 𝑏, 𝑐。
• 给定⽤户特征𝐱，预估⽤户对节点𝑎的兴趣𝑝! 𝑎𝐱。
• 给定𝐱和𝑎，预估⽤户对节点𝑏的兴趣𝑝" 𝑏𝑎; 𝐱。
• 给定𝐱, 𝑎, 𝑏，预估⽤户对节点𝑐的兴趣𝑝# 𝑐𝑎, 𝑏; 𝐱。

## Page 10

预估用户对路径的兴趣
• ⽤3 个节点表⽰⼀条路径：path =
𝑎, 𝑏, 𝑐。
• 给定⽤户特征𝐱，预估⽤户对节点𝑎的兴趣𝑝! 𝑎𝐱。
• 给定𝐱和𝑎，预估⽤户对节点𝑏的兴趣𝑝" 𝑏𝑎; 𝐱。
• 给定𝐱, 𝑎, 𝑏，预估⽤户对节点𝑐的兴趣𝑝# 𝑐𝑎, 𝑏; 𝐱。
• 预估⽤户对path =
𝑎, 𝑏, 𝑐兴趣：
𝑝𝑎, 𝑏, 𝑐|𝐱
= 𝑝! 𝑎𝐱× 𝑝" 𝑏𝑎; 𝐱× 𝑝# 𝑐𝑎, 𝑏; 𝐱.

## Page 11

神经网络
Softmax
𝑎
选择
𝐩'
𝐱
⽤
户
特
征
⋮
𝐾
⋮
𝐾
⋮
𝐾
L1
L2
L3

## Page 12

⊕
神经网络
Softmax
𝑏
选择
神经网络
Softmax
选择
emb 𝑎
𝐩'
𝐩(
𝐱
𝑎
embedding
𝐱
⽤
户
特
征

## Page 13

⊕
神经网络
Softmax
𝑐
选择
⊕
神经网络
Softmax
选择
神经网络
Softmax
𝑎
选择
embedding
emb 𝑎
emb 𝑏
𝐩'
𝐩(
𝐩)
𝐱
⽤
户
特
征
embedding
emb 𝑎
𝐱
𝑏

## Page 14

线上召回

## Page 15

线上召回
• 第⼀步：给定⽤户特征，⽤beam search 召回⼀
批路径。
• 第⼆步：利⽤索引“path à List item ”，召回
⼀批物品。
• 第三步：对物品做打分和排序，选出⼀个⼦集。
召回：⽤户→ 路径→ 物品

## Page 16

Beam Search
• 假设有3 层，每层𝐾个节点，那么⼀共有𝐾# 条
路径。
• ⽤神经⽹络给所有𝐾# 条路径打分，计算量太⼤。
• ⽤beam search，可以减⼩计算量。
• 需要设置超参数beam size。

## Page 17

Beam Search (size = 1)
𝑝! 1 𝐱
𝑝! 2 𝐱
𝑝! 3 𝐱
𝑝! 4 𝐱
𝑝! 5 𝐱
𝑝! 𝐾𝐱
⋮
𝐾
L1

## Page 18

⋮
𝐾
L2
Beam Search (size = 1)
⋮
𝐾
L1
𝑝" 1 5; 𝐱
𝑝" 2 5; 𝐱
𝑝" 3 5; 𝐱
𝑝" 4 5; 𝐱
𝑝" 5 5; 𝐱
𝑝" 𝐾5; 𝐱
⋮

## Page 19

Beam Search (size = 1)
⋮
𝐾
⋮
𝐾
L1
L2

## Page 20

⋮
𝐾
L3
Beam Search (size = 1)
⋮
𝐾
⋮
𝐾
L1
L2
𝑝# 1 5, 4; 𝐱
𝑝# 2 5, 4; 𝐱
𝑝# 3 5, 4; 𝐱
𝑝# 4 5, 4; 𝐱
𝑝# 5 5, 4; 𝐱
𝑝# 𝐾5, 4; 𝐱
⋮

## Page 21

⋮
𝐾
L2
Beam Search (size = 1)
⋮
𝐾
⋮
𝐾
L1
L3

## Page 22

⋮
𝐾
L2
Beam Search (size = 1)
⋮
𝐾
⋮
𝐾
L1
L3
选中路径path = 5, 4, 1

## Page 23

Beam Search
• ⽤户对path =
𝑎, 𝑏, 𝑐兴趣：
𝑝𝑎, 𝑏, 𝑐|𝐱
= 𝑝! 𝑎𝐱× 𝑝" 𝑏𝑎; 𝐱× 𝑝# 𝑐𝑎, 𝑏; 𝐱.
• 最优的路径：
𝑎⋆, 𝑏⋆, 𝑐⋆
= argmax
4,5,6
𝑝𝑎, 𝑏, 𝑐| 𝐱
• 贪⼼算法（beam size = 1）选中的路径𝑎, 𝑏, 𝑐未必是
最优的路径。

## Page 24

Beam Search (size = 4)
L1
⋮
𝐾
𝑝! 1 𝐱
𝑝! 2 𝐱
𝑝! 3 𝐱
𝑝! 4 𝐱
𝑝! 5 𝐱
𝑝! 𝐾𝐱
⋮
𝑝! 6 𝐱
𝑝! 7 𝐱
𝑝! 8 𝐱

## Page 25

Beam Search (size = 4)
L1
⋮
𝐾
⋮
𝐾
L2

## Page 26

Beam Search (size = 4)
L1
⋮
𝐾
⋮
𝐾
L2
• 对于每个被选中的节点a，
计算⽤户对路径[a, b] 的兴
趣：
𝑝! 𝑎𝐱× 𝑝" 𝑏𝑎; 𝐱.
• 算出4×𝐾个分数，每个分
数对应⼀条路径，选出分数
top 4 路径。

## Page 27

Beam Search (size = 4)
L1
⋮
𝐾
⋮
𝐾
L2
• 对于每个被选中的节点a，
计算⽤户对路径[a, b] 的兴
趣：
𝑝! 𝑎𝐱× 𝑝" 𝑏𝑎; 𝐱.
• 算出4×𝐾个分数，每个分
数对应⼀条路径，选出分数
top 4 路径。

## Page 28

Beam Search (size = 4)
L1
⋮
𝐾
⋮
𝐾
L2
⋮
𝐾
L3

## Page 29

Beam Search (size = 4)
L1
⋮
𝐾
⋮
𝐾
L2
⋮
𝐾
L3

## Page 30

Beam Search (size = 4)
L1
⋮
𝐾
⋮
𝐾
L2
⋮
𝐾
L3

## Page 31

线上召回
• 第⼀步：给定⽤户特征，⽤神经⽹络做预估，⽤
beam search 召回⼀批路径。
• 第⼆步：利⽤索引，召回⼀批物品。
• 查看索引path à List item 。
• 每条路径对应多个物品。
• 第三步：对物品做排序，选出⼀个⼦集。
线上召回：user à path à item

## Page 32

训练

## Page 33

训练
• 神经⽹络𝑝𝑎, 𝑏, 𝑐| 𝐱预估⽤户对路径𝑎, 𝑏, 𝑐的兴趣。
• 把⼀个物品表征为多条路径
𝑎, 𝑏, 𝑐，建⽴索引：
• item à List path ，
• path à List item 。
• 正样本(user, item)：click(user, item) = 1。
同时学习神经⽹络参数和物品表征

## Page 34

学习神经网络参数
• 物品表征为𝐽条路径：𝑎!, 𝑏!, 𝑐! , ⋯, 𝑎:, 𝑏:, 𝑐: 。
• ⽤户对路径𝑎, 𝑏, 𝑐的兴趣：
𝑝𝑎, 𝑏, 𝑐| 𝐱= 𝑝! 𝑎| 𝐱× 𝑝" 𝑏| 𝑎; 𝐱× 𝑝# 𝑐| 𝑎, 𝑏; 𝐱.
• 如果⽤户点击过物品，说明⽤户对𝐽条路径感兴趣。

## Page 35

学习神经网络参数
• 物品表征为𝐽条路径：𝑎!, 𝑏!, 𝑐! , ⋯, 𝑎:, 𝑏:, 𝑐: 。
• ⽤户对路径𝑎, 𝑏, 𝑐的兴趣：
𝑝𝑎, 𝑏, 𝑐| 𝐱= 𝑝! 𝑎| 𝐱× 𝑝" 𝑏| 𝑎; 𝐱× 𝑝# 𝑐| 𝑎, 𝑏; 𝐱.
• 如果⽤户点击过物品，说明⽤户对𝐽条路径感兴趣。
• 应该让∑;<!
:
𝑝𝑎;, 𝑏;, 𝑐; | 𝐱变⼤。
• 损失函数：loss = −log ∑;<!
:
𝑝𝑎;, 𝑏;, 𝑐; | 𝐱
。

## Page 36

学习物品表征
• ⽤户user 对路径path = 𝑎, 𝑏, 𝑐的兴趣记作：
𝑝path | user
= 𝑝𝑎, 𝑏, 𝑐| 𝐱.
• 物品item 与路径path 的相关性：
score item, path
= ∑=>?@ 𝑝path | user × click user, item .
用户对路径的兴趣
是否点击（0或1）

## Page 37

学习物品表征
• ⽤户user 对路径path = 𝑎, 𝑏, 𝑐的兴趣记作：
𝑝path | user
= 𝑝𝑎, 𝑏, 𝑐| 𝐱.
• 物品item 与路径path 的相关性：
score item, path
= ∑=>?@ 𝑝path | user × click user, item .
• 根据score item, path 选出𝐽条路径作为item 的表征。

## Page 38

表征：物品à 路径
Item
用户对路径的兴趣：
𝑝path | user
0.1
0.5
0.8
0.2
用户点击物品：
click user, item
Path

## Page 39

学习物品表征
• 选出𝐽条路径𝛱= path!, ⋯, path: ，作为物品的表征。
• 损失函数（选择与item ⾼度相关的path）：
loss item, 𝛱
= −log ∑$%!
&
score item, path$
.
• 正则项（避免过多的item 集中在⼀条path 上）：
reg(path$) = number of items on path$
'.

## Page 40

学习物品表征
• 假设已经把物品表征为𝐽条路径𝛱= path!, ⋯, path: 。
• 每次固定pathA ABC，并从未被选中的路径中，选出⼀条
作为新的pathC：
path( ←argmin)*+,! loss item, 𝛱+ 𝛼⋅reg path( .
• 选中的路径有较⾼的分数score item, pathC ，⽽且路径上
的物品数量不会太多。
⽤贪⼼算法更新路径

## Page 41

训练
• 神经⽹络判断⽤户对路径的兴趣：
𝑝path | 𝐱.
• 训练所需的数据：（1）“物品→
路径”的索引，（2）⽤户点击过
的物品。
• 如果⽤户点击过物品，且物品对
应路径path，则更新神经⽹络参
数使𝑝path | 𝐱变⼤。
更新神经⽹络
更新物品的表征

## Page 42

训练
• 神经⽹络判断⽤户对路径的兴趣：
𝑝path | 𝐱.
• 训练所需的数据：（1）“物品→
路径”的索引，（2）⽤户点击过
的物品。
• 如果⽤户点击过物品，且物品对
应路径path，则更新神经⽹络参
数使𝑝path | 𝐱变⼤。
更新神经⽹络
更新物品的表征
• 判断物品与路径的相关性：
物品⟵⽤户⟶路径
• 让每个物品关联𝐽条路径。
• 物品和路径要有很⾼的相关性。
• ⼀条路径上不能有过多的物品。
神经网络的打分
用户点击过物品

## Page 43

总结

## Page 44

Deep Retrieval
• 给定⽤户特征𝒙，⽤神经⽹络预估⽤户对路径path = 𝑎, 𝑏, 𝑐
的兴趣，分数记作𝑝path | 𝐱.
• ⽤beam search 寻找分数𝑝path | 𝐱最⾼的𝑠条path 。
召回：⽤户→ 路径→ 物品

## Page 45

Deep Retrieval
• 给定⽤户特征𝒙，⽤神经⽹络预估⽤户对路径path = 𝑎, 𝑏, 𝑐
的兴趣，分数记作𝑝path | 𝐱.
• ⽤beam search 寻找分数𝑝path | 𝐱最⾼的𝑠条path 。
• 利⽤索引“path à List item ”召回每条路径上的𝑛个物品。
• ⼀共召回𝑠×𝑛个物品，对物品做初步排序，返回分数最⾼的
若⼲物品。
召回：⽤户→ 路径→ 物品

## Page 46

Deep Retrieval
• ⼀个物品被表征为𝐽条路径：path!, ⋯, path:。
• 如果⽤户点击过物品，则更新神经⽹络参数，使分数增⼤：
∑;<!
:
𝑝path; | 𝐱.
训练：同时学习⽤户—路径和物品—路径的关系

## Page 47

Deep Retrieval
• ⼀个物品被表征为𝐽条路径：path!, ⋯, path:。
• 如果⽤户点击过物品，则更新神经⽹络参数，使分数增⼤：
∑;<!
:
𝑝path; | 𝐱.
• 如果⽤户对路径的兴趣分数𝑝path | 𝐱较⾼，且⽤户点击过物
品item，则item 与path 具有相关性。
• 寻找与item 最相关的𝐽条path，且避免⼀条路径上物品过多。
训练：同时学习⽤户—路径和物品—路径的关系

## Page 48

Thank You!
http://wangshusen.github.io/

### 其它召回通道

# 其它召回通道

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_11.pdf

## Page 1

其他召回通道
王树森
http://wangshusen.github.io/
ShusenWang@xiaohongshu.com

## Page 2

地理位置召回

## Page 3

• ⽤户可能对附近发⽣的事感兴趣。
• GeoHash：对经纬度的编码，地图上⼀个长⽅形区域。
• 索引：GeoHash à 优质笔记列表（按时间倒排）。
• 这条召回通道没有个性化。
GeoHash召回

## Page 4

⋯
GeoHash召回
⋯
GeoHash：
最新发布的𝑘篇优质笔记ID：
6G6VVF
6FH4MC
⋮

## Page 5

⋯
GeoHash召回
⋯
GeoHash：
6G6VVF
6FH4MC
⋮
根据⽤户定位的GeoHash，取回该地点最新的𝑘篇优质笔记。
最新发布的𝑘篇优质笔记ID：

## Page 6

• ⽤户可能对同城发⽣的事感兴趣。
• 索引：城市à 优质笔记列表（按时间倒排）。
• 这条召回通道没有个性化。
同城召回

## Page 7

作者召回

## Page 8

• ⽤户对关注的作者发布的笔记感兴趣。
• 索引：
⽤户à 关注的作者
作者à 发布的笔记
• 召回：
⽤户à 关注的作者à 最新的笔记
关注作者召回

## Page 9

• 如果⽤户对某笔记感兴趣（点赞、收藏、转发），
那么⽤户可能对该作者的其他笔记感兴趣。
• 索引：⽤户à 有交互的作者
• 召回：⽤户à 有交互的作者à 最新的笔记
有交互的作者召回

## Page 10

• 如果⽤户喜欢某作者，那么⽤户喜欢相似的作者。
• 索引：作者à 相似作者
• 召回：⽤户à 感兴趣的作者
相似作者召回
à 相似作者à 最新的笔记
（𝑘个作者）
（𝑛个作者）
（𝑛𝑘个作者）
（𝑛𝑘篇笔记）

## Page 11

缓存召回

## Page 12

• 背景：
• 精排输出⼏百篇笔记，送⼊重排。
• 重排做多样性抽样，选出⼏⼗篇。
• 精排结果⼀⼤半没有曝光，被浪费。
• 精排前50，但是没有曝光的，缓存起来，
作为⼀条召回通道。
缓存召回
想法：复⽤前𝑛次推荐精排的结果。

## Page 13

• ⼀旦笔记成功曝光，就从缓存退场。
• 如果超出缓存⼤⼩，就移除最先进⼊缓存
的笔记。
• 笔记最多被召回10 次，达到10 次就退场。
• 每篇笔记最多保存3 天，达到3 天就退场。
缓存召回
缓存⼤⼩固定，需要退场机制。

## Page 14

• 地理位置召回：
• GeoHash 召回、同城召回。
• 作者召回：
• 关注的作者、有交互的作者、相似的作者。
• 缓存召回。
总结

## Page 15

Thank You!
http://wangshusen.github.io/

## Page 16

长期招聘优秀的算法工程师
• 部门：⼩红书社区技术部。
• ⽅向：搜索、推荐。
• 职位：校招、社招、实习。
• 地点：上海、北京。
• 联系⽅式：ShusenWang@xiaohongshu.com

### 曝光过滤

# 曝光过滤

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_12.pdf

## Page 1

曝光过滤& Bloom Filter
王树森
http://wangshusen.github.io/
ShusenWang@xiaohongshu.com

## Page 2

曝光过滤问题
• 如果⽤户看过某个物品，则不再把该物品曝光给该⽤户。
• 对于每个⽤户，记录已经曝光给他的物品。（⼩红书只召
回1 个⽉以内的笔记，因此只需要记录每个⽤户最近1 个
⽉的曝光历史。）
• 对于每个召回的物品，判断它是否已经给该⽤户曝光过，
排除掉曾经曝光过的物品。
• ⼀位⽤户看过𝑛个物品，本次召回𝑟个物品，如果暴⼒对
⽐，需要𝑂𝑛𝑟的时间。

## Page 3

Bloom Filter
• Bloom filter 判断⼀个物品ID 是否在已曝光的物品集合中。
• 如果判断为no，那么该物品⼀定不在集合中。
• 如果判断为yes，那么该物品很可能在集合中。（可能误伤，
错误判断未曝光物品为已曝光，将其过滤掉。）
参考⽂献：
• Burton H. Bloom. Space/time trade-offs in hash coding with allowable
errors. Communications of the ACM, 1970.

## Page 4

Bloom Filter
• Bloom filter 把物品集合表征为⼀个𝑚维⼆进制向量。
• 每个⽤户有⼀个曝光物品的集合，表征为⼀个向量，需要𝑚
bit 的存储。
• Bloom filter 有𝑘个哈希函数，每个哈希函数把物品ID 映射
成介于0 和𝑚−1 之间的整数。
参考⽂献：
• Burton H. Bloom. Space/time trade-offs in hash coding with allowable
errors. Communications of the ACM, 1970.

## Page 5

Bloom Filter (𝑘= 1)
⋯
⼆进制向量：
𝑚bits

## Page 6

Bloom Filter (𝑘= 1)
⋯
⼆进制向量：
已曝光物品：
ID!
ID"
𝑚bits

## Page 7

Bloom Filter (𝑘= 1)
⋯
⼆进制向量：
已曝光物品：
ID!
ID#
ID$
ID"
𝑚bits

## Page 8

Bloom Filter (𝑘= 1)
⋯
⼆进制向量：
已曝光物品：
ID!
ID#
ID%
ID$
ID&
ID"
𝑚bits

## Page 9

Bloom Filter (𝑘= 1)
⋯
⼆进制向量：
已曝光物品：
召回的物品：
ID'
未曝光
ID!
ID#
ID%
ID$
ID&
ID"

## Page 10

Bloom Filter (𝑘= 1)
⋯
⼆进制向量：
已曝光物品：
召回的物品：
ID'
ID%
未曝光
已曝光
ID!
ID#
ID%
ID$
ID&
ID"

## Page 11

Bloom Filter (𝑘= 1)
⋯
⼆进制向量：
已曝光物品：
召回的物品：
ID'
ID%
ID(
未曝光
已曝光
被误判为已曝光
ID!
ID#
ID%
ID$
ID&
ID"

## Page 12

Bloom Filter (𝑘= 3)
⋯
⼆进制向量：

## Page 13

Bloom Filter (𝑘= 3)
⋯
⼆进制向量：
已曝光物品：
ID!
ℎ!
ℎ"
ℎ#

## Page 14

Bloom Filter (𝑘= 3)
⋯
⼆进制向量：
已曝光物品：
ID!
ID"
ℎ!
ℎ"
ℎ#

## Page 15

Bloom Filter (𝑘= 3)
⋯
⼆进制向量：
已曝光物品：
ID!
ID"
召回的物品：
ID(
ID$
未曝光
已曝光
ℎ!
ℎ"
ℎ#

## Page 16

Bloom Filter (𝑘= 3)
⋯
⼆进制向量：
已曝光物品：
ID!
ID"
召回的物品：
ID(
ID$
未曝光
已曝光
ℎ!
ℎ"
ℎ#
ID)
未曝光
被误判为已曝光

## Page 17

Bloom Filter
• 曝光物品集合⼤⼩为𝑛，⼆进制向量维度为𝑚，使⽤𝑘个哈
希函数。
• Bloom filter 误伤的概率为𝛿≈1 −exp −
*+
,
*
。
• 𝑛越⼤，向量中的1 越多，误伤概率越⼤。（未曝光物品的
𝑘个位置恰好都是1 的概率⼤。）
• 𝑚越⼤，向量越长，越不容易发⽣哈希碰撞。
• 𝑘太⼤、太⼩都不好，𝑘有最优取值。

## Page 18

Bloom Filter
• 曝光物品集合⼤⼩为𝑛，⼆进制向量维度为𝑚，使⽤𝑘个哈
希函数。
• Bloom filter 误伤的概率为𝛿≈1 −exp −
*+
,
*
。
• 设定可容忍的误伤概率为𝛿

## Page 19

Bloom Filter
• 曝光物品集合⼤⼩为𝑛，⼆进制向量维度为𝑚，使⽤𝑘个哈
希函数。
• Bloom filter 误伤的概率为𝛿≈1 −exp −
*+
,
*
。
• 设定可容忍的误伤概率为𝛿，那么最优参数为：
𝑘= 1.44 ⋅ln
!
- ,
𝑚= 2𝑛⋅ln
!
- .

## Page 20

曝光过滤的链路
召回
排序
物品1
⋮
物品2
物品𝑞
实时流处理
（Kafka+Flink）
曝光过滤服务
（Bloom Filter）
写
⼆进制向量

## Page 21

曝光过滤的链路
召回
排序
物品1
⋮
物品2
物品𝑞
实时流处理
（Kafka+Flink）
曝光过滤服务
（Bloom Filter）
写
⼆进制向量

## Page 22

曝光过滤的链路
召回
排序
物品1
⋮
物品2
物品𝑞
实时流处理
（Kafka+Flink）
曝光过滤服务
（Bloom Filter）
写
⼆进制向量

## Page 23

曝光过滤的链路
召回
排序
物品1
⋮
物品2
物品𝑞
实时流处理
（Kafka+Flink）
曝光过滤服务
（Bloom Filter）
写
⼆进制向量

## Page 24

Bloom Filter的缺点
• Bloom filter 把物品的集合表⽰成⼀个⼆进制向量。
• 每往集合中添加⼀个物品，只需要把向量𝑘个位置的
元素置为1。（如果原本就是1，则不变。）
• Bloom filter 只⽀持添加物品，不⽀持删除物品。从集
合中移除物品，无法消除它对向量的影响。
• 每天都需要从物品集合中移除年龄⼤于1 个⽉的物品。
（超龄物品不可能被召回，没必要把它们记录在
Bloom filter，降低𝑛可以降低误伤率。）

## Page 25

Thank You!
http://wangshusen.github.io/
