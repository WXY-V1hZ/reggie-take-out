package com.nenood.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nenood.reggie.common.R;
import com.nenood.reggie.entity.Employee;
import com.nenood.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("login")
    public R<Employee> login(HttpSession session, @RequestBody Employee employee) {

        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        if (emp == null) {
            return R.error("用户名或密码错误");
        }

        if (!password.equals(emp.getPassword())) {
            return R.error("用户名或密码错误");
        }

        if (emp.getStatus() == 0) {
            return R.error("用户已锁定");
        }

        // 将当前会话中employee属性设置为empId
        session.setAttribute("employee", emp.getId());

        return R.success(emp);

    }

    @PostMapping("logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("登出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工: {}", employee);

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //? 使用MetaObjectHandler自动填充字段, 不再需要手动修改
        /*employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);*/

        employeeService.save(employee);

        return R.success("新增成员成功");
    }

    @GetMapping("page")
    public R<Page<Employee>> page(HttpServletRequest request,
                                  @RequestParam("page") int page,
                                  @RequestParam("pageSize") int pageSize,
                                  @RequestParam(value = "name", required = false) String name) {
        log.info("员工分页查询...");
        log.info("page: {}, pageSize: {}, name: {}", page, pageSize, name);

        Page<Employee> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("Update: {}", employee);

        // Long empId = (Long) request.getSession().getAttribute("employee");

        //? 使用MetaObjectHandler自动填充字段, 不再需要手动修改
        /*employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);*/

        employeeService.updateById(employee);

        return R.success("修改成功");
    }

    @GetMapping("{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据Id查找员工信息...");
        Employee employee = employeeService.getById(id);

        if (employee == null) {
            return R.error("查询错误");
        }

        return R.success(employee);
    }

}
