# 简单的召回通道

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/07_ColdStart_02.pdf

## Page 1

物品冷启动：简单的召回通道
王树森
http://wangshusen.github.io/
ShusenWang@xiaohongshu.com

## Page 2

召回的难点

## Page 3

召回的依据
⾃带图⽚、⽂字、地点。
算法或⼈⼯标注的标签。
没有⽤户点击、点赞等信息。
没有笔记ID embedding。

## Page 4

冷启召回的困难
• 缺少⽤户交互，还没学好笔记ID embedding，导
致双塔模型效果不好。
• 缺少⽤户交互，导致ItemCF 不适⽤。

## Page 5

冷启召回的困难
ItemCF 不适⽤于物品冷启动

## Page 6

冷启召回的困难
ItemCF 不适⽤于物品冷启动

## Page 7

召回通道
ItemCF召回（不适⽤）
双塔模型（改造后适⽤）
类⽬、关键词召回（适⽤）
聚类召回（适⽤）
Look-Alike召回（适⽤）

## Page 8

双塔模型

## Page 9

双塔模型
用户ID、离散特征、连续特征
笔记ID、离散特征、连续特征
余弦相似度
𝐚
𝐛
特征变换
神经网络
特征变换
神经网络

## Page 10

ID Embedding
改进⽅案1：新笔记使⽤default embedding。
• 物品塔做ID embedding 时，让所有新笔记共享⼀个
ID，⽽不是⽤⾃⼰真正的ID。
• Default embedding：共享的ID 对应的embedding
向量。
• 到下次模型训练的时候，新笔记才有⾃⼰的ID
embedding 向量。

## Page 11

ID Embedding
• 查找top k 内容最相似的⾼曝笔记。
• 把k 个⾼曝笔记的embedding 向量取平均，作为新
笔记的embedding。
改进⽅案2：利⽤相似笔记embedding 向量。
改进⽅案1：新笔记使⽤default embedding。

## Page 12

多个向量召回池
• 多个召回池，让新笔记有更多曝光机会。
• 1 ⼩时新笔记，
• 6 ⼩时新笔记，
• 24 ⼩时新笔记，
• 30 天笔记。
• 共享同⼀个双塔模型，那么多个召回池不增
加训练的代价。

## Page 13

类⽬召回

## Page 14

用户画像
• 感兴趣的类⽬：
美⾷、科技数码、电影……
• 感兴趣的关键词：
纽约、职场、搞笑、程序员、⼤学……

## Page 15

基于类目的召回
类⽬：
笔记ID列表（按时间倒排）：
美⾷
⋮
旅游
美妆

## Page 16

基于类目的召回
• 系统维护类⽬索引：
类⽬à 笔记列表（按时间倒排）
• ⽤类⽬索引做召回：
⽤户画像à 类⽬à 笔记列表
• 取回笔记列表上前k 篇笔记（即最新的k 篇）。

## Page 17

基于关键词的召回
• 系统维护关键词索引：
关键词à 笔记列表（按时间倒排）
• 根据⽤户画像上的关键词做召回。

## Page 18

缺点
• 缺点1：只对刚刚发布的新笔记有效。
• 取回某类⽬/关键词下最新的k 篇笔记。
• 发布⼏⼩时之后，就再没有机会被召回。
• 缺点2：弱个性化，不够精准。

## Page 19

总结
ItemCF召回（不适⽤）
双塔模型（改造后适⽤）
类⽬、关键词召回（适⽤）
聚类召回（适⽤）
Look-Alike召回（适⽤）

## Page 20

Thank You!
http://wangshusen.github.io/

## Page 21

长期招聘优秀的算法工程师
• 部门：⼩红书社区技术部。
• ⽅向：搜索、推荐。
• 职位：校招、社招、实习。
• 地点：上海、北京。
• 联系⽅式：ShusenWang@xiaohongshu.com
