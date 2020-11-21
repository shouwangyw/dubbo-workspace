package com.yw.service;

import com.yw.model.Employee;

/**
 * @author yangwei
 */
public interface EmployeeService {
    void addEmployee(Employee employee);
    Employee findEmployeeById(int id);
    Integer findEmployeeCount();
}
