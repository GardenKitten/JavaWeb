package cn.edu.scnu.controller;

import cn.edu.scnu.entity.*;
import cn.edu.scnu.mapper.CustomerMapper;
import cn.edu.scnu.service.CartService;
import cn.edu.scnu.service.CustomerService;
import cn.edu.scnu.service.OrderService;
import cn.edu.scnu.service.ShoplistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    private CustomerService customerService;
    private CartService cartService;
    private OrderService orderService;
    private ShoplistService shoplistService;

    public OrderController(CustomerService customerService, CartService cartService, OrderService orderService, ShoplistService shoplistService) {
        this.customerService = customerService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.shoplistService = shoplistService;
    }

    @RequestMapping("/order/order")
    public String order(String cartIds, HttpSession session, Model model){
        TbMember member = (TbMember)session.getAttribute("memberLogin");
        if(member == null){
            return "forward:/index/tologin";
        }
        model.addAttribute("member",member);
        //查找收货地址
        List<Customer> customers = customerService.findByEmail(member.getEmail());
        model.addAttribute("customers",customers);

        String[] arrCartIds = cartIds.split(",");
        List<Cart> carts = new ArrayList<Cart>();
        int total = 0;
        int sum = 0;
        for(String cid:arrCartIds){
            Integer cartID = Integer.parseInt(cid);
            Cart cart = cartService.getById(cartID);
            total += cart.getNum() * cart.getYourprice();
            sum += cart.getNum();
            carts.add(cart);
        }
        model.addAttribute("sum",sum);
        model.addAttribute("total",total);
        model.addAttribute("carts",carts);
        session.setAttribute("cartIds",cartIds);

        return "order";
    }

    @RequestMapping("/order/addOrder")
    @ResponseBody
    public String addOrder(MyOrder order, HttpSession session){
        TbMember member = (TbMember) session.getAttribute("memberLogin");
        if(member == null)
            return "login";
        order.setEmail(member.getEmail());
        order.setInputtime(new Timestamp(System.currentTimeMillis()));
        order.setStatus("未付款");
        orderService.save(order);
        //shoplist
        String cartIds = (String) session.getAttribute("cartIds");
        String[] arrCartIds = cartIds.split(",");
        for(String cid:arrCartIds){
            Cart cart = cartService.getById(cid);
            Shoplist shoplist = new Shoplist();
            shoplist.setOrderId(order.getOrderId());
            shoplist.setEmail(member.getEmail());
            shoplist.setFlowerid(cart.getFlowerid());
            shoplist.setNum(cart.getNum());
            shoplist.setPjstar(5);
            shoplistService.save(shoplist);
            cartService.removeById(cid);
        }
        return "success";

    }
    @RequestMapping("/order/showOrder")
    public String showorder(HttpSession session, Model model){
        //1)判断是否登录
        TbMember member = (TbMember) session.getAttribute("memberLogin");
        if(member == null){
            return "forward:/index/toLogin";
        }
        //2)查找该email用户的所有订单，按‘order_id’降序排列
        List<Showorder> showorders = orderService.showorder(member.getEmail());
        model.addAttribute("showorders",showorders);

        //3）根据每个订单的订单号，查找showshoplist视图中对应该订单的所有商品信息
        // 创建map，存放该用户的所有订单信息
        Map<Integer,List> map = new HashMap<Integer,List>();
        for(Showorder order:showorders){
            List<Showshoplist> showshoplists = orderService.findShoplistByOrderId(order.getOrderId());
            map.put(order.getOrderId(),showshoplists);
        }
        model.addAttribute("map",map);
        return "showorder";
    }

    @RequestMapping("/order/delorder")
    public String delorder(Integer id){
        orderService.removeById(id);
        shoplistService.delete(id);
        return "forward:/order/showOrder";
    }

    @RequestMapping("/order/pay")
    public String pay(Integer orderId){
        orderService.updateOrder(orderId,"已付款");
        return "forward:/order/showOrder";
    }

    @RequestMapping("/order/updateOrder")
    public String updateOrder(Integer orderId){
        orderService.updateOrder(orderId,"未评价");
        return "forward:/order/showOrder";
    }


}
