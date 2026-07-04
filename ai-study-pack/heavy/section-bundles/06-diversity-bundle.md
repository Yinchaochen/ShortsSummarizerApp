# Section 6: 多样性

## Why This Section Matters

- Treat diversity as a retention and satisfaction problem, not as a cosmetic problem.
- Learn both simple and advanced approaches: rules, MMR, and DPP.

## Common Mistakes To Avoid

- Do not assume high relevance alone produces healthy long-term feeds.

## Topic List

- Official #29: `多样性的度量` (missing from the user's 42-part playlist)
- Official #30: `MMR 算法` (user playlist part `28`)
- Official #31: `规则约束` (user playlist part `29`)
- Official #32: `DPP：数学基础` (user playlist part `30`)
- Official #33: `DPP：多样性算法` (user playlist part `31`)

## Official Source Links

- Section note: https://github.com/wangshusen/RecommenderSystem/blob/main/Notes/06_Rerank.pdf
- `多样性的度量`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/06_Rerank_01.pdf`, Bilibili `https://www.bilibili.com/video/BV1ne4y1v7mC`, YouTube `https://youtu.be/uCIlk7N1dvk`
- `MMR 算法`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/06_Rerank_02.pdf`, Bilibili `https://www.bilibili.com/video/BV1dV4y1V7Kg`, YouTube `https://youtu.be/tCa4yackga0`
- `规则约束`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/06_Rerank_03.pdf`, Bilibili `https://www.bilibili.com/video/BV1om4y1F7y5`, YouTube `https://youtu.be/84kK1h0FS3Y`
- `DPP：数学基础`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/06_Rerank_04.pdf`, Bilibili `https://www.bilibili.com/video/BV1re411F7cp`, YouTube `https://youtu.be/HjpJeUSekKs`
- `DPP：多样性算法`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/06_Rerank_05.pdf`, Bilibili `https://www.bilibili.com/video/BV1Md4y1c7uB`, YouTube `https://youtu.be/wi8xVHiZZr4`

## Local Extract Files

- Note extract: `../source-extracts/notes/06_Rerank.md`
- `多样性的度量`: `../source-extracts/slides/06_Rerank_01.md`
- `MMR 算法`: `../source-extracts/slides/06_Rerank_02.md`
- `规则约束`: `../source-extracts/slides/06_Rerank_03.md`
- `DPP：数学基础`: `../source-extracts/slides/06_Rerank_04.md`
- `DPP：多样性算法`: `../source-extracts/slides/06_Rerank_05.md`

## Inline Note Extract

# 多样性 - Official Note

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Notes/06_Rerank.pdf

## Page 1

第六部分
重排
王树森 著

## Page 2

[No extractable text on this page]

## Page 3

第1 章多样性
工业界的实践经验表明多样性对推荐系统的业务指标有非常显著的影响，多样性做
得好，则用户使用推荐的时长、活跃用户数、用户留存等指标都会显著提升。如果你是
推荐系统的典型用户，你一定会发现这条经验非常符合你的直觉。举个例子，即便你非
常热爱篮球，满屏的NBA 视频也会让你感到枯燥和无趣，刷APP 的时间会变短，用户
粘性会降低，甚至会卸载APP。
本章介绍工业界推荐系统中多样性的标准做法。1.1 节介绍一些基础知识，包括物品
相似性的度量和提升多样性的主要思路。1.2 节讲解MMR 多样性算法，以及它与滑动窗
口、规则约束的结合。1.3 节讲解DPP 多样性算法，这节的数学很多，仅适合熟悉矩阵
的读者阅读。1.4 节介绍MGS 算法，它求解的问题与DPP 完全相同，但是算法实现更简
单。
1.1 背景
本节重点讨论两个问题。第一，相似性的度量。在推荐系统中，如果曝光给用户的
物品之间有较高的相似性，则说明多样性差。想要研究和提升多样性，我们首先需要定
义和量化物品两两之间的相似性。第二，提升多样性的方法。在推荐系统的链路上，应
该在哪些环节上做特殊处理，提升多样性？
1.1.1 度量物品相似性
度量相似的方法有很多种，其中最简单、计算速度最快的方法是使用物品的属性标
签，比如一级类目、二级类目、品牌、等等。如果两个物品的属性标签相同，则它们的
相似度为1，否则为0。举个例子，物品i 和j 的（一级类目、二级类目、品牌）分别是
（美妆，彩妆，香奈儿）和（美妆，香水，香奈儿），则它们的三种相似度分数为
sim1(i, j) = 1,
sim2(i, j) = 0,
sim3(i, j) = 1.
对三个分数求加权和即可得到相似度总分，其中的权重需要根据经验设置。值得注意的
是，类目、品牌等标签通常是由NLP 算法根据物品描述推断出的，未必准确。
工业界更主流的方法是基于向量表征计算相似度。设物品i 和j 的向量表征分别为
vi 和vj，如果|⟨vi, vj⟩| 越大，则说明两个物品越相似。其中值得研究和深挖的问题是如
何学习物品的向量表征。首先介绍一种错误的做法，即使用召回的双塔模型，利用其中的
物品塔将物品映射为向量表征。双塔模型基于用户与物品交互学习的物品向量表征，但
学到的物品向量表征不适用于重排多样性问题，主要原因有两点。第一，物品的曝光次
数有严重的头部效应，绝大部分的物品曝光次数很少，模型学不好它们的向量表征。第
二，这样学到的向量不是物品内容的表征，而是物品兴趣点的表征。两个物品封面图、标
题相似，但向量表征未必相似。

## Page 4

第1 章多样性
当前最先进的方法是用CLIP〔5〕预训练方法学习图文内容的向量表征。CLIP 的好处
是无需人工标注，可以用小红书的海量数据做预训练，学到的模型无需ﬁne-tune 即可用
于提取特征向量。如图1.1 所示，用CV 模型（比如卷积神经网络）提取图片的向量表
征ai，用NLP 模型（比如BERT）提取文本的向量表征bi。在小红书中，同一篇笔记
的图片和文字往往是相关的，因此(ai, bi) 组成一对正样本。如果从全体样本中随机抽取
笔记i 和j，那么(ai, bj) 组成一对负样本。在做训练时，应当最大化|⟨ai, bi⟩|，最小化
|⟨ai, bj⟩|。CLIP 正是基于这种思想，用batch 内负采样的方式预训练CV 模型和NLP 模
型。在做完推理之后，可以把ai、bi、[ai; bi]、或者ai + bi 作为笔记i 的向量表征，用
于计算笔记两两之间的相似度。
!!
图⽚
笔记#1:
\\
"!
⽂字
!"
图⽚
笔记#2:
\\
""
⽂字
!#
图⽚
笔记#$:
\\
"#
⽂字
⋮
正样本
负样本
图1.1: CLIP 方法如图所示，每篇笔记i 取一张图片和一段文字，分别映射为向量ai 和bi
1.1.2 提升多样性的方法
我们的目标是让曝光给用户的物品具有多样性，即物品之间的相似度较低。为了达
到这个目标，工业界普遍的做法是在粗排和精排之后，用后处理的方式提升物品的多样
性，如图1.2 所示。在推荐的链路上，粗排的精排的唯一任务就是准确地做pointwise 打
分，尽量准确地预估点击、交互、时长。pointwise 的意思是把每个物品作为独立的个体，
不考虑物品之间的关联。
召回
几亿
物品
几千
物品
后处理
粗排
几百
物品
精排
后处理
物品1
物品2
物品#
⋮
图1.2: 推荐系统的链路如图所示，精排的后处理通常被称作重排，而粗排的后处理通常不被视
作一个独立的环节
具体来说，召回通道返回数千物品，进入粗排环节。对于每个物品i，粗排模型预估
点击率、交互率等指标，并将预估指标融合成一个分数，记作rewardi，它反映用户对物

