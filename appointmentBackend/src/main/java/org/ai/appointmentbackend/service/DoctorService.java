package org.ai.appointmentbackend.service;

import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.DoctorEntity;

public interface DoctorService {

    Response fetchAllDoctors();
    Response fetchDoctorById(Long id);
    Response updateDoctor(String email, DoctorEntity doctorEntity);
    Response deleteDoctor(Long doctorId);
    Response getDoctorsBySpecialization(String specialization);
    Response checkDoctorAvailability(Long doctorId);
    Response changeDoctorAvailability(Long doctorId);

    Response getDoctorById(Long id);
}
