package org.ai.appointmentbackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.ai.appointmentbackend.configuration.JwtTokenProvider;
import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.AppointmentEntity;
import org.ai.appointmentbackend.enumpack.AppointmentStatus;
import org.ai.appointmentbackend.repository.AppointmentRepository;
import org.ai.appointmentbackend.request.AppointmentRequest;
import org.ai.appointmentbackend.service.AppointmentService;
import org.ai.appointmentbackend.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping ("/api")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PaymentService paymentService;

    public AppointmentController(AppointmentService appointmentService, JwtTokenProvider jwtTokenProvider, AppointmentRepository appointmentRepository, PaymentService paymentService) {
        this.appointmentService = appointmentService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")

    public ResponseEntity<Response> createStripeSession(@RequestBody Map<String, String> payload,
                                                        HttpServletRequest request) {

       Response response=paymentService.createStripeSession(payload, request);
       return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @PostMapping("/verify")
    public ResponseEntity<Response> verifyStripePayment(@RequestBody Map<String, String> payload) {
        Response response = paymentService.verifyStripePayment(payload);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }






    // Book a new appointment (Patient only)
    @PostMapping("/appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Response> bookAppointment(@RequestBody AppointmentRequest appointment) {
        Response response = appointmentService.bookAppointment(appointment);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Reschedule an appointment (Patient or Admin)
    @PutMapping("/appointments/{id}/reschedule")
    public ResponseEntity<Response> rescheduleAppointment(
            @PathVariable Long id,
          @RequestBody AppointmentEntity appointmentDetails) {
        Response response = appointmentService.rescheduleAppointment(appointmentDetails, id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Cancel an appointment (Patient or Admin)
    @PostMapping("/appointments/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN','DOCTOR')")
    public ResponseEntity<Response> cancelAppointment(@PathVariable Long id) {
        Response response = appointmentService.cancelAppointment(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // complete an appointment (Patient or Admin)
    @PostMapping("/appointments/complete/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR')")
    public ResponseEntity<Response> completeAppointment(@PathVariable Long id) {
        Response response = appointmentService.completeAppointment(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Get appointments for a specific patient (Patient or Admin)
    @GetMapping("/patients/{id}/appointments")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<Response> getPatientAppointments(@PathVariable Long id) {
        Response response = appointmentService.getAppointmentsForPatient(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Endpoint to get appointments for a doctor
    @GetMapping("/doctor/appointments")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public ResponseEntity<Response> getDoctorAppointments(@RequestHeader("Authorization") String authHeader) {
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
            response = appointmentService.getAppointmentsForDoctor(email);
        } catch (Exception e) {
            response = new Response();
            response.setStatusCode(500);
            response.setMessage("We're having trouble retrieving your schedule. Please try again later.");
        }

        // Return the response
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Get appointments by status (Admin only)
    @GetMapping("/appointments/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> getAppointmentsByStatus(
            @RequestParam AppointmentStatus status) {
        Response response = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/appointments")
    @PreAuthorize("hasAnyRole( 'ADMIN')")
    public ResponseEntity<Response> getAllAppointments() {
        Response response = appointmentService.getAllAppointments();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

}
