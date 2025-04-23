package org.ai.appointmentbackend.dto;


import org.ai.appointmentbackend.entity.AddressEntity;

import java.util.Map;
import java.util.Set;


public class DoctorDto {
    private Long id;
    private String specialization;
    private String contactNumber;
    private String availability;
    private String experience;
    private String degree;
    private Long fees;
    private String aboutDoctor;
    private UserDto user;
    private Map<String, Set<String>> slots_booked;

    public DoctorDto() {
    }

    public DoctorDto(Long id, String specialization, String contactNumber, String availability, String experience, String degree, Long fees, String aboutDoctor, UserDto user, Map<String, Set<String>> slots_booked) {
        this.id = id;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.availability = availability;
        this.experience = experience;
        this.degree = degree;
        this.fees = fees;
        this.aboutDoctor = aboutDoctor;
        this.user = user;
        this.slots_booked = slots_booked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public Long getFees() {
        return fees;
    }

    public void setFees(Long fees) {
        this.fees = fees;
    }

    public String getAboutDoctor() {
        return aboutDoctor;
    }

    public void setAboutDoctor(String aboutDoctor) {
        this.aboutDoctor = aboutDoctor;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public Map<String, Set<String>> getSlots_booked() {
        return slots_booked;
    }

    public void setSlots_booked(Map<String, Set<String>> slots_booked) {
        this.slots_booked = slots_booked;
    }
}
