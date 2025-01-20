package com.nenood.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nenood.reggie.common.R;
import com.nenood.reggie.entity.Category;
import com.nenood.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 分类的增删改查
 */
@RestController
@RequestMapping("category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    // 新增分类
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("新增分类: {}", category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    // 分页查询分类
    @GetMapping("page")
    public R<Page<Category>> page(HttpServletRequest request,
                                  @RequestParam("page") int page,
                                  @RequestParam("pageSize") int pageSize) {
        log.info("菜品分页查询...");
        log.info("page: {}, pageSize: {}", page, pageSize);

        // 分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        // 条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<Category>();
        // 根据sort值来排序
        queryWrapper.orderByAsc(Category::getSort);
        // 进行分页查询
        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    // 删除分类
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long id) {
        log.info("删除分类{}...", id);
        categoryService.delete(id);
        return R.success("删除成功");
    }

    // 修改分类
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("修改分类{}...", category.getId());
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    // 根据分类编号查询分类
    @GetMapping("list")
    public R<List<Category>> list(Category category) { //? SpringMvc会自动将参数绑定至对象的属性上
        // 条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // 先判断实例中是否有type属性，然后添加条件
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        // sort为第一关键字，更新时间为第二关键字
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        return R.success(categoryService.list(queryWrapper));
    }
}
