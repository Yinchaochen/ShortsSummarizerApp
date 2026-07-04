# 聚类召回

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/07_ColdStart_03.pdf

## Page 1

物品冷启动：聚类召回
王树森
http://wangshusen.github.io/
ShusenWang@xiaohongshu.com

## Page 2

聚类召回
• 如果⽤户喜欢⼀篇笔记，那么他会喜欢内容相似的
笔记。
• 事先训练⼀个神经⽹络，基于笔记的类⽬和图⽂内
容，把笔记映射到向量。
• 对笔记向量做聚类，划分为1000 cluster，记录每个
cluster 的中⼼⽅向。(k-means 聚类，⽤余弦相似
度。）
基本思想

## Page 3

聚类召回
• ⼀篇新笔记发布之后，⽤神经⽹络把它映射到⼀个
特征向量。
• 从1000 个向量（对应1000 个cluster）中找到最相
似的向量，作为新笔记的cluster。
• 索引：
cluster à 笔记ID列表（按时间倒排）
聚类索引

## Page 4

聚类召回
线上召回
• 给定⽤户ID，找到他的last-n 交互的笔记列表，把
这些笔记作为种⼦笔记。
• 把每篇种⼦笔记映射到向量，寻找最相似的cluster。
（知道了⽤户对哪些cluster 感兴趣。）
• 从每个cluster 的笔记列表中，取回最新的𝑚篇笔
记。

## Page 5

聚类召回
• 给定⽤户ID，找到他的last-n 交互的笔记列表，把
这些笔记作为种⼦笔记。
• 把每篇种⼦笔记映射到向量，寻找最相似的cluster。
（知道了⽤户对哪些cluster 感兴趣。）
• 从每个cluster 的笔记列表中，取回最新的𝑚篇笔
记。
• 最多取回𝑚𝑛篇新笔记。
线上召回

## Page 6

内容相似度模型

## Page 7

提取图文特征
CNN
BERT

## Page 8

提取图文特征
CNN
BERT
全连
接层
concatenation
笔记的
特征向量

## Page 9

图片
文字
图片
文字
两篇笔记内容相似度
𝐚
𝐛
全连接层
BERT
CNN
全连接层
BERT
CNN

## Page 10

图片
文字
图片
文字
两篇笔记内容相似度
余弦相似度：cos 𝐚, 𝐛=
𝐚, 𝐛
𝐚𝟐⋅
𝐛𝟐
𝐚
𝐛
全连接层
BERT
CNN
全连接层
BERT
CNN

## Page 11

训练内容相似度模型

## Page 12

模型的训练
种子笔记
负样本笔记
正样本笔记

## Page 13

模型的训练
𝐚
𝐛!
种子笔记
负样本笔记
正样本笔记
𝐛"
神经⽹络
(CNN+BERT+FC)
神经⽹络
(CNN+BERT+FC)
神经⽹络
(CNN+BERT+FC)

## Page 14

模型的训练
cos 𝐚, 𝐛%
𝐚
𝐛!
种子笔记
负样本笔记
正样本笔记
𝐛"
cos 𝐚, 𝐛&
神经⽹络
(CNN+BERT+FC)
神经⽹络
(CNN+BERT+FC)
神经⽹络
(CNN+BERT+FC)

## Page 15

模型的训练
基本想法：⿎励cos 𝐚, 𝐛! ⼤于cos 𝐚, 𝐛"
Triplet logistic loss:
𝐿𝐚, 𝐛!, 𝐛"
= log 1 + exp cos 𝐚, 𝐛" −cos 𝐚, 𝐛!
.
Triplet hinge loss:
𝐿𝐚, 𝐛!, 𝐛"
= max 0, cos 𝐚, 𝐛" + 𝑚−cos 𝐚, 𝐛!
.

## Page 16

<种子笔记，正样本>
• 筛选条件：
• 只⽤⾼曝光笔记作为⼆元组（因为有充⾜的⽤户交互信息）。
• 两篇笔记有相同的⼆级类⽬，⽐如都是“菜谱教程”。
• ⽤ItemCF 的物品相似度选正样本。
⽅法⼀：⼈⼯标注⼆元组的相似度
⽅法⼆：算法⾃动选正样本

## Page 17

<种子笔记，负样本>
• 从全体笔记中随机选出满⾜条件的：
• 字数较多（神经⽹络提取的⽂本信息有效）。
• 笔记质量⾼，避免图⽂无关。

## Page 18

聚类召回总结
• 基本思想：根据⽤户的点赞、收藏、转发记录，推荐内容
相似的笔记。
• 线下训练：多模态神经⽹络把图⽂内容映射到向量。
• 线上服务：
⽤户喜欢的笔记à 特征向量à 最近的Cluster à 新笔记

## Page 19

Thank You!
http://wangshusen.github.io/

## Page 20

长期招聘优秀的算法工程师
• 部门：⼩红书社区技术部。
• ⽅向：搜索、推荐。
• 职位：校招、社招、实习。
• 地点：上海、北京。
• 联系⽅式：ShusenWang@xiaohongshu.com
