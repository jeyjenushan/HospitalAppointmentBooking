package org.ai.appointmentbackend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ai.appointmentbackend.entity.AppointmentEntity;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDashboard {

    private Double earnings;
    private Long appointments;
    private Long patients;
    private List<AppointmentDto> latestAppointments;

    // Getters and Setters
    public Double getEarnings() {
        return earnings;
    }

    public void setEarnings(Double earnings) {
        this.earnings = earnings;
    }

    public Long getAppointments() {
        return appointments;
    }

    public void setAppointments(Long appointments) {
        this.appointments = appointments;
    }

    public Long getPatients() {
        return patients;
    }

    public void setPatients(Long patients) {
        this.patients = patients;
    }

    public List<AppointmentDto> getLatestAppointments() {
        return latestAppointments;
    }

    public void setLatestAppointments(List<AppointmentDto> latestAppointments) {
        this.latestAppointments = latestAppointments;
    }
}
