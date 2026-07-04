# Look-Alike人群扩散

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/07_ColdStart_04.pdf

## Page 1

物品冷启动：Look-Alike人群扩散
王树森
http://wangshusen.github.io/
ShusenWang@xiaohongshu.com

## Page 2

Look-Alike起源于互联⽹广告

## Page 3

Look-Alike⽤于互联⽹广告
• 年龄25~35。
• 本科学历以上。
• 关注科技数码。
• 喜欢苹果电⼦产品。
Tesla Model 3 典型⽤户：

## Page 4

Look-Alike⽤于互联⽹广告
• 年龄25~35。
• 本科学历以上。
• 关注科技数码。
• 喜欢苹果电⼦产品。
Tesla Model 3 典型⽤户：
种⼦⽤户

## Page 5

Look-Alike⽤于互联⽹广告
种⼦⽤户
Look-Alike⽤户
⼈群扩散

## Page 6

Look-Alike⽤于互联⽹广告
• 如何计算两个⽤户的相似度？
• UserCF：两个⽤户有共同的兴趣点。
• Embedding：两个⽤户向量的cosine较⼤。

## Page 7

Look-Alike⽤于新笔记召回

## Page 8

Look-Alike⼈群扩散召回
• 点击、点赞、收藏、转发——⽤户对笔记可能
感兴趣。
• 把有交互的⽤户作为新笔记的种⼦⽤户。
• ⽤look-alike 在相似⽤户中扩散。

## Page 9

种⼦⽤户
（有点赞等⾏为）
新笔记
Look-Alike⽤于新笔记召回

## Page 10

种⼦⽤户
（有点赞等⾏为）
新笔记
Look-Alike⽤于新笔记召回
⽤户向量
平均

## Page 11

种⼦⽤户
（有点赞等⾏为）
新笔记
Look-Alike⽤于新笔记召回
⽤户向量
平均
特征向量

## Page 12

新笔记
Look-Alike⽤于新笔记召回
特征向量
• 近线更新特征向量。
• 特征向量是有交互的⽤户的向
量的平均。
• 每当有⽤户交互该物品，更新
笔记的特征向量。

## Page 13

Look-Alike⽤于新笔记召回
向量数据库
（储存新笔记特征向量）
⽤户
最近邻查找
⋮

## Page 14

种⼦⽤户
新笔记
Look-Alike⽤于新笔记召回
交互⾏为
相似⽤户

## Page 15

种⼦⽤户
新笔记
Look-Alike⽤于新笔记召回
交互⾏为
相似⽤户

## Page 16

Thank You!
http://wangshusen.github.io/

## Page 17

长期招聘优秀的算法工程师
• 部门：⼩红书社区技术部。
• ⽅向：搜索、推荐。
• 职位：校招、社招、实习。
• 地点：上海、北京。
• 联系⽅式：ShusenWang@xiaohongshu.com
