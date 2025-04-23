package org.ai.appointmentbackend.service;

import org.ai.appointmentbackend.dto.DashboardData;
import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.AdminEntity;
import org.ai.appointmentbackend.entity.DoctorEntity;
import org.ai.appointmentbackend.mapper.DtoConverter;
import org.ai.appointmentbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DashboardServiceImplementation implements DashboardService{
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;


    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private PatientRepository patientRepository;

    public Response getDashboardData() {
        Response response = new Response();
        try {





            DashboardData dashboardData = new DashboardData();
            dashboardData.setDoctorCount(doctorRepository.count());
            dashboardData.setPatientCount(patientRepository.count());
            dashboardData.setAdminCount(adminRepository.count());
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

    @Override
    public Response getAllAdmins() {
        Response response = new Response();
        try {
            List<AdminEntity> admins = adminRepository.findAll();

            if ( admins.isEmpty()) {
                response.setStatusCode(404);
                response.setMessage("No admins found in the system");
                response.setDoctorDtos(Collections.emptyList());
            } else {
                response.setAdminDtos(DtoConverter.convertAdminEntityListToAdminDtoList(admins));
                response.setStatusCode(200);
                response.setMessage("admins retrieved successfully");
            }

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Failed to retrieve admins. Please try again later.");
            response.setAdminDtos(Collections.emptyList());
        }
        return response;
    }

}
