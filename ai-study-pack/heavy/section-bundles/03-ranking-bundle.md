# Section 3: 排序

## Why This Section Matters

- Learn ranking as a multi-objective optimization problem.
- Understand score fusion, task conflict, and why watch-time matters in video products.
- Understand the role difference between coarse rank and fine rank.

## Common Mistakes To Avoid

- Do not optimize only CTR.
- Do not confuse ranking model structure with business value definition.

## Topic List

- Official #16: `多目标排序模型` (user playlist part `16`)
- Official #17: `Multi-gate Mixture-of-Experts (MMoE)` (missing from the user's 42-part playlist)
- Official #18: `预估分数融合` (user playlist part `17`)
- Official #19: `播放时长建模` (user playlist part `18`)
- Official #20: `推荐系统的特征` (user playlist part `19`)
- Official #21: `粗排三塔模型` (user playlist part `20`)

## Official Source Links

- `多目标排序模型`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/03_Rank_01.pdf`, Bilibili `https://www.bilibili.com/video/BV19t4y1p7UM`, YouTube `https://youtu.be/kY4W46MQqsg`
- `Multi-gate Mixture-of-Experts (MMoE)`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/03_Rank_02.pdf`, Bilibili `https://www.bilibili.com/video/BV14Y411M74v`, YouTube `https://youtu.be/JIEwaPARjfk`
- `预估分数融合`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/03_Rank_03.pdf`, Bilibili `https://www.bilibili.com/video/BV1YT411578u`, YouTube `https://youtu.be/D2iqM2puJ2I`
- `播放时长建模`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/03_Rank_04.pdf`, Bilibili `https://www.bilibili.com/video/BV1394y1277M`, YouTube `https://youtu.be/SiyvcJzr2bg`
- `推荐系统的特征`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/03_Rank_05.pdf`, Bilibili `https://www.bilibili.com/video/BV1gN4y157TM`, YouTube `https://youtu.be/J7N4xjqg0rk`
- `粗排三塔模型`: slides `https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/03_Rank_06.pdf`, Bilibili `https://www.bilibili.com/video/BV1Dd4y1m7KT`, YouTube `https://youtu.be/0CvouPv47SA`

## Local Extract Files

- `多目标排序模型`: `../source-extracts/slides/03_Rank_01.md`
- `Multi-gate Mixture-of-Experts (MMoE)`: `../source-extracts/slides/03_Rank_02.md`
- `预估分数融合`: `../source-extracts/slides/03_Rank_03.md`
- `播放时长建模`: `../source-extracts/slides/03_Rank_04.md`
- `推荐系统的特征`: `../source-extracts/slides/03_Rank_05.md`
- `粗排三塔模型`: `../source-extracts/slides/03_Rank_06.md`
## Inline Slide Extracts

### 多目标排序模型

# 多目标排序模型

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/03_Rank_01.pdf

## Page 1

多目标排序模型
王树森
http://wangshusen.github.io/

## Page 2

推荐系统的链路
粗排、精排
重排
笔记池
召回
笔记1
笔记2
笔记80
⋮
几千
物品
几百
物品

## Page 3

用户—笔记的交互
• 对于每篇笔记，系统记录：
• 曝光次数（number of impressions）
• 点击次数（number of clicks）
• 点赞次数（number of likes）
• 收藏次数（number of collects）
• 转发次数（number of shares）

## Page 4

用户—笔记的交互
• 点击率= 点击次数/ 曝光次数
• 点赞率= 点赞次数/ 点击次数
• 收藏率= 收藏次数/ 点击次数
• 转发率= 转发次数/ 点击次数

## Page 5

排序的依据
• 排序模型预估点击率、点赞率、收藏率、
转发率等多种分数。
• 融合这些预估分数。（⽐如加权和。）
• 根据融合的分数做排序、截断。

## Page 6

多⽬标模型

## Page 7

用户特征
物品特征
统计特征
场景特征

## Page 8

物品特征
Concatenation
统计特征
场景特征
用户特征
神经⽹络

## Page 9

点击率
点赞率
收藏率
转发率
神经⽹络
全连接层+Sigmoid
物品特征
Concatenation
统计特征
场景特征
用户特征

## Page 10

神经⽹络
全连接层+Sigmoid
物品特征
Concatenation
统计特征
场景特征
用户特征
点击率
点赞率
收藏率
转发率
𝑝!
𝑝"
𝑝#
𝑝$
预估：

