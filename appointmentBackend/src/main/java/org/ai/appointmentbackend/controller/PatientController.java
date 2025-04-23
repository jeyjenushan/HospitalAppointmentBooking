package org.ai.appointmentbackend.controller;
import org.ai.appointmentbackend.configuration.JwtTokenProvider;
import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.PatientEntity;
import org.ai.appointmentbackend.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final JwtTokenProvider jwtTokenProvider;

    public PatientController(PatientService patientService, JwtTokenProvider jwtTokenProvider) {
        this.patientService = patientService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //admin only
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<Response> getAllPatients(){
        Response response=patientService.getAllPatients();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    //admin only or patient them selves
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<Response> getPatientById(@PathVariable Long id){
        Response response=patientService.getPatientById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }


    //admin or patient
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<Response> updatePatient(@PathVariable Long id,
                                                  @RequestBody PatientEntity patientEntity){
        Response response=patientService.updatePatient(id,patientEntity);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    //Admin Only
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deletePatient(@PathVariable Long id){
        Response response=patientService.deletePatient(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }





    //Admin Only or person them selves
    //Not check Yet
    @GetMapping("/{id}/medical-records")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR') or (hasRole('PATIENT') and #id == principal.id)")
    public ResponseEntity<Response> fetchPatientMedicalRecords(@PathVariable Long id){
        Response response=patientService.fetchPatientMedicalHistory(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
