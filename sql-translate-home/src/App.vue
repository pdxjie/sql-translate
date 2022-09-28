<template>
  <div id="app">

    <a-layout id="components-layout-demo-fixed">
    <a-layout-header class="header" :style="{ position: 'fixed', zIndex: 1, width: '100%'}">
      <h1 class="title"> 🛠 多功能SQL生成器</h1>
      <a href="https://gitee.com/gao-wumao/sql-transformation" class="description">想要了解全部语法功能 请击查看详细文档</a>
      <div class="action">
        <a-select class="type" default-value="请选择类型" style="width: 120px;" @change="handleChange">
          <a-select-option value="1">
            插入语句
          </a-select-option>
          <a-select-option value="2">
            更新语句
          </a-select-option>
          <a-select-option value="3">
            建表语句
          </a-select-option>
        </a-select>
        <a-button type="primary" class="generate" @click="generatorSql">转译SQL</a-button>
        <a-button type="primary" ghost class="replace" @click="replaceKey">替换属性</a-button>
        <!-- <a-button class="export">Mock例子</a-button> -->
        <a-select default-value="导入例子" style="width: 120px;" @change="mockChange">
          <a-select-option value="1">
            插入语法示例
          </a-select-option>
          <a-select-option value="2">
            更新语法示例
          </a-select-option>
          <a-select-option value="3">
            建表语法示例
          </a-select-option>
        </a-select>
      </div>
    </a-layout-header>
    <a-layout-content :style="{marginTop: '64px', marginLeft:'10px',marginRight:'10px',height:'85vh'}">
      <a-row>
        <a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
         <MonacoEditorVue 
          @onCodeChange="excelTextChanged"  types="text" :codes="textarea" style="margin-right:5px">
        </MonacoEditorVue>
        </a-col>
        <a-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
          <GeneratorSql 
            @onCodeChange="SqlTextChanged"  types="text" :sqlCodes="sqlTextarea" style="margin-left:5px">
          </GeneratorSql>
        </a-col>
      </a-row>
    </a-layout-content>
    <a-layout-footer :style="{ textAlign: 'center' }">
      <CommonFooter/>
    </a-layout-footer>
  </a-layout>
  <!-- 替换属性抽屉  title="替换JSON属性"-->
  <a-drawer
      :width="720"
      :visible="visible"
      :body-style="{ paddingBottom: '80px' }"
      @close="onClose"
    >
      <template slot="title">
        替换JSON属性 
        <a-popover placement="rightTop">
        <template slot="content">
          <p>当需要转义的JSON块中存在与需要转义</p>
          <p>SQL的字段不一致时进行新旧替换</p>
          <p>默认id字段为主键</p>
        </template>
        <template slot="title">
          <span>提示</span>
        </template>
        <a-icon type="question-circle"/>
      </a-popover>
        
      </template>
      <a-form-model
        ref="dynamicValidateForm"
        :model="dynamicValidateForm"
        v-bind="formItemLayoutWithOutLabel"
      >
        <a-form-model-item
          v-for="(domain, index) in dynamicValidateForm.domains"
          :key="domain.key"
          v-bind="index === 0 ? formItemLayout : {}"
          :label="index === 0 ? '属性替换' : ''"
          :prop="'domains.' + index + '.value'">
          <a-input
            v-model="domain.oldVal"
            placeholder="需要替换的属性"
            style="width: 30%; margin-right: 8px"
          />
          <a-input
            v-model="domain.newVal"
            placeholder="新属性"
            style="width: 30%; margin-right: 8px"
          />
          <a-icon
            v-if="dynamicValidateForm.domains.length > 1"
            class="dynamic-delete-button"
            type="minus-circle-o"
            :disabled="dynamicValidateForm.domains.length === 1"
            @click="removeDomain(domain)"
          />
        </a-form-model-item>
        <a-form-model-item v-bind="formItemLayoutWithOutLabel">
          <a-button type="dashed" style="width: 60%" @click="addDomain">
            <a-icon type="plus" /> 
          </a-button>
        </a-form-model-item>
      </a-form-model>
      <div
        :style="{
          position: 'absolute',
          right: 0,
          bottom: 0,
          width: '100%',
          borderTop: '1px solid #e9e9e9',
          padding: '10px 16px',
          background: '#fff',
          textAlign: 'right',
          zIndex: 1,
        }"
      >
        <a-button :style="{ marginRight: '8px' }" @click="onClose">
          取消
        </a-button>
        <a-button type="primary" @click="replaceOldKey">
          确定
        </a-button>
      </div>
    </a-drawer>
  </div>
</template>

<script>
//import { format } from 'sql-formatter';
import init from './init/initData.json'
import initJson from './init/createInit.json'
import updateJson from './init/updateInit.json'
import InsertJson from './init/insertInit.json'
import MonacoEditorVue from './components/MonacoEditor.vue';
import GeneratorSql from './components/GeneratorSql.vue'
import CommonFooter from './components/CommonFooter.vue'
export default {
  name: 'App',
  components: {
    MonacoEditorVue,
    GeneratorSql,
    CommonFooter
},
  data() {
    return {
      sqlTextarea:"",
      transFrom:{},
      type:'',
      textarea: "",
      visible: false,
      formItemLayout: {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 4 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 20 },
        },
      },
      formItemLayoutWithOutLabel: {
        wrapperCol: {
          xs: { span: 24, offset: 0 },
          sm: { span: 20, offset: 4 },
        },
      },
      dynamicValidateForm: {
        domains: [{}],
      },
    }
  },
  created(){
    this.textarea = JSON.stringify(init,null,4)
    this.dynamicValidateForm.domains = []
  },
  methods: {
    excelTextChanged(text) {
      this.textarea = text
    },
    SqlTextChanged() {
      this.formatSql()
    },
    replaceKey() {
      this.visible = true
    },
    onClose() {
      this.visible = false;
    },
    removeDomain(item) {
      let index = this.dynamicValidateForm.domains.indexOf(item);
      if (index !== -1) {
        this.dynamicValidateForm.domains.splice(index, 1);
      }
    },
    addDomain() {
      this.dynamicValidateForm.domains.push({
      });
    },
    replaceOldKey(){
      this.visible = false
    },
    handleChange(val){
      this.type = val
    },
    mockChange(val) {
      if (val === '1') {//insert
        this.textarea = JSON.stringify(InsertJson,null,4)
      } else if (val == '2') {//insert
        this.textarea = JSON.stringify(updateJson,null,4)
      } else if (val === '3') {
        this.textarea = JSON.stringify(initJson,null,4)
      }
    },
    //转译SQL
    generatorSql() {
      this.$axios.post('/transform',  {
        textarea: this.textarea,
        type: this.type,
        domains:this.dynamicValidateForm.domains
      }).then(res=>{
        if (res.data.code === 0) {
          this.sqlTextarea =  res.data.data
          //this.sqlTextarea = sqlFormatter.format(this.sqlTextarea);
          console.log(this.sqlTextarea)
          console.log(this.sqlTextarea)
          console.log(res)
        } else {
          this.$message.warn(res.data.msg)
        }
      })
      this.dynamicValidateForm.domains = []
    },
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
