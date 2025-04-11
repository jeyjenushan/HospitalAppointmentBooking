package org.ai.appointmentbackend.repository;

import org.ai.appointmentbackend.entity.AppointmentEntity;
import org.ai.appointmentbackend.enumpack.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity,Long> {

    List<AppointmentEntity> findByPatientId(Long patientId);
    List<AppointmentEntity> findByDoctorId(Long doctorId);
    List<AppointmentEntity> findByAppointmentStatus(AppointmentStatus appointmentStatus);

    List<AppointmentEntity> findTop10ByOrderByDateDesc();
}
