package com.zzz.pro.pojo.form;

import lombok.Data;

//用户筛选条件
@Data
public class UserFilterForm {
    private Integer sex;
    private Integer minAge;
    private Integer maxAge;
    private String pos;
}
