# Swing模型

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/02_Retrieval_02.pdf

## Page 1

Swing召回通道
王树森
http://wangshusen.github.io/
ShusenWang@xiaohongshu.com

## Page 2

ItemCF的原理
• 物品相似度：如果喜欢𝑖!、𝑖"的⽤户有很⼤的重叠，
那么𝑖!与𝑖" 相似。
• ⽤户喜欢物品𝑖!
• 物品𝑖!与𝑖" 相似
⽤户很可能喜欢物品𝑖"

## Page 3

ItemCF的物品相似度
• 喜欢物品𝑖! 的⽤户记作集合𝒲!。
• 喜欢物品𝑖" 的⽤户记作集合𝒲"。
• 定义交集𝒱= 𝒲! ∩𝒲"。

## Page 4

ItemCF的物品相似度
• 喜欢物品𝑖! 的⽤户记作集合𝒲!。
• 喜欢物品𝑖" 的⽤户记作集合𝒲"。
• 定义交集𝒱= 𝒲! ∩𝒲"。
• 两个物品的相似度：
𝑠𝑖𝑚𝑖!, 𝑖"
=
𝒱
𝒲!
⋅𝒲"
.

## Page 5

ItemCF的物品相似度

## Page 6

ItemCF的物品相似度
交集𝒱= 𝒲! ∩𝒲"
骂川普
⽀持绿⾊能源

## Page 7

假如重合的用户是一个小圈子⋯⋯
某个微信群
《某⽹站护肤品打折》
《字节裁员了》

## Page 8

Swing模型
• ⽤户𝑢! 喜欢的物品记作集合𝒥!。
• ⽤户𝑢" 喜欢的物品记作集合𝒥"。
• 定义两个⽤户的重合度：
overlap 𝑢!, 𝑢" =
𝒥! ∩𝒥" 。
• ⽤户𝑢! 和𝑢" 的重合度⾼，则他们可能来⾃⼀个⼩
圈⼦，要降低他们的权重。

## Page 9

Swing模型
• 喜欢物品𝑖! 的⽤户记作集合𝒲!。
• 喜欢物品𝑖" 的⽤户记作集合𝒲"。
• 定义交集𝒱= 𝒲! ∩𝒲"。
• 两个物品的相似度：
𝑠𝑖𝑚𝑖!, 𝑖"
= 4
#!∈𝒱
#"∈𝒱
𝛼+ overlap 𝑢!, 𝑢"
.

## Page 10

总结
• Swing 与ItemCF 唯⼀的区别在于物品相似度。
• ItemCF：两个物品重合的⽤户⽐例⾼，则判定两个
物品相似。
• Swing：额外考虑重合的⽤户是否来⾃⼀个⼩圈⼦。
• 同时喜欢两个物品的⽤户记作集合𝒱。
• 对于𝒱中的⽤户𝑢! 和𝑢"，重合度记作overlap 𝑢!, 𝑢" 。
• 两个⽤户重合度⼤，则可能来⾃⼀个⼩圈⼦，权重降低。

## Page 11

长期招聘优秀的算法工程师
• 部门：⼩红书社区技术部。
• ⽅向：搜索、推荐。
• 职位：校招、社招、实习。
• 地点：上海、北京。
• 联系⽅式：ShusenWang@xiaohongshu.com

## Page 12

Thank You!
http://wangshusen.github.io/
