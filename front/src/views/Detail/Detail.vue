<template>
  <div class="container" style="width: 90%;display: flex">
    <div class="news-info">
      <div class="info">
          <div  class="news-card">
            <h3>{{info.title ? info.title : ""}}</h3>
            <p>来源：{{info.origin ? info.origin : "佚名"}} {{info.timeline}}</p>
            <p style="margin-top: 10px">关键词：{{info.keywords? info.keywords.join(" | ") : "无"}}</p>
          </div>
      </div>
      <div class="content">
        <div v-html="info.content"></div>
        <el-button :colors="colors" @click="doAction(1,info.newsId)">点赞</el-button>
        <el-button :colors="colors" @click="doAction(2,info.newsId)">收藏</el-button>
      </div>
    </div>
    <div class="recommend">
      <h1 title="基于物品的相似推荐(itemCF)">看过该新闻的人还看了</h1>
        <el-card v-for="item in itemcf"  body-style="padding: 2px 5px;" shadow="hover" :key="item.newsId * 10" class="card">
          <router-link :to="{path: '/detail', query: {newsId: item.newsId} }" class="a-name">
            <span class="rec-title">{{item.title}}</span>
          </router-link>
        </el-card>
      <h1 title="基于内容的相似推荐(contentCF)">喜欢该新闻的人也喜欢</h1>
        <el-card v-for="item in contentbased" body-style="padding: 2px 5px;" shadow="hover" :key="item.newsId / 10" class="card">
          <router-link :to="{path: '/detail', query: {newsId: item.newsId} }" class="a-name">
            <span class="rec-title">{{item.title}}</span>
          </router-link>
        </el-card>
    </div>
  </div>
</template>

<script src="./Detail.ts" lang="ts"></script>

<style src="./Detail.css" scoped>
</style>
