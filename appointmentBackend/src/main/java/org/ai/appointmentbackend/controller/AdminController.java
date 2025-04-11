package org.ai.appointmentbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.AdminEntity;
import org.ai.appointmentbackend.entity.DoctorEntity;
import org.ai.appointmentbackend.service.AuthService;
import org.ai.appointmentbackend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AuthService authService;
    @Autowired
    private DashboardService dashboardService;

    @PostMapping(value = "/register/doctor", consumes = {"multipart/form-data"})
    public ResponseEntity<Response> doctorRegister(@RequestPart("doctor") String doctorString,   @RequestPart(value = "image", required = false) MultipartFile imageFile) throws Exception {
        System.out.println("jdjdjdjdjdjdjjd");
        ObjectMapper objectMapper=new ObjectMapper();
        DoctorEntity doctorEntity=null;
        doctorEntity=objectMapper.readValue(doctorString,DoctorEntity.class);
        Response response = authService.RegisterDoctor(doctorEntity,imageFile);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    @PostMapping(value = "/register/admin", consumes = {"multipart/form-data"})
    public ResponseEntity<Response> adminRegister(  @RequestPart("admin") String adminString,  @RequestPart(value = "image", required = false) MultipartFile imageFile) throws Exception {
        ObjectMapper objectMapper=new ObjectMapper();
        AdminEntity adminEntity=null;
        adminEntity=objectMapper.readValue(adminString,AdminEntity.class);
        Response response = authService.RegisterAdmin(adminEntity,imageFile);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }



}
