package kz.trastinvest.demo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.trastinvest.demo.dto.request.EmployeeRequest;
import kz.trastinvest.demo.model.Employee;
import kz.trastinvest.demo.service.EmployeeService;
import kz.trastinvest.demo.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Employee> create(
            @RequestPart("data") String json,
            @RequestPart("photo") MultipartFile photo
    ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        EmployeeRequest request = mapper.readValue(json, EmployeeRequest.class);
        String photoUrl = fileStorageService.uploadFile(photo);
        request.setPhoto(photoUrl);
        return ResponseEntity.ok(employeeService.create(request));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Employee> update(
            @PathVariable Long id,
            @RequestPart("data") String json,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        EmployeeRequest request = mapper.readValue(json, EmployeeRequest.class);

        if (photo != null && !photo.isEmpty()) {
            String photoUrl = fileStorageService.uploadFile(photo);
            request.setPhoto(photoUrl);
        }

        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getAllEmployee")
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }
}
