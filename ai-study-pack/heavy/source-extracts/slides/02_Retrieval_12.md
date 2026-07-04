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
