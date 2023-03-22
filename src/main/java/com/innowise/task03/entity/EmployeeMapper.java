package com.innowise.task03.entity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    EmployeeDTO employeeToEmployeeDTO(Employee employee);
    Employee employeeDTOToEmployee(EmployeeDTO employeeDTO);

    @Mapping(target = "id", source = "externalID")
    Employee employeeDTOWithExternalIDToEmployee(EmployeeDTO employeeDTO, Long externalID);

    List<Employee> employeeDTOListToEmployeeList(List<EmployeeDTO> employeeDTOList);

    List<EmployeeDTO> employeeListToEmployeeDTOList(List<Employee> employeeList);


}
