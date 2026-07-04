# 召回

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/08_Improvement_02.pdf

## Page 1

涨指标的方法：召回
王树森
http://wangshusen.github.io/

## Page 2

召回模型& 召回通道
• 推荐系统有⼏⼗条召回通道，它们的召回总量是固定的。
总量越⼤，指标越好，粗排计算量越⼤。
• 双塔模型（two-tower）和item-to-item（I2I）是最重要的
两类召回模型，占据召回的⼤部分配额。
• 有很多⼩众的模型，占据的配额很少。在召回总量不变的
前提下，添加某些召回模型可以提升核⼼指标。
• 有很多内容池，⽐如30天物品、1天物品、6⼩时物品、新
⽤户优质内容池、分⼈群内容池。
• 同⼀个模型可以⽤于多个内容池，得到多条召回通道。

## Page 3

双塔模型

## Page 4

改进双塔模型
• 简单正样本：有点击的（⽤户，物品）⼆元组。
• 简单负样本：随机组合的（⽤户，物品）⼆元组。
• 困难负样本：排序靠后的（⽤户，物品）⼆元组。
⽅向1：优化正样本、负样本。

## Page 5

改进双塔模型
• Baseline：⽤户塔、物品塔分别是全连接⽹络，各输出⼀
个向量，分别作为⽤户、物品的表征。
• 改进：⽤户塔、物品塔分别⽤DCN 代替全连接⽹络。
• 改进：在⽤户塔中使⽤⽤户⾏为序列（last-n）。
• 改进：使⽤多向量模型代替单向量模型。（标准的双塔模
型也叫单向量模型。）
⽅向2：改进神经⽹络结构。

## Page 6

改进双塔模型
!"#
$%#
⋯
!"#
!$#
%&#
'(#
⋯
!"&'
$%&'

## Page 7

改进双塔模型
!"#
$%#
⋯
!"#
!$#
%&#
'(#
⋯
!"&'
$%&'

## Page 8

改进双塔模型
!"#
$%#
⋯
!"#
!$#
%&#
'(#
⋯
!"&'
$%&'

## Page 9

改进双塔模型
!"#
$%#
⋯
!"#
!$#
%&#
'(#
⋯
!"&'
$%&'

## Page 10

改进双塔模型
!"#
$%#
⋯
!"#
!$#
%&#
'(#
⋯
!"&'
$%&'

## Page 11

改进双塔模型
• Baseline：做⼆分类，让模型学会区分正样本和负样本。
• 改进：结合⼆分类、batch 内负采样。（对于batch 内负
采样，需要做纠偏。）
• 改进：使⽤⾃监督学习⽅法，让冷门物品的embedding 学
得更好。
⽅向3：改进模型的训练⽅法。

## Page 12

Item-to-Item (I2I)

## Page 13

Item-to-Item (I2I)
• I2I 是⼀⼤类模型，基于相似物品做召回。
• 最常见的⽤法是U2I2I (user → item → item)。
• ⽤户𝑢喜欢物品𝑖!（⽤户历史上交互过的物品）。
• 寻找𝑖! 的相似物品𝑖"（即I2I）。
• 将𝑖" 推荐给𝑢。

## Page 14

Item-to-Item (I2I)
• I2I 是⼀⼤类模型，基于相似物品做召回。
• 最常见的⽤法是U2I2I (user → item → item)。
• 如何计算物品相似度？
• ⽅法1：ItemCF 及其变体。
• ⼀些⽤户同时喜欢物品𝑖! 和𝑖"，则认为𝑖! 和𝑖" 相似。
• ItemCF 、Online ItemCF、Swing、Online Swing 都是基于
相同的思想。
• 线上同时使⽤上述4 种I2I 模型，各分配⼀定配额。

## Page 15

Item-to-Item (I2I)
• I2I 是⼀⼤类模型，基于相似物品做召回。
• 最常见的⽤法是U2I2I (user → item → item)。
• 如何计算物品相似度？
• ⽅法1：ItemCF 及其变体。
• ⽅法2：基于物品向量表征，计算向量相似度。（双塔模
型、图神经⽹络均可计算物品向量表征。）

## Page 16

⼩众的召回模型

## Page 17

类似I2I 的模型
• U2U2I (user → user → item)：已知⽤户𝑢! 与𝑢" 相似，且
𝑢" 喜欢物品𝑖，那么给⽤户𝑢! 推荐物品𝑖。
• U2A2I (user → author → item)：已知⽤户𝑢喜欢作者𝑎，
且𝑎发布物品𝑖，那么给⽤户𝑢推荐物品𝑖。
• U2A2A2I (user → author → author → item)：已知⽤户𝑢
喜欢作者𝑎!，且𝑎! 与𝑎" 相似，𝑎" 发布物品𝑖，那么给
⽤户𝑢推荐物品𝑖。

## Page 18

更复杂的模型
• Path-based Deep Network (PDN) [1]
• Deep Retrieval [2]
• Sparse-Interest Network (SINE) [3]
• Multi-task Multi-view Graph Representation Learning (M2GRL) [4]
参考⽂献
1. Li et al. Path-based Deep Network for Candidate Item Matching in Recommenders. In SIGIR, 2021.
2. Gao et al. Learning an end-to-end structure for retrieval in large-scale recommendations. In CIKM,
2021.
3. Tan et al. Sparse-interest network for sequential recommendation. In WSDM, 2021.
4. Wang et al. M2GRL: A multitask multi-view graph representation learning framework for web-
scale recommender systems. In KDD, 2020.

## Page 19

总结：改进召回模型
• 双塔模型：优化正负样本、改进神经⽹络结构、改进训练
的⽅法。
• I2I 模型：同时使⽤ItemCF 及其变体、使⽤物品向量表征
计算物品相似度。
• 添加⼩众的召回模型，⽐如PDN、Deep Retrieval、SINE、
M2GRL 等模型。
• 在召回总量不变的前提下，调整各召回通道的配额。（可
以让各⽤户群体⽤不同的配额。）

## Page 20

Thank You!
http://wangshusen.github.io/
