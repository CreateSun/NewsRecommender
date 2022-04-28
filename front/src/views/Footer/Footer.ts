import { Component, Prop, Vue, Emit, Watch } from "vue-property-decorator";

@Component
export default class Header extends Vue {
    /*
            如何给子组件传值，在 vue 里面是通过 prop 解决的
            首先：
                子组件定义接收数据的变量，如下
            然后：
                在父组件内通过 :childMsg='xxx' 把数据传递进来
        */
    @Prop({
        type: String,
        required: false,
        default: String
    })
    public childMsg!: string

}
