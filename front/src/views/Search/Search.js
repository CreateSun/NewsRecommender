import * as tslib_1 from "tslib";
import { Component, Vue, Watch } from "vue-property-decorator";
let Search = class Search extends Vue {
    constructor() {
        super(...arguments);
        this.searchResult = [];
        this.colors = ['#99A9BF', '#F7BA2A', '#FF9900'];
    }
    routerChanged() {
        console.log("进入watch");
        console.log(this.$route.query.searchInput);
        this.getData('/business/rest/news/search', String(this.$route.query.searchInput));
    }
    created() {
        console.log("进入create");
        this.getData('/business/rest/news/search', String(this.$route.query.searchInput));
    }
    async getData(url, searchInput) {
        // 动画加载过程中，若 axios 出现异常会导致动画无法关闭
        const loading = this.$loading({
            lock: true,
            text: 'Loading',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
        });
        try {
            let res = await this.axios.get(url, {
                params: {
                    query: searchInput
                }
            });
            console.dir(res);
            this.searchResult = res.data.news;
        }
        catch (err) {
            console.error('请求：' + url + ' 异常 ' + err);
        }
        this.$nextTick(() => {
            loading.close();
        });
    }
    async doAction(action, newsId) {
        // ?score=8&username=abc
        let actionArr = ["view", "like", "collect"];
        console.log('用户行为： newsId: ' + newsId + " rate: " + action);
        let user = localStorage.getItem('userId');
        let res = await this.axios.get(`/business/rest/users/${actionArr[action]}/` + newsId, {
            params: {
                newsId,
                userId: user
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
], Search.prototype, "routerChanged", null);
Search = tslib_1.__decorate([
    Component
], Search);
export default Search;
//# sourceMappingURL=Search.js.map