package com.npc.xibo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npc.xibo.entity.Employee;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
