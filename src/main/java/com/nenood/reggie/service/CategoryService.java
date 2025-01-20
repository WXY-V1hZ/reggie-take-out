package com.nenood.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nenood.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    void delete(Long id);
}