## Page 5

1.2 Maximal Marginal Relevance（MMR）
品i 的兴趣。此时有数千物品，每个物品i 带有一个兴趣分数rewardi。假如不考虑多样
性，那么只需根据rewardi 对数千物品做排序，选出分数最高的数百物品送入精排。精
排亦是如此，给数百物品做pointwise 打分，假如不考虑多样性，则选出分数最高的数十
物品曝光给用户。
如果考虑到多样性，在粗排、精排模型打分之后，该如何从候选物品中选出一个子
集呢？设每个候选物品i 带有一个模型打的分数rewardi，这个分数是用户对物品的兴趣，
可以视作物品本身的价值。做选择时，我们不能把每个物品看做一个独立的个体，而是
要考虑到物品之间的相似度，结合rewardi 与相似度分数，得到物品的总分。后面两节介
绍的MMR 和DPP 正是基于这样的思想。MMR 与DPP 的时间复杂度较高，可以用于精
排的后处理，但不能用于粗排的后处理。在精排之后从数百物品中选出数十个不需要很
大计算量，但是在粗排之后从数千物品中选出数百个则需要很大的计算量。粗排后处理
阶段的多样性算法与MMR、DPP 有相似之处，但此处的算法更简单、粗糙、速度更快。
1.2 Maximal Marginal Relevance（MMR）
设精排返回n 个物品，它们的精排分数记作reward1, · · · , rewardn。把物品i 和j
的相似度记作sim(i, j)，数值越大表示两个物品越相似。如图1.3 所示，用集合S 表示
已经选中的物品，用集合R 表示未选中的物品。对于集合R 中的物品i，定义marginal
relevance（MR）〔1〕：
MRi = θ · rewardi
!
"#
$
物品本身价值
−(1 −θ) · max
j∈S sim(i, j)
!
"#
$
多样性分数
.
(1.1)
公式右边第一项rewardi 的意思是物品i 的精排分数，比如预估点击率、点赞率等指标的
加权和，它的值越大对物品i 越有利。第二项maxj∈S sim(i, j) 表示候选物品i 与所有被
选中的物品之间的相似度。假如i 与某个被选中的物品j ∈S 相似，则maxj∈S sim(i, j)
较大，对物品i 起抑制作用，不利于物品i 被选中。公式中的θ 是介于0 和1 之间的超参
数，平衡精排分数与多样性。MMR 计算候选物品集合R 中每个物品的分数MRi，并选
出分数最高的物品：
argmax
i∈R
MRi.
(1.2)
选中的物品i 既要有高精排分数rewardi，也要不能与集合S 中的物品相似。
⋯
已选中的物品（记作!）
未选中的物品（记作ℛ）
图1.3: 集合S 包含已选中的物品，集合R 包含未选中的物品

## Page 6

第1 章多样性
1.2.1 算法描述与分析
MMR 算法概括如下。初始时，选中的物品集合S 为空集，而未选中物品集合R 为
全集[n] = {1, · · · , n}。首先选取rewardi 最大的作为第一个物品，将其从集合R 移到集
合S。然后用公式(1.2) 寻找当前最优的物品，将其从集合R 移到集合S。重复这个步骤
k −1 次，则S 中包含k 个物品，它们是最终曝光给用户的物品。
我们定义n 为候选物品的总数，k 为选出的物品数量，设物品用d 维向量表征，则
算法的时间复杂度为O(nk2 + nkd)。具体是这样分析的。当一个物品i 被选中时，我们
计算它与R 中所有物品的相似度，并将算出的|R| 个相似度存储。算法一共重复这个步
骤k −1 次，总共计算
(n −1) + (n −2) + · · · + (n −k) = O(nk)
个相似度分数。计算每个相似度分数的时间开销为O(d)，因此计算相似度的总时间开销
为O(nkd)。每一步还需要计算|R| 个分数MRi，计算每个MRi 的时间开销为|S|，因此
每一步的时间开销为O(|R| · |S|) = O(nk)。算法运行k −1 步，因此花在(1.2) 上的总时
间为O(nk2)。
1.2.2 滑动窗口
上述的标准MMR 算法在实践中有个缺点：当集合S 较大时，几乎不可能从R 中找
出一个物品i，使得i 与所有选中的物品j ∈S 都不相似。已经选中的物品越多，即S 越
大，则越难从R 中找出与S 不相似的物品。为了解决这个问题，实际实现MMR 的时候
可以滑动窗口（sliding window）来解决上述问题。如图1.4 所示，设定一个大小固定的滑
动窗口W，其中包含若干最新选中的物品。计算多样性分数的时候，用maxj∈W sim(i, j)
代替maxj∈S sim(i, j)，也就是说最新选出的物品i 只需要跟最近选中的|W| 个物品不相
似，而不需要跟S 中所有物品都不相似。用滑动窗口，则式(1.1) 变成
argmax
i∈R
%
θ · rewardi + (1 −θ) · max
j∈W sim(i, j)
&
.
与式(1.1) 相比，唯一的区别就是把S 替换成W。
⋯
已选中的物品（记作!）
未选中的物品（记作ℛ）
滑动窗⼝（记作#）
图1.4: 滑动窗口的示意图
设w = |W| 为滑动窗口的大小，它满足w ≤k。使用滑动窗口，则总时间复杂度
为O(nkd + nkw)，这说明滑动窗口可以减小计算量。具体是这样分析的。与之前相同，
总共需要计算O(nk) 个相似度分数，时间复杂度为O(nkd)。每一步需要计算|R| 个分
数MRi，计算每个MRi 所需时间开销为O(|W|)。因此每一步需要花费O(nw) 时间计算

## Page 7

1.3 Determinantal Point Process（DPP）
MR 分数，算法运行k −1 步，花费在计算MR 分数上的总时间为O(nkw)。
1.2.3 规则约束
实际重排的过程中，除了考虑rewardi 和多样性分数MRi，还需要加很多业务规则。
这些业务规则大多是为提升用户体验而制定的，它们是硬性的规则，必须要满足。下面
举几个例子。1
小红书推荐的笔记分为图文笔记和视频笔记，最多连续出现5 篇图文笔记，最多连
续出现5 篇视频笔记。举个例子，如果排名i 到i + 4 的全都是图文笔记，那么排
在i + 5 的必须是视频笔记。这类规则叫做“最多出连续出现k 篇某种笔记”，这个
例子中k = 5。
推荐的笔记中有运营推广笔记，这些笔记的rewardi 不是模型真实的打分，而是在
模型打分的基础上乘以了大于1 的系数，以此让运营推广的笔记获得更多流量。为
了限制运营推广的笔记曝光过多影响体验，限制条件为每9 篇最多出1 篇。也就是
说，如果排名i 的笔记是运营推广的，那么i + 1 到i + 8 都不能是运营推广的笔记。
这类规则叫做“每k 篇笔记最多出1 篇某种类型的笔记”，这个例子中k = 9。
排名最高的t 篇笔记是用户最容易看到的，而且有很大比例的用户不往下翻，因此
顶部的笔记质量要有保障。小红书推荐的笔记中有电商笔记，即在笔记图片或视频
下方有一个电商卡片。为了限制电商笔记在顶部曝光过多影响体验，限制条件为前
1 篇最多出0 篇（即第一篇笔记不能是电商笔记），前4 篇最多出1 篇。这类规则
叫做“前t 篇笔记最多有k 篇某种类型的笔记”，两个例子分别设置(t = 1, k = 0) 和
(t = 4, k = 1)。
重排需要将MMR 与规则相结合，在满足规则的前提下最大化MRi。MMR 每一轮都求解
argmaxi∈R MRi，此处的R 是未被选中的物品的集合。由于有多种规则约束，每一步都需
要先用规则做排除，从R 中剔除不符合规则的，得到的子集记作R′。求解argmaxi∈R′ MRi
得到既符合规则，也具有高价值和多样性的物品。
1.3 Determinantal Point Process（DPP）
精排输出n 个候选物品，已知它们的向量表征v1, · · · , vn ⊂Rd，并且有精排的打
分reward1, · · · , rewardn。我们的目标是从中选出k 个物品，记作集合S，它是[n] =
{1, · · · , n} 的子集。与MMR 类似，DPP 的目标是让集合S 中的物品既有很高的价值，也
具有多样性。两种方法的区别在于如何衡量多样性，MMR 用物品两两之间的相似度衡
量多样性，而DPP 用行列式衡量整个集合S 的多样性。
1例子中的数字不是小红书业务中的真实数据

