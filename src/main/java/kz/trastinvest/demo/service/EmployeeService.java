package kz.trastinvest.demo.service;

import kz.trastinvest.demo.dto.request.EmployeeRequest;
import kz.trastinvest.demo.model.Employee;
import kz.trastinvest.demo.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Employee create(EmployeeRequest request) {
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setPosition(request.getPosition());
        employee.setNumber(request.getNumber());
        employee.setPhoto(request.getPhoto());
        return employeeRepository.save(employee);
    }

    public Employee update(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setName(request.getName());
        employee.setPosition(request.getPosition());
        employee.setNumber(request.getNumber());

        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            employee.setPhoto(request.getPhoto());
        }

        return employeeRepository.save(employee);
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }

    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }
}
