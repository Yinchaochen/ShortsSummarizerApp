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