## Page 8

第1 章多样性
1.3.1 超平行体（parallelotope）
给定一组向量v1, · · · , vk ∈Rd，而且满足k ≤d。那么Rd 空间中的k-维超平行体
定义为：
P
'
v1, · · · , vk
(
=
)
α1v1 + · · · + αkvk
** 0 ≤α1, · · · , αk ≤1
+
.
向量v1, · · · , vk ∈Rd 被称作超平形体的边，它们唯一确定一个超平形体。超平行体中的
点是向量v1, · · · , vk ∈Rd 的线性组合，且系数取值为α1, · · · , αk ∈[0, 1]。如图1.5 所示，
平行四边形（parallelograms）平行六面体（parallelepiped）分别是k = 2 和k = 3 时的超
平行体。
!!
!"
(a) 平行四边形
!!
!"
!#
(b) 平行六面体
图1.5: 平行四边形和平行六面体都是特殊的超平行体
计算超平行体的体积，需要对向量v1, · · · , vk 做正交化。以图1.6 中的平行四边形
为例，它的面积等于底和高的长度相乘。如果我们以v1 为底，那么高就是
q2 = v2 −Projv1(v2),
其中
Projv1(v2) = ⟨v1, v2⟩
∥v1∥2
v1.
上式中的Projv1(v2) 表示将v2 投影到v1 的方向上。不难证明，v1 与q2 正交，即两个
向量的內积等于零。平行四边形P 的面积等于
vol(P) =
,,v1
,,
2 ·
,,q2
,,
2.
!!
!"
""
Proj!! %"
(a) 平行四边形
!!
!"
!#
""
"#
(b) 平行六面体
图1.6: 超平行体的体积示意图

## Page 9

