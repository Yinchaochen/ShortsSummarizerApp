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
