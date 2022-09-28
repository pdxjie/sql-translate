# 操作语法
::: warning 注意
本站内置了一些相关操作的语法，需要根据语法方可得到相关功能的执行结果，否则可不行哟！
:::

### create table相关语法

####  **@table**
```json
{
  "id": "1",
  "username": "pdx",
  "@table": "user"
}
```
- `指定需要创建的表名`
- `"@table":"user"` ——> `create table user`

#### **@size**
```json
{
  "id@12": "1",
  "username@30": "pdx"
}
```
- `指定字段的大小`
- `"id@12":"100"` ——> `id int(12)`


### update where相关语法

####  **@table**
```json
{
  "username": "pdx",
  "@table": "user"
}
```
- `指定需要执行更新的表名`
- `"@table":"user"` ——> `update user`

####  **key#symbol**

```json
{
  "username": "pdx",
  "@table": "user",
  "id#=": "1"
}
```
- `# 前指定的是条件字段 # 后面属性指定的是操作符号 < > != = `
- `"id#=": "1"` ——> `where id = 1`

### insert into相关语法

####  **@table**
```json
{
  "id": "1",
  "username": "pdx",
  "@table": "user"
}
```
- `指定需要执行更新的表名`
- `"@table":"user"` ——> `insert into user`

#### **@pid**

```json
{
  "id": "1",
  "text": "pdx",
  "value": "1",
  "children": [
    {
      "id": "1",
      "text": "pdx",
      "value": "1",
      "children": []
    }
  ],
  "@table": "user",
  "@pid": "id"
}
```
- `在执行生成insert 语句时指定作为pid值的字段 "@pid"对应的value值代表着作为pid的值所指代的字段`
- `"@pid": "id"` ——> `子级的pid的值自动会被插入父级id值`

::: danger 警告
在 插入（insert into）操作模式中，@pid必须和children数组共同使用，且children数组中存在子数据
:::

::: tip 复杂需求
如何想要将一个JSON数组转译生成insert 语句时，我们需要在该数组中的首部或尾部添加一个对象JSON，
通过使用`@table` 和 `@pid`来设定表名和自定义作为pid的字段。如下所示
:::

现有一个