1.3 Determinantal Point Process（DPP）
以图1.6 中的平行六面体为例，它的体积等于底面积乘以高。前面推导过，底面积等
于∥v1∥2 · ∥q2∥2。平行六面体的高等于
q3 = v3 −Projv1(v3) −Projq2(v3).
不难证明，q3 与v1 正交，而且q3 也与q2 正交。平行六面体P 的体积等于
vol(P) =
,,v1
,,
2 ·
,,q2
,,
2 ·
,,q3
,,
2.
上面超平形体的定义要求k ≤d。举个例子，当k = 2、d = 3 时，3 维空间中的
向量v1, v2 ∈R3 可以确定一个平行四边形，即3 维空间中的2 维超平形体。但是当
k = 3、d = 2 时，2 维空间中的向量v1, v2, v3 ∈R2 不可能确定一个平行六面体。此外，
v1, · · · , vk ∈Rd 线性无关时，超平形体才是有意义的。否则，如果它们线性相关，则它
们会落到一个k −1 维的超平面上，导致它们组成的k 维超平形体的体积等于0。
1.3.2 多样性的度量
接下来，我们讨论如何度量向量的多样性。给定k 个向量v1, · · · , vk ∈Rd，且它们
满足∥vi∥2 = 1，∀i。我们做如下假设：如果向量vi 和vj 接近于平行，即內积v⊤
i vj 接
近1 或−1，则认为物品i 与j 相似；如果向量vi 和vj 接近于正交，即內积v⊤
i vj 接近
0，则认为物品i 与j 不相似。
基于以上假设，我们可以用超平行体P(v1, · · · , vk) 的体积衡量衡量k 个物品的多
样性，它的体积介于0 和1 之间。当向量v1, · · · , vk 两两正交时，物品的多样性最大化，
此时的超平行体是一个正方体，它的体积等于1。假如存在两个向量vi = ±vj，这说明
物品的多样性不足，此时的超平行体的体积最小化，等于0。
超平行体的体积与行列式之间存在某些数学上的等价性。把向量v1, · · · , vk ∈Rd 作
为矩阵V 的列，即
V
= [v1, v2, · · · , vk] ∈Rd×k.
当k = d 时，V 是方阵，行列式与体积有这样的关系：
** det
'
V
( ** = vol
-
P
'
v1, · · · , vk
( .
.
(1.3)
V ⊤V 是大小为k × k 的对称半正定矩阵，它的行列式非负。当k ≤d 时，V ⊤V 的行列
式与体积有这样的关系：
det
'
V ⊤V
(
= vol
-
P
'
v1, · · · , vk
( .2
.
(1.4)
式(1.3) 是数学中一个众所周知的结论，它的的证明在很多书中都有，比如〔4〕。式(1.4)
并不是一个很显然的结论，因此本书对其做严格的证明。如果不感兴趣，或者对矩阵不
熟悉，可以跳过下面的证明。
证明由于k ≤d，对于任何一个d 维空间中的k 维子空间H，我们可以找到一个d × d
的正交矩阵R，对向量v1, · · · , vk ∈Rd 做旋转，使得Rv1, · · · , Rvk ∈Rd 全部落在子
空间H 上。设子空间H 由前k 个标准正交基组成，那么H 上所有点的后d −k 个元素

## Page 10

第1 章多样性
恒等于零：
Rvi =

ui

.
上式中的ui 是k 维向量，0 是d−k 维的全零向量。把v1, · · · , vk ∈Rd 作为矩阵V ∈Rd×k
的列，把u1, · · · , uk ∈Rk 作为矩阵U ∈Rk×k 的列，那么有
RV =

U

.
(1.5)
很显然，{ui} 组成的超平行体与{[ui; 0]} 组成的超平行体有相同的体积：
vol
-
P
'
u1, · · · , uk
(.
= vol

P



u1

, · · · ,

uk





.
根据ui 的定义有Rvi = [ui; 0]，结合上式可得
vol
-
P
'
u1, · · · , uk
(.
= vol
-
P
'
Rv1, · · · , Rvk
(.
.
(1.6)
Rvi 相当于对vi 做旋转，而对超平行体做旋转不影响体积，因此有
vol
-
P
'
Rv1, · · · , Rvk
(.
= vol
-
P
'
v1, · · · , vk
(.
.
(1.7)
结合式(1.6) 和(1.7) 可得
vol
-
P
'
u1, · · · , uk
(.
= vol
-
P
'
v1, · · · , vk
(.
.
(1.8)
R 是d × d 的正交矩阵，由正交矩阵的性质可得R⊤R = Id（即d × d 的单位矩阵）。因
此有(RV )⊤(RV ) = V ⊤(R⊤R)V = V ⊤V 。结合式(1.5) 可得
V ⊤V
= (RV )⊤(RV ) =
U ⊤

U

= U ⊤U.
由于U 是k × k 的方阵，由行列式的性质可得det(U ⊤U) = det(U)2。结合上式可得
det
'
V ⊤V
(
= det
'
U ⊤U
(
= det
'
U
(2.
(1.9)
由于U 是方阵，由式(1.3) 可得
det
'
U
(2 = vol
-
P
'
u1, · · · , uk
( .2
.
(1.10)
结合式(1.8)、(1.9)、(1.10) 可得
det
'
V ⊤V
(
= det
'
U
(2 = vol
-
P
'
u1, · · · , uk
( .2
= vol
-
P
'
v1, · · · , vk
( .2
.
由上式可证(1.4)。
□
1.3.3 k-DPP
前面介绍了超平行体、体积、行列式，接下来我们回到本节的主题——用DPP 解决
重排多样性问题。精排给n 个物品打分，它们的向量表征为v1, · · · , vn ∈Rd。我们希望
选出k 个物品，使得它们组成的超平行体P(v1, · · · , vk) 的体积较大。计算一个n × n 的
相似度矩阵A，它是一个n×n 的对称半正定矩阵，它的第(i, j) 个元素等于aij = v⊤
i vj。
设集合S 是[n] 的子集，它的大小为k。定义AS 是A 的一个子矩阵，大小为k × k。如

## Page 11

1.3 Determinantal Point Process（DPP）
果i, j ∈S，则aij 是AS 的一个元素。
我们根据集合S（|S| = k）从v1, · · · , vn ⊂Rd 中选出k 个向量，作为矩阵VS ∈Rd×k
的列。那么k × k 的矩阵AS 可以写成：
AS = V ⊤
S VS.
设P(VS) 为选出的k 个向量张成的超平行体。如果k ≤d，那么由式(1.4) 可得：
det
'
AS
(
= det
'
V ⊤
S VS
(
= vol
'
P
'
VS
( (2.
对等式两边取对数，可得
log det
'
AS
(
= 2 · log vol
'
P
'
VS
( (
.
(1.11)
前面已经讨论过，可以用超平行体的体积度量集合S 中物品的多样性，因此也可以用
log det
'
AS
(
度量多样性。k-DPP 是一种传统的统计机器学习方法，它的目标函数是选出
k 个物品组成集合S，使得log det(AS) 最大化。
Hulu 2018 年的论文〔2〕将k-DPP 应用于推荐系统重排，具体为求解这个优化问题：
argmax
S:|S|=k
θ ·
9:
i∈S
rewardi
;
+ (1 −θ) · log det(AS).
(1.12)
这是个组合优化问题，从n 个物品中选出k 个，目前没有高效的方法精确求解这个组合
优化问题。Hulu 的论文使用一种贪心算法求解(1.12)。用S 表示已选中的物品，用R 表
示未选中的物品。算法的每一步求解这样一个问题：
argmax
i∈R
<
θ · rewardi + (1 −θ) · log det
'
AS∪{i}
(=
.
(1.13)
暴力求解(1.13) 是可行的，但是计算量比较大。想要计算行列式det
'
AS∪{i}
(
，需要对矩
阵做特征分解，代价是O
'
|S|3(
。对于每一个i ∈R，都需要做特征分解，因此暴力求解
(1.13) 的代价是O
'
|S|3 · |R|
(
= O(nk3)。如果是从n 个物品中选出k 个物品，即让集合
S 的大小从1 增长到k，那么需要求解(1.13) 一共k 次，总的代价是O(nk4)。Hulu 的论
文提出了一种巧妙的数值算法求解(1.13)，原理是已知AS 的矩阵分解，可以快速算出
AS∪{i} 的矩阵分解，从而让行列式的计算变得更快。
1.3.4 数值算法推导
接下来我们推导Hulu 论文中求解(1.13) 的数值算法，对数学不感兴趣的读者可以跳
过。给定矩阵A，算法总共花费O(nk2) 时间选出k 个物品。因为AS 是对称正定矩阵，
所以它存在Cholesky 分解AS = LL⊤，这里的L 是大小为|S|×|S| 的下三角矩阵。下三
角矩阵的意思是对角线以上的元素都为零。矩阵AS∪{i} 比AS 多了一行和一列，记作：
AS∪{i} =

AS
ai
a⊤
i
aii

.
(1.14)

## Page 12

第1 章多样性
上式中的ai 的元素是v⊤
i vj，∀j ∈S，而aii = v⊤
i vi = 1。矩阵AS∪{i} 的Cholesky 分解
可以写作：
AS∪{i} =

L
c⊤
i
di



L
c⊤
i
di


⊤
,
(1.15)
其中ci 和di 是未知的。由公式(1.16) 和(1.15) 可得：
AS∪{i} =

AS
ai
a⊤
i

=

LL⊤
Lci
c⊤
i L⊤
c⊤
i ci + d2
i

.
我们得到两个公式：
ai = Lci
和
1 = cT
i ci + d2
i .
L 和ai 是已知的，L 是上一轮算出的Cholesky 分解，ai 包含矩阵A 的元素。我们需
要求出未知的ci 和di。由于L 是下三角矩阵，只需要O(|S|2) 的浮点数运算即可求出
ci = L−1ai。然后就可以算出d2
i = 1 −c⊤
i ci。有了di，我们就能快速求出det(AS∪{i})。
由下三角矩阵和行列式的定义可知：
det



L
c⊤
i
di



= det(L) × di.
由于det(XY ) = det(X) det(Y )，我们得到
det
'
AS∪{i}
(
= det




L
cT
i
di



L
c⊤
i
di


⊤

= det(L)2 × d2
i .
贪心算法的公式(1.13) 可以等价写作：
argmax
i∈R
θ · rewardi + (1 −θ) ·
-
log det(L)2 + log d2
i
.
由于L 与i 无关，上面的公式可以等价写作
argmax
i∈R
θ · rewardi + (1 −θ) · log d2
i .
(1.16)
这样我们就推导出了求解k-DPP 的贪心算法：
1. 输入：n 个物品的向量表征v1, · · · , vn ∈Rd 和分数reward1, · · · , rewardn。
2. 计算n × n 的相似度矩阵A，它的第(i, j) 个元素等于aij = vT
i vj。时间复杂度为
O(n2d)。
3. 选中reward 分数最高的物品，记作i。初始化集合S = {i} 和1 × 1 的矩阵L =
@
A
。（由于aii = v⊤
i vi = 1，此时AS = [aii] = LL⊤。）
4. 做循环，从t = 1 到k −1：
(a). 对于每一个i ∈R：
I. 行向量[aT
i , 1] 是矩阵AS∪{i} 的最后一行。
II. 求解线性方程组ai = Lci，得到ci。时间复杂度为O(|S|2)。
III. 计算d2
i = 1 −cT
i ci。
(b). 求解(1.16)：i⋆= argmaxi∈R θ · rewardi + (1 −θ) · log d2
i .
(c). 更新集合S ←S ∪{i⋆}。

## Page 13

1.4 Modiﬁed Gram-Schmidt（MGS）
(d). 更新下三角矩阵：
L ←

L
cT
i⋆
di⋆

.
5. 返回集合S，其中包含k 个物品。
该算法总时间复杂度为O(n2d + nk3)。如果进一步优化线性方程组ai = Lci 的求解，那
么总时间复杂度可以降低到O(n2d + nk2)。原理是在第t 轮循环中，利用第t −1 轮对
ai = Lci 的求解。这里的数学有点复杂，就不展开介绍了。
1.3.5 DPP 多样性算法的扩展
上节介绍的滑动窗口方法适用于DPP，而且对DPP 是非常必要的。设集合S 为选中
的物品的集合，R 为未选中的物品的集合。DPP 用AS 的行列式衡量集合S 的多样性，
算法的每一步求解这样一个问题：
argmax
i∈R
<
θ · rewardi + (1 −θ) · log det
'
AS∪{i}
(=
.
DPP 存在一个严重的问题，随着集合S 逐渐变大，其中必然会包含很多相似的物品，这
会导致行列式det(AS) 会坍缩到0，它的对数会接近负无穷。因此，实践中会用滑动窗
口方法解决这个问题。方法跟1.2 节类似，设置一个较小的滑动窗口W，把上式中的S
替换成W：
argmax
i∈R
<
θ · rewardi + (1 −θ) · log det
'
AW∪{i}
(=
.
(1.17)
求解这个问题的数值算法比较复杂，此处就不详细推导了。
上节介绍的规则约束也适用于DPP，具体用法几乎一样。DPP 的每一步都求解式
(1.17)，从集合R 中选出一个物品。如果应用规则约束，则会排除掉R 中的部分物品，得
到子集R′ ⊂R。把式(1.17) 中的R 替换成R′，得到
argmax
i∈R′
<
θ · rewardi + (1 −θ) · log det
'
AW∪{i}
(=
.
(1.18)
数值算法几乎没有任何区别。由于候选集R 缩小成R′，实际的计算量会有所减小。
1.4 Modiﬁed Gram-Schmidt（MGS）
上节介绍了DPP，它是重排中最常用的多样性算法，而且算法的时间复杂度不高，可
以比较快地从几百个物品中选出几十个。DPP 的一个缺点在于数值算法的编程实现比较
复杂，如果实现得不好，算法效率不高。小红书论文〔3〕提出用MGS 算法求解DPP，算
法的实现非常简单。
1.4.1 向量正交化
Gram-Schmidt（GS）正交化︒给定一组线性独立（linearly independent）的向量
v1, · · · , vk ∈Rd，而且满足k ≤d。GS 输出一组正交基q1, · · · , qk ∈Rd，对于任意

## Page 14

第1 章多样性
i ̸= j，满足q⊤
i qj = 0。GS 具体这样做计算：
q1
=
v1,
q2
=
v2 −Projq1(v2),
q3
=
v3 −Projq1(v3) −Projq2(v3),
q4
=
v4 −Projq1(v4) −Projq2(v4) −Projq3(v4),
...
qk
=
vk −
k−1
:
i=1
Projqi(vk).
上式中的投影符号定义为
Proju(v) = ⟨u, v⟩
∥u∥2
u.
不难发现，超平行体
P
'
v1, · · · , vk
(
=
)
α1v1 + · · · + αkvk
** 0 ≤α1, · · · , αk ≤1
+
的体积等于
vol
'
P
(
=
,,q1
,,
2 ·
,,q2
,,
2 · · ·
,,qk
,,
2.
(1.19)
设v1, · · · vk 都是单位向量，即二范数等于1。当v1, · · · vk 两两正交时，超平行体P 是
一个正方体，此时它的体积最大化，vol(P) 等于1。当v1, · · · vk 接近线性相关，即存在
i 和β1, · · · , βk 使得
vi ≈
:
j̸=i
βjvj,
此时超平行体P 的体积接近0。GS 算法得到的正交基不唯一。如果调整GS 算法中
v1, · · · , vk 的顺序，那么输出的q1, · · · , qk 会截然不同，但这并不会影响体积公式(1.19)
的正确性。如图1.7 所示，调整v1, · · · , vk 的顺序，只是会影响谁是底、谁是高，而算出
的体积仍然是相同的。
!! = #!
#"
!"
(a) 以q1 为底，以q2 为高
!!
"" = !"
"!
(b) 以q2 为底，以q1 为高
图1.7: 如果调整GS 算法中v1 和v2 的顺序，那么得到的q1 和q2 完全不同，但计算面积的公式
vol(P) = ∥q1∥2 · ∥q2∥2 仍然正确
Modiﬁed Gram-Schmidt（MGS）正交化︒GS 存在数值稳定性问题，即在算法运行

## Page 15

1.4 Modiﬁed Gram-Schmidt（MGS）
的过程中，误差会累加，导致GS 输出的q1, · · · , qk 并非严格的正交基。实践中更常用
MGS，它与GS 在数学上完全等价，但是数值更稳定。
在上文的讨论中，算法的输入是一组线性独立的向量v1, · · · , vk ∈Rd，且有k ≤d。
而在重排的场景下，算法的输入是向量v1, · · · , vn ∈Rd，我们需要从n 个物品中选出k
个，且有n ≫d ≥k。MGS 首先做初始化：
q1 ←−v1, q2 ←−v2,
· · · ,
qn ←−vn.
MGS 算法运行k −1 步：
第2 步：
q2 ←−q2 −Projq1(q2),
· · · ,
qn ←−qn −Projq1(qn);
第3 步：
q3 ←−q3 −Projq2(q3),
· · · ,
qn ←−qn −Projq2(qn);
第4 步：
q4 ←−q4 −Projq3(q4),
· · · ,
qn ←−qn −Projq3(qn);
...
第k 步：
qk ←−qk −Projqk−1(qk),
· · · ,
qn ←−qn −Projqk−1(qn);
对于任意的t ≤k，在第t 步之后，向量q1, · · · , qt 是一组正交基。此外，对于任意i > t
和j ≤t，有q⊤
i qj = 0。如图1.8 中例子所示，在MGS 算法运行2 步之后，算出的q3 和
q4 均正交于v1 和v2 张成的平面。
!!
!"
"#
!#
"$
!$
图1.8: 在MGS 运行2 步之后，算出的q3 和q4 正交于v1 和v2 张成的平面
1.4.2 MGS 求解DPP
跟上一节的设定相同，精排输出n 个物品，它们的向量表征为v1, · · · , vn ⊂Rd，且
它们满足∥v1∥2 = · · · = ∥vn∥2 = 1。用集合S 表示已经选中的物品，用集合R 表示未选
中的物品。为了记号方便，设vol(S) 为集合S 中物品向量组成的超平行体的体积。由于
体积与行列式的等价性，DPP 多样性算法可以等价写作：
argmax
i∈R
<
θ · rewardi + (1 −θ) · log vol
'
S ∪{i}
(=
.
(1.20)
下面我们利用MGS 多样性算法求解式(1.20)。
为了符号方便，我们假设在MGS 的第t 步结束后，选中的物品为S = {1, · · · , t}，
未选中的物品为R = {t + 1, · · · , n}。MGS 输出的向量为q1, · · · , qn。MGS 算法保证：

## Page 16

第1 章多样性
向量q1, · · · , qt 是一组正交基，即它们两两正交。
对于任意的i > t 和j ≤t，有q⊤
i qj = 0。
因此，根据体积公式可得：
vol
'
S
(
= ∥q1∥2 · ∥q2∥2 · · · ∥qt∥2,
vol
'
S ∪{i}
(
= vol
'
S
(
· ∥qi∥2.
由上可得：
log vol
'
S ∪{i}
(
= log vol
'
S
(
+ log
'
∥qi∥2
(
.
由于log vol(S) 与物品i 无关，式(1.20) 可以等价写作：
argmax
i∈R
<
θ · rewardi + (1 −θ) · log
'
∥qi∥2
(=
.
(1.21)
MGS 多样性算法概括如下：
1. 初始时，选择reward 最大的物品加入集合S。为了记号方便，我们假设选中了1 号
物品，那么此时S = {1}，R = {2, · · · , n}。
2. 做循环，从t = 2 到k：
(a). 做正交化，时间复杂度为O(|R| · d)。
qt ←−qt −Projqt−1(qt),
· · · ,
qn ←−qn −Projqt−1(qn).
(b). 求解(1.21)，从R 中选出物品i。交换物品i 与t 的位置，然后把物品t 从集
合R 移到S。此时S = {1, · · · , t}，R = {t + 1, · · · , n}。
算法循环k −1 步，每步的时间复杂度为O(|R| · d) = O(nd)，因此算法总的时间复杂度
为O(ndk)。

## Page 17

参考文献
[1] J. Carbonell, J. Goldstein. The use of MMR, diversity-based reranking for reordering documents and producing
summaries. International ACM SIGIR Conference on Research and Development in Information Retrieval. 1998
335–336
[2] L. Chen, G. Zhang, E. Zhou. Fast greedy map inference for determinantal point process to improve recommen-
dation diversity. Advances in Neural Information Processing Systems (NIPS), 2018. 31
[3] Y. Huang, W. Wang, L. Zhang, R. Xu. Sliding spectrum decomposition for diversiﬁed recommendation. the 27th
ACM SIGKDD Conference on Knowledge Discovery & Data Mining (KDD). 2021 3041–3049
[4] D. Margalit, J. Rabinoﬀ, L. Rolen. Interactive linear algebra. Georgia Institute of Technology, 2017
[5] A. Radford, J. W. Kim, C. Hallacy, A. Ramesh, G. Goh, S. Agarwal, G. Sastry, A. Askell, P. Mishkin, J. Clark,
et al. Learning transferable visual models from natural language supervision. International Conference on
Machine Learning (ICML). 2021 8748–8763

## Inline Slide Extracts

### 多样性的度量

# 多样性的度量

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/06_Rerank_01.pdf

## Page 1

推荐系统中的多样性
王树森
http://wangshusen.github.io/

## Page 2

物品相似性的度量

## Page 3

相似性的度量
• 基于物品属性标签。
• 类⽬、品牌、关键词……
• 基于物品向量表征。
• ⽤召回的双塔模型学到的物品向量（不好）。
• 基于内容的向量表征（好）。

## Page 4

基于物品属性标签
• 物品属性标签：类⽬、品牌、关键词……
• 根据⼀级类⽬、⼆级类⽬、品牌计算相似度。
• 物品𝑖：美妆、彩妆、⾹奈⼉。
• 物品𝑗：美妆、⾹⽔、⾹奈⼉。
• 相似度：sim! 𝑖, 𝑗= 1，sim" 𝑖, 𝑗= 0，sim# 𝑖, 𝑗= 1。

## Page 5

双塔模型的物品向量表征
物品特征
𝐛
𝐚
⽤户特征
余弦相似度：cos 𝐚, 𝐛
⽤户塔
物品塔
物品向量表征

## Page 6

基于图文内容的物品表征
CNN
BERT

## Page 7

基于图文内容的物品向量表征
• CLIP [1] 是当前公认最有效的预训练⽅法。
• 思想：对于图⽚—⽂本⼆元组，预测图⽂是否匹配。
• 优势：无需⼈⼯标注。⼩红书的笔记天然包含图⽚+
⽂字，⼤部分笔记图⽂相关。
参考⽂献：
1. Radford et al. Learning transferable visual models from natural language
supervision. In ICML, 2021.

## Page 8

基于图文内容的物品表征
⽂字:
图⽚:
⋮

## Page 9

基于图文内容的物品表征
正样本
⋮
⽂字:
图⽚:

## Page 10

负样本
基于图文内容的物品表征
• ⼀个batch 内有𝑚对正样本。
• ⼀张图⽚和𝑚−1 条⽂本组成
负样本。
• 这个batch 内⼀共有𝑚𝑚−1
对负样本。
⋮
⽂字:
图⽚:

## Page 11

提升多样性的⽅法

## Page 12

推荐系统的链路
召回
几亿
物品
几千
物品
后处理
粗排
几百
物品
精排
后处理
物品1
物品2
物品𝑘
⋮
• 粗排和精排⽤多⽬标模型对物品做pointwise打分。
• 对于物品𝑖，模型输出点击率、交互率的预估，融
合成分数reward!。
• reward! 表⽰⽤户对物品𝑖的兴趣，即物品本⾝价
值。

## Page 13

推荐系统的链路
召回
几亿
物品
几千
物品
后处理
粗排
几百
物品
精排
后处理
物品1
物品2
物品𝑘
⋮
• 给定𝑛个候选物品，排序模型打分
reward", ⋯, reward#。
• 从𝑛个候选物品中选出𝑘个，既要它们的
总分⾼，也需要它们有多样性。

## Page 14

推荐系统的链路
召回
几亿
物品
几千
物品
后处理
粗排
几百
物品
精排
后处理
物品1
物品2
物品𝑘
⋮
被称为“重排”
也需要多样性算法

## Page 15

Thank You!
http://wangshusen.github.io/

### MMR 算法

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

### 规则约束

# 规则约束

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/06_Rerank_03.pdf

## Page 1

重排的规则
王树森
http://wangshusen.github.io/

## Page 2

重排的规则
• ⼩红书推荐系统的物品分为图⽂笔记、视频笔记。
• 最多连续出现𝑘= 5 篇图⽂笔记，最多连续出现𝑘= 5
篇视频笔记。
• 如果排𝑖到𝑖+ 4 的全都是图⽂笔记，那么排在𝑖+ 5
的必须是视频笔记。
规则：最多连续出现𝑘篇某种笔记
注：不是小红书的真实数据

## Page 3

重排的规则
• 运营推广笔记的精排分会乘以⼤于1 的系数（boost），
帮助笔记获得更多曝光。
• 为了防⽌boost 影响体验，限制每𝑘= 9 篇笔记最多
出现1 篇运营推广笔记。
• 如果排第𝑖位的是运营推广笔记，那么排𝑖+ 1 到𝑖+
8 的不能是运营推广笔记。
规则：每𝑘篇笔记最多出现1 篇某种笔记
注：不是小红书的真实数据

## Page 4

重排的规则
• 排名前𝑡篇笔记最容易被看到，对⽤户体验最重要。
（⼩红书的top 4 为⾸屏）
• ⼩红书推荐系统有带电商卡⽚的笔记，过多可能会影
响体验。
• 前𝑡= 1 篇笔记最多出现𝑘= 0 篇带电商卡⽚的笔记。
• 前𝑡= 4 篇笔记最多出现𝑘= 1 篇带电商卡⽚的笔记。
规则：前𝑡篇笔记最多出现𝑘篇某种笔记
注：不是小红书的真实数据

## Page 5

MMR + 重排规则
• MMR 每⼀轮选出⼀个物品：
argmax
!∈ℛ
𝜃⋅reward! −
1 −𝜃⋅max
$∈𝒲sim 𝑖, 𝑗
.
ℛ是未选中的物品
MR! 分数

## Page 6

MMR + 重排规则
• MMR 每⼀轮选出⼀个物品：
argmax
!∈ℛ
𝜃⋅reward! −
1 −𝜃⋅max
$∈𝒲sim 𝑖, 𝑗
.
ℛ是未选中的物品
• 重排结合MMR 与规则，在满⾜规则的前提下最⼤化MR。
• 每⼀轮先⽤规则排除掉ℛ中的部分物品，得到⼦集ℛ&。
MR! 分数

## Page 7

MMR + 重排规则
• MMR 每⼀轮选出⼀个物品：
argmax
!∈ℛ
𝜃⋅reward! −
1 −𝜃⋅max
$∈𝒲sim 𝑖, 𝑗
.
把ℛ替换成⼦集ℛ"
• 重排结合MMR 与规则，在满⾜规则的前提下最⼤化MR。
• 每⼀轮先⽤规则排除掉ℛ中的部分物品，得到⼦集ℛ&。
• MMR 公式中的ℛ替换成⼦集ℛ&，选中的物品符合规则。

## Page 8

Thank You!
http://wangshusen.github.io/

### DPP：数学基础

# DPP：数学基础

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/06_Rerank_04.pdf

## Page 1

DPP：数学基础
王树森
http://wangshusen.github.io/

## Page 2

超平行体
𝒗!
𝒗"
• 2 维空间的超平⾏体为平⾏四边形。
• 平⾏四边形中的点可以表⽰为：
𝒙= 𝛼!𝒗! + 𝛼"𝒗".
• 系数𝛼! 和𝛼" 取值范围是0, 1 。

## Page 3

超平行体
𝒗!
𝒗"
𝒙
• 2 维空间的超平⾏体为平⾏四边形。
• 平⾏四边形中的点可以表⽰为：
𝒙= 𝛼!𝒗! + 𝛼"𝒗".
• 系数𝛼! 和𝛼" 取值范围是0, 1 。
= !
" 𝒗! + !
" 𝒗"

## Page 4

超平行体
• 2 维空间的超平⾏体为平⾏四边形。
• 平⾏四边形中的点可以表⽰为：
𝒙= 𝛼!𝒗! + 𝛼"𝒗".
• 系数𝛼! 和𝛼" 取值范围是0, 1 。
𝒗!
𝒗"
𝒙= 𝒗! + 𝒗"

## Page 5

超平行体
• 3 维空间的超平⾏体为平⾏六⾯体。
• 平⾏六⾯体中的点可以表⽰为：
𝒙= 𝛼!𝒗! + 𝛼"𝒗" + 𝛼#𝒗#.
• 系数𝛼!, 𝛼", 𝛼# 取值范围是0, 1 。
𝒗!
𝒗"
𝒗#

## Page 6

超平行体
• ⼀组向量𝒗!, ⋯, 𝒗$ ∈ℝ% 可以确定⼀个𝑘维超平⾏体：
𝒫𝒗!, ⋯, 𝒗$ = 𝛼!𝒗! + ⋯+ 𝛼$𝒗$ | 0 ≤𝛼!, ⋯, 𝛼$ ≤1 .

## Page 7

超平行体
• ⼀组向量𝒗!, ⋯, 𝒗$ ∈ℝ% 可以确定⼀个𝑘维超平⾏体：
𝒫𝒗!, ⋯, 𝒗$ = 𝛼!𝒗! + ⋯+ 𝛼$𝒗$ | 0 ≤𝛼!, ⋯, 𝛼$ ≤1 .
• 要求𝑘≤𝑑，⽐如𝑑= 3 维空间中有𝑘= 2 维平⾏四边形。
• 如果𝒗!, ⋯, 𝒗$ 线性相关，则体积vol 𝒫= 0。（例：有𝑘=
3 个向量，落在⼀个平⾯上，则平⾏六⾯体的体积为0。）

## Page 8

平行四边形的面积
• ⾯积=
底
" ×
⾼
"。
• 以𝒗! 为底，计算⾼𝒒"，两个向量
必须正交。
𝒗!
𝒗"
𝒒"
⾼
底

## Page 9

平行四边形的面积
• 计算𝒗" 在𝒗! 上的投影：
Proj𝒗! 𝒗" =
𝒗!"𝒗#
𝒗!
#
# ⋅𝒗!.
𝒗!
𝒗"
Proj𝒗! 𝒗"
以𝒗! 为底，如何计算⾼𝒒"？

## Page 10

平行四边形的面积
• 计算𝒗" 在𝒗! 上的投影：
Proj𝒗! 𝒗" =
𝒗!"𝒗#
𝒗!
#
# ⋅𝒗!.
• 计算𝒒" = 𝒗" −Proj𝒗! 𝒗" 。
• 性质：底𝒗! 与⾼𝒒" 正交。
𝒗!
𝒗"
𝒒"
Proj𝒗! 𝒗"
以𝒗! 为底，如何计算⾼𝒒"？

## Page 11

平行四边形的面积
• 计算𝒗! 在𝒗" 上的投影：
Proj𝒗# 𝒗! =
𝒗!"𝒗#
𝒗#
#
# ⋅𝒗".
𝒗!
𝒗"
以𝒗" 为底，如何计算⾼𝒒!？
Proj𝒗" 𝒗#

## Page 12

平行四边形的面积
• 计算𝒗! 在𝒗" 上的投影：
Proj𝒗# 𝒗! =
𝒗!"𝒗#
𝒗#
#
# ⋅𝒗".
• 计算𝒒! = 𝒗! −Proj𝒗# 𝒗! 。
• 性质：底𝒗" 与⾼𝒒! 正交。
𝒗!
𝒗"
以𝒗" 为底，如何计算⾼𝒒!？
𝒒!
Proj𝒗" 𝒗#

## Page 13

平行六面体的体积
• 体积= 底⾯积×
⾼
" 。
• 平⾏四边形𝒫𝒗!, 𝒗" 是平⾏六⾯
体𝒫𝒗!, 𝒗", 𝒗# 的底。
• ⾼𝒒# 垂直于底𝒫𝒗!, 𝒗" 。
𝒗!
𝒗"
𝒗#
𝒒#

## Page 14

平行六面体的体积
• 设𝒗!、𝒗"、𝒗# 都是单位向量。
• 当三个向量正交时，平⾏六⾯体为
正⽅体，体积最⼤化，vol = 1。
• 当三个向量线性相关时，体积最⼩
化，vol = 0。
体积何时最⼤化、最⼩化？
𝒗!
𝒗"
𝒗#
𝒒#

## Page 15

衡量物品多样性
• 给定𝑘个物品，把它们表征为单位
向量𝒗!, ⋯, 𝒗$ ∈ℝ%。（𝑑≥𝑘）
• ⽤超平⾏体的体积衡量物品的多样
性，体积介于0 和1 之间。
• 如果𝒗!, ⋯, 𝒗$ 两两正交（多样性
好），则体积最⼤化，vol = 1。
• 如果𝒗!, ⋯, 𝒗$ 线性相关（多样性
差），则体积最⼩化，vol = 0。
𝒗!
𝒗"
𝒗#
𝒒#

## Page 16

衡量物品多样性
• 给定𝑘个物品，把它们表征为单位
向量𝒗!, ⋯, 𝒗$ ∈ℝ%。（𝑑≥𝑘）
• 把它们作为矩阵𝑽∈ℝ%×$ 的列。
• 设𝑑≥𝑘，⾏列式与体积满⾜：
det 𝑽!𝑽
= vol 𝒫𝒗", ⋯, 𝒗#
$.
• 因此，可以⽤⾏列式det 𝑽(𝑽衡量
向量𝒗!, ⋯, 𝒗$ 的多样性。
𝑽
𝒗! 𝒗"
𝒗$
⋯
=
𝑑×𝑘

## Page 17

Thank You!
http://wangshusen.github.io/

### DPP：多样性算法

# DPP：多样性算法

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/06_Rerank_05.pdf

## Page 1

DPP：多样性算法
王树森
http://wangshusen.github.io/

## Page 2

多样性问题
• 精排给𝑛个物品打分：reward!, ⋯, reward"。
• 𝑛个物品的向量表征：𝒗!, ⋯, 𝒗" ∈ℝ#。
• 从𝑛个物品中选出𝑘个物品，组成集合𝒮。
• 价值⼤：分数之和∑!∈𝒮reward! 越⼤越好。
• 多样性好：𝒮中𝑘个向量组成的超平形体𝒫𝒮的
体积越⼤越好。

## Page 3

多样性问题
• 集合𝒮中的𝑘个物品的向量作为列，
组成矩阵𝑽𝒮∈ℝ#×&。
• 以这𝑘个向量作为边，组成超平形体
𝒫𝒮。
• 体积vol 𝒫𝒮
可以衡量𝒮中物品的
多样性。
• 设𝑘≤𝑑，⾏列式与体积满⾜：
det 𝑽𝒮
" 𝑽𝒮
= vol 𝒫𝒮
$.
𝑽𝒮
⋯
=
集合𝒮中物品的向量
𝑑×𝑘

## Page 4

行列式点过程（DPP）
• DPP 是⼀种传统的统计机器学习⽅法：
argmax
𝒮: 𝒮&'
log det 𝑽𝒮
( 𝑽𝒮.
参考⽂献：
1. Chen et al. Fast greedy map inference for determinantal point process to improve
recommendation diversity. In NIPS, 2018.
• Hulu 的论⽂[1] 将DPP 应⽤在推荐系统：
argmax
𝒮: 𝒮&'
𝜃⋅∑!∈𝒮reward! + 1 −𝜃⋅log det 𝑽𝒮
( 𝑽𝒮.

## Page 5

行列式点过程（DPP）
• DPP 应⽤在推荐系统：
argmax
𝒮: 𝒮&'
𝜃⋅∑!∈𝒮reward! + 1 −𝜃⋅log det 𝑽𝒮
( 𝑽𝒮.
• 设𝑨为𝑛×𝑛的矩阵，它的𝑖, 𝑗元素为𝑎'( = 𝒗'
)𝒗(。
• 给定向量𝒗!, ⋯, 𝒗" ∈ℝ#，需要𝑂𝑛*𝑑时间计算𝑨。
• 𝑨𝒮= 𝑽𝒮
) 𝑽𝒮为𝑨的⼀个𝑘×𝑘⼦矩阵。如果𝑖, 𝑗∈𝒮，则
𝑎'( 是𝑨𝒮的⼀个元素。
= 𝑨𝒮
𝑘×𝑘

## Page 6

行列式点过程（DPP）
• ⽤𝒮表⽰已选中的物品，⽤ℛ表⽰未选中的物品，贪⼼
算法求解：
argmax
)∈ℛ
𝜃⋅reward) +
1 −𝜃⋅log det 𝑨𝒮∪)
.
• DPP 应⽤在推荐系统：
argmax
𝒮: 𝒮&'
𝜃⋅∑!∈𝒮reward! + 1 −𝜃⋅log det 𝑨𝒮.
• DPP 是个组合优化问题，从集合1, ⋯, 𝑛中选出⼀个⼤
⼩为𝑘的⼦集𝒮。

## Page 7

求解DPP

## Page 8

暴力算法
• 贪⼼算法求解：
argmax
)∈ℛ
𝜃⋅reward) +
1 −𝜃⋅log det 𝑨𝒮∪)
.
• 对于单个𝑖，计算𝑨𝒮∪' 的⾏列式需要𝑂𝒮, 时间。
• 对于所有的𝑖∈ℛ，计算⾏列式需要时间𝑂𝒮, ⋅ℛ。
• 需要求解上式𝑘次才能选出𝑘个物品。如果暴⼒计算⾏
列式，那么总时间复杂度为
𝑂𝒮, ⋅ℛ⋅𝑘= 𝑂𝑛𝑘- .

## Page 9

暴力算法
• 贪⼼算法求解：
argmax
)∈ℛ
𝜃⋅reward) +
1 −𝜃⋅log det 𝑨𝒮∪)
.
• 暴⼒算法的总时间复杂度为
𝑂𝑛*𝑑+ 𝑛𝑘- .

## Page 10

Hulu的快速算法
• Hulu 的论⽂设计了⼀种数值算法，仅需𝑂𝑛*𝑑+ 𝑛𝑘*
的时间从𝑛个物品中选出𝑘个物品。
• 给定向量𝒗!, ⋯, 𝒗" ∈ℝ#，需要𝑂𝑛*𝑑时间计算𝑨。
• ⽤𝑂𝑛𝑘* 时间计算所有的⾏列式（利⽤Cholesky 分
解）。

## Page 11

Hulu的快速算法
• Cholesky 分解𝑨𝒮= 𝑳𝑳)，其中𝑳是下三⾓矩阵（对⾓线
以上的元素全零）。
• Cholesky 分解可供计算𝑨𝒮的⾏列式。
• 下三⾓矩阵𝑳的⾏列式det 𝑳等于𝑳对⾓线元素乘积。
• 𝑨𝒮的⾏列式为det 𝑨𝒮= det 𝑳$ = ∏) 𝑙))
$.
• 已知𝑨𝒮= 𝑳𝑳)，则可以快速求出所有𝑨𝒮∪' 的Cholesky
分解，因此可以快速算出所有𝑨𝒮∪' 的⾏列式。

## Page 12

Hulu的快速算法
• 贪⼼算法求解：
argmax
)∈ℛ
𝜃⋅reward) +
1 −𝜃⋅log det 𝑨𝒮∪)
.
• 初始时𝒮中只有⼀个物品，𝑨𝒮是1×1 的矩阵，
• 每⼀轮循环，基于上⼀轮算出的𝑨𝒮= 𝑳𝑳)，快速求出
𝑨𝒮∪' 的Cholesky 分解（∀𝑖∈ℛ），从⽽求出
log det 𝑨𝒮∪'
。

## Page 13

DPP 的扩展

## Page 14

滑动窗口
• ⽤𝒮表⽰已选中的物品，⽤ℛ表⽰未选中的物品，DPP 的
贪⼼算法求解：
argmax
)∈ℛ
𝜃⋅reward) +
1 −𝜃⋅log det 𝑨𝒮∪)
.
• 随着集合𝒮增⼤，其中相似物品越来越多，物品向量会趋
近线性相关。
• ⾏列式det 𝑨𝒮会坍缩到零，对数趋于负无穷。

## Page 15

⋯
!"#$%&'( 𝒮)
*!"#$%&'( ℛ)
+,-.&'( 𝒲)
滑动窗口

## Page 16

滑动窗口
• 贪⼼算法：
argmax
)∈ℛ
𝜃⋅reward) +
1 −𝜃⋅log det 𝑨𝒮∪)
.
• ⽤滑动窗⼝：argmax
)∈ℛ
𝜃⋅reward) +
1 −𝜃⋅log det 𝑨𝒲∪)
.
⋯
!"#$%&'( 𝒮)
*!"#$%&'( ℛ)
+,-.&'( 𝒲)

## Page 17

规则约束
• 贪⼼算法每轮从ℛ中选出⼀个物品：
argmax
)∈ℛ
𝜃⋅reward) +
1 −𝜃⋅log det 𝑨𝒲∪)
.
• 有很多规则约束，例如最多连续出5 篇视频笔记（如果已
经连续出了5 篇视频笔记，下⼀篇必须是图⽂笔记）。
• ⽤规则排除掉ℛ中的部分物品，得到⼦集ℛ.，然后求解：
argmax
)∈ℛ!
𝜃⋅reward) +
1 −𝜃⋅log det 𝑨𝒲∪)
.

## Page 18

Thank You!
http://wangshusen.github.io/
