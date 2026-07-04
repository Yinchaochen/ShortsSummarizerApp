# Section 4: 交叉结构

## Why This Section Matters

- Understand why feature interaction is central in sparse recommendation settings.
- Learn the roles of FM, DCN, LHUC, SENet, and FiBiNET.

## Common Mistakes To Avoid

- Do not assume a plain MLP can replace explicit or structured feature crosses in every case.

## Topic List

- Official #22: `Factorized Machine (FM)` (user playlist part `21`)
- Official #23: `Deep & Cross Network (深度交叉网络)` (user playlist part `22`)
- Official #24: `LHUC` (user playlist part `23`)
- Official #25: `SENet & FiBiNET` (user playlist part `24`)

## Official Source Links

- `Factorized Machine (FM)`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/04_Cross_01.pdf`, Bilibili `https://www.bilibili.com/video/BV15V4y1x7Ht`, YouTube `https://youtu.be/exVPXVFPMDk`
- `Deep & Cross Network (深度交叉网络)`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/04_Cross_02.pdf`, Bilibili `https://www.bilibili.com/video/BV1LP411L7Z2`, YouTube `https://youtu.be/yNeRO5m63JQ`
- `LHUC`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/04_Cross_03.pdf`, Bilibili `https://www.bilibili.com/video/BV1jU4y1z7Tc`, YouTube `https://youtu.be/TxIedW94hu0`
- `SENet & FiBiNET`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/04_Cross_04.pdf`, Bilibili `https://www.bilibili.com/video/BV1SY4y1M7bD`, YouTube `https://youtu.be/nF37qtNvw1E`

## Local Extract Files

- `Factorized Machine (FM)`: `../source-extracts/slides/04_Cross_01.md`
- `Deep & Cross Network (深度交叉网络)`: `../source-extracts/slides/04_Cross_02.md`
- `LHUC`: `../source-extracts/slides/04_Cross_03.md`
- `SENet & FiBiNET`: `../source-extracts/slides/04_Cross_04.md`
## Inline Slide Extracts

### Factorized Machine (FM)

# Factorized Machine (FM)

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/04_Cross_01.pdf

## Page 1

Factorized Machine (FM)
王树森
http://wangshusen.github.io/

## Page 2

线性模型
• 有𝑑个特征，记作𝐱= 𝑥!, ⋯, 𝑥" 。
• 线性模型：
𝑝= 𝑏+ ∑#$!
"
𝑤#𝑥#.
• 模型有𝑑+ 1 个参数：𝐰= 𝑤!, ⋯, 𝑤" 和𝑏。
• 预测是特征的加权和。（只有加，没有乘。）

## Page 3

二阶交叉特征
• 有𝑑个特征，记作𝐱= 𝑥!, ⋯, 𝑥" 。
• 模型有𝑂𝑑% 个参数。
• 线性模型+ ⼆阶交叉特征：
𝑝= 𝑏+ ∑!"#
$
𝑤!𝑥! + ∑!"#
$
∑%"!&#
$
𝑢!%𝑥!𝑥%.

## Page 4

二阶交叉特征
• 线性模型+ ⼆阶交叉特征：
𝑝= 𝑏+ ∑!"#
$
𝑤!𝑥! + ∑!"#
$
∑%"!&#
$
𝑢!%𝑥!𝑥%.

## Page 5

二阶交叉特征
• 线性模型+ ⼆阶交叉特征：
𝑝= 𝑏+ ∑!"#
$
𝑤!𝑥! + ∑!"#
$
∑%"!&#
$
𝑢!%𝑥!𝑥%.
矩阵𝐔
𝑑⾏
𝑑列

## Page 6

二阶交叉特征
• 线性模型+ ⼆阶交叉特征：
𝑝= 𝑏+ ∑!"#
$
𝑤!𝑥! + ∑!"#
$
∑%"!&#
$
𝑢!%𝑥!𝑥%.
矩阵𝐔
矩阵𝐕
矩阵𝐕&
≈
⋅
𝑑⾏
𝑑列
𝑑⾏
𝑘⾏
𝑘列

## Page 7

二阶交叉特征
• 线性模型+ ⼆阶交叉特征：
𝑝= 𝑏+ ∑!"#
$
𝑤!𝑥! + ∑!"#
$
∑%"!&#
$
𝑢!%𝑥!𝑥%.
矩阵𝐔
矩阵𝐕
矩阵𝐕&
≈
⋅
𝑢!"
𝐯"
#
𝐯$
≈𝐯#
&𝐯'

## Page 8

二阶交叉特征
• 线性模型+ ⼆阶交叉特征：
𝑝= 𝑏+ ∑!"#
$
𝑤!𝑥! + ∑!"#
$
∑%"!&#
$
𝑢!%𝑥!𝑥%.
• Factorized Machine (FM)：
𝑝= 𝑏+ ∑!"#
$
𝑤!𝑥! + ∑!"#
$
∑%"!&#
$
𝐯"
#𝐯$ 𝑥!𝑥%.
• FM 模型有𝑂𝑘𝑑个参数。（𝑘≪𝑑）
≈𝐯#
&𝐯'

## Page 9

Factorized Machine
• FM 是线性模型的替代品，能⽤线性回归、逻辑回归
的场景，都可以⽤FM。
• FM 使⽤⼆阶交叉特征，表达能⼒⽐线性模型更强。

## Page 10

Factorized Machine
• FM 是线性模型的替代品，能⽤线性回归、逻辑回归
的场景，都可以⽤FM。
• FM 使⽤⼆阶交叉特征，表达能⼒⽐线性模型更强。
• 通过做近似𝑢#' ≈𝐯#
&𝐯'，FM 把⼆阶交叉权重的数量
从𝑂𝑑% 降低到𝑂𝑘𝑑。
参考⽂献：
• Steffen Rendle. Factorization machines. In ICDM, 2010.

## Page 11

Thank You!
http://wangshusen.github.io/

### Deep & Cross Network (深度交叉网络)

# Deep & Cross Network (深度交叉网络)

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/04_Cross_02.pdf

## Page 1

深度交叉网络（DCN）
王树森
http://wangshusen.github.io/

## Page 2

召回、排序模型

## Page 3

双塔模型
用户特征
物品特征
余弦相似度：cos 𝐚, 𝐛
𝐚
𝐛
用户塔
物品塔
可以用任意
网络结构

## Page 4

点击率
点赞率
收藏率
转发率
全连接层+Sigmoid
物品特征
Concatenation
统计特征
场景特征
用户特征
神经⽹络
(shared bottom)
可以用任意
网络结构

## Page 5

物品特征
用户特征
统计特征场景特征
𝑝! 𝑝" 𝑝#
𝑞! 𝑞" 𝑞#
𝐱!
𝐱"
𝐱#
神经网络
Softmax
激活函数
神经网络
Softmax
激活函数
对向量做加权平均
𝑝!𝐱! + 𝑝"𝐱" + 𝑝#𝐱#
𝑞!𝐱! + 𝑞"𝐱" + 𝑞#𝐱#
第2号
神经网络
第1号
神经网络
第3号
神经网络
可以用任意网络结构

## Page 6

交叉层
（Cross Layer）

## Page 7

𝐱!
𝐱"
⋯
交叉层(Cross Layer)

## Page 8

𝐱!
𝐱"
全连
接层
𝐲
𝐳
⋯
交叉层(Cross Layer)
Hadamard
Product

## Page 9

𝐱!
𝐱"
全连
接层
𝐲
+
𝐱"#$
𝐳
⋯
交叉层(Cross Layer)
Hadamard
Product

## Page 10

𝐱!
𝐱"
全连
接层
𝐲
𝐱"#$
𝐳
⋯
交叉层(Cross Layer)
交叉层的输出
交叉层的输⼊
+
Hadamard
Product

## Page 11

交叉层(Cross Layer)
+
⋅
+
∘
=
𝐱"
𝐱"#$
𝐱!
𝐱"
𝐛
𝐖
交叉层的输⼊

## Page 12

交叉层(Cross Layer)
+
⋅
+
∘
=
𝐱"
𝐱"#$
𝐱!
𝐱"
𝐛
𝐖
全连接层

## Page 13

交叉层(Cross Layer)
+
⋅
+
∘
=
𝐱"
𝐱"#$
𝐱!
𝐱"
𝐛
𝐖
Hadamard Product

## Page 14

交叉层(Cross Layer)
+
⋅
+
∘
=
𝐱"
𝐱"#$
𝐱!
𝐱"
𝐛
𝐖

## Page 15

交叉层(Cross Layer)
+
⋅
+
∘
=
𝐱"
𝐱"#$
𝐱!
𝐱"
𝐛
𝐖
输出

