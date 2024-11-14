package com.npc.old_school.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.npc.old_school.dto.xibo.EmployeeDTO;
import com.npc.old_school.exception.ResultResponse;
import com.npc.old_school.service.EmployeeService;

import jakarta.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/relateAccount/{userid}")
    public ResultResponse getRelateAccountUid(@PathVariable String userid) {
        return ResultResponse.success(employeeService.getRelateAccountUid(userid));
    }

    @PostMapping
    public ResultResponse create() throws IOException {
        return ResultResponse.success(employeeService.createEmployee());
    }

    @GetMapping("/{id}")
    public ResultResponse getById(@PathVariable Long id) throws IOException {
        return ResultResponse.success(employeeService.getEmployeeById(id));
    }

    @PutMapping("/{id}")
    public ResultResponse update(@PathVariable Long id,
            @Valid @RequestPart("data") EmployeeDTO employeeDTO,
            @RequestPart(value = "images", required = false) MultipartFile images) throws IOException {
        return ResultResponse.success(employeeService.updateEmployee(id, employeeDTO,
                images));
    }

    @DeleteMapping("/{id}")
    public ResultResponse delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResultResponse.success();
    }

    @GetMapping
    public ResultResponse getAllEmployees() {
        return ResultResponse.success(employeeService.getAllEmployees());
    }

}