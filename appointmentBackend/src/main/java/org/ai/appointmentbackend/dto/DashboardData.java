package org.ai.appointmentbackend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardData{
    private long doctorCount;
    private long appointmentCount;
    private long patientCount;
    private List<AppointmentDto> latestAppointments;

    public long getDoctorCount() {
        return doctorCount;
    }

    public void setDoctorCount(long doctorCount) {
        this.doctorCount = doctorCount;
    }

    public long getAppointmentCount() {
        return appointmentCount;
    }

    public void setAppointmentCount(long appointmentCount) {
        this.appointmentCount = appointmentCount;
    }

    public long getPatientCount() {
        return patientCount;
    }

    public void setPatientCount(long patientCount) {
        this.patientCount = patientCount;
    }

    public List<AppointmentDto> getLatestAppointments() {
        return latestAppointments;
    }

    public void setLatestAppointments(List<AppointmentDto> latestAppointments) {
        this.latestAppointments = latestAppointments;
    }
}