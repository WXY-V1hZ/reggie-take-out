package com.nenood.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nenood.reggie.entity.ShoppingCart;
import com.nenood.reggie.mapper.ShoppingCartMapper;
import com.nenood.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl
        extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements ShoppingCartService{
}
