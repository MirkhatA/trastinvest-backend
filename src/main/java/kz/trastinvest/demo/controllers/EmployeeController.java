package kz.trastinvest.demo.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Create employee", description = "Add a new employee with photo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee created")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Employee> create(
            @Parameter(
                    description = "Employee JSON, example:\n" +
                            "{\n" +
                            "  \"name\": \"John Doe\",\n" +
                            "  \"position\": \"Manager\",\n" +
                            "  \"number\": \"+1234567890\"\n" +
                            "}",
                    required = true)
            @RequestPart("data") String json,

            @Parameter(description = "Photo file", required = true)
            @RequestPart("photo") MultipartFile photo
    ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        EmployeeRequest request = mapper.readValue(json, EmployeeRequest.class);
        String photoUrl = fileStorageService.uploadFile(photo);
        request.setPhoto(photoUrl);
        return ResponseEntity.ok(employeeService.create(request));
    }

    @Operation(summary = "Update employee", description = "Update employee details and optionally replace photo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee updated")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Employee> update(
            @PathVariable Long id,

            @Parameter(
                    description = "Employee JSON, example:\n" +
                            "{\n" +
                            "  \"name\": \"Updated Name\",\n" +
                            "  \"position\": \"Updated Position\",\n" +
                            "  \"number\": \"+9876543210\"\n" +
                            "}",
                    required = true)
            @RequestPart("data") String json,

            @Parameter(description = "Optional new photo file")
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
