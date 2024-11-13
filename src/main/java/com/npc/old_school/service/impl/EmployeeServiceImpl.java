package com.npc.old_school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.npc.old_school.dto.xibo.EmployeeDTO;
import com.npc.old_school.entity.Employee;
import com.npc.old_school.entity.EmployeeImageEntity;
import com.npc.old_school.exception.BusinessException;
import com.npc.old_school.mapper.EmployeeImageMapper;
import com.npc.old_school.mapper.EmployeeMapper;
import com.npc.old_school.service.EmployeeService;
import com.npc.old_school.util.FileUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.Collections;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private EmployeeImageMapper imageMapper;

    @Autowired
    private FileUtil fileUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Employee createEmployee(EmployeeDTO dto, MultipartFile image) {
        // 检查工号是否存在
        if (employeeMapper.exists(new LambdaQueryWrapper<Employee>()
                .eq(Employee::getUid, dto.getUid()))) {
            throw new BusinessException("工号已存在");
        }

        // 创建员工
        Employee employee = new Employee();
        BeanUtils.copyProperties(dto, employee);

        // 处理图片
        try {
            if (image != null && !image.isEmpty()) {
                String imagePath = fileUtil.saveFile(image, "images");
                employee.setImagePath(imagePath);
                employee.setSubAccount(true);
            }
        } catch (IOException e) {
            throw new BusinessException("文件处理失败: " + e.getMessage());
        }
        employeeMapper.insert(employee);
        return getEmployeeById(employee.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Employee updateEmployee(Long id, EmployeeDTO dto, MultipartFile image) {
        // 检查员工是否存在
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "员工不存在");
        }

        // 检查工号是否被其他员工使用
        if (!employee.getUid().equals(dto.getUid()) &&
                employeeMapper.exists(new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUid, dto.getUid()))) {
            throw new BusinessException("工号已存在");
        }

        // 更新基本信息
        BeanUtils.copyProperties(dto, employee);

        // 处理新图片
        try {
            if (image != null && !image.isEmpty()) {
                // 删除物理文件
                if (employee.getImagePath() != null) {
                    fileUtil.deleteFile(employee.getImagePath());
                }
                // 保存新图片
                String imagePath = fileUtil.saveFile(image, "images");
                employee.setImagePath(imagePath);
            }
        } catch (IOException e) {
            throw new BusinessException("文件处理失败: " + e.getMessage());
        }
        employeeMapper.updateById(employee);
        return getEmployeeById(id);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "员工不存在");
        }
        return employee;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployee(Long id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "员工不存在");
        }

        // 删除员工
        employeeMapper.deleteById(id);

        // 删除关联的图片记录
        imageMapper.delete(new LambdaQueryWrapper<EmployeeImageEntity>()
                .eq(EmployeeImageEntity::getEmployeeId, id));
    }
}