package com.prueba.backend.config;

import com.prueba.backend.model.Eps;
import com.prueba.backend.model.Patient;
import com.prueba.backend.model.User;
import com.prueba.backend.repository.EpsRepository;
import com.prueba.backend.repository.PatientRepository;
import com.prueba.backend.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Component
public class ExcelDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ExcelDataLoader.class);

    private final EpsRepository epsRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;

    public ExcelDataLoader(EpsRepository epsRepository, UserRepository userRepository, PatientRepository patientRepository) {
        this.epsRepository = epsRepository;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("La base de datos ya contiene información. Omitiendo la carga del Excel.");
            return;
        }

        // Search for the excel file in multiple paths
        File excelFile = findExcelFile();
        if (excelFile == null || !excelFile.exists()) {
            log.warn("No se encontró el archivo Excel de datos sintéticos. Omitiendo inicialización automática.");
            return;
        }

        log.info("Cargando datos sintéticos desde: {}", excelFile.getAbsolutePath());

        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            DataFormatter formatter = new DataFormatter();

            // 1. Load EPS Catalogs
            log.info("Cargando catálogo de EPS...");
            Sheet catalogsSheet = workbook.getSheet("Catalogos");
            Map<String, Eps> epsMap = new HashMap<>();
            
            if (catalogsSheet != null) {
                // Row 0 is header: eps_codigo, eps_nombre, Unnamed: 2...
                for (int i = 1; i <= catalogsSheet.getLastRowNum(); i++) {
                    Row row = catalogsSheet.getRow(i);
                    if (row == null) continue;
                    
                    Cell codeCell = row.getCell(0);
                    Cell nameCell = row.getCell(1);
                    
                    if (codeCell != null && nameCell != null) {
                        String code = formatter.formatCellValue(codeCell).trim();
                        String name = formatter.formatCellValue(nameCell).trim();
                        
                        if (!code.isEmpty() && !name.isEmpty()) {
                            Eps eps = new Eps(code, name);
                            epsRepository.save(eps);
                            epsMap.put(code, eps);
                        }
                    }
                }
            }
            log.info("Se cargaron {} EPS.", epsMap.size());

            // 2. Load Demo Users
            log.info("Cargando usuarios de demostración...");
            Sheet usersSheet = workbook.getSheet("Usuarios_Login");
            int usersCount = 0;
            if (usersSheet != null) {
                for (int i = 1; i <= usersSheet.getLastRowNum(); i++) {
                    Row row = usersSheet.getRow(i);
                    if (row == null) continue;
                    
                    String username = getCellValueAsString(row.getCell(1), formatter);
                    String name = getCellValueAsString(row.getCell(2), formatter);
                    String role = getCellValueAsString(row.getCell(3), formatter);
                    String activeStr = getCellValueAsString(row.getCell(4), formatter);
                    String password = getCellValueAsString(row.getCell(5), formatter);
                    if (username != null && !username.isEmpty()) {
                        boolean active = "true".equalsIgnoreCase(activeStr) || "1".equals(activeStr);
                        String email = username + "@goecosystem.com";
                        User user = new User(username, password, name, email, role, active);
                        userRepository.save(user);
                        usersCount++;
                    }
                }
            }
            log.info("Se cargaron {} usuarios de demostración.", usersCount);

            // 3. Load Patients
            log.info("Cargando pacientes sintéticos (1,000 registros)...");
            Sheet patientsSheet = workbook.getSheet("Pacientes");
            List<Patient> patientsToSave = new ArrayList<>();
            
            if (patientsSheet != null) {
                for (int i = 1; i <= patientsSheet.getLastRowNum(); i++) {
                    Row row = patientsSheet.getRow(i);
                    if (row == null) continue;
                    
                    String typeDoc = getCellValueAsString(row.getCell(1), formatter);
                    String document = getCellValueAsString(row.getCell(2), formatter);
                    String fullName = getCellValueAsString(row.getCell(3), formatter);
                    LocalDate birthDate = getCellValueAsLocalDate(row.getCell(4));
                    String gender = getCellValueAsString(row.getCell(5), formatter);
                    String phone = getCellValueAsString(row.getCell(6), formatter);
                    String email = getCellValueAsString(row.getCell(7), formatter);
                    String epsCode = getCellValueAsString(row.getCell(8), formatter);
                    String city = getCellValueAsString(row.getCell(10), formatter);
                    String priority = getCellValueAsString(row.getCell(11), formatter);
                    String status = getCellValueAsString(row.getCell(12), formatter);
                    LocalDateTime createdAt = getCellValueAsLocalDateTime(row.getCell(13));
                    LocalDateTime updatedAt = getCellValueAsLocalDateTime(row.getCell(14));
                    
                    if (document != null && !document.isEmpty() && fullName != null && !fullName.isEmpty()) {
                        Patient patient = new Patient();
                        patient.setDocumentType(typeDoc);
                        patient.setDocumentNumber(document);
                        patient.setFullName(fullName);
                        patient.setBirthDate(birthDate);
                        patient.setGender(gender);
                        patient.setPhone(phone);
                        patient.setEmail(email);
                        patient.setCity(city);
                        patient.setPriority(priority);
                        patient.setStatus(status);
                        patient.setCreatedAt(createdAt != null ? createdAt : LocalDateTime.now());
                        patient.setUpdatedAt(updatedAt != null ? updatedAt : LocalDateTime.now());
                        
                        Eps eps = epsMap.get(epsCode);
                        if (eps == null) {
                            // fallback search in DB
                            eps = epsRepository.findByCode(epsCode).orElse(null);
                        }
                        
                        if (eps != null) {
                            patient.setEps(eps);
                            patientsToSave.add(patient);
                        } else {
                            log.warn("Paciente {} omitido: EPS Código '{}' no existe en catálogo.", fullName, epsCode);
                        }
                    }
                }
                
                if (!patientsToSave.isEmpty()) {
                    patientRepository.saveAll(patientsToSave);
                }
            }
            log.info("Se cargaron {} pacientes exitosamente en la base de datos.", patientsToSave.size());
        }
    }

    private File findExcelFile() {
        // Try multiple paths to find the file
        String[] paths = {
            "../Datos_Sinteticos_Prueba_Full_Stack_Junior_2026.xlsx",
            "Datos_Sinteticos_Prueba_Full_Stack_Junior_2026.xlsx",
            "C:/Users/leona/OneDrive/Desktop/prueba/Datos_Sinteticos_Prueba_Full_Stack_Junior_2026.xlsx"
        };
        for (String path : paths) {
            File f = new File(path);
            if (f.exists()) {
                return f;
            }
        }
        return null;
    }

    private String getCellValueAsString(Cell cell, DataFormatter formatter) {
        if (cell == null) return null;
        String val = formatter.formatCellValue(cell).trim();
        return val.isEmpty() ? null : val;
    }

    private LocalDate getCellValueAsLocalDate(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        try {
            // fallback string parse
            String str = cell.getStringCellValue();
            return LocalDate.parse(str);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime getCellValueAsLocalDateTime(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue();
        }
        try {
            // fallback string parse
            String str = cell.getStringCellValue();
            return LocalDateTime.parse(str);
        } catch (Exception e) {
            return null;
        }
    }
}
