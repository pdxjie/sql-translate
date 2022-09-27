<template>
    <div>
        <a-tooltip placement="top">
        <template slot="title">
            <span>欢迎提出建议和 Bug 反馈</span>
        </template>
        <a-icon type="bug" />
        <a href="https://support.qq.com/products/435498" class="anthor">产品反馈</a>
        </a-tooltip>

        <a-tooltip placement="top">
        <template slot="title">
            <span>
            <img src="../assets/wechat.jpg" class="weichat" alt="">
            </span>
        </template>
        <a-icon type="message" />
        <span class="anthor">联系作者</span>
        </a-tooltip>

        <a-tooltip placement="top">
        <template slot="title">
            <span> 查看本站源码，欢迎 star 🌟</span>
        </template>
        <a-icon type="github" />
        <a href="https://gitee.com/gao-wumao/sql-transformation" class="anthor">支持项目</a>
        </a-tooltip>
    </div>
</template>
<script>

</script>
<style scoped>
.anthor {
  margin-left: 5px;
  margin-right: 20px;
  color: #000;
}

.weichat {
  width: 150px;
  height: 150px;
}
</style>