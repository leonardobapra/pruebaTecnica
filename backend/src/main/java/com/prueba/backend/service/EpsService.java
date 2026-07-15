package com.prueba.backend.service;

import com.prueba.backend.dto.EpsDto;
import com.prueba.backend.model.Eps;
import com.prueba.backend.repository.EpsRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EpsService {

    private final EpsRepository epsRepository;

    public EpsService(EpsRepository epsRepository) {
        this.epsRepository = epsRepository;
    }

    public List<EpsDto> getAllEps() {
        return epsRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public EpsDto getEpsById(Long id) {
        Eps eps = epsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EPS no encontrada con ID: " + id));
        return convertToDto(eps);
    }

    public EpsDto createEps(EpsDto dto) {
        if (epsRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Ya existe una EPS con el código: " + dto.getCode());
        }
        Eps eps = new Eps(dto.getCode(), dto.getName());
        Eps saved = epsRepository.save(eps);
        return convertToDto(saved);
    }

    public EpsDto updateEps(Long id, EpsDto dto) {
        Eps eps = epsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EPS no encontrada con ID: " + id));
        
        if (!eps.getCode().equals(dto.getCode()) && epsRepository.existsByCode(dto.getCode())) {
            throw new RuntimeException("Ya existe otra EPS con el código: " + dto.getCode());
        }
        
        eps.setCode(dto.getCode());
        eps.setName(dto.getName());
        Eps updated = epsRepository.save(eps);
        return convertToDto(updated);
    }

    public void deleteEps(Long id) {
        if (!epsRepository.existsById(id)) {
            throw new RuntimeException("EPS no encontrada con ID: " + id);
        }
        epsRepository.deleteById(id);
    }

    public EpsDto convertToDto(Eps eps) {
        return new EpsDto(eps.getId(), eps.getCode(), eps.getName());
    }
}
