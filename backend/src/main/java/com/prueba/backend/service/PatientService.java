package com.prueba.backend.service;

import com.prueba.backend.dto.PatientDto;
import com.prueba.backend.model.Eps;
import com.prueba.backend.model.Patient;
import com.prueba.backend.repository.EpsRepository;
import com.prueba.backend.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final EpsRepository epsRepository;

    public PatientService(PatientRepository patientRepository, EpsRepository epsRepository) {
        this.patientRepository = patientRepository;
        this.epsRepository = epsRepository;
    }

    @Transactional(readOnly = true)
    public Page<PatientDto> getPatients(String search, String status, String priority, Pageable pageable) {
        return patientRepository.findByFilters(search, status, priority, pageable)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public PatientDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));
        return convertToDto(patient);
    }

    @Transactional
    public PatientDto createPatient(PatientDto dto) {
        validatePatientDto(dto, null);

        Eps eps = epsRepository.findByCode(dto.getEpsCode())
                .orElseThrow(() -> new RuntimeException("La EPS especificada no existe: " + dto.getEpsCode()));

        Patient patient = new Patient();
        copyDtoToEntity(dto, patient, eps);

        Patient saved = patientRepository.save(patient);
        return convertToDto(saved);
    }

    @Transactional
    public PatientDto updatePatient(Long id, PatientDto dto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con ID: " + id));

        validatePatientDto(dto, id);

        Eps eps = epsRepository.findByCode(dto.getEpsCode())
                .orElseThrow(() -> new RuntimeException("La EPS especificada no existe: " + dto.getEpsCode()));

        copyDtoToEntity(dto, patient, eps);

        Patient updated = patientRepository.save(patient);
        return convertToDto(updated);
    }

    @Transactional
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("Paciente no encontrado con ID: " + id);
        }
        patientRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getDashboardIndicators() {
        Map<String, Long> indicators = new HashMap<>();
        indicators.put("total", patientRepository.count());
        indicators.put("pendientes", patientRepository.countByStatus("Pendiente"));
        indicators.put("enAtencion", patientRepository.countByStatus("En atención"));
        indicators.put("atendidos", patientRepository.countByStatus("Atendido"));
        indicators.put("prioridadAlta", patientRepository.countByPriority("Alta"));
        return indicators;
    }

    private void validatePatientDto(PatientDto dto, Long currentId) {
        if (dto.getFullName() == null || dto.getFullName().trim().isEmpty()) {
            throw new RuntimeException("El nombre completo es obligatorio.");
        }
        if (dto.getDocumentNumber() == null || dto.getDocumentNumber().trim().isEmpty()) {
            throw new RuntimeException("El número de documento es obligatorio.");
        }
        if (dto.getBirthDate() == null) {
            throw new RuntimeException("La fecha de nacimiento es obligatoria.");
        }
        if (dto.getBirthDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("La fecha de nacimiento no puede ser futura.");
        }
        if (dto.getEpsCode() == null || dto.getEpsCode().trim().isEmpty()) {
            throw new RuntimeException("La EPS es obligatoria.");
        }
        if (dto.getPriority() == null || dto.getPriority().trim().isEmpty()) {
            throw new RuntimeException("La prioridad es obligatoria.");
        }
        if (dto.getStatus() == null || dto.getStatus().trim().isEmpty()) {
            throw new RuntimeException("El estado es obligatorio.");
        }

        patientRepository.findByDocumentNumber(dto.getDocumentNumber()).ifPresent(existing -> {
            if (currentId == null || !existing.getId().equals(currentId)) {
                throw new RuntimeException("Ya existe un paciente con el número de documento: " + dto.getDocumentNumber());
            }
        });
    }

    private void copyDtoToEntity(PatientDto dto, Patient patient, Eps eps) {
        patient.setDocumentType(dto.getDocumentType());
        patient.setDocumentNumber(dto.getDocumentNumber());
        patient.setFullName(dto.getFullName());
        patient.setBirthDate(dto.getBirthDate());
        patient.setGender(dto.getGender());
        patient.setPhone(dto.getPhone());
        patient.setEmail(dto.getEmail());
        patient.setEps(eps);
        patient.setCity(dto.getCity());
        patient.setPriority(dto.getPriority());
        patient.setStatus(dto.getStatus());
        
        if (dto.getCreatedAt() != null) {
            patient.setCreatedAt(dto.getCreatedAt());
        }
        if (dto.getUpdatedAt() != null) {
            patient.setUpdatedAt(dto.getUpdatedAt());
        }
    }

    public PatientDto convertToDto(Patient patient) {
        PatientDto dto = new PatientDto();
        dto.setId(patient.getId());
        dto.setDocumentType(patient.getDocumentType());
        dto.setDocumentNumber(patient.getDocumentNumber());
        dto.setFullName(patient.getFullName());
        dto.setBirthDate(patient.getBirthDate());
        dto.setGender(patient.getGender());
        dto.setPhone(patient.getPhone());
        dto.setEmail(patient.getEmail());
        dto.setEpsCode(patient.getEps().getCode());
        dto.setEpsName(patient.getEps().getName());
        dto.setCity(patient.getCity());
        dto.setPriority(patient.getPriority());
        dto.setStatus(patient.getStatus());
        dto.setCreatedAt(patient.getCreatedAt());
        dto.setUpdatedAt(patient.getUpdatedAt());
        return dto;
    }
}
