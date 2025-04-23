package org.ai.appointmentbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ai.appointmentbackend.enumpack.Role;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {
    private String message;
    private int statusCode;
    private String token;
    private Role role;
    private String expirationTime;
    private AdminDto adminDto;
    private AppointmentDto appointmentDto;
    private DoctorDto doctorDto;
    private DoctorDashboard doctorDashboard;
    private PatientDto patientDto;
    private UserDto userDto;
    private List<PatientDto>patientDtos;
    private List<AdminDto>adminDtos;
    private List<DoctorDto>doctorDtos;
    private List<AppointmentDto>appointmentDtos;
    private DashboardData dashboardData;
    private Object data;


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


    public List<AdminDto> getAdminDtos() {
        return adminDtos;
    }

    public void setAdminDtos(List<AdminDto> adminDtos) {
        this.adminDtos = adminDtos;
    }

    public DoctorDashboard getDoctorDashboard() {
        return doctorDashboard;
    }

    public void setDoctorDashboard(DoctorDashboard doctorDashboard) {
        this.doctorDashboard = doctorDashboard;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public AdminDto getAdminDto() {
        return adminDto;
    }

    public void setAdminDto(AdminDto adminDto) {
        this.adminDto = adminDto;
    }

    public AppointmentDto getAppointmentDto() {
        return appointmentDto;
    }

    public void setAppointmentDto(AppointmentDto appointmentDto) {
        this.appointmentDto = appointmentDto;
    }

    public DoctorDto getDoctorDto() {
        return doctorDto;
    }

    public void setDoctorDto(DoctorDto doctorDto) {
        this.doctorDto = doctorDto;
    }

    public PatientDto getPatientDto() {
        return patientDto;
    }

    public void setPatientDto(PatientDto patientDto) {
        this.patientDto = patientDto;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }

    public List<PatientDto> getPatientDtos() {
        return patientDtos;
    }

    public void setPatientDtos(List<PatientDto> patientDtos) {
        this.patientDtos = patientDtos;
    }

    public List<DoctorDto> getDoctorDtos() {
        return doctorDtos;
    }

    public void setDoctorDtos(List<DoctorDto> doctorDtos) {
        this.doctorDtos = doctorDtos;
    }

    public List<AppointmentDto> getAppointmentDtos() {
        return appointmentDtos;
    }

    public void setAppointmentDtos(List<AppointmentDto> appointmentDtos) {
        this.appointmentDtos = appointmentDtos;
    }

    public DashboardData getDashboardData() {
        return dashboardData;
    }

    public void setDashboardData(DashboardData dashboardData) {
        this.dashboardData = dashboardData;
    }





}
