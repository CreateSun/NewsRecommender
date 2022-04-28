<template>
  <div class="header">
    <div class="site" v-on:click="goHome" title="回到首页">
      <img width="42" style="margin: 1rem 1rem" src="@/assets/logo-school.png" class="site-img" />
      <div class="title">
        <span style="font-size: 1.2rem">E-News 新闻推荐系统</span>
        <br>
        <span></span>
        <el-link type="primary">https://blog.createsun.icu/</el-link>
      </div>
    </div>
    <div class="search-container">
      <div class="search">
        <el-input type="text" v-model="searchInput" :clearable="true" placeholder="搜索新闻" />
      </div>
      <div class="search-button">
        <el-button type="info" plain @click="doSearch">搜索</el-button>
      </div>
      <div class="user" v-on:click="showUserInfo" v-if="username != 'error'">
        <el-popover placement="bottom" width="200" trigger="hover">
          <img width="32" :src="avatarUrl" class="site-img" slot="reference" />
          <p>用户名：{{username}}</p>
          <p>系统状态：在线</p>
          <router-link :to="{path: '/user', query: {username} }" class="a-name">
            <span class="hot-title">个人中心</span>
          </router-link>
          <el-button v-on:click="doLogout">退出登陆</el-button>
        </el-popover>
      </div>
    </div>

  </div>
</template>

<script src="./Header.ts" lang="ts"></script>

<style src="./Header.css" scoped>
</style>
