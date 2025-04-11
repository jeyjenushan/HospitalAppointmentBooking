package org.ai.appointmentbackend.controller;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;

import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.ai.appointmentbackend.configuration.JwtTokenProvider;
import org.ai.appointmentbackend.dto.AppointmentDto;
import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.AppointmentEntity;
import org.ai.appointmentbackend.entity.UserEntity;
import org.ai.appointmentbackend.enumpack.AppointmentStatus;
import org.ai.appointmentbackend.mapper.DtoConverter;
import org.ai.appointmentbackend.repository.AppointmentRepository;
import org.ai.appointmentbackend.request.AppointmentRequest;
import org.ai.appointmentbackend.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping ("/api")
public class AppointmentController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${currency}")
    private String currency;


    @Autowired
    private  AppointmentService appointmentService;
    @Autowired
    private  JwtTokenProvider jwtTokenProvider;
    @Autowired
    private AppointmentRepository appointmentRepository;





    @PostMapping("/pay")

    public ResponseEntity<Response> createStripeSession(@RequestBody Map<String, String> payload,
                                                        HttpServletRequest request) {

        Stripe.apiKey = stripeApiKey;


        Response response = new Response();
        try {
            String appointmentId = payload.get("appointmentId");

            String origin = request.getHeader("Origin");
            AppointmentEntity appointmentOpt = appointmentService.getAppointmentById(Long.valueOf(appointmentId));
            System.out.println(appointmentOpt);
            if (appointmentOpt == null) {
                response.setStatusCode(400);
                response.setMessage("Appointment not found");
                return ResponseEntity.badRequest().body(response);
            }


            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName("Appointment Fees")
                                                    .build())
                                    .setUnitAmount((long) (appointmentOpt.getAmount() * 100))
                                    .build())
                    .setQuantity(1L)
                    .build();


            SessionCreateParams params = SessionCreateParams.builder()
                    .addLineItem(lineItem)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(origin + "/verify?success=true&appointmentId=" + appointmentOpt.getId())
                    .setCancelUrl(origin + "/verify?success=false&appointmentId=" + appointmentOpt.getId())
                    .build();

            Session session = Session.create(params);


            // Setting response with relevant data
            response.setStatusCode(200);
            response.setMessage("Stripe session created successfully");
            response.setData(Map.of("session_url", session.getUrl()));

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }


    @PostMapping("/verify")
    public ResponseEntity<Response> verifyStripePayment(@RequestBody Map<String, String> payload) {
        Response response = new Response();
        try {

            String appointmentId = payload.get("appointmentId");
            System.out.println("The appointmentid is "+appointmentId);
            String success = payload.get("success");

            if (success.equals("true")) {
                AppointmentEntity appointment = appointmentService.getAppointmentById(Long.valueOf(appointmentId));

                if (appointment != null) {
                    appointment.setPayment(true);
                    appointment.setCompleted(false);
                    appointment.setCancelled(false);
                    appointment.setAppointmentStatus(AppointmentStatus.COMPLETED);
                    appointmentRepository.save(appointment); // Save the updated entity

                    response.setStatusCode(200);
                    response.setMessage("Payment Successful");

                    // If needed, include appointmentDto in the response
                    AppointmentDto appointmentDto = DtoConverter.convertAppointmentEntityToAppointmentDto(appointment);
                    response.setAppointmentDto(appointmentDto);

                } else {
                    response.setStatusCode(400);
                    response.setMessage("Appointment not found");
                }
            } else {
                response.setStatusCode(400);
                response.setMessage("Payment Failed");
            }

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return ResponseEntity.ok(response);
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

    // Cancel an appointment (Patient or Admin)
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
