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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
    public String showflower(){
        return "showflower";
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

    //登出
    @RequestMapping("/index/logOut")
    public String logOut( HttpSession session){
        session.removeAttribute("memberLogin");
        return "redirect:/index";
    }

    @RequestMapping("/index/generateExcelReport")
    public ResponseEntity<byte[]> generateExcelReport() {
        // 获取鲜花销售数据（用实际的数据获取逻辑替换这部分）
        List<Flower> flowerSalesData = flowerService.getFlowerSalesData();

        // 创建Excel工作簿和工作表
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("电影评分报表");

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("电影名称");
        headerRow.createCell(1).setCellValue("日期");
        headerRow.createCell(2).setCellValue("评分");

        // 填充数据行
        int rowNum = 1;
        for (Flower flower : flowerSalesData) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(flower.getFname());
            row.createCell(1).setCellValue(flower.getMyclass());
            row.createCell(2).setCellValue(flower.getPrice());
        }

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "movie_price_report.xlsx");

        // 将工作簿转换为字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 以ResponseEntity形式返回字节数组
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }

    // 添加价格图表生成的请求处理方法
    @GetMapping("/api/getPriceChartData")
    @ResponseBody
    public ResponseEntity<List<Flower>> getPriceChartData() {
        List<Flower> priceChartData = flowerService.getTopPriceFlowers(10);
        return ResponseEntity.ok(priceChartData);
    }

    // 添加销量图表生成的请求处理方法
    @GetMapping("/api/getSalesChartData")
    @ResponseBody
    public ResponseEntity<List<Flower>> getSalesChartData() {
        List<Flower> salesChartData = flowerService.getTopSalesFlowers(10);
        return ResponseEntity.ok(salesChartData);
    }
}
