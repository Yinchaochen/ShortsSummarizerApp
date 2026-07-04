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
