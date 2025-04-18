package org.ai.appointmentbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "doctors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String specialization;
    private String contactNumber;
    private String availability="unavailable";
    private String experience;
    private String Degree;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "slots_booked",
            joinColumns = @JoinColumn(name = "doctor_id")
    )
    @MapKeyColumn(name = "slot_date") // Date key like "day_month_year"
    @Column(name = "slot_time") // Slot time, e.g., "10:00"
    private Map<String, Set<String>> slotsBooked;

    private Long fees;
    private String aboutDoctor;

    @Embedded
    private AddressEntity address1;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;



    @OneToMany(mappedBy = "doctor",cascade = CascadeType.ALL)
    private List<AppointmentEntity> appointments=new ArrayList<>();





    public Map<String, Set<String>> getSlotsBooked() {
        return slotsBooked;
    }

    public void setSlotsBooked(Map<String, Set<String>> slotsBooked) {
        this.slotsBooked = slotsBooked;
    }

    public AddressEntity getAddress1() {
        return address1;
    }


    public void setAddress1(AddressEntity address1) {
        this.address1 = address1;
    }


    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getDegree() {
        return Degree;
    }

    public void setDegree(String degree) {
        Degree = degree;
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




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<AppointmentEntity> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentEntity> appointments) {
        this.appointments = appointments;
    }
}
