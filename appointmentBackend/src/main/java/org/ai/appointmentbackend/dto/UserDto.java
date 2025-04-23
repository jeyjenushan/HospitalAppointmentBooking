package org.ai.appointmentbackend.dto;


import org.ai.appointmentbackend.enumpack.Role;
public class UserDto {


private Long id;
private String name;
private String email;
private Long patientId;
private Long doctorId;
private Long adminId;
private String password;
private Role role;
private byte[] image;

    public UserDto() {
    }

    public UserDto(Long id, String name, Long patientId, String email, Long doctorId, Long adminId, String password, Role role, byte[] image) {
        this.id = id;
        this.name = name;
        this.patientId = patientId;
        this.email = email;
        this.doctorId = doctorId;
        this.adminId = adminId;
        this.password = password;
        this.role = role;
        this.image = image;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }




}
