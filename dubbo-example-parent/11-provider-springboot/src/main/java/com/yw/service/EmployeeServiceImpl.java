package com.yw.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.yw.dao.EmployeeDao;
import com.yw.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 实现类
 */
@Service        // Dubbo的注解
@Component
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeDao employeeDao;

    // 当有对象插入时会清空 realTimeCache 缓存空间
    @CacheEvict(value = "realTimeCache", allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addEmployee(Employee employee) {
        employeeDao.insertEmployee(employee);
    }

    // 一旦有了大量查询结果，则会将此结果写入到realTimeCache缓存
    // key是 employee_ 加上方法参数
    @CacheEvict(value = "realTimeCache", key = "'employee_'+#id")
    @Override
    public Employee findEmployeeById(int id) {
        return employeeDao.selectEmployeeById(id);
    }

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    // 双重检测锁机制解决 redis 的热点缓存问题
    @Override
    public Integer findEmployeeCount() {
        // 获取Redis操作对象
        BoundValueOperations<Object, Object> ops = redisTemplate.boundValueOps("count");
        // 从缓存获取数据
        Object count = ops.get();
        if(count == null){
            synchronized (this){
                count = ops.get();
                if(count == null){
                    System.out.println("============");
                    // 从DB中查询
                    count = employeeDao.selectEmployeeCount();
                    // 将查询结果存放到Redis
                    ops.set(count, 10, TimeUnit.SECONDS);
                }
            }
        }
        return (Integer) count;
    }
}
