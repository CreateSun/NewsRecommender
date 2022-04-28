<!--Author: CreateSun -->
<!--Date: 2022/4/25 -->

<template>
    <div class="container" style="width: 90%">
        <div class="decription">
            <el-descriptions title="用户中心">
                <el-descriptions-item label="用户ID">{{userId}}</el-descriptions-item>
                <el-descriptions-item label="用户名">{{username}}</el-descriptions-item>
                <el-descriptions-item label="年龄">{{info.age}}</el-descriptions-item>
                <el-descriptions-item label="偏好">
                    <el-tag v-for="item in info.prefGenres">{{item}}</el-tag>
                </el-descriptions-item>
            </el-descriptions>
        </div>

        <div class="list">
            <el-tabs v-model="activeName" @tab-click="handleClick">
                <el-tab-pane label="我的点赞" name="like">
                    <div v-for="item in like.slice(0, MAX_LIKE_NUM)" :key="item.newsId" class="card">
                        <router-link :to="{path: '/detail', query: {newsId: item.newsId} }" class="a-name">
                            <h5 class="name">{{item.title}}</h5>
                        </router-link>
                        <p class="info">{{item.category}} | {{item.origin}}        {{item.timeline}}</p>
                    </div>
                    <el-empty v-if="like.length===0" description="暂无数据"></el-empty>
                </el-tab-pane>
                <el-tab-pane label="我的收藏" name="collect">
                    <div v-for="item in collect.slice(0, MAX_COLLECT_NUM)" :key="item.newsId+1122" class="card">
                        <router-link :to="{path: '/detail', query: {newsId: item.newsId} }" class="a-name">
                            <h5 class="name">{{item.title}}</h5>
                        </router-link>
                        <p class="info">{{item.category}} | {{item.origin}}        {{item.timeline}}</p>
                    </div>
                    <el-empty v-if="collect.length===0" description="暂无数据"></el-empty>
                </el-tab-pane>
            </el-tabs>
        </div>

    </div>
</template>

<script src="./User.ts" lang="ts"></script>

<style src="./User.css" scoped>

</style>
