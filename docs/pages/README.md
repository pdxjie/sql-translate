# 简介
[Sql-Translation](https://github.com/pdxjie/sql-translate)（简称ST）是一个`Json`转译`SQL`工具，在同类工具的基础上增强了功能，为节省时间、提高工作效率而生

::: tip 初心

[ST](https://github.com/pdxjie/sql-translate) 的初心就是为了减少一些无必要时间浪费，将精力放在具有价值的工作业务中。

:::
![](../../asset/bac.png)

### 特性
- 内置主键：`JSON`块如果包含id字段，在选择建表操作模式时内部会自动为id设置`primary key`
- 界面友好：支持在线编辑`JSON`代码，支持代码高亮、语法校验、代码格式化、查找和替换、代码块折叠等，体验良好
- 支持生成建表语句：按照内置语法编写`JSON`，支持生成创建表的`SQL`语句
- 支持生成更新语句：按照内置语法编写`JSON`，支持生成创更新的`SQL`语句，可配置单条件、多条件更新操作
- 支持生成插入语句：按照内置语法编写`JSON`，支持生成创插入的`SQL`语句，如果`JSON`中包含(children)子嵌套，可按照相关语法指定作为父级id的字段
- 内置操作语法：该工具在选取不同的操作模式时，内置特定的使用语法规范
- 支持字段替换：需转译的`JSON`中字段与对应的`SQL`字段不一致时可以选择字段替换

### 支持数据库

:::warning 注意
目前仅支持MySQL一种数据库
:::

### 代码托管
> [GitHub](https://github.com/pdxjie/sql-translate) |  [Gitee](https://gitee.com/gao-wumao/sql-transformation)