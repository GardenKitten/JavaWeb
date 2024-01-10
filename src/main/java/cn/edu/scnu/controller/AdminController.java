package cn.edu.scnu.controller;

import cn.edu.scnu.entity.Flower;
import cn.edu.scnu.entity.MyFlower;
import cn.edu.scnu.service.FlowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private FlowerService flowerService;
    @RequestMapping("/admin/flowerindex")
    public String flowerindex(Model model){
        List<Flower> flowerList=flowerService.findAll();
        model.addAttribute("flowers",flowerList);
        return "flowerindex";
    }
    @RequestMapping("/admin/floweradd")
    public String floweradd(Model model){
        List<String> fclasslist=flowerService.findfclass();
        model.addAttribute("fclasses",fclasslist);
        return "floweradd";
    }
    @RequestMapping("/admin/saveFlower")
    public String saveFlower(MyFlower myFlower, Model model){
        String msg=flowerService.saveFlower(myFlower);
        model.addAttribute("msg",msg);
        return "redirect:/admin/flowerindex";
    }
    @RequestMapping("/admin/saveUpdate")
    public String saveUpdate(Flower flower){
        flowerService.updateFlower(flower);
        return "redirect:/admin/flowerindex";
    }
    @RequestMapping("/admin/adminlogOut")
    public String adminlogOut(Model model){
        return "adminlogin";
    }
    @RequestMapping("/admin/toAdminLogin")
    public String toAdminLogin(Model model){
        return "adminlogin";
    }
    @RequestMapping("/admin/flowerupdate")
    public String flowerupdate(String flowerid, Model model){
        List<String> fclasslist=flowerService.findfclass();
        model.addAttribute("fclasses",fclasslist);

        Flower flower=flowerService.findFlowerById(flowerid);
        model.addAttribute("flower",flower);
        return "flowerupdate";
    }
    @RequestMapping("/admin/flowerdelete")
    public String flowerdelete(String flowerid){
        flowerService.delete(flowerid);
        return "redirect:/admin/flowerindex";
    }
}
