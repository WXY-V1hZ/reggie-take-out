package com.nenood.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nenood.reggie.entity.User;
import com.nenood.reggie.mapper.UserMapper;
import com.nenood.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl
        extends ServiceImpl<UserMapper, User>
        implements UserService {

}
