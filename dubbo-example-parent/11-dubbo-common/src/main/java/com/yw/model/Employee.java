package com.yw.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yangwei
 */
@Data
public class Employee implements Serializable{
    private Integer id;
    private String name;
    private int age;
}
