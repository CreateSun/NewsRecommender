import { Component, Prop, Vue, Watch } from "vue-property-decorator";

export default class Charts extends Vue{

    public info:any = {};

    public async getDataList() {
        // 动画加载过程中，若 axios 出现异常会导致动画无法关闭
        const loading = this.$loading({
            lock: true,
            text: 'Loading',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
        });
        let res1 = await this.axios.get('/business/rest/news')
        this.info = res1.data.news
    }
}
