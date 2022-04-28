import * as tslib_1 from "tslib";
import { Component, Vue, Emit } from "vue-property-decorator";
let Login = class Login extends Vue {
    constructor() {
        super(...arguments);
        /*
              子组件如何给父组件传递数据：
              首先：
                  导入 Emit
              然后：
                  定义发送数据的事件 @Emit('bindSend') send(msg: string) { };
              最后：
                  定义发送数据的方法，这个方法要调用上面的 send 方法
              最最后：
                  父组件接收值用 @bindSend="" 这个事件接收数据，里面放一个回调函数
                  用该回调函数处理子组件返回的数据
          */
        this.msg = 'updateUserData';
        this.isRegisterStatus = false;
        this.form = {
            'username': 'admin',
            'password': '123456'
        };
    }
    // bindSend 为父组件引用子组件上 绑定的事件名称
    send(msg) { }
    ; // send 处理给父组件传值的逻辑
    // 通过触发这个事件来处理发送的内容数据的逻辑，然后执行 @Emit() 定义的 sen(msg: string){} 事件
    propMsg() {
        this.send(this.msg);
    }
    async created() {
        // 检测浏览器是否缓存了用户登录信息，没有就登陆，否则就进入首页
        // dubug 的时候手动打开开发者工具的 Applicatuion 清空 localStorage
        let user = localStorage.getItem('user');
        if (user == null) {
            console.log('没有找到已登陆用户');
        }
        else {
            console.log('发现已登陆用户：' + user);
            // 直接前往首页
            this.$router.push({ name: 'home' });
        }
    }
    async doLogin() {
        console.dir(this.form);
        // 发送登录请求
        let res = await this.axios.get('/business/rest/users/login', {
            params: {
                username: this.form.username,
                password: this.form.password
            }
        });
        if (res.data.success) {
            await this.$alert('登录成功', '提示', {
                confirmButtonText: '确定'
            });
            // 将登陆信息缓存在浏览器里面
            localStorage.setItem('userId', res.data.user.userId);
            localStorage.setItem('username', this.form.username);
            // 通知Head组件该更新数据了
            this.propMsg();
            // 前往首页
            await this.$router.push({ name: 'home', query: { "isFirst": res.data.user.first } });
        }
        else {
            this.$alert('登陆失败', '提示', {
                confirmButtonText: '确定'
            });
            this.form = {};
        }
    }
    async doRegister() {
        console.dir(this.form);
        // 向后端发送注册请求
        let res = await this.axios.get('/business/rest/users/register', {
            params: {
                username: this.form.username,
                password: this.form.password
            }
        });
        if (res.data.success) {
            await this.$alert('注册成功', '提示', {
                confirmButtonText: '确定'
            });
            // 成功后跳回到上一级登录页面
            this.goRegister();
        }
        else {
            this.$alert('注册失败', '提示', {
                confirmButtonText: '确定'
            });
        }
    }
    goRegister() {
        this.form = {
            'username': '',
            'password': ''
        };
        this.isRegisterStatus = !this.isRegisterStatus;
    }
};
tslib_1.__decorate([
    Emit('bindSend')
], Login.prototype, "send", null);
Login = tslib_1.__decorate([
    Component
], Login);
export default Login;
//# sourceMappingURL=Login.js.map