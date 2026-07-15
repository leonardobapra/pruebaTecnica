package com.prueba.backend.controller;

import com.prueba.backend.dto.EpsDto;
import com.prueba.backend.service.EpsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eps")
@CrossOrigin(origins = "*")
public class EpsController {

    private final EpsService epsService;

    public EpsController(EpsService epsService) {
        this.epsService = epsService;
    }

    @GetMapping
    public ResponseEntity<List<EpsDto>> getAllEps() {
        return ResponseEntity.ok(epsService.getAllEps());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EpsDto> getEpsById(@PathVariable Long id) {
        return ResponseEntity.ok(epsService.getEpsById(id));
    }

    @PostMapping
    public ResponseEntity<EpsDto> createEps(@RequestBody EpsDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(epsService.createEps(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EpsDto> updateEps(@PathVariable Long id, @RequestBody EpsDto dto) {
        return ResponseEntity.ok(epsService.updateEps(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEps(@PathVariable Long id) {
        epsService.deleteEps(id);
        return ResponseEntity.noContent().build();
    }
}
