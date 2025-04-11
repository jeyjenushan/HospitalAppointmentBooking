package org.ai.appointmentbackend.controller;



import lombok.AllArgsConstructor;
import org.ai.appointmentbackend.configuration.JwtTokenProvider;
import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.DoctorEntity;
import org.ai.appointmentbackend.service.DashboardService;
import org.ai.appointmentbackend.service.DoctoDashBoardService;
import org.ai.appointmentbackend.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/doctors")
@AllArgsConstructor
public class DoctorController {

    @Autowired
    private  DoctorService doctorService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private DoctoDashBoardService doctoDashBoardService;



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

    @GetMapping("/patientappointments/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Response> getDoctorIdChoosePatientAppointments(@PathVariable Long id) {

        Response response = doctorService.getDoctorById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    // Endpoint to get appointments for a doctor
    @GetMapping("/doctor/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Response> getDoctorAppointments(@PathVariable Long id) {
        Response response=doctorService.getDoctorById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }


    // Doctor and Admin accessible endpoints
    @PutMapping("/doctor/update-profile")
    public ResponseEntity<Response> updateDoctor(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DoctorEntity updatedDoctor) {
        Response response;

        try {
            System.out.println("Jenushan....");
            // Step 1: Extract the token from the Authorization header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response = new Response();
                response.setStatusCode(400);
                response.setMessage("Token not provided or incorrect format");
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }
            String token = authHeader.substring(7); // Remove the "Bearer " prefix

            // Step 2: Extract the doctor ID from the token
            String email = jwtTokenProvider.extractUsername(token);


             response = doctorService.updateDoctor(email, updatedDoctor);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }catch (Exception e) {
        response = new Response();
        response.setStatusCode(500);
        response.setMessage(e.getMessage());
        return ResponseEntity.status(response.getStatusCode()).body(response);}
    }




    // Doctor  accessible endpoints
    @GetMapping("/doctor/dashboard")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Response> getDashBoard(
            @RequestHeader("Authorization") String authHeader
        ) {
        Response response;

        try {
            // Step 1: Extract the token from the Authorization header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response = new Response();
                response.setStatusCode(400);
                response.setMessage("Token not provided or incorrect format");
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }
            String token = authHeader.substring(7); // Remove the "Bearer " prefix

            // Step 2: Extract the doctor ID from the token
            String email = jwtTokenProvider.extractUsername(token);




            // Step 4: Call the service to fetch the appointments for the doctor
            response = doctoDashBoardService.getDoctorDashBoard(email);
        } catch (Exception e) {
            response = new Response();
            response.setStatusCode(500);
            response.setMessage("We're having trouble retrieving your schedule. Please try again later.");
        }

        // Return the response
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


}
