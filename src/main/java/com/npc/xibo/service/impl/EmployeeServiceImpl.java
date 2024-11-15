package com.npc.xibo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.npc.xibo.dto.weixin.WeixinTokenResponse;
import com.npc.xibo.dto.weixin.WeixinUpdateResponse;
import com.npc.xibo.dto.weixin.WeixinUserListResponse;
import com.npc.xibo.dto.xibo.EmployeeDTO;
import com.npc.xibo.entity.Employee;
import com.npc.xibo.entity.EmployeeImageEntity;
import com.npc.xibo.exception.BusinessException;
import com.npc.xibo.mapper.EmployeeImageMapper;
import com.npc.xibo.mapper.EmployeeMapper;
import com.npc.xibo.service.EmployeeService;
import com.npc.xibo.util.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Paths;
import java.util.ArrayList;

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
    public List<Employee> createEmployee() {
        // 1. 获取token的URL
        String tokenUrl = UriComponentsBuilder
                .fromHttpUrl("https://qyapi.weixin.qq.com/cgi-bin/gettoken")
                .queryParam("corpid", "wwe5a361a0b6e7b8d9")
                .queryParam("corpsecret", "wGZJLDwBhECjI7slfxLrGU62OnKJi4d5el0hdJrcpTU")
                .build()
                .toUriString();

        // 2. 获取token
        WeixinTokenResponse tokenResponse = restTemplate.getForObject(tokenUrl, WeixinTokenResponse.class);
        if (tokenResponse == null || tokenResponse.getErrcode() != 0) {
            throw new BusinessException("获取token失败");
        }

        String getUserUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/list_id?access_token="
                + tokenResponse.getAccess_token();
        Map<String, Object> request = new HashMap<>();
        request.put("limit", 1000);
        WeixinUserListResponse response = restTemplate.postForObject(getUserUrl, request, WeixinUserListResponse.class);
        if (response == null || response.getDept_user() == null) {
            throw new BusinessException("获取用户列表失败");
        }

        List<Employee> createdEmployees = new ArrayList<>();
        for (WeixinUserListResponse.DeptUser deptUser : response.getDept_user()) {
            Employee employee = new Employee();
            employee.setUserid(deptUser.getUserid());
            employeeMapper.insert(employee);
            createdEmployees.add(employee);
        }
        return createdEmployees;
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
        if (!employee.getUserid().equals(dto.getUserid()) &&
                employeeMapper.exists(new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getUserid, dto.getUserid()))) {
            throw new BusinessException("工号已存在");
        }

        // 处理关联账号互换
        if (dto.getRelateAccountUid() != null) {
            Employee relatedEmployee = employeeMapper.selectOne(new LambdaQueryWrapper<Employee>()
                    .eq(Employee::getUserid, dto.getRelateAccountUid()));
            if (relatedEmployee != null) {
                relatedEmployee.setRelateAccountUid(employee.getUserid());
                employeeMapper.updateById(relatedEmployee);
            }
        }

        // 更新基本信息
        BeanUtils.copyProperties(dto, employee);
        String fileName;
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
                // 获取文件名和后缀
                fileName = Paths.get(imagePath).getFileName().toString();
                log.info("文件名: {}", fileName);
                String response = updateImageUrl(employee.getUserid(), fileName);
                if (response == null) {
                    throw new BusinessException("失败: " + response);
                }
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

    @Override
    public List<Employee> getAllEmployees() {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Employee::getId);
        return employeeMapper.selectList(wrapper);
    }

    @Autowired
    private RestTemplate restTemplate;

    private String updateImageUrl(String userId, String imageName) {
        // 1. 获取token的URL
        String tokenUrl = UriComponentsBuilder
                .fromHttpUrl("https://qyapi.weixin.qq.com/cgi-bin/gettoken")
                .queryParam("corpid", "wwe5a361a0b6e7b8d9")
                .queryParam("corpsecret", "wGZJLDwBhECjI7slfxLrGU62OnKJi4d5el0hdJrcpTU")
                .build()
                .toUriString();

        // 2. 获取token
        WeixinTokenResponse tokenResponse = restTemplate.getForObject(tokenUrl, WeixinTokenResponse.class);
        if (tokenResponse == null || tokenResponse.getErrcode() != 0) {
            throw new BusinessException("获取token失败");
        }

        // 3. 使用token更新图片
        String updateUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token="
                + tokenResponse.getAccess_token();

        Map<String, Object> request = new HashMap<>();
        request.put("userid", userId);

        Map<String, Object> externalAttr = new HashMap<>();
        externalAttr.put("type", 1);
        externalAttr.put("name", "加微异常");
        externalAttr.put("web", Map.of("url", "http://192.168.23.105/qrcode?id=" + imageName, "title", "点击这里联系我"));

        request.put("external_profile", Map.of(
                "external_attr", Arrays.asList(externalAttr)));

        WeixinUpdateResponse response = restTemplate.postForObject(updateUrl, request, WeixinUpdateResponse.class);

        if (response == null || response.getErrcode() != 0) {
            throw new BusinessException("更新失败: " + (response != null ? response.getErrmsg() : "未知错误"));
        }

        return response.getErrmsg();
    }

    @Override
    public String getRelateAccountUid(String userid) {
        log.info("userid: {}", userid);
        Employee employee = employeeMapper.selectOne(new LambdaQueryWrapper<Employee>()
                .eq(Employee::getUserid, userid));

        if (employee == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "未找到该员工信息！");
        }

        return employee.getRelateAccountUid();
    }
}