<template>
  <div class="container" style="width: 90%">
    <h1>搜索结果</h1>

    <div v-for="item in searchResult" :key="item.newsId" class="card">
      <router-link :to="{path: '/detail', query: {newsId: item.newsId} }" class="a-name">
        <h5 class="name">{{item.title}}</h5>
      </router-link>
      <p class="info">{{item.category}} | {{item.origin}}        {{item.timeline}}</p>
    </div>
  </div>
</template>

<script src="./Search.ts" lang="ts"></script>

<style src="./Search.css" scoped>
</style>
