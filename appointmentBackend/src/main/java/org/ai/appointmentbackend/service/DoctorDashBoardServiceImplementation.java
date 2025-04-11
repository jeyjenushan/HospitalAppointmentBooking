package org.ai.appointmentbackend.service;

import org.ai.appointmentbackend.dto.DoctorDashboard;
import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.AppointmentEntity;
import org.ai.appointmentbackend.entity.DoctorEntity;
import org.ai.appointmentbackend.mapper.DtoConverter;
import org.ai.appointmentbackend.repository.AppointmentRepository;
import org.ai.appointmentbackend.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DoctorDashBoardServiceImplementation implements DoctoDashBoardService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public Response getDoctorDashBoard(String email) {

        Response response = new Response();
        DoctorEntity doctor=doctorRepository.findByUserEmail(email);
        List<AppointmentEntity> appointments = appointmentRepository.findByDoctorId(doctor.getId());

        Double earnings = 0.0;
        Set<Long> patients = new HashSet<>();

        for (AppointmentEntity appointment : appointments) {
            if (appointment.isCompleted() && appointment.isCompleted() || appointment.getAmount()!= null ) {
                earnings += appointment.getAmount();
            }
            patients.add(appointment.getPatient().getId());
        }

        appointments.sort((a, b) -> b.getId().compareTo(a.getId())); // Sort in reverse order

        DoctorDashboard dashboard = new DoctorDashboard();
        dashboard.setEarnings(earnings);
        dashboard.setAppointments((long) appointments.size());
        dashboard.setPatients((long) patients.size());
        dashboard.setLatestAppointments(DtoConverter.convertAppointmentEntityListToAppointmentDtoList(appointments));
        response.setStatusCode(200);
        response.setMessage("Success");
        response.setDoctorDashboard(dashboard);

        return response;
    }
}
