package org.ai.appointmentbackend.service;

import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.AppointmentEntity;
import org.ai.appointmentbackend.enumpack.AppointmentStatus;
import org.ai.appointmentbackend.request.AppointmentRequest;

public interface AppointmentService {

    Response bookAppointment(AppointmentRequest appointment);
    Response cancelAppointment(Long appointmentId);
    Response rescheduleAppointment(AppointmentEntity appointment,Long appointmentId);
    Response getAppointmentsForPatient(Long patientId);
    Response getAppointmentsForDoctor(String email);
    Response getAppointmentsByStatus(AppointmentStatus status);
    Response getAllAppointments();
     AppointmentEntity getAppointmentById(Long id);
    Response completeAppointment(Long id);
}
