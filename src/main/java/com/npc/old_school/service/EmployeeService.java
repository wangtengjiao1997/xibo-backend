package com.npc.old_school.service;

import com.npc.old_school.dto.xibo.EmployeeDTO;
import com.npc.old_school.entity.Employee;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface EmployeeService {
    List<Employee> createEmployee() throws IOException;

    Employee getEmployeeById(Long id) throws IOException;

    List<Employee> getAllEmployees();

    Employee updateEmployee(Long id, EmployeeDTO dto, MultipartFile image) throws IOException;

    void deleteEmployee(Long id);

    String getRelateAccountUid(String userid);

}