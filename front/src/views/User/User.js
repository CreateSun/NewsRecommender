import * as tslib_1 from "tslib";
import { Component, Vue, Watch } from "vue-property-decorator";
let User = class User extends Vue {
    constructor() {
        super(...arguments);
        this.searchResult = [];
        this.colors = ['#99A9BF', '#F7BA2A', '#FF9900'];
        this.username = '';
        this.userId = '';
        this.info = {
            prefGenres: ["戏剧"],
            age: 20
        };
        this.MAX_COLLECT_NUM = 10;
        this.MAX_LIKE_NUM = 10;
        this.like = [];
        this.collect = [];
        this.activeName = 'like';
    }
    routerChanged() {
        console.log("进入watch");
        // console.log(this.$route.query.searchInput)
        // this.getData('/business/rest/news/search', String(this.$route.query.searchInput))
    }
    mounted() {
        this.$nextTick(() => {
            let userId = localStorage.getItem('userId');
            let username = localStorage.getItem('username');
            this.userId = userId || 'error';
            this.username = username || 'error';
        });
    }
    created() {
        console.log("进入create");
        this.getUserData('/business/rest/users/info', 0);
        this.getUserData('/business/rest/users/likeList', 1);
        this.getUserData('/business/rest/users/collectList', 2);
    }
    async getUserData(url, type) {
        // 动画加载过程中，若 axios 出现异常会导致动画无法关闭
        const loading = this.$loading({
            lock: true,
            text: 'Loading',
            spinner: 'el-icon-loading',
            background: 'rgba(0, 0, 0, 0.7)'
        });
        try {
            let userId = localStorage.getItem('userId');
            this.axios.get(url, {
                params: {
                    userId
                }
            }).then(res => {
                if (res.data.success) {
                    switch (type) {
                        case 0:
                            this.info = res.data.user;
                            break;
                        case 1:
                            this.like = res.data.data;
                            break;
                        case 2:
                            this.collect = res.data.data;
                            break;
                    }
                }
            });
        }
        catch (err) {
            console.error('请求：' + url + ' 异常 ' + err);
        }
        this.$nextTick(() => {
            loading.close();
        });
    }
    doMore(index) {
        switch (index) {
            case 1:
                this.MAX_COLLECT_NUM = 19;
                break;
        }
    }
    undoMore(index) {
        switch (index) {
            case 1:
                this.MAX_COLLECT_NUM = 5;
                break;
            case 2:
                this.MAX_LIKE_NUM = 5;
                break;
        }
    }
    handleClick(tab, event) {
        // console.log(tab, event);
    }
};
tslib_1.__decorate([
    Watch('$route')
], User.prototype, "routerChanged", null);
User = tslib_1.__decorate([
    Component
], User);
export default User;
//# sourceMappingURL=User.js.map