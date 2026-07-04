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
