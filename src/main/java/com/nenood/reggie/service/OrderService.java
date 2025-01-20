package com.nenood.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nenood.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
