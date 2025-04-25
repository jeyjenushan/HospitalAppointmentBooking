package org.ai.appointmentbackend.service;

import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.DoctorEntity;

import java.util.Map;
import java.util.Set;

public interface DoctorService {
    Response deleteDoctor(Long doctorId);
    Response fetchAllDoctors();
    Response updateDoctor(String email, DoctorEntity doctorEntity);
    Response getDoctorsBySpecialization(String specialization);
    Response getDoctorAppointments(Long doctorId);
    Response checkDoctorAvailability(Long doctorId);
    Response changeDoctorAvailability(Long doctorId);
    Response isAuthHeader(String authHeader);
    Response fetchDoctorById(Long doctorId);
    Response getDoctor(String email);
}
