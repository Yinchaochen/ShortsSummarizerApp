# MMR 算法

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/06_Rerank_02.pdf

## Page 1

Maximal Marginal Relevance (MMR)
王树森
http://wangshusen.github.io/

## Page 2

多样性
• 精排给𝑛个候选物品打分，融合之后的分数为
reward!, ⋯,
reward"
• 把第𝑖和𝑗个物品的相似度记作sim 𝑖, 𝑗。
• 从𝑛个物品中选出𝑘个，既要有⾼精排分数，
也要有多样性。

## Page 3

MMR多样性算法
⋯
!"#$%&'( 𝒮)
⋯
*!"#$%&'( ℛ)

## Page 4

MMR多样性算法
物品𝑖的
精排分数
物品𝑖的
多样性分数
• 计算集合ℛ中每个物品𝑖的Marginal Relevance 分数：
MR# = 𝜃⋅reward# −
1 −𝜃⋅max
$∈𝒮sim 𝑖, 𝑗.
⋯
!"#$%&'( 𝒮)
⋯
*!"#$%&'( ℛ)

## Page 5

MMR多样性算法
⋯
!"#$%&'( 𝒮)
• 计算集合ℛ中每个物品𝑖的Marginal Relevance 分数：
MR# = 𝜃⋅reward# −
1 −𝜃⋅max
$∈𝒮sim 𝑖, 𝑗.
⋯
*!"#$%&'( ℛ)

## Page 6

MMR多样性算法
⋯
!"#$%&'( 𝒮)
• 计算集合ℛ中每个物品𝑖的Marginal Relevance 分数：
MR# = 𝜃⋅reward# −
1 −𝜃⋅max
$∈𝒮sim 𝑖, 𝑗.
• Maximal Marginal Relevance (MMR)：
argmax
#∈ℛ
MR# .
⋯
*!"#$%&'( ℛ)

## Page 7

1. 已选中的物品𝒮初始化为空集，未选中的物品ℛ初始化
为全集1, ⋯, 𝑛。
2. 选择精排分数reward# 最⾼的物品，从集合ℛ移到𝒮。
3. 做𝑘−1 轮循环：
a. 计算集合ℛ中所有物品的分数
MR!
!∈ℛ。
b. 选出分数最⾼的物品，将其从ℛ移到𝒮。
MMR多样性算法

## Page 8

滑动窗口
• MMR：argmax
#∈ℛ
𝜃⋅reward# −
1 −𝜃⋅max
$∈𝒮sim 𝑖, 𝑗
.

## Page 9

滑动窗口
• MMR：argmax
#∈ℛ
𝜃⋅reward# −
1 −𝜃⋅max
$∈𝒮sim 𝑖, 𝑗
.
• 已选中的物品越多（即集合𝒮越⼤），越难找出物品𝑖∈ℛ，
使得𝑖与𝒮中的物品都不相似。
• 设sim 的取值范围是[0, 1]。当𝒮很⼤时，多样性分数
max
$∈𝒮sim 𝑖, 𝑗
总是约等于1，导致MMR 算法失效。
• 解决⽅案：设置⼀个滑动窗⼝𝒲，⽐如最近选中的10 个物
品，⽤𝒲代替MMR 公式中的𝒮。

## Page 10

⋯
!"#$%&'( 𝒮)
*!"#$%&'( ℛ)
+,-.&'( 𝒲)
滑动窗口

## Page 11

⋯
!"#$%&'( 𝒮)
*!"#$%&'( ℛ)
+,-.&'( 𝒲)
滑动窗口
• 标准MMR：argmax
#∈ℛ
𝜃⋅reward# −
1 −𝜃⋅max
$∈𝒮sim 𝑖, 𝑗
.
• ⽤滑动窗⼝：argmax
#∈ℛ
𝜃⋅reward# −
1 −𝜃⋅max
$∈𝒲sim 𝑖, 𝑗
.

## Page 12

Thank You!
http://wangshusen.github.io/
