package com.yw.dao;

import com.yw.model.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

// 自动Mapper的动态代理
@Mapper
@Repository
public interface EmployeeDao {
    void insertEmployee(Employee employee);
    Integer selectEmployeeCount();
    Employee selectEmployeeById(int id);
}