## Page 16

交叉⽹络
（Cross Network）

## Page 17

交叉层
(参数: 𝐖!, 𝐛!)
𝐱!
𝐱$
交叉网络(Cross Network)
= 𝐱! ∘𝐖!𝐱! + 𝐛! + 𝐱!

## Page 18

交叉层
(参数: 𝐖!, 𝐛!)
交叉层
(参数: 𝐖", 𝐛")
𝐱!
𝐱$
𝐱%
交叉网络(Cross Network)
= 𝐱! ∘𝐖$𝐱$ + 𝐛$ + 𝐱$

## Page 19

𝐱!
𝐱$
𝐱%
𝐱&
交叉网络(Cross Network)
交叉层
(参数: 𝐖!, 𝐛!)
交叉层
(参数: 𝐖", 𝐛")
交叉层
(参数: 𝐖#, 𝐛#)

## Page 20

参考文献
• 这节课介绍的是Cross Network V2 [1]。
• ⽼版本的Cross Network 在论⽂[2] 中提出。
参考⽂献：
1. Ruoxi Wang et al. DCN V2: Improved Deep & Cross Network and Practical
Lessons for Web-scale Learning to Rank Systems. In WWW, 2021.
2. Ruoxi Wang et al. Deep & Cross Network for Ad Click Predictions. In
ADKDD, 2017.

## Page 21

深度交叉网络(Deep & Cross Network)
全连接⽹络
交叉⽹络
⽤户特征
物品特征
其它特征

## Page 22

深度交叉网络(Deep & Cross Network)
全连接⽹络
交叉⽹络
全连接层
⽤户特征
物品特征
其它特征

## Page 23

深度交叉网络(Deep & Cross Network)
全连接⽹络
交叉⽹络
全连接层
深度交叉⽹络
⽤户特征
物品特征
其它特征

## Page 24

Thank You!
http://wangshusen.github.io/

### LHUC

# LHUC

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/04_Cross_03.pdf

## Page 1

LHUC网络结构
王树森
http://wangshusen.github.io/

## Page 2

点击率
点赞率
收藏率
转发率
全连接层+Sigmoid
物品特征
Concatenation
统计特征
场景特征
用户特征
神经⽹络
(shared bottom)

## Page 3

Learning Hidden Unit Contributions (LHUC)
• LHUC 起源于语⾳识别[1]。
• 快⼿将LHUC 应⽤在推荐精排[2]，称作PPNet。
参考⽂献：
1. Pawel Swietojanski, Jinyu Li, & Steve Renals. Learning hidden unit contributions
for unsupervised acoustic model adaptation. IEEE/ACM Transactions on Audio,
Speech, and Language Processing, 2016.
2. 快⼿落地万亿参数推荐精排模型，2021。链接：
https://ai.51cto.com/art/202102/644214.html

## Page 4

语⾳识别中的LHUC
语⾳
信号
说话者
的特征

## Page 5

全
连
接
层
神
经
⽹
络
语⾳识别中的LHUC
语⾳
信号
说话者
的特征
[多个全连接层] à [Sigmoid 乘以2]

## Page 6

全
连
接
层
神
经
⽹
络
语⾳识别中的LHUC
语⾳
信号
说话者
的特征
Hadamard
Product

## Page 7

全
连
接
层
全
连
接
层
神
经
⽹
络
神
经
⽹
络
语⾳识别中的LHUC
语⾳
信号
说话者
的特征
Hadamard
Product

## Page 8

全
连
接
层
全
连
接
层
神
经
⽹
络
神
经
⽹
络
语⾳识别中的LHUC
语⾳
信号
说话者
的特征
Hadamard
Product
Hadamard
Product

## Page 9

全
连
接
层
全
连
接
层
神
经
⽹
络
神
经
⽹
络
语⾳识别中的LHUC
语⾳
信号
说话者
的特征
Hadamard
Product
Hadamard
Product
输出

## Page 10

全
连
接
层
全
连
接
层
语⾳识别中的LHUC
语⾳
信号
说话者
的特征
[多个全连接层] à [Sigmoid 乘以2]
神
经
⽹
络
神
经
⽹
络

## Page 11

全
连
接
层
全
连
接
层
神
经
⽹
络
神
经
⽹
络
语⾳识别中的LHUC
语⾳
信号
说话者
的特征
Hadamard
Product
Hadamard
Product

## Page 12

全
连
接
层
全
连
接
层
神
经
⽹
络
神
经
⽹
络
推荐系统排序模型中的LHUC
物品
特征
⽤户
特征
Hadamard
Product
Hadamard
Product

## Page 13

Thank You!
http://wangshusen.github.io/

### SENet & FiBiNET

# SENet & FiBiNET

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/04_Cross_04.pdf

## Page 1

SENet & Bilinear Cross
王树森
http://wangshusen.github.io/

## Page 2

SENet
参考⽂献：
1.
Jie Hu, Li Shen, and Gang Sun. Squeeze-and-Excitation Networks. In CVPR, 2018.
2.
Tongwen Huang, Zhiqi Zhang, and Junlin Zhang. FiBiNET: Combining Feature Importance
and Bilinear feature Interaction for Click-Through Rate Prediction. In RecSys, 2019.

## Page 3

SENet
⋮
Embedding
⋮
⽤户ID
物品ID
物品类⽬
物品关键词

## Page 4

SENet
⋮

## Page 5

⋮
𝑚×𝑘
SENet

## Page 6

⋮
AvgPool
FC+ReLU
FC+Sigmoid
𝑚×1
𝑚×𝑘
𝑚
𝑟×1
SENet
𝑚×1

## Page 7

AvgPool
FC+ReLU
FC+Sigmoid
⋮
row-wise
multiply
𝑚×1
𝑚
𝑟×1
𝑚×𝑘
SENet
⋮
𝑚×𝑘
𝑚×1

## Page 8

AvgPool
FC+ReLU
FC+Sigmoid
⋮
row-wise
multiply
𝑚×1
𝑚
𝑟×1
𝑚×𝑘
SENet
⋮
𝑚×𝑘
𝑚×1

## Page 9

SENet
⋮
Embedding
⋮
⽤户ID
物品ID
物品类⽬
物品关键词
⋮
SENet
Embedding 向量维度可以不同
𝑚个离散特征
𝑚个向量

## Page 10

SENet
• SENet 对离散特征做field-wise 加权。
• Field：
• ⽤户ID Embedding 是64 维向量。
• 64 个元素算⼀个field，获得相同的权重。
• 如果有𝑚个fields，那么权重向量是𝑚维。

## Page 11

Field 间特征交叉

## Page 12

特征交叉
=
⋅
𝑓!"
𝐱!
#
𝐱"
=
∘
𝐟!"
𝐱"
𝐱!
內积
哈达玛乘积
𝑚fields
à
𝑚"个实数
𝑚fields
à
𝑚"个向量

## Page 13

特征交叉
=
⋅
𝑓!"
𝐱"
𝑚fields
à
𝑚" 个交叉特征（实数）
Bilinear Cross（內积）
𝐖!"
⋅
𝐱!
#

## Page 14

特征交叉
=
⋅
𝑓!"
𝐱"
𝑚fields
à
𝑚"/2 个参数矩阵
Bilinear Cross（內积）
𝐖!"
⋅
𝐱!
#

## Page 15

特征交叉
=
⋅
𝐟!"
𝐱"
Bilinear Cross（哈达玛乘积）
𝐱!
∘
𝐖!"

## Page 16

特征交叉
=
⋅
𝐟!"
𝐱"
𝑚fields
à
𝑚"个向量
Bilinear Cross（哈达玛乘积）
𝐱!
∘
𝐖!"

## Page 17

小结
1. SENet 对离散特征做field-wise 加权。
2. Field 间特征交叉：
• 向量內积
• 哈达玛乘积
• Bilinear cross

## Page 18

FiBiNet
参考⽂献：
•
Tongwen Huang, Zhiqi Zhang, and Junlin Zhang. FiBiNET: Combining Feature Importance
and Bilinear feature Interaction for Click-Through Rate Prediction. In RecSys, 2019.

## Page 19

⋮
离散
特征
Concatenate
Bilinear
Embedding
⋮

## Page 20

⋮
离散
特征
⋮
Concatenate
Bilinear
SENet
Embedding
⋮
Bilinear

## Page 21

⋮
离散
特征
Concatenate
上层
⽹络
⋮
Concatenate
Bilinear
SENet
Embedding
⋮
Bilinear
连续
特征

## Page 22

Thank You!
http://wangshusen.github.io/
