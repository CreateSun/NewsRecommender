import {Component, Prop, Vue} from "vue-property-decorator";

@Component
export default class Home extends Vue {
    // 实时推荐
    // stream：StreamRecs
    public stream: any = [];
    // 离线推荐
    // hot：RateMoreRecentlynews
    public hot: any = [];
    // rate：RateMorenews
    public mostAction: any = [];
    // offine：UserRecs
    public offline: any = [];
    // 页面上商品最大展示数量
    public MAX_SHOW_NUM1: number = 5;
    public MAX_SHOW_NUM2: number = 5;
    public MAX_SHOW_NUM3: number = 10;
    public MAX_SHOW_NUM4: number = 5;

    public colors: any = ['#99A9BF', '#F7BA2A', '#FF9900'];

    public bannerList = [
        'https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/52db3dee6f8ccfdf1e73e4a9665b24f9.jpg',
        'https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/63bab54049c48fa65494c16f7e2d60e4.png?w=2452&h=920',
        'https://cdn.cnbj1.fds.api.mi-img.com/news-images/mi11ultra/section1-1.jpg',
        'https://cdn.cnbj1.fds.api.mi-img.com/news-images/shouhuan5/shouhuan5-tab13.jpg',
        'https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/5e896d87c53a449509c14701673098bb.jpg?thumb=1&w=1533&h=575&f=webp&q=90',
        'https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/933683565f5273b4591403daab09212c.jpg?thumb=1&w=1533&h=575&f=webp&q=90'
    ];

    public bannerContent = [
        "国内",
        "军事",
        "航空",
        "社会",
        "娱乐",
        "国际",
        "艺术",
    ];

    public sectionPicList = [
        'https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/431e5fd6bfd1b67d096928248be18303.jpg?thumb=1&w=1533&h=150&f=webp&q=90',
        'https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/1c79d1e322e91e187cfc40af08e47977.jpg?thumb=1&w=1533&h=150&f=webp&q=90',
        'https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/89c2a209b742fce9b10d9d196149d634.jpg?thumb=1&w=1533&h=150&f=webp&q=90'
    ];

    public created() {
        this.getRecommendData('/business/rest/news/stream', 0);
        this.getRecommendData('/business/rest/news/hot', 1);
        this.getRecommendData('/business/rest/news/mostAction', 2);
        this.getRecommendData('/business/rest/news/offline', 3)
    }

    public getRecommendData(url: string, index: number) {
        let id = localStorage.getItem('userId');
        this.axios.get(url, {
            params: {
                userId: id,
                num: 30
            }
        }).then(
            (res) => {
                if (res.data.success) {
                    switch (index) {
                        case 0:
                            this.stream = res.data.news;
                            break;
                        case 1:
                            this.hot = res.data.news.sort((x: { hot: number; }, y: { hot: number; }) => y.hot-x.hot);
                            break;
                        case 2:
                            this.mostAction = res.data.news;
                            break;
                        case 3:
                            this.offline = res.data.news;
                            break
                    }
                }
            }
        ).catch(
            (err) => {
                console.log('请求: ' + url + ' 的途中发生错误 ' + err)
            }
        )

    }

    public doMore(index: number) {
        switch (index) {
            case 1:
                this.MAX_SHOW_NUM1 = 19;
                break;
            case 2:
                this.MAX_SHOW_NUM2 = 19;
                break;
            case 3:
                this.MAX_SHOW_NUM3 = 19;
                break;
            case 4:
                this.MAX_SHOW_NUM4 = 19;
                break
        }
    }

    public undoMore(index: number) {
        switch (index) {
            case 1:
                this.MAX_SHOW_NUM1 = 5;
                break;
            case 2:
                this.MAX_SHOW_NUM2 = 5;
                break;
            case 3:
                this.MAX_SHOW_NUM3 = 5;
                break;
            case 4:
                this.MAX_SHOW_NUM4 = 5;
                break
        }
    }

    public activeName:String = 'top';

    public handleClick(tab:Number, event:Object) {
        // console.log(tab, event);
    }
}
