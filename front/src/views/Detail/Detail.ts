import { Component, Prop, Vue, Watch } from "vue-property-decorator";
import VueRouter, { Route } from 'vue-router';

@Component({
    name: 'ComponentName',
    filters: {
        numFilter(value: number) {
            // 截取当前数据到小数点后两位
            let realVal = parseFloat(String(value)).toFixed(2)
            return realVal
        }
    }
})
export default class Detail extends Vue {

    public info: any = {}

    public itemcf: any = []

    public contentbased: any = []

    public colors: any = ['#99A9BF', '#F7BA2A', '#FF9900']

    @Watch('$route')
    private routerChanged(val: Route, oldVal: Route) {
        if (val.name === "detail" && val.query.newsId != oldVal.query.newsId) {
            this.info = {}
            this.itemcf = []
            this.contentbased = []
            this.getDataList(Number(val.query.newsId))
        }
    }

    public created() {
        this.info = {}
        this.itemcf = []
        this.contentbased = []
        this.getDataList(Number(this.$route.query.newsId))
    }

    public async getDataList(newsId: number) {
        // 动画加载过程中，若 axios 出现异常会导致动画无法关闭
        const loading = this.$loading({
            lock: true,
            text: 'Loading',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
        });
        try {
            this.doAction(0, newsId)
            let res1 = await this.axios.get('/business/rest/news/info/' + newsId)
            this.info = res1.data.news

            let res2 = await this.axios.get('/business/rest/news/itemcf/' + newsId)
            this.itemcf = res2.data.news

            let res3 = await this.axios.get('/business/rest/news/contentbased/' + newsId)
            this.contentbased = res3.data.news
        } catch (err) {
            console.error('请求出现异常：' + err)
        }

        this.$nextTick(() => { // 以服务的方式调用的 Loading 需要异步关闭
            loading.close();
        });
    }

    public async doAction(action: number, newsId: number) {
        // ?score=8&username=abc
        let actionArr = ["view", "like", "collect"];
        console.log('用户行为： newsId: ' + newsId + " action: " + actionArr[action]);
        let userId = localStorage.getItem('userId');
        let res = await this.axios.get(`/business/rest/users/${actionArr[action]}`, {
            params: {
                newsId,
                userId: userId
            }
        });
        if (res.data.success == true) {
            console.log("埋点发送成功！")
        } else {
            console.error("埋点发送失败！")
        }
    }
}
