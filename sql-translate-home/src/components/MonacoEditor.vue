<template>
  <div id="monaco-editor-box" >
    <div id="monaco-editor" ref="monacoEditor">
    </div>
  </div>
</template>

<script>
import * as monaco from "monaco-editor/esm/vs/editor/editor.main";
export default {
  name:"MonacoEditor",
  components: {},
  props: {
    // 名称
    name: {
      type: String,
      default:"JSON"
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
          fontSize: 10, //字体大小
          autoIndent: true, //自动布局
          quickSuggestionsDelay: 500,   //代码提示延时
          
        };
      },
    },
    codes: {
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
    codes: function (newValue) {
      console.debug("Code editor: content change");
      if (this.editor) {
        if (newValue !== this.editor.getValue()) {
          this.editor.setValue(newValue);
          //this.editor.trigger(this.editor.getValue(), 'editor.action.formatDocument')
          this.editor.getAction(["editor.action.formatDocument"]).run();
          this.editor.setValue(this.editor.getValue())
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
      // 初始化编辑器，确保dom已经渲染
      this.editor = monaco.editor.create(self.$refs.monacoEditor, {
        value: self.codeValue || self.codes, // 编辑器初始显示内容
        language: 'json', // 支持语言
        theme: "vs-dark", // 主题
        selectOnLineNumbers: true, //显示行号
        editorOptions: self.editorOptions,
        colorDecorators: true, // 颜色装饰器
        minimap: {
          enabled: false, // 不要小地图
        },
        foldingStrategy: 'indentation', // 代码可分小段折叠
        fontSize:17,
        "semanticHighlighting.enabled":'configuredByTheme',
        trimAutoWhitespace:true,
        autoIndent: true,
        quickSuggestionsDelay:200,
        autoClosingBrackets: 'always', // 是否自动添加结束括号(包括中括号) "always" | "languageDefined" | "beforeWhitespace" | "never"
        autoClosingDelete: 'always', // 是否自动删除结束括号(包括中括号) "always" | "never" | "auto"
        readOnly: false, // 只读
      });
      // self.$emit("onMounted", self.editor); //编辑器创建完成回调
      self.editor.onDidChangeModelContent(function (event) {
          //编辑器内容changge事件
          self.codesCopy = self.editor.getValue();
          self.$emit("onCodeChange", self.editor.getValue(), event);
      });
    },
    
  },
};
</script>

<style scoped>
#monaco-editor {
    margin-top: 22px;
  /* width: 50%; */
  height: 600px;
}
</style>