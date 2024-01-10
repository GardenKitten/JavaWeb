package cn.edu.scnu.service;

import cn.edu.scnu.common.MD5Util;
import cn.edu.scnu.common.MapperUtil;
import cn.edu.scnu.entity.TbMember;
import cn.edu.scnu.mapper.MemberMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MemberService extends ServiceImpl<MemberMapper, TbMember> {
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    public TbMember login(String email,String password) {
        // 进入数据库查询
        TbMember member=memberMapper.selectById(email);
        if(member.getPassword().equals(MD5Util.md5(password))) {
            return member;
        }else{
            return null;
        }
    }


    public String register(String email,String password){
        TbMember member=new TbMember(email,MD5Util.md5(password));
        if(memberMapper.insert(member)>0){
            String ticket= UUID.randomUUID().toString();
            // 判断如果插入成功，将当前注册结果进行缓存，并设置用户登录超时30分钟（设置过期时间为30分钟）
            redisTemplate.opsForValue().set(ticket,email,30, TimeUnit.MINUTES);
            return ticket;
        }else{
            return "";
        }
    }
}
