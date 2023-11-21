<template>
  <div id="app">
    <a-layout id="components-layout-demo-fixed">
      <LayoutHeader ref="header" @fillCompiler="fillCompiler" @executeResult="executeResult" :textarea="textarea"/>
      <a-layout-content :style="{marginTop: '64px', marginLeft:'10px',marginRight:'10px',height:'85vh'}">
        <a-row>
          <a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
            <MonacoEditorVue
                @onCodeChange="excelTextChanged" types="text" :codes="textarea" style="margin-right:5px">
            </MonacoEditorVue>
          </a-col>
          <a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
            <GeneratorSql types="text" :sqlCodes="sqlTextarea" style="margin-left:5px"></GeneratorSql>
          </a-col>
        </a-row>
      </a-layout-content>
      <a-layout-footer :style="{ textAlign: 'center' }">
        <CommonFooter/>
      </a-layout-footer>
    </a-layout>
  </div>
</template>
<script>
import MonacoEditorVue from "@/components/MonacoEditor.vue";
import GeneratorSql from "@/components/GeneratorSql.vue";
import CommonFooter from "@/components/CommonFooter.vue";
import init from '@/init/initData.json'
import LayoutHeader from "@/layout/LayoutHeader.vue";
export default {
  name: "LayoutContent",
  components: {
    LayoutHeader,
    MonacoEditorVue,
    GeneratorSql,
    CommonFooter
  },
  data() {
    return {
      sqlTextarea: '',
      transFrom: {},
      textarea: ''
    }
  },
  created () {
    this.textarea = JSON.stringify(init, null, 4)
  },
  mounted () {
    // 页面挂载完成后，初始化表单数据
    this.$nextTick(() => {
      this.$refs.header.dynamicValidateForm.domains = []
    })
  },
  methods: {
    // 监听文本框内容变化
    excelTextChanged (text) {
      this.textarea = text
    },

    // 执行的结果
    executeResult (result) {
      console.log(result, 'result')
      this.sqlTextarea = result
    },

    // 填充编译器内容
    fillCompiler (val) {
      this.textarea = val
    }
  }
}
</script>

<style scoped>
.ant-layout-header {
  height: 85px;
  padding: 0 50px;
  line-height: 85px;
  background: #FFFF;
  min-width: 900px;
}
.title {
  float: left;
  font-size: 25px;
}
.description {
  position: absolute;
  top: 0px;
  left: 50%;
  transform: translate(-50%);
}
.action {
  float: right;
}
.generate {
  margin-right: 20px;
  height: 37px;
}
.export {
  height: 37px;
}
.header {
  position: relative;
}
.ant-btn {
  border-radius: 0px!important;
}
.replace {
  margin-right: 20px;
  height: 37px;
}
/deep/ .ant-select-selection--single {
  position: relative;
  height: 37px!important;
  line-height: 37px;
  cursor: pointer;
}
.type {
  margin-right: 20px;
}
/deep/ .ant-select-selection {
  border-radius: 0px!important;
  border-top-width: 0.02px
}
/deep/ .ant-select-selection__rendered {
  line-height: 37px;
}
</style>