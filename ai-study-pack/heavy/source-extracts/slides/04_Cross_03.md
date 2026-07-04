# LHUC

Source: https://github.com/wangshusen/RecommenderSystem/blob/main/Slides/04_Cross_03.pdf

## Page 1

LHUC网络结构
王树森
http://wangshusen.github.io/

## Page 2

点击率
点赞率
收藏率
转发率
全连接层+Sigmoid
物品特征
Concatenation
统计特征
场景特征
用户特征
神经⽹络
(shared bottom)

## Page 3

Learning Hidden Unit Contributions (LHUC)
• LHUC 起源于语⾳识别[1]。
• 快⼿将LHUC 应⽤在推荐精排[2]，称作PPNet。
参考⽂献：
1. Pawel Swietojanski, Jinyu Li, & Steve Renals. Learning hidden unit contributions
for unsupervised acoustic model adaptation. IEEE/ACM Transactions on Audio,
Speech, and Language Processing, 2016.
2. 快⼿落地万亿参数推荐精排模型，2021。链接：
https://ai.51cto.com/art/202102/644214.html

## Page 4

语⾳识别中的LHUC
语⾳
信号
说话者
的特征

## Page 5

全
连
接
层
神
经
⽹
络
语⾳识别中的LHUC
语⾳
信号
说话者
的特征
[多个全连接层] à [Sigmoid 乘以2]

## Page 6

全
连
接
层
神
经
⽹
络
语⾳识别中的LHUC
语⾳
信号
说话者
的特征
Hadamard
Product

## Page 7

全
连
接
层
全
连
接
层
神
经
⽹
络
神
经
⽹
络
语⾳识别中的LHUC
语⾳
信号
说话者
的特征
Hadamard
Product

## Page 8

全
连
接
层
全
连
接
层
神
经
⽹
络
神
经
⽹
络
语⾳识别中的LHUC
语⾳
信号
说话者
的特征
Hadamard
Product
Hadamard
Product

## Page 9

全
连
接
层
全
连
接
层
神
经
⽹
络
神
经
⽹
络
语⾳识别中的LHUC
语⾳
信号
说话者
的特征
Hadamard
Product
Hadamard
Product
输出

## Page 10

全
连
接
层
全
连
接
层
语⾳识别中的LHUC
语⾳
信号
说话者
的特征
[多个全连接层] à [Sigmoid 乘以2]
神
经
⽹
络
神
经
⽹
络

## Page 11

全
连
接
层
全
连
接
层
神
经
⽹
络
神
经
⽹
络
语⾳识别中的LHUC
语⾳
信号
说话者
的特征
Hadamard
Product
Hadamard
Product

## Page 12

全
连
接
层
全
连
接
层
神
经
⽹
络
神
经
⽹
络
推荐系统排序模型中的LHUC
物品
特征
⽤户
特征
Hadamard
Product
Hadamard
Product

## Page 13

Thank You!
http://wangshusen.github.io/
