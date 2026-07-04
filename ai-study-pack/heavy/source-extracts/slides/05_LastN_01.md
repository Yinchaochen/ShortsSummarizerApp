# 用户行为序列特征

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/05_LastN_01.pdf

## Page 1

用户行为序列建模
王树森
http://wangshusen.github.io/

## Page 2

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

## Page 3

⋯
向量：
⋯
物品ID：
平均
Embedding

## Page 4

LastN特征
• LastN：⽤户最近的𝑛次交互（点击、点赞等）的
物品ID。
• 对LastN 物品ID 做embedding，得到𝑛个向量。
• 把𝑛个向量取平均，作为⽤户的⼀种特征。
• 适⽤于召回双塔模型、粗排三塔模型、精排模型。
参考⽂献：
• Covington, Adams, and Sargin. Deep neural networks for YouTube
recommendations. In ACM Conference on Recommender Systems, 2016.

## Page 5

小红书的实践
⋯
平均
点击的LastN
Embedding

## Page 6

小红书的实践
⋯
平均
点击的LastN
⋯
点赞的LastN
⋯
平均
收藏的LastN
⋯
Embedding

## Page 7

Thank You!
http://wangshusen.github.io/
