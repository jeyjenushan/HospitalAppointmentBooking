package org.ai.appointmentbackend.service;

import org.ai.appointmentbackend.dto.DashboardData;
import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.DoctorEntity;
import org.ai.appointmentbackend.mapper.DtoConverter;
import org.ai.appointmentbackend.repository.AppointmentRepository;
import org.ai.appointmentbackend.repository.DoctorRepository;
import org.ai.appointmentbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImplementation implements DashboardService{
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private AppointmentRepository appointmentRepository;

    public Response getDashboardData() {
        Response response = new Response();
        try {





            DashboardData dashboardData = new DashboardData();
            dashboardData.setDoctorCount(doctorRepository.count());
            dashboardData.setPatientCount(userRepository.count());
            dashboardData.setAppointmentCount(appointmentRepository.count());
            dashboardData.setLatestAppointments(DtoConverter.convertAppointmentEntityListToAppointmentDtoList(appointmentRepository.findTop10ByOrderByDateDesc()));
            response.setStatusCode(200);
            response.setMessage("Dashboard data retrieved successfully");
            response.setDashboardData(dashboardData);

            return response;
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving dashboard data: " + e.getMessage());
        }
        return response;
    }

}
