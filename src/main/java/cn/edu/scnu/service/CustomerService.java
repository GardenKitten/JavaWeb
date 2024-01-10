package cn.edu.scnu.service;

import cn.edu.scnu.entity.Customer;
import cn.edu.scnu.entity.TbMember;
import cn.edu.scnu.mapper.CustomerMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class CustomerService extends ServiceImpl<CustomerMapper, Customer> {

    private CustomerMapper customerMapper;
    @Autowired
    public CustomerService(CustomerMapper customerMapper){
        this.customerMapper = customerMapper;
    }

    public List<Customer> findByEmail(String email) {
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email",email);
        return customerMapper.selectList(queryWrapper);
    }

    public void setDefault(int custId, int originalId) {
        Customer customer = customerMapper.selectById(custId);
        Customer _customer = customerMapper.selectById(originalId);
        _customer.setCdefault("0");
        customer.setCdefault("1");
        customerMapper.updateById(_customer);
        customerMapper.updateById(customer);
    }

    public void editCustomer(Customer customer) {
        customerMapper.updateById(customer);
    }
}
