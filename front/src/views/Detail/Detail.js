import * as tslib_1 from "tslib";
import { Component, Vue, Watch } from "vue-property-decorator";
let Detail = class Detail extends Vue {
    constructor() {
        super(...arguments);
        this.info = {};
        this.itemcf = [];
        this.contentbased = [];
        this.colors = ['#99A9BF', '#F7BA2A', '#FF9900'];
    }
    routerChanged(val, oldVal) {
        if (val.name === "detail" && val.query.newsId != oldVal.query.newsId) {
            this.info = {};
            this.itemcf = [];
            this.contentbased = [];
            this.getDataList(Number(val.query.newsId));
        }
    }
    created() {
        this.info = {};
        this.itemcf = [];
        this.contentbased = [];
        this.getDataList(Number(this.$route.query.newsId));
    }
    async getDataList(newsId) {
        // 动画加载过程中，若 axios 出现异常会导致动画无法关闭
        const loading = this.$loading({
            lock: true,
            text: 'Loading',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
        });
        try {
            this.doAction(0, newsId);
            let res1 = await this.axios.get('/business/rest/news/info/' + newsId);
            this.info = res1.data.news;
            let res2 = await this.axios.get('/business/rest/news/itemcf/' + newsId);
            this.itemcf = res2.data.news;
            let res3 = await this.axios.get('/business/rest/news/contentbased/' + newsId);
            this.contentbased = res3.data.news;
        }
        catch (err) {
            console.error('请求出现异常：' + err);
        }
        this.$nextTick(() => {
            loading.close();
        });
    }
    async doAction(action, newsId) {
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
            console.log("埋点发送成功！");
        }
        else {
            console.error("埋点发送失败！");
        }
    }
};
tslib_1.__decorate([
    Watch('$route')
], Detail.prototype, "routerChanged", null);
Detail = tslib_1.__decorate([
    Component({
        name: 'ComponentName',
        filters: {
            numFilter(value) {
                // 截取当前数据到小数点后两位
                let realVal = parseFloat(String(value)).toFixed(2);
                return realVal;
            }
        }
    })
], Detail);
export default Detail;
//# sourceMappingURL=Detail.js.map