## Page 11

𝑝!
𝑝"
𝑝#
𝑝$
预估：
点击率
点赞率
收藏率
转发率
𝑦!
𝑦"
𝑦#
𝑦$
目标：
有点击
无点赞
无收藏
有转发

## Page 12

𝑝!
𝑝"
𝑝#
𝑝$
预估：
点击率
点赞率
收藏率
转发率
𝑦!
𝑦"
𝑦#
𝑦$
目标：
CrossEntropy 𝑦!, 𝑝!
= −𝑦! ⋅ln 𝑝! + 1 −𝑦! ⋅ln 1 −𝑝!
训练：
•
总的损失函数：∑%&!
$
𝛼% ⋅CrossEntropy 𝑦%, 𝑝% 。
•
对损失函数求梯度，做梯度下降更新参数。

## Page 13

训练
• 困难：类别不平衡。
• 每100次曝光，约有10次点击、90次无点击。
• 每100次点击，约有10次收藏、90次无收藏。
• 解决⽅案：负样本降采样（down-sampling）。
• 保留⼀⼩部分负样本。
• 让正负样本数量平衡，节约计算。
注：不是小红书的真实数据

## Page 14

预估值校准

## Page 15

预估值校准
• 正样本、负样本数量为𝑛' 和𝑛(。
• 对负样本做降采样，抛弃⼀部分负样本。
• 使⽤𝛼⋅𝑛( 个负样本，𝛼∈(0, 1) 是采样率。
• 由于负样本变少，预估点击率⼤于真实点击率。

## Page 16

预估值校准
• 真实点击率：𝑝)*+, =
-!
-! ' -" （期望）。
• 预估点击率：𝑝.*,/ =
-!
-! ' 0⋅-" （期望）。

## Page 17

