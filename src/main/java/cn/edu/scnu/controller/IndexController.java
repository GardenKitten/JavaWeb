package cn.edu.scnu.controller;

import cn.edu.scnu.entity.Flower;
import cn.edu.scnu.entity.Shoplist;
import cn.edu.scnu.entity.TbMember;
import cn.edu.scnu.service.FlowerService;
import cn.edu.scnu.service.MemberService;
import cn.edu.scnu.service.ShoplistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class IndexController {
    @Autowired
    private MemberService memberService;
    @Autowired
    private FlowerService flowerService;

    @Autowired
    private ShoplistService shoplistService;

    @RequestMapping("/index")
    public String index(@RequestParam(name = "pageNo",defaultValue = "1")Integer pageNo,
                        @RequestParam(name = "pageSize",defaultValue = "10")Integer pageSize,
                        String cailiao,
                        String fclass,
                        String fclass1,
                        boolean price,
                        Model model){
//        if(minprice==null){
//            minprice=0;
//        }
//        if(maxprice==null||maxprice<minprice){
//            maxprice=Integer.MAX_VALUE;
//        }
        Map<String, Object> map=flowerService.queryPage(cailiao,fclass,fclass1,price,pageNo,pageSize);
        //  Map<String, Object> map=flowerService.queryPage(pageNo,pageSize);
        Integer count=(Integer) map.get("count");
        List<Flower> flowerList=(List<Flower>) map.get("flowerlist");
        int pageCount=(count%pageSize==0)?(count/pageSize):(count/pageSize+1);
        model.addAttribute("cailiao",cailiao);
        model.addAttribute("fclass",fclass);
        model.addAttribute("fclass1",fclass1);
        model.addAttribute("price",price);
//        model.addAttribute("minprice",minprice);
//        model.addAttribute("maxprice",maxprice);
        model.addAttribute("pageCount",pageCount);
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("flowerlist",flowerList);
        model.addAttribute("fclasses",flowerService.findfclass());
        model.addAttribute("fclasses1",flowerService.findfclass1());
        //model.addAttribute("fclasses1",flowerService.findfclass1());
        return "index";
    }
    @RequestMapping("/index/flowerdetail")
    public String flowerdetail(String flowerid,Model model){
        model.addAttribute("flower",flowerService.findFlowerById(flowerid));
        List<Shoplist> shopLists = shoplistService.findAll();
        model.addAttribute("shoplists",shopLists);
        return "flowerdetail";
    }

    @RequestMapping("/index/showflower")
    public String showflower(Model model){

        // 获取随机鲜花列表
        List<Flower> randomFlowers = flowerService.getRandomFlower(70);

        // 分别设置不同排行方式的随机鲜花列表
        model.addAttribute("weeklyRanking", flowerService.getRandomFlower(20));
        model.addAttribute("monthlyRanking", flowerService.getRandomFlower(20));
        model.addAttribute("popularityRanking", flowerService.getRandomFlower(20));
        model.addAttribute("regionalRanking", flowerService.getRandomFlower(20));

        return "showflower";
    }

    //会员权限控制
    @RequestMapping("/index/flowerdetail/doPlayOn")
    //@ResponseBody
    public String doPlayOn(String flowerid,HttpSession session){
        System.out.println("会员播放被调用");
        TbMember member =(TbMember)session.getAttribute("memberLogin");
        Flower flower=flowerService.findFlowerById(flowerid);
        if (member != null) {

            boolean permission =flowerService.checkUser(flower,member);
            if(permission==true){
                //String movieURL=flower.getUrl();
                return "movieURL";
            }
            return "VIPsubscribe";

        }

        else
            return "VIPregister";


    }

    @RequestMapping("/toTest")
    public String test(){

        return "test";
    }
    @RequestMapping("/index/toLogin")
    public String toLogin(){
        return "login";
    }

    //跳转普通注册界面
    @RequestMapping("/index/toRegister")
    public String toRegister(){
        return "register";
    }


    //跳转会员注册界面
    @RequestMapping("/index/toVIPRegister")
    public String toVIPRegister(){
        return "VIPregister";
    }

    //跳转会员开通界面
    @RequestMapping("/index/toVIPsubscribe")
    public String toVIPsubscribe(){
        return "VIPsubscribe";
    }

    //普通用户登录
    @RequestMapping("/index/doLogin")
    @ResponseBody
    public String doLogin(String email, String password, HttpSession session) {
        TbMember member = memberService.login(email, password);
        member.setPassword(""); //去敏
        if (member != null) {
            session.setAttribute("memberLogin", member);
            return "登录成功!";
        } else {
            return "登录失败!";
        }
    }


    //普通用户注册，设置jifen为0
    @RequestMapping("/index/doRegister")
    @ResponseBody
    public String doRegister(String email,String passw1,HttpServletResponse response){
        // 调用业务层确定合法并且存储数据
        String ticket=memberService.register(email,passw1,0);
        //System.out.println("调用了非会员注册");
        // 控制层存储业务层注册成功的rediskey值
        // !"".equals(ticket)&&ticket!=null
        if(!StringUtils.isEmpty(ticket)){
            // ticket非空，表示redis已经存好当前注册的结果
            // 将ticket添加到cookie，cookie过期时间设置为-1，表示：不设置生效时间，默认浏览器关闭即失效。
            Cookie cookie=new Cookie("FLOWER_TICKET",ticket);
            cookie.setMaxAge(-1);
            cookie.setPath("/");
            response.addCookie(cookie);
            return "注册成功";
        }else{

            return "注册失败！";
        }
    }

    //会员注册,设置jifen为1
    @RequestMapping("/index/doVIPRegister")
    @ResponseBody
    public String doVIPRegister(String email,String passw1,HttpServletResponse response){
        // 调用业务层确定合法并且存储数据
        String ticket=memberService.register(email,passw1,1);
        //System.out.println("调用了会员注册");
        // 控制层存储业务层注册成功的rediskey值
        // !"".equals(ticket)&&ticket!=null
        if(!StringUtils.isEmpty(ticket)){
            // ticket非空，表示redis已经存好当前注册的结果
            // 将ticket添加到cookie，cookie过期时间设置为-1，表示：不设置生效时间，默认浏览器关闭即失效。
            Cookie cookie=new Cookie("FLOWER_TICKET",ticket);
            cookie.setMaxAge(-1);
            cookie.setPath("/");
            response.addCookie(cookie);
            return "会员注册成功！";
        }else{
            return "注册失败！";
        }
    }

    //会员开通
    @RequestMapping("/index/doVIPsubscribe")
    @ResponseBody
    public String doVIPsubscribe(String email,String passw1,HttpServletResponse response){
        // 调用业务层确定合法并且存储数据
        boolean success=memberService.subscribe(email,passw1);

        if(success){

            return "开通成功";
        }else{
            return "您已经是会员了！";
        }
    }

    //播放会员影片


    //登出
    @RequestMapping("/index/logOut")
    public String logOut( HttpSession session){
        session.removeAttribute("memberLogin");
        return "redirect:/index";
    }

}
