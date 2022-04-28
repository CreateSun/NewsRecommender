package com.tqz.business.rest;

import com.tqz.business.model.domain.News;
import com.tqz.business.model.domain.User;
import com.tqz.business.model.recom.HotRecommendation;
import com.tqz.business.model.recom.Recommendation;
import com.tqz.business.model.request.*;
import com.tqz.business.service.*;
import com.tqz.business.utils.Constant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/business/rest/news")
@Controller
@CrossOrigin
public class NewsRestApi {

    @Autowired
    private RecommenderService recommenderService;
    @Autowired
    private NewsService newsService;
    @Autowired
    private UserService userService;

    /**
     * 获取热门推荐
     * @param model
     * @return
     */
    @RequestMapping(value = "/hot", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getHotNews(@RequestParam("num") int num, Model model) {
        List<HotRecommendation> recommendations = null;
        try {
            recommendations = recommenderService.getHotRecommendations(new HotRecommendationRequest(20));
            model.addAttribute("success", true);
            model.addAttribute("news", newsService.getHotRecommendNews(recommendations));
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("msg", e.getMessage());
        }
        return model;
    }



    /**
     * 获取用户行为最多的新闻
     * @param model
     * @return
     */
    @RequestMapping(value = "/mostAction", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getActionMoreNews(@RequestParam("num") int num, Model model) {
        List<Recommendation> recommendations = null;
        try {
            recommendations = recommenderService.getActionMoreRecommendations(new RateMoreRecommendationRequest(num));
            model.addAttribute("success", true);
            model.addAttribute("news", newsService.getRecommendNews(recommendations));
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("msg", e.getMessage());
        }
        return model;
    }

    /**
     * 基于物品的协同过滤
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/itemcf/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getItemCFNews(@PathVariable("id") int id, Model model) {
        List<Recommendation> recommendations = null;
        try {
            recommendations = recommenderService.getItemCFRecommendations(new ItemCFRecommendationRequest(id));
            model.addAttribute("success", true);
            model.addAttribute("news", newsService.getRecommendNews(recommendations));
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("msg", e.getMessage());
        }
        return model;
    }

    /**
     * 基于内容的推荐
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/contentbased/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getContentBasedNews(@PathVariable("id") String id, Model model) {
        List<Recommendation> recommendations = null;
        try {
            recommendations = recommenderService.getContentBasedRecommendations(new ContentBasedRecommendationRequest(id));
            model.addAttribute("success", true);
            model.addAttribute("news", newsService.getRecommendNews(recommendations));
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("msg", e.getMessage());
        }
        return model;
    }

    /**
     * 获取单个新闻的信息
     *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "/info/{id}", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getNewsInfo(@PathVariable("id") String id, Model model) {
        model.addAttribute("success", true);
        model.addAttribute("news", newsService.findByNewsId(id));
        return model;
    }

    /**
     * 模糊查询新闻
     *
     * @param query
     * @param model
     * @return
     */
    @RequestMapping(value = "/search", produces = "application/json", method = RequestMethod.GET, headers = {})
    @ResponseBody
    public Model getSearchNews(@RequestParam("query") String query, Model model) {
        List<News> news = null;
        try {
            news = newsService.findByNewsName(new String(query.getBytes("ISO-8859-1"), "UTF-8"));
            model.addAttribute("success", true);
            model.addAttribute("news", news);
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("msg", e.getMessage());
            model.addAttribute("extra", "可能是编码问题导致查询异常");
        }
        return model;
    }


    /**
     * 离线推荐
     *
     * @param userId
     * @param num
     * @param model
     * @return
     */
    @RequestMapping(value = "/offline", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getOfflineNews(@RequestParam("userId") String userId, @RequestParam("num") int num, Model model) {
        User user = null;
        List<Recommendation> recommendations = null;
        try {
            user = userService.findByUserId(userId);
            // 主动设置一个 admin 账号，避免offine数据获取失败
            if (user != null && user.getUsername().equals("admin")) {
                user.setUid("1");
            }
            assert user != null;
            recommendations = recommenderService.getCollaborativeFilteringRecommendations(new UserRecommendationRequest(user.getUserId(), num));
            model.addAttribute("success", true);
            model.addAttribute("news", newsService.getRecommendNews(recommendations));
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("msg", e.getMessage());
        }
        return model;
    }

    // todo 实时推荐
    /**
     * 实时推荐
     */
    @RequestMapping(value = "/stream", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model getStreamNews(@RequestParam("userId") String username, @RequestParam("num") int num, Model model) {
        User user = null;
        List<Recommendation> recommendations = null;
        try {
            user = userService.findByUserId(username);
            recommendations = recommenderService.getStreamRecommendations(new UserRecommendationRequest(user.getUserId(), num));
            model.addAttribute("success", true);
            model.addAttribute("news", newsService.getRecommendNews(recommendations));
        } catch (Exception e) {
            model.addAttribute("success", false);
            model.addAttribute("msg", e.getMessage());
        }
        return model;
    }
}
