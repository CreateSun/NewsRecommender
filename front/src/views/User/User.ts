import {Component, Prop, Vue, Watch} from "vue-property-decorator";

@Component
export default class User extends Vue {

    public searchResult: any = [];

    public colors: Array<String> = ['#99A9BF', '#F7BA2A', '#FF9900'];
    public username: string = '';
    public userId: string = '';
    public info = {
        prefGenres: ["戏剧"],
        age: 20
    };

    public MAX_COLLECT_NUM: number = 10;
    public MAX_LIKE_NUM: number = 10;

    public like = [];
    public collect = [];

    @Watch('$route')
    public routerChanged() {
        console.log("进入watch")
        // console.log(this.$route.query.searchInput)
        // this.getData('/business/rest/news/search', String(this.$route.query.searchInput))
    }

    public mounted() {
        this.$nextTick(() => {
            let userId = localStorage.getItem('userId');
            let username = localStorage.getItem('username');
            this.userId = userId || 'error';
            this.username = username || 'error';
        })
    }

    public created() {
        console.log("进入create");
        this.getUserData('/business/rest/users/info', 0);
        this.getUserData('/business/rest/users/likeList', 1);
        this.getUserData('/business/rest/users/collectList', 2);
    }

    public async getUserData(url: string, type: number) {
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
                        case 0: this.info = res.data.user;break;
                        case 1: this.like = res.data.data;break;
                        case 2: this.collect = res.data.data;break;
                    }
                }
            });

        } catch (err) {
            console.error('请求：' + url + ' 异常 ' + err)
        }
        this.$nextTick(() => { // 以服务的方式调用的 Loading 需要异步关闭
            loading.close();
        });
    }


    public doMore(index: number) {
        switch (index) {
            case 1:
                this.MAX_COLLECT_NUM = 19;
                break;
        }
    }

    public undoMore(index: number) {
        switch (index) {
            case 1:
                this.MAX_COLLECT_NUM = 5;
                break;
            case 2:
                this.MAX_LIKE_NUM = 5;
                break;

        }
    }

    public activeName: String = 'like';

    public handleClick(tab: Number, event: Object) {
        // console.log(tab, event);
    }

}
