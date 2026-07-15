package com.prueba.backend.controller;

import com.prueba.backend.dto.PatientDto;
import com.prueba.backend.service.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patients")
    public ResponseEntity<Page<PatientDto>> getPatients(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.DESC;
        String property = "id";

        if (sortParams.length > 0) {
            property = sortParams[0];
        }
        if (sortParams.length > 1 && "asc".equalsIgnoreCase(sortParams[1])) {
            direction = Sort.Direction.ASC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, property));
        return ResponseEntity.ok(patientService.getPatients(search, status, priority, pageable));
    }

    @GetMapping("/patients/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PostMapping("/patients")
    public ResponseEntity<?> createPatient(@RequestBody PatientDto dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(patientService.createPatient(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/patients/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable Long id, @RequestBody PatientDto dto) {
        try {
            return ResponseEntity.ok(patientService.updatePatient(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/patients/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Long>> getDashboardIndicators() {
        return ResponseEntity.ok(patientService.getDashboardIndicators());
    }
}
