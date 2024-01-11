package cn.edu.scnu.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
//@Builder
// implements Serializable
public class Flower implements Serializable{
    private static final long serialVersionUID=-3710185376322109571L;
    @TableId
    private Integer flowerid; //电影id
    private String fname;   //电影名字

    private String myclass; //上映日期
    private String fclass;  //电影类型
    private String fclass1; //国家
    private String cailiao; //演员
    private String baozhuang;//编剧
    private String huayu;   //语言
    private String director;//导演
    private String shuoming;//电影简介
    private String url;     //电影播放地址
    private String length;  //片长
    private Integer price;  //电影评分
    private String picturem;//电影图片地址
//    private Integer yourprice;
//    private String pictures;1
//    private String pictureb;
//    private String pictured;
//    private String cailiaopicture;
//    private String bzpicture;
//    private String tejia;
//    private Integer sellednum;
}