预估值校准
• 真实点击率：𝑝)*+, =
-!
-! ' -" （期望）。
• 预估点击率：𝑝.*,/ =
-!
-! ' 0⋅-" （期望）。
• 由上⾯两个等式可得校准公式[1]：
𝑝)*+, =
0⋅2#$%&
!(2#$%& '0⋅2#$%&.
参考⽂献：
1.
Xinran He et al. Practical lessons from predicting clicks on ads at Facebook. In the 8th
International Workshop on Data Mining for Online Advertising.

## Page 18

Thank You!
http://wangshusen.github.io/

### Multi-gate Mixture-of-Experts (MMoE)

# Multi-gate Mixture-of-Experts (MMoE)

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/03_Rank_02.pdf

## Page 1

Multi-gate Mixture-of-Experts
(MMoE)
王树森
http://wangshusen.github.io/

## Page 2

第2号
神经网络
第1号
神经网络
第3号
神经网络
𝐱!
𝐱"
𝐱#
物品特征
用户特征
统计特征场景特征

## Page 3

第2号
神经网络
第1号
神经网络
第3号
神经网络
𝐱!
𝐱"
𝐱#
三个“专家”
物品特征
用户特征
统计特征场景特征

## Page 4

神经网络
Softmax
激活函数
第2号
神经网络
第1号
神经网络
第3号
神经网络
𝑝! 𝑝" 𝑝#
𝐱!
𝐱"
𝐱#
物品特征
用户特征
统计特征场景特征

## Page 5

神经网络
Softmax
激活函数
神经网络
Softmax
激活函数
第2号
神经网络
第1号
神经网络
第3号
神经网络
𝑝! 𝑝" 𝑝#
𝑞! 𝑞" 𝑞#
𝐱!
𝐱"
𝐱#
物品特征
用户特征
统计特征场景特征

## Page 6

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
第2号
神经网络
第1号
神经网络
第3号
神经网络
物品特征
用户特征
统计特征场景特征

## Page 7

𝑝! 𝑝" 𝑝#
𝑞! 𝑞" 𝑞#
𝐱!
𝐱"
𝐱#
权重

## Page 8

𝑝! 𝑝" 𝑝#
𝑞! 𝑞" 𝑞#
𝐱!
𝐱"
𝐱#
权重
对向量做加权平均
𝑝!𝐱! + 𝑝"𝐱" + 𝑝#𝐱#

## Page 9

𝑝! 𝑝" 𝑝#
𝑞! 𝑞" 𝑞#
𝐱!
𝐱"
𝐱#
对向量做加权平均
𝑝!𝐱! + 𝑝"𝐱" + 𝑝#𝐱#
𝑞!𝐱! + 𝑞"𝐱" + 𝑞#𝐱#
权重

## Page 10

𝑝! 𝑝" 𝑝#
𝑞! 𝑞" 𝑞#
𝐱!
𝐱"
𝐱#
对向量做加权平均
𝑝!𝐱! + 𝑝"𝐱" + 𝑝#𝐱#
𝑞!𝐱! + 𝑞"𝐱" + 𝑞#𝐱#
神经网络
点击率
点赞率
权重

## Page 11

极化现象
（Polarization）

## Page 12

物品特征
用户特征
统计特征场景特征
第2号
神经网络
第1号
神经网络
第3号
神经网络
𝐱!
𝐱"
𝐱#
极化(polarize)：Softmax输出值⼀个接近1，其余接近0。
神经网络
Softmax
激活函数
𝑞! 𝑞" 𝑞#
神经网络
Softmax
激活函数
𝑝! 𝑝" 𝑝#

## Page 13

物品特征
用户特征
统计特征场景特征
第2号
神经网络
第1号
神经网络
第3号
神经网络
𝐱!
𝐱"
𝐱#
极化(polarize)：Softmax输出值⼀个接近1，其余接近0。
神经网络
Softmax
激活函数
神经网络
Softmax
激活函数

## Page 14

物品特征
用户特征
统计特征场景特征
第2号
神经网络
第1号
神经网络
第3号
神经网络
𝐱!
𝐱"
𝐱#
极化(polarize)：Softmax输出值⼀个接近1，其余接近0。
神经网络
Softmax
激活函数
神经网络
Softmax
激活函数

## Page 15

物品特征
用户特征
统计特征场景特征
第2号
神经网络
第1号
神经网络
第3号
神经网络
𝐱!
𝐱"
𝐱#
极化(polarize)：Softmax输出值⼀个接近1，其余接近0。
神经网络
Softmax
激活函数
神经网络
Softmax
激活函数
dead

## Page 16

解决极化问题
• 如果有𝑛个“专家”，那么每个softmax 的输⼊和输
出都是𝑛维向量。
• 在训练时，对softmax 的输出使⽤dropout。
• Softmax 输出的𝑛个数值被mask 的概率都是10%。
• 每个“专家”被随机丢弃的概率都是10%。

## Page 17

参考文献
• Google 的论⽂[1] 提出MMoE 模型。
• YouTube 的论⽂[2] 提出极化问题的解决⽅案。
参考⽂献：
1. Jiaqi Ma et al. Modeling Task Relationships in Multi-task Learning with
Multi-gate Mixture-of-Experts. In KDD, 2018.
2. Zhe Zhao et al. Recommending What Video to Watch Next: A Multitask
Ranking System. In RecSys, 2019.

## Page 18

Thank You!
http://wangshusen.github.io/

### 预估分数融合

# 预估分数融合

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/03_Rank_03.pdf

## Page 1

预估分数的融合
王树森
http://wangshusen.github.io/

## Page 2

𝑝!"#!$ + 𝑤% ⋅𝑝"#$& + 𝑤' ⋅𝑝!(""&!) + ⋯
融合预估分数
简单的加权和

## Page 3

𝑝!"#!$ + 𝑤% ⋅𝑝"#$& + 𝑤' ⋅𝑝!(""&!) + ⋯
融合预估分数
简单的加权和
𝑝!"#!$ ⋅
1 + 𝑤% ⋅𝑝"#$& + 𝑤' ⋅𝑝!(""&!) + ⋯
点击率乘以其他项的加权和

## Page 4

𝑝!"#!$ + 𝑤% ⋅𝑝"#$& + 𝑤' ⋅𝑝!(""&!) + ⋯
融合预估分数
简单的加权和
𝑝!"#!$ ⋅
1 + 𝑤% ⋅𝑝"#$& + 𝑤' ⋅𝑝!(""&!) + ⋯
点击率乘以其他项的加权和
= #点击
#曝光
= #点赞
#点击

## Page 5

1 + 𝑤% ⋅𝑝)#*& +! ⋅
1 + 𝑤' ⋅𝑝"#$& +" ⋯
融合预估分数
海外某短视频APP的融分公式

## Page 6

