package com.tqz.business.rest;

import com.tqz.business.model.domain.User;
import com.tqz.business.model.request.LoginUserRequest;
import com.tqz.business.model.request.NewsRatingRequest;
import com.tqz.business.model.request.RegisterUserRequest;
import com.tqz.business.model.request.UserActionRequset;
import com.tqz.business.service.KafkaLogProducer;
import com.tqz.business.service.UserService;
import com.tqz.business.service.RatingService;
import com.tqz.business.utils.Constant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RequestMapping("/business/rest/users")
@Controller
@CrossOrigin
public class UserRestApi {
    private static Logger logger = Logger.getLogger(NewsRestApi.class.getName());

    @Autowired
    private UserService userService;
    @Autowired
    private RatingService ratingService;

    @RequestMapping(value = "/info", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model info(@RequestParam("userId") String userId, Model model) {
        User user = userService.findByUserId(userId);
        model.addAttribute("success", user != null);
        model.addAttribute("user", user);
        return model;
    }

    /*  用户  登录 / 注册  */
    @RequestMapping(value = "/login", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model login(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        User user = userService.loginUser(new LoginUserRequest(username, password));
        model.addAttribute("success", user != null);
        model.addAttribute("user", user);
        return model;
    }

    @RequestMapping(value = "/register", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model addUser(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        if (userService.checkUserExist(username)) {
            model.addAttribute("success", false);
            model.addAttribute("message", " 用户名已经被注册！");
            return model;
        }
        model.addAttribute("success", userService.registerUser(new RegisterUserRequest(username, password)));
        return model;
    }

    /*  用户点赞  */
    @RequestMapping(value = "/like", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model userLike(@RequestParam("userId") String userId, @RequestParam("newsId") String newsId, Model model) {
//        this.rateToNewsOnline(newsId, 2, userId);
        model.addAttribute("success", userService.likeNews(new UserActionRequset(userId, newsId, new Date().getTime()), true));
        return model;
    }
    @RequestMapping(value = "/like", produces = "application/json", method = RequestMethod.DELETE)
    @ResponseBody
    public Model userDisLike(@RequestParam("userId") String userId, @RequestParam("newsId") String newsId, Model model) {
        model.addAttribute("success", userService.likeNews(new UserActionRequset(userId, newsId, new Date().getTime()), false));
        return model;
    }

    @RequestMapping(value = "/likeList", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model userLikeList(@RequestParam("userId") String userId, Model model) {
        List list = userService.getLikeNewsList(userId);
        model.addAttribute("success", list != null);
        model.addAttribute("data", list);
        return model;
    }
    /* 用户浏览 */
    @RequestMapping(value = "/view", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model userView(@RequestParam("userId") String userId, @RequestParam("newsId") String newsId, Model model) {
        //this.rateToNewsOnline(newsId, 1, userId);
        model.addAttribute("success", userService.viewNews(new UserActionRequset(userId, newsId, new Date().getTime())));
        return model;
    }

    /* 用户收藏 */
    @RequestMapping(value = "/collect", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model userCollect(@RequestParam("userId") String userId, @RequestParam("newsId") String newsId, Model model) {
//        this.rateToNewsOnline(newsId, 3, userId);
        model.addAttribute("success", userService.collectNews(new UserActionRequset(userId, newsId, new Date().getTime()), true));
        return model;
    }
    @RequestMapping(value = "/collect", produces = "application/json", method = RequestMethod.DELETE)
    @ResponseBody
    public Model userDisCollect(@RequestParam("userId") String userId, @RequestParam("newsId") String newsId, Model model) {
        model.addAttribute("success", userService.collectNews(new UserActionRequset(userId, newsId, new Date().getTime()), false));
        return model;
    }

    @RequestMapping(value = "/collectList", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Model userCollectList(@RequestParam("userId") String userId, Model model) {
        List list = userService.getCollectNewsList(userId);
        model.addAttribute("success", list != null);
        model.addAttribute("data", list);
        return model;
    }

    // 用户给新闻打分-实时推荐
    private void rateToNewsOnline(String id, int action, String userId) {
        double score;
        switch (action) {
            case 1: score = 5.0;break; // view
            case 2: score = 15.0;break; // like
            case 3: score = 25.0;break; // collect
            default:score = 6.0;break;
        }
        try {
            User user = userService.findByUserId(userId);
            NewsRatingRequest request = new NewsRatingRequest(user.getUserId(), id, score);
            boolean complete = ratingService.newsRating(request);
            // 埋点日志
            if (complete) {
                System.out.print("=========埋点=========");
                String text = user.getUserId() + "|" + id + "|" + request.getScore() + "|" + System.currentTimeMillis() / 1000;
                logger.info(Constant.NEWS_RATING_PREFIX + ":" + text);
                KafkaLogProducer.produceLog(text);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
