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
