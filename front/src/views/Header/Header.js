import * as tslib_1 from "tslib";
import { Component, Prop, Vue, Emit, Watch } from "vue-property-decorator";
let Header = class Header extends Vue {
    constructor() {
        super(...arguments);
        this.avatarUrl = "https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png";
        this.searchInput = '';
        this.username = '';
        this.msg = 'logout';
    }
    mounted() {
        this.$nextTick(() => {
            let userId = localStorage.getItem('username');
            this.username = userId || 'error';
        });
    }
    updateUserData(oldVal, newVal) {
        console.log("侦测到 Header 里面的 childMsg 发生变化了，old：" + oldVal + " new：" + newVal);
        let username = localStorage.getItem('username');
        this.username = username || 'error';
    }
    doSearch() {
        // 跳转到搜索页面
        this.$router.push({
            path: '/search',
            query: {
                searchInput: this.searchInput
            }
        });
    }
    goHome() {
        this.$router.push({ name: 'home' });
    }
    showUserInfo() {
        console.log('123456');
    }
    async doLogout() {
        localStorage.clear();
        await this.$alert('退出成功', '提示', {
            confirmButtonText: '确定'
        });
        this.username = 'error' || 'error';
        // 通知赋组件，更改 fatherVar 的值，fatherVar值一改变其他的依赖组件会立刻变化
        this.propMsg();
        this.$router.push({ name: 'login' });
    }
    send(msg) { }
    ;
    propMsg() {
        this.send(this.msg);
    }
};
tslib_1.__decorate([
    Prop({
        type: String,
        required: false,
        default: String
    })
], Header.prototype, "childMsg", void 0);
tslib_1.__decorate([
    Watch("childMsg")
], Header.prototype, "updateUserData", null);
tslib_1.__decorate([
    Emit('bindSend')
], Header.prototype, "send", null);
Header = tslib_1.__decorate([
    Component
], Header);
export default Header;
//# sourceMappingURL=Header.js.map