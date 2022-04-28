<template>
  <div id="app">
    <Header :childMsg="fatherVar" @bindSend="propMsg"></Header>
    <keep-alive>
      <router-view @bindSend="propMsg"/>
    </keep-alive>
    <Footer></Footer>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { Header,Footer } from "@/views";

@Component({
  components: {
    Header,
    Footer
  }
})
export default class App extends Vue {

  // 子组件显示的信息
  public fatherVar: any = "default";

  // 接收子组件发送数据是 触发的事件
  public propMsg(msg: string) {
    this.fatherVar = msg;
    console.log('在 App 里面接收到子组件的值：' + msg)
  }
}
</script>

<style lang="stylus">
body {
  margin: 0;
  background-color #f5f5f5;
}
</style>
