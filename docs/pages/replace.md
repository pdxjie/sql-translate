# 替换属性
::: tip 解释说明
一般当JSON中的字段和对应数据库表中的字段不一致时，需要用到属性替换功能；
该功能在本站三种操作模式上都适用
:::

### 快速测试

#### create table 操作模式
假设现有一段JSON数据，如下所示：
```json
{
  "id": "123",
  "userName": "pdx",
  "passWord": "123456",
  "@table": "user"
}
```
**需求：数据库字段为`user_name`、`pass_word`，使用属性替换生产建表SQL语句**<br>
<br>
页面实际操作如下：
![](../../asset/ScreenGif.gif)

输出SQL结果如下显示：
```sql
create table `user`(
  `id` varchar(12),
  `pass_word` varchar(30),
  `user_name` varchar(24)
);
```

#### update where 操作模式
假设现定义了一个更新用户信息的接口，其中接口接收的Body体的JSON数据如下(按照id修改)：
```json
{
  "id": "123",
  "username": "pdx",
  "password": "123456"
}
```
需求:数据库对应的字段分别为：`user_name`、`pass_word`，并将信息修改为如下：
```json
{
    "username":"admin",
    "password":"123456"
}
```
页面实际操作如下：
![](../../asset/SQL.gif)

输出SQL结果如下显示：
```sql
update
  `user`
set
  `pass_word` = '123456',
  `user_name` = 'admin'
where
  `id` = '1';
```