# 简介
[Sql-Translation](https://github.com/pdxjie/sql-translate)（简称ST）是一个`Json`转译`SQL`工具，在同类工具的基础上增强了功能，为节省时间、提高工作效率而生
# 前言
多功能SQL生成器遵循 **“轻页面、重逻辑”** 的原则，由极简页面来处理复杂任务，且它不仅仅是一个项目，而是以“降低时间成本、提高效率”为目标的执行工具。
# 整体技术选型
项目前端是采用的Vue框架，后端使用的是SpringBoot实现，由于只是做JSON的转译处理，所以无需使用数据库等其他工具。


> [ST](https://github.com/pdxjie/sql-translate) 的初心就是为了减少一些无必要时间浪费，将精力放在具有价值的工作业务中。


![bac](https://user-images.githubusercontent.com/108468186/202339749-827b7e8c-acbc-4359-9725-08da32b5e042.png)
# 解决痛点

下面就让我来给大家介绍一下[ST](https://github.com/pdxjie/sql-translate) 可以解决哪些痛点问题：
- 需要将大量JSON中的数据导入到数据库中，但是JSON中包含大量父子嵌套关系 ——> [可以使用本站](https://github.com/pdxjie/sql-translation)
- 在进行JSON数据导入数据库时，遇到JSON字段与数据库字段不一致需要替换字段时 ——> [可以使用本站](https://github.com/pdxjie/sql-translation)
- 根据Apifox工具来实现更新或新增接口（前提是对接口已经完成了设计工作），提供了Body体数据，而且不想手动编写SQL时 ——> [可以使用本站](https://github.com/pdxjie/sql-translation)
对上述三点进行进行举例说明（按照顺序）：

第一种情况：
```json
{
    "id": "320500000",
    "text": "苏州工业园区",
    "value": "320500000",
    "children": [         
        {
            "id": "320505006",
            "text": "斜塘街道",
            "value": "320505006",
            "children": []
        },
         {
            "id": "320505007",
            "text": "娄葑街道",
            "value": "320505007",
            "children": []
        },
      ....
    ]
}
```
第二种情况：
![img_3](https://user-images.githubusercontent.com/108468186/202339921-b14d118f-fffb-4b57-be26-95f4bc60f056.png)

第三种情况：
![img_4](https://user-images.githubusercontent.com/108468186/202339938-d9a2217a-9a62-4761-84fa-ed99e56d46f1.png)



# 项目划分
>点击下方标题链接查看详情

1. [多功能SQL生成器主站💡](http://www.json-sql.com) <br>
实现将JSON转译为所需可执行SQL语句、支持多级嵌套、属性替换、内嵌语法，从而让复杂无趣的工作简单化。<br>
技术分析：
   - Vue框架
   - AntDesign UI UI组件库
   - MonacoEditor 编辑器
   - sql-formatter SQL格式化

2. [后端实现🛠](https://github.com/pdxjie/sql-translate/tree/main/sql-translate-main) <br>
主要处理转译JSON的相关逻辑。<br>
技术分析：
   - SpringBoot
   - fastjson

2. [多功能SQL生成器文档📚](https://pdxjie.github.io/translate.github.io/) <br>
多功能SQL生成器项目的使用指南和详细介绍。<br>
技术分析：
   - Vuepress
    
## （前端）快速开始

**请保证Node.js版本 > 10 :warning:**
1. 下载项目到本地
```shell
git clone https://github.com/pdxjie/sql-translate.git
```
2. 进入目录，安装依赖
```shell
cd sql-translate-home
yarn install
```
3. 启动本地项目
```shell
npm run serve
```

## （后端）快速开始
**JDK版本>=8 工具——>IDEA :warning:**
```shell
cd sql-translate-main
```
## 注意：
由于本项目内置了部分操作语法，如需顺利使用并得到满足需求的SQL语句 还需依照具体语法进行对应操作[点击前往操作文档](https://pdxjie.github.io/translate.github.io/)！
