import * as tslib_1 from "tslib";
import { Component, Vue } from "vue-property-decorator";
let Home = class Home extends Vue {
    constructor() {
        super(...arguments);
        // 实时推荐
        // stream：StreamRecs
        this.stream = [];
        // 离线推荐
        // hot：RateMoreRecentlynews
        this.hot = [];
        // rate：RateMorenews
        this.mostAction = [];
        // offine：UserRecs
        this.offline = [];
        // 页面上商品最大展示数量
        this.MAX_SHOW_NUM1 = 5;
        this.MAX_SHOW_NUM2 = 5;
        this.MAX_SHOW_NUM3 = 10;
        this.MAX_SHOW_NUM4 = 5;
        this.colors = ['#99A9BF', '#F7BA2A', '#FF9900'];
        this.bannerList = [
            'https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/52db3dee6f8ccfdf1e73e4a9665b24f9.jpg',
            'https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/63bab54049c48fa65494c16f7e2d60e4.png?w=2452&h=920',
            'https://cdn.cnbj1.fds.api.mi-img.com/news-images/mi11ultra/section1-1.jpg',
            'https://cdn.cnbj1.fds.api.mi-img.com/news-images/shouhuan5/shouhuan5-tab13.jpg',
            'https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/5e896d87c53a449509c14701673098bb.jpg?thumb=1&w=1533&h=575&f=webp&q=90',
            'https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/933683565f5273b4591403daab09212c.jpg?thumb=1&w=1533&h=575&f=webp&q=90'
        ];
        this.bannerContent = [
            "国内",
            "军事",
            "航空",
            "社会",
            "娱乐",
            "国际",
            "艺术",
        ];
        this.sectionPicList = [
            'https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/431e5fd6bfd1b67d096928248be18303.jpg?thumb=1&w=1533&h=150&f=webp&q=90',
            'https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/1c79d1e322e91e187cfc40af08e47977.jpg?thumb=1&w=1533&h=150&f=webp&q=90',
            'https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/89c2a209b742fce9b10d9d196149d634.jpg?thumb=1&w=1533&h=150&f=webp&q=90'
        ];
        this.activeName = 'top';
    }
    created() {
        this.getRecommendData('/business/rest/news/stream', 0);
        this.getRecommendData('/business/rest/news/hot', 1);
        this.getRecommendData('/business/rest/news/mostAction', 2);
        this.getRecommendData('/business/rest/news/offline', 3);
    }
    getRecommendData(url, index) {
        let id = localStorage.getItem('userId');
        this.axios.get(url, {
            params: {
                userId: id,
                num: 30
            }
        }).then((res) => {
            if (res.data.success) {
                switch (index) {
                    case 0:
                        this.stream = res.data.news;
                        break;
                    case 1:
                        this.hot = res.data.news.sort((x, y) => y.hot - x.hot);
                        break;
                    case 2:
                        this.mostAction = res.data.news;
                        break;
                    case 3:
                        this.offline = res.data.news;
                        break;
                }
            }
        }).catch((err) => {
            console.log('请求: ' + url + ' 的途中发生错误 ' + err);
        });
    }
    doMore(index) {
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
                break;
        }
    }
    undoMore(index) {
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
                break;
        }
    }
    handleClick(tab, event) {
        // console.log(tab, event);
    }
};
Home = tslib_1.__decorate([
    Component
], Home);
export default Home;
//# sourceMappingURL=Home.js.map