融合预估分数
国内某短视频APP的融分公式
• 根据预估时长𝑝)#*&，对𝑛篇候选视频做排序。
• 如果某视频排名第𝑟)#*&，则它得分
%
,#$%&
'
-.。
• 对点击、点赞、转发、评论等预估分数做类似处理。
• 最终融合分数：
𝑤!
𝑟"#$%
&!
+ 𝛽!
+
𝑤'
𝑟()#(*
&"
+ 𝛽'
+
𝑤+
𝑟)#*%
&# + 𝛽+
+ ⋯

## Page 7

融合预估分数
某电商的融分公式
• 电商的转化流程：
曝光à 点击à 加购物车à 付款
• 模型预估：𝑝!"#!$、𝑝!/0)、𝑝1/2。
• 最终融合分数：
𝑝()#(*
&!
×
𝑝(,-"
&"
×
𝑝.,/
&#
× price&$

## Page 8

Thank You!
http://wangshusen.github.io/

### 播放时长建模

# 播放时长建模

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/03_Rank_04.pdf

## Page 1

视频播放建模
王树森
http://wangshusen.github.io/

## Page 2

视频播放时长

## Page 3

图文vs 视频
• 图⽂笔记排序的主要依据：
点击、点赞、收藏、转发、评论……
• 视频排序的依据还有播放时长和完播。
• 直接⽤回归拟合播放时长效果不好。建议⽤YouTube
的时长建模[1]。
参考⽂献：
1.
Paul Covington, Jay Adams, & Emre Sargin. Deep Neural Networks for YouTube
Recommendations. In RecSys, 2016.

## Page 4

神经⽹络
𝑧
全连接层
⋯
用户特征
视频特征
统计特征
场景特征
全连接层

## Page 5

神经⽹络
全连接层
𝑝=
!"# $
%&!"# $
𝑧
𝑦=
'
%&'
CE 𝑦, 𝑝
= 𝑦⋅log 𝑝+ 1 −𝑦⋅log 1 −𝑝
全连接层
⋯
用户特征
视频特征
统计特征
场景特征

## Page 6

神经⽹络
全连接层
exp 𝑧
𝑧
𝑝=
!"# $
%&!"# $
𝑦=
'
%&'
如果𝑝= 𝑦，那么exp 𝑧= 𝑡。
全连接层
⋯
用户特征
视频特征
统计特征
场景特征

## Page 7

神经⽹络
𝑝=
!"# $
%&!"# $
⽤作推理，
预估时长𝑡
⽤作训练
全连接层
⋯
𝑧
全连接层
用户特征
视频特征
统计特征
场景特征
exp 𝑧

## Page 8

视频播放时长建模
• 把最后⼀个全连接层的输出记作𝑧。设𝑝= sigmoid 𝑧。
• 实际观测的播放时长记作𝑡。（如果没有点击，则𝑡= 0。）
• 做训练：最⼩化交叉熵损失
−
𝑡
1 + 𝑡⋅log 𝑝+
1 + 𝑡⋅log 1 −𝑝
.

## Page 9

视频播放时长建模
• 把最后⼀个全连接层的输出记作𝑧。设𝑝= sigmoid 𝑧。
• 实际观测的播放时长记作𝑡。（如果没有点击，则𝑡= 0。）
• 做训练：最⼩化交叉熵损失
−
𝑡
1 + 𝑡⋅log 𝑝+
1 + 𝑡⋅log 1 −𝑝
.
• 做推理：把exp 𝑧作为播放时长的预估。
• 把exp 𝑧作为融分公式中的⼀项。

## Page 10

视频完播

## Page 11

视频完播
• 例：视频长度10分钟，实际播放4 分钟，则实际播放
率为𝑦= 0.4。
• 让预估播放率𝑝拟合𝑦：
loss = 𝑦⋅log 𝑝+ 1 −𝑦⋅log 1 −𝑝.
• 线上预估完播率，模型输出𝑝= 0.73，意思是预计播
放73%。
回归⽅法

## Page 12

视频完播
• 定义完播指标，⽐如完播80%。
• 例：视频长度10分钟，播放>8分钟作为正样本，播放
<8分钟作为负样本。
• 做⼆元分类训练模型：播放>80% vs 播放<80%。
• 线上预估完播率，模型输出𝑝= 0.73，意思是
ℙ(播放> 80%) = 0.73.
⼆元分类⽅法

## Page 13

视频完播
0.1
0.2
0.3
0.4
0.5
0.6
完播率
视频时长（秒）
⽤函数𝑓视频时长
拟合完播率
不能直接把预估的完播率⽤到融分公式（why？）

## Page 14

视频完播
• 线上预估完播率，然后做调整：
𝑝!"#"$% = 预估完播率
𝑓视频长度
• 把𝑝!"#"$% 作为融分公式中的⼀项。

## Page 15

Thank You!
http://wangshusen.github.io/

### 推荐系统的特征

# 推荐系统的特征

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/03_Rank_05.pdf

## Page 1

排序模型的特征
王树森
http://wangshusen.github.io/

## Page 2

用户画像（User Profile）
• ⽤户ID（在召回、排序中做embedding）。
• ⼈⼝统计学属性：性别、年龄。
• 账号信息：新⽼、活跃度……
• 感兴趣的类⽬、关键词、品牌。

## Page 3

物品画像（Item Profile）
• 物品ID（在召回、排序中做embedding）。
• 发布时间（或者年龄）。
• GeoHash（经纬度编码）、所在城市。
• 标题、类⽬、关键词、品牌……
• 字数、图⽚数、视频清晰度、标签数……
• 内容信息量、图⽚美学……

## Page 4

用户统计特征
• ⽤户最近30天（7天、1天、1⼩时）的曝光数、点击数、
点赞数、收藏数……
• 按照笔记图⽂/视频分桶。（⽐如最近7天，该⽤户对图
⽂笔记的点击率、对视频笔记的点击率。）
• 按照笔记类⽬分桶。（⽐如最近30天，⽤户对美妆笔记
的点击率、对美⾷笔记的点击率、对科技数码笔记的点
击率。）

## Page 5

笔记统计特征
• 笔记最近30天（7天、1天、1⼩时）的曝光数、点击数、
点赞数、收藏数……
• 按照⽤户性别分桶、按照⽤户年龄分桶……
• 作者特征：
• 发布笔记数
• 粉丝数
• 消费指标（曝光数、点击数、点赞数、收藏数）

## Page 6

场景特征（Context）
• ⽤户定位GeoHash（经纬度编码）、城市。
• 当前时刻（分段，做embedding）。
• 是否是周末、是否是节假⽇。
• ⼿机品牌、⼿机型号、操作系统。

## Page 7

特征处理
• 离散特征：做embedding。
• ⽤户ID、笔记ID、作者ID。
• 类⽬、关键词、城市、⼿机品牌。
• 连续特征：做分桶，变成离散特征。
• 年龄、笔记字数、视频长度。
• 连续特征：其他变换。
• 曝光数、点击数、点赞数等数值做log 1 + 𝑥。
• 转化为点击率、点赞率等值，并做平滑。

## Page 8

小结
1. ⽤户画像特征。
2. 笔记画像特征。
3. ⽤户统计特征。
4. 笔记统计特征。
5. 场景特征。

## Page 9

特征覆盖率
• 很多特征无法覆盖100% 样本。
• 例：很多⽤户不填年龄，因此⽤户年龄特征的
覆盖率远⼩于100%。
• 例：很多⽤户设置隐私权限，APP 不能获得⽤
户地理定位，因此场景特征有缺失。
• 提⾼特征覆盖率，可以让精排模型更准。

## Page 10

数据服务
1. ⽤户画像（User Profile）。
2. 物品画像（Item Profile）。
3. 统计数据。

## Page 11

主服务器
数据服务
用户请求
召回
服务器
排序
服务器
物品ID、
用户ID、
场景特征

## Page 12

排序
服务器
物品画像
用户画像
统计数据
主服务器
用户特征
物品特征
统计特征
数据服务
用户请求
物品ID、
用户ID、
场景特征
较为静态
动态
静态

## Page 13

排序
服务器
TF
Serving
物品画像
用户画像
统计数据
主服务器
特征打包
用户特征
物品特征
统计特征
数据服务
用户请求
物品ID、
用户ID、
场景特征
较为静态
动态
静态

## Page 14

Thank You!
http://wangshusen.github.io/

### 粗排三塔模型

# 粗排三塔模型

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/03_Rank_06.pdf

## Page 1

粗排
王树森
http://wangshusen.github.io/

## Page 2

粗排vs 精排
• 给⼏千篇笔记打分。
• 单次推理代价必须⼩。
• 预估的准确性不⾼。
粗排
精排
• 给⼏百篇笔记打分。
• 单次推理代价很⼤。
• 预估的准确性更⾼。

## Page 3

精排模型& 双塔模型

## Page 4

点击率
点赞率
收藏率
转发率
神经⽹络
(shared bottom)
全连接层+Sigmoid
Concatenation
物品特征
统计特征
场景特征
⽤户特征
精排模型

## Page 5

精排模型
• 前期融合：先对所有特征做concatenation，
再输⼊神经⽹络。
• 线上推理代价⼤：如果有𝑛篇候选笔记，整
个⼤模型要做𝑛次推理。

## Page 6

双塔模型
⽤户特征
物品特征
余弦相似度：cos 𝐚, 𝐛
𝐚
𝐛
⽤户塔
物品塔
线上
推理
存储在
数据库

## Page 7

双塔模型
• 后期融合：把⽤户、物品特征分别输⼊不同的神经
⽹络，不对⽤户、物品特征做融合。
• 线上计算量⼩：
• ⽤户塔只需要做⼀次线上推理，计算⽤户表征𝐚。
• 物品表征𝐛事先储存在向量数据库中，物品塔在线
上不做推理。
• 预估准确性不如精排模型。

## Page 8

粗排的三塔模型
参考⽂献：
•
Zhe Wang et al. COLD: Towards the Next Generation of Pre-Ranking System. In DLP-
KDD, 2020.

## Page 9

⽤户塔
（很⼤）
物品塔
（较⼤）
交叉塔
（较⼩）
⽤户特征
场景特征
物品特征（静态）
统计特征、交叉特征

## Page 10

⽤户塔
（很⼤）
点击率
点赞率
收藏率
转发率
全连接层+Sigmoid
物品塔
（较⼤）
交叉塔
（较⼩）
⽤户特征
场景特征
物品特征（静态）
统计特征、交叉特征
Concatenation & Cross

## Page 11

⽤户特征
场景特征
物品特征（静态）
⽤户塔
（很⼤）
物品塔
（较⼤）
交叉塔
（较⼩）
•
只有⼀个⽤户，⽤户塔
只做⼀次推理。
•
即使⽤户塔很⼤，总计
算量也不⼤。
粗排的三塔模型
统计特征、交叉特征

## Page 12

⽤户特征
场景特征
物品特征（静态）
⽤户塔
（很⼤）
物品塔
（较⼤）
交叉塔
（较⼩）
•
只有⼀个⽤户，⽤户塔
只做⼀次推理。
•
即使⽤户塔很⼤，总计
算量也不⼤。
•
有𝑛个物品，理论上物
品塔需要做𝑛次推理。
•
PS 缓存物品塔的输出向
量，避免绝⼤部分推理。
粗排的三塔模型
统计特征、交叉特征
缓存

## Page 13

⽤户特征
场景特征
物品特征（静态）
⽤户塔
（很⼤）
物品塔
（较⼤）
交叉塔
（较⼩）
•
只有⼀个⽤户，⽤户塔
只做⼀次推理。
•
即使⽤户塔很⼤，总计
算量也不⼤。
•
有𝑛个物品，理论上物
品塔需要做𝑛次推理。
•
PS 缓存物品塔的输出向
量，避免绝⼤部分推理。
•
统计特征动态变化，缓
存不可⾏。
•
有𝑛个物品，交叉塔必
须做𝑛次推理。
粗排的三塔模型
统计特征、交叉特征

## Page 14

Concatenation & Cross
点击率
点赞率
收藏率
转发率
全连接层+Sigmoid
⽤户塔
（很⼤）
物品塔
（较⼤）
交叉塔
（较⼩）
⽤户特征
场景特征
物品特征（静态）
统计特征、交叉特征

## Page 15

Concatenation & Cross
点击率
点赞率
收藏率
转发率
全连接层+Sigmoid
•
有𝑛个物品，模型上层需要做𝑛次推理。
•
粗排推理的⼤部分计算量在模型上层。

## Page 16

三塔模型的推理
• 从多个数据源取特征：
• 1 个⽤户的画像、统计特征。
• 𝑛个物品的画像、统计特征。
• ⽤户塔：只做1 次推理。
• 物品塔：未命中缓存时需要做推理。
• 交叉塔：必须做𝑛次推理。
• 上层⽹络做𝑛次推理，给𝑛个物品打分。

## Page 17

Thank You!
http://wangshusen.github.io/
