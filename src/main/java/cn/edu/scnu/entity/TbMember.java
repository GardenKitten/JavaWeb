package cn.edu.scnu.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

//import java.io.Serializable;

@Data
@TableName(value = "tb_member")
public class TbMember implements Serializable {
    private static final long serialVersionUID=1L;

    @TableId
    private String email;
    private String password;
    private String mname;
    private String mobile;
    private String address;
    private int jifen;
    private double ye;

    public TbMember(){}
    public TbMember(String email, String password,int jifen) {
        this.email = email;
        this.password = password;
        this.jifen=jifen;//是否是会员，是则为1，不是则为0
    }
}
