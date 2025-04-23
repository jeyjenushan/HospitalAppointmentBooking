package org.ai.appointmentbackend.service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import org.ai.appointmentbackend.dto.AppointmentDto;
import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.AppointmentEntity;
import org.ai.appointmentbackend.enumpack.AppointmentStatus;
import org.ai.appointmentbackend.mapper.DtoConverter;
import org.ai.appointmentbackend.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentServiceImplementation implements PaymentService{
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    private final AppointmentRepository appointmentRepository;

    private final AppointmentService appointmentService;

    public PaymentServiceImplementation(AppointmentRepository appointmentRepository, AppointmentService appointmentService) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
    }

    @Override
    public Response verifyStripePayment(Map<String, String> payload) {
        Response response = new Response();
        try {

            String appointmentId = payload.get("appointmentId");
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
        return response;
    }

    @Override
    public Response createStripeSession(Map<String, String> payload, HttpServletRequest request) {

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
                return response;
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
        return response;
    }
}
