<template>
  <div id="monaco-editor-box" >
    <div id="monaco-editor" ref="monacoEditor"/>
  </div>
</template>

<script>
import { format } from 'sql-formatter';
import sqlFormatter from 'sql-formatter';
import * as monaco from "monaco-editor/esm/vs/editor/editor.main";
import 'monaco-editor/esm/vs/basic-languages/javascript/javascript.contribution';
export default {
  name:"Generator",
  components: {},
  props: {
    // 名称
    name: {
      type: String,
      default:"test"
    },
    editorOptions: {
      type: Object,
      default: function () {
        return {
          selectOnLineNumbers: true,
          roundedSelection: false,
          readOnly: false, // 只读
          writeOnly: false,
          cursorStyle: "line", //光标样式
          automaticLayout: true, //自动布局
          glyphMargin: true, //字形边缘
          useTabStops: false,
          autoIndent: true, //自动布局
          quickSuggestionsDelay: 500,   //代码提示延时
        };
      },
    },
    sqlCodes: {
      type: String,
      default: function () {
        return "";
      },
    },
  },
  data() {
    return {
      editor: null, //文本编辑器
      isSave: true, //文件改动状态，是否保存
      codeValue: null, //保存后的文本
    };
  },
  watch: {
    sqlCodes: function (newValue) {
      console.debug("Code editor: content change");
      if (this.editor) {
        if (newValue !== this.editor.getValue()) {
          this.editor.setValue(
            format(newValue, {
              language: 'sql',
              tabWidth: 2,
              keywordCase: 'upper',
              linesBetweenQueries: 2,
              indentStyle: 'Standard'
            }),
          );
          // this.editor.setValue(
          //   format(newValue)
          // )
          this.editor.getAction(["editor.action.formatDocument"]).run();
        }
      }
    }
  },
  mounted() {
    this.initEditor();
  },
  methods: {
    initEditor() {
        const self = this;
        var sql = sqlFormatter.format(self.sqlCodes)
        // 初始化编辑器，确保dom已经渲染
        this.editor = monaco.editor.create(self.$refs.monacoEditor, {
        value: self.codeValue || sql, // 编辑器初始显示内容
        language:'sql', // 支持语言
        theme: "vs-dark", // 主题
        readOnly: false, //是否只读
        selectOnLineNumbers: true, //显示行号
        editorOptions: self.editorOptions,
        colorDecorators: true, // 颜色装饰器
        minimap: {
          enabled: false, // 不要小地图
        },
        foldingStrategy: 'indentation', // 代码可分小段折叠
        fontSize:16,
        "semanticHighlighting.enabled":'configuredByTheme',
        trimAutoWhitespace:true,
        autoIndent:true,
        autoClosingBrackets: 'always', // 是否自动添加结束括号(包括中括号) "always" | "languageDefined" | "beforeWhitespace" | "never"
        autoClosingDelete: 'always', // 是否自动删除结束括号(包括中括号) "always" | "never" | "auto"
      });
      // self.$emit("onMounted", self.editor); //编辑器创建完成回调
      self.editor.onDidChangeModelContent(function (event) {
          //编辑器内容changge事件
          self.codesCopy = self.editor.getValue();
         
          self.$emit("onCodeChange", self.editor.getValue(), event);
      });
    },
    // 自动格式化代码
    format() {
      
      // 或者
      // this.editor.getAction(['editor.action.formatDocument']).run()
    },
  }
};
</script>

<style scoped>
#monaco-editor {
  margin-top: 22px;
  height: 600px;
}
</style>