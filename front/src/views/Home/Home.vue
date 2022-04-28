<template>
    <div class="container" style="width: 90%">
        <!--        轮播图-->
        <div class="banner">
            <div class="category">
                <ul>
                    <li v-for="item in bannerContent" v-bind:key="item">{{item}}<i class="el-icon-arrow-right"></i></li>
                </ul>
            </div>
            <div class="carousel">
                <div class="block">
                    <el-carousel trigger="click" height="350px" :autoplay="false">
                        <el-carousel-item v-for="item in bannerList" :key="item">
                            <!--              <h3 class="small">{{ item }}</h3>-->
                            <div class="img-box">
                                <img :src="item" alt="">
                            </div>
                        </el-carousel-item>
                    </el-carousel>
                </div>
            </div>
        </div>

        <div class="section">
            <div class="base-news">
                <h1 class="home-title" title="点击量新闻推荐">大家都在看
                    <span style="float:right;">
<!--                <span @click="doMore(4)" class="more" v-if="MAX_SHOW_NUM4 === 5">查看更多</span>-->
<!--                <span @click="undoMore(4)" class="more" v-if="MAX_SHOW_NUM4 === 19">收起更多</span>-->
            </span>
                </h1>
                <div v-for="(item,index) in mostAction.slice(10, 20)"
                     :key="item.newsId"
                     class="base-card">
                    <router-link :to="{path: '/detail', query: {newsId: item.newsId} }" class="a-name">
                        <span class="title">{{item.title}}</span>
                    </router-link>
                    <p class="info">分类：{{item.category}}  |   来自：{{item.origin}} <span class="timeline">{{item.timeline}}</span></p>
                </div>
                <h1 class="home-title" title="实时推荐">猜你喜欢
                    <span style="float: right">
                <span @click="doMore(1)" class="more" v-if="MAX_SHOW_NUM1 == 5">查看更多</span>
                <span @click="undoMore(1)" class="more" v-if="MAX_SHOW_NUM1 == 19">收起更多</span>
            </span>
                </h1>
                <span v-if="stream.length == 0">
<!--      <p>你当前的账号是首次使用，且无评论操作，暂无实时推荐数据可查询</p>-->
                    <!--      <p>你需要使用一段时间后才能获得实时推荐数据</p>-->
                    <!--      <p>你也可以使用系统默认账号访问，以体验实时推荐数据</p>-->
      <p class="blank-tip">暂无推荐</p>
    </span>
                <div class="card-box" v-if="stream.length != 0">

                    <el-card
                            v-for="item in stream.slice(0, MAX_SHOW_NUM1)"
                            :key="item.newsId + 1"
                            class="card"
                    >

                        <img :src="item.imageUrl" alt="新闻图片" class="image"/>
                        <router-link :to="{path: '/detail', query: {newsId: item.newsId} }" class="a-name">
                            <h5 class="name">{{item.title}}</h5>
                        </router-link>
                    </el-card>

                </div>

                <h1 class="home-title" title="离线推荐">看了又看
                    <span style="float: right">
                <span @click="doMore(2)" class="more" v-if="MAX_SHOW_NUM2 == 5">查看更多</span>
                <span @click="undoMore(2)" class="more" v-if="MAX_SHOW_NUM2 == 19">收起更多</span>
            </span>
                </h1>
                <span v-if="offline.length == 0">
<!--      <p>你当前的账号是首次使用，暂无离线个性化离线推荐数据可查询</p>-->
                    <!--      <p>你需要使用一段时间后才能获得个性化离线推荐数据</p>-->
                    <!--      <p>你也可以使用系统默认账号访问，以体验个性化离线推荐数据</p>-->
            <p class="blank-tip">暂无推荐</p>
        </span>
        <span v-if="offline.length != 0">
            <el-card v-for="item in offline.slice(0, MAX_SHOW_NUM2)"
                    :key="item.newsId - 1"
                    class="card"
            >
                <router-link :to="{path: '/detail', query: {newsId: item.newsId} }" class="a-name">
                    <h5 class="name">{{item.title}}</h5>
                </router-link>
            </el-card>
        </span>
            </div>
            <div class="hot-news">
                <div class="top-news">
                    <h1 class="home-title" title="热门新闻">近期热门
                        <span style="float:right;">
                <span @click="doMore(3)" class="more" v-if="MAX_SHOW_NUM3 == 5">查看更多</span>
                <span @click="undoMore(3)" class="more" v-if="MAX_SHOW_NUM3 == 19">收起更多</span>
            </span>
                    </h1>
                    <el-tabs v-model="activeName" @tab-click="handleClick">
                        <el-tab-pane label="热度排行" name="top">
                            <el-card body-style="padding: 0 5px;" shadow="hover"
                                     v-for="(item,index) in hot.slice(0, MAX_SHOW_NUM3)" :key="item.newsId * 10"
                                     class="card">
                                <span :class="{'red': index<3}" class="hot-order">{{index+1}}</span>
                                <router-link :to="{path: '/detail', query: {newsId: item.newsId} }" class="a-name">
                                    <span class="hot-title">{{item.news.title}}</span>
                                </router-link>
                                <span class="hot-value">{{item.hot}}</span>
                            </el-card>
                        </el-tab-pane>
                        <el-tab-pane label="最多点赞" name="like">
                            <el-card body-style="padding: 0 5px;" shadow="hover"
                                     v-for="(item,index) in mostAction.slice(0, 10)" :key="item.newsId * 10" class="card">
                                <span :class="{'red': index<3}" class="hot-order">{{index+1}}</span>
                                <router-link :to="{path: '/detail', query: {newsId: item.newsId} }" class="a-name">
                                    <span class="hot-title">{{item.title}}</span>
                                </router-link>
                                <span class="hot-value">{{item.timeline.split(" ")[0]}}</span>
                            </el-card>
                        </el-tab-pane>
                        <el-tab-pane label="最多收藏" name="collect">
                            <el-card body-style="padding: 0 5px;" shadow="hover"
                                     v-for="(item,index) in mostAction.slice(20, 30)" :key="item.newsId * 10" class="card">
                                <span :class="{'red': index<3}" class="hot-order">{{index+1}}</span>
                                <router-link :to="{path: '/detail', query: {newsId: item.newsId} }" class="a-name">
                                    <span class="hot-title">{{item.title}}</span>
                                </router-link>
                                <span class="hot-value">{{item.timeline.split(" ")[0]}}</span>
                            </el-card>
                        </el-tab-pane>
                    </el-tabs>
                </div>
            </div>
        </div>

    </div>
</template>

<script src="./Home.ts" lang="ts"></script>

<style src="./carousel.css" scoped></style>

<style src="./Home.css" scoped></style>

<style>
    .container {
        width: 1200px;
        margin: 0 auto;
    }
</style>
