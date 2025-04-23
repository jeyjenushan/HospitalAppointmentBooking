package org.ai.appointmentbackend.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.ai.appointmentbackend.configuration.JwtTokenProvider;
import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.DoctorEntity;
import org.ai.appointmentbackend.service.DoctoDashBoardService;
import org.ai.appointmentbackend.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping("/api/doctors")

public class DoctorController {

    private final DoctorService doctorService;
    private final JwtTokenProvider jwtTokenProvider;
    private final DoctoDashBoardService doctoDashBoardService;

    public DoctorController(DoctorService doctorService, JwtTokenProvider jwtTokenProvider, DoctoDashBoardService doctoDashBoardService) {
        this.doctorService = doctorService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.doctoDashBoardService = doctoDashBoardService;
    }

    //admin only access
    @DeleteMapping("/admin/doctors/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteDoctor(@PathVariable Long id) {
        Response response = doctorService.deleteDoctor(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    // Public endpoints
    @GetMapping
    public ResponseEntity<Response> getAllDoctors() {
        Response response = doctorService.fetchAllDoctors();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping(params = "specialization")
    public ResponseEntity<Response> getDoctorsBySpecialization(
            @RequestParam String specialization) {
        Response response = doctorService.getDoctorsBySpecialization(specialization);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Doctor and Admin accessible endpoints
    @PutMapping("/doctor/update-profile")
    public ResponseEntity<Response> updateDoctor(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DoctorEntity updatedDoctor) {

        Response response1 = doctorService.isAuthHeader(authHeader);
        String email = jwtTokenProvider.extractUsername(response1.getToken());
        Response response = doctorService.updateDoctor(email, updatedDoctor);
        return ResponseEntity.status(response.getStatusCode()).body(response);


    }





    @GetMapping("/{id}/availability")
    public ResponseEntity<Response> checkDoctorAvailability(
            @PathVariable Long id) {
        Response response = doctorService.checkDoctorAvailability(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @PutMapping("/{doctorId}/availability")
    public ResponseEntity<Response> changeDoctorAvailability(
            @PathVariable Long doctorId) {
        Response response = doctorService.changeDoctorAvailability(doctorId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @GetMapping("/getDoctor/{doctorId}")
    public ResponseEntity<Response> getDoctor(
            @PathVariable Long doctorId) {
        Response response = doctorService.fetchDoctorById(doctorId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }





        // Doctor  accessible endpoints
    @GetMapping("/doctor/dashboard")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Response> getDashBoard(
            @RequestHeader("Authorization") String authHeader
        ) {
        Response response1 = doctorService.isAuthHeader(authHeader);
        String email = jwtTokenProvider.extractUsername(response1.getToken());
        Response response = doctoDashBoardService.getDoctorDashBoard(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


}
