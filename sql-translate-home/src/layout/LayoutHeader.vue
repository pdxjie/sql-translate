<template>
  <div>
    <a-layout-header class="header" :style="{ position: 'fixed', zIndex: 1, width: '100%'}">
      <h1 class="title"> 🛠 多功能SQL生成器</h1>
      <!-- <a href="https://gitee.com/gao-wumao/sql-transformation" class="description">想要了解全部语法功能 请击查看详细文档</a> -->
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
    <!-- 替换属性弹窗 -->
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
import {transfromRequest} from "@/api/baseApi";
import {INIT, INSERT, SUCCESS_CODE, UPDATE} from "@/constant/baseConstant";
import initJson from "@/init/createInit.json";
import InsertJson from "@/init/insertInit.json";
import updateJson from "@/init/updateInit.json";

export default {
  name: 'LayoutHeader',
  props: {
    textarea: {
      type: String,
      default: ''
    }
  },
  components: {
  },
  data () {
    return {
      type: '',
      visible: false,
      dynamicValidateForm: {
        domains: [{}]
      },
      formItemLayoutWithOutLabel: {
        wrapperCol: {
          xs: { span: 24, offset: 0 },
          sm: { span: 20, offset: 4 },
        },
      },
      formItemLayout: {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 4 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 20 },
        },
      }
    }
  },
  methods: {
    // 选择需要转换的类型
    handleChange (val) {
      this.type = val
    },

    // 替换属性弹窗是否显示
    replaceKey () {
      this.visible = true
    },

    // Mock 数据导入选择
    mockChange (val) {
      switch (val) {
        case INIT:
          this.$emit('fillCompiler', JSON.stringify(initJson,null,4))
          break;
        case INSERT:
          this.$emit('fillCompiler', JSON.stringify(InsertJson,null,4))
          break;
        case UPDATE:
          this.$emit('fillCompiler', JSON.stringify(updateJson,null,4))
          break;
      }
    },

    // 关闭弹窗
    onClose () {
      this.visible = false;
    },

    // 关闭弹窗
    replaceOldKey (){
      this.visible = false
    },

    // 添加自定义替换字段
    addDomain () {
      this.dynamicValidateForm.domains.push({})
    },

    // 移除自定义替换字段
    removeDomain (item) {
      let index = this.dynamicValidateForm.domains.indexOf(item);
      if (index !== -1) {
        this.dynamicValidateForm.domains.splice(index, 1);
      }
    },

    // 生成 SQL
    generatorSql () {
      const transformData = {
        textarea: this.textarea,
        type: this.type,
        domains: this.dynamicValidateForm.domains
      }
      transfromRequest(transformData).then(res => {
        if (res.code === SUCCESS_CODE) {
          this.$emit('executeResult', res.data)
          this.dynamicValidateForm.domains = []
        } else {
          this.$message.warn(res.message)
        }
      })
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