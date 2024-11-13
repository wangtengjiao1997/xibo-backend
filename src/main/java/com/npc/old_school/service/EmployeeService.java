package com.npc.old_school.service;

import com.npc.old_school.dto.xibo.EmployeeDTO;
import com.npc.old_school.entity.Employee;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface EmployeeService {
    Employee createEmployee(EmployeeDTO dto, MultipartFile image) throws IOException;

    Employee getEmployeeById(Long id) throws IOException;

    Employee updateEmployee(Long id, EmployeeDTO dto, MultipartFile image) throws IOException;

    void deleteEmployee(Long id);

}