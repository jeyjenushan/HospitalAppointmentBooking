package org.ai.appointmentbackend.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.ai.appointmentbackend.dto.AppointmentDto;
import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.AppointmentEntity;
import org.ai.appointmentbackend.entity.DoctorEntity;
import org.ai.appointmentbackend.entity.PatientEntity;
import org.ai.appointmentbackend.entity.UserEntity;
import org.ai.appointmentbackend.enumpack.AppointmentStatus;
import org.ai.appointmentbackend.mapper.DtoConverter;
import org.ai.appointmentbackend.repository.AppointmentRepository;
import org.ai.appointmentbackend.repository.DoctorRepository;
import org.ai.appointmentbackend.repository.PatientRepository;
import org.ai.appointmentbackend.repository.UserRepository;
import org.ai.appointmentbackend.request.AppointmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service

public class AppointmentServiceImplementation implements AppointmentService{
    @Autowired
    private  AppointmentRepository appointmentRepository;
    @Autowired
    private  PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private  UserRepository userRepository;


    @Override
    public Response bookAppointment(AppointmentRequest appointmentRequest) {
        Response response = new Response();

        try {
            // Validate required fields
            if (appointmentRequest.getPatientId() == null || appointmentRequest.getDoctorId() == null) {
                response.setStatusCode(400);
                response.setMessage("Please select both a patient and doctor for the appointment");
                return response;
            }

            if (appointmentRequest.getDate() == null || appointmentRequest.getTime() == null) {
                response.setStatusCode(400);
                response.setMessage("Please select both appointment date and time");
                return response;
            }

            // Fetch doctor and patient entities
            PatientEntity patient = patientRepository.findById(appointmentRequest.getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            DoctorEntity doctor = doctorRepository.findById(appointmentRequest.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));

            String slotDate = String.valueOf(appointmentRequest.getDate());  // format: "day_month_year"
            String slotTime = String.valueOf(appointmentRequest.getTime());

            // Check if the slot is already booked
            Map<String, Set<String>> doctorSlots = doctor.getSlotsBooked();
            Set<String> bookedSlots = doctorSlots.get(slotDate);

            if (bookedSlots != null && bookedSlots.contains(slotTime)) {
                response.setStatusCode(400);
                response.setMessage("This time slot is already booked.");
                return response;
            }



            AppointmentEntity appointmentEntity = new AppointmentEntity();
            appointmentEntity.setPatient(patient);
            appointmentEntity.setDoctor(doctor);
            appointmentEntity.setDate(appointmentRequest.getDate());
            appointmentEntity.setTime(appointmentRequest.getTime());
            appointmentEntity.setAppointmentStatus(AppointmentStatus.SCHEDULED);
            appointmentEntity.setAmount(doctor.getFees());
            //appointmentEntity.setPayment(appointmentRequest.isPayment());

            AppointmentEntity savedAppointment = appointmentRepository.save(appointmentEntity);
            AppointmentDto appointmentDto = DtoConverter.convertAppointmentEntityToAppointmentDto(savedAppointment);


            // Remove the booked slot from the doctor's available slots
            if (bookedSlots != null) {
                bookedSlots.add(slotTime);  // mark slot as booked
            } else {
                bookedSlots = new HashSet<>();
                bookedSlots.add(slotTime);
            }

            doctorSlots.put(slotDate, bookedSlots);
            doctor.setSlotsBooked(doctorSlots);
            doctorRepository.save(doctor);  // Save the updated doctor entity







            // Format confirmation message
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            String formattedDate = savedAppointment.getDate().format(dateFormatter);
            String formattedTime = savedAppointment.getTime().format(timeFormatter);


            response.setStatusCode(200);
            response.setMessage("Your appointment has been successfully booked for " +
                    formattedDate + " at " + formattedTime);
            response.setAppointmentDto(appointmentDto);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            //response.setMessage("We couldn't process your appointment booking. Please try again later.");

        }
        return response;
    }



    @Override
    public Response cancelAppointment(Long appointmentId) {
        Response response = new Response();

        try {
            Optional<AppointmentEntity> optionalAppointment = appointmentRepository.findById(appointmentId);

            if (optionalAppointment.isPresent()) {
                AppointmentEntity appointment = optionalAppointment.get();

                // Check if already cancelled
                if (appointment.getAppointmentStatus() == AppointmentStatus.CANCELLED) {
                    response.setStatusCode(400);
                    response.setMessage("This appointment was already cancelled");
                    return response;
                }

                appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
                appointmentRepository.save(appointment);

                response.setStatusCode(200);
                response.setMessage("Your appointment has been successfully cancelled");
                response.setAppointmentDto(DtoConverter.convertAppointmentEntityToAppointmentDto(appointment));
            } else {
                response.setStatusCode(404);
                response.setMessage("We couldn't find the appointment you're trying to cancel");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("We couldn't process your cancellation request. Please try again later.");
        }
        return response;
    }




    @Override
    public Response rescheduleAppointment(AppointmentEntity appointment, Long appointmentId) {
        Response response = new Response();

        try {
            if (appointment.getTime() == null) {
                response.setStatusCode(400);
                response.setMessage("Please select a new time for your appointment");
                return response;
            }

            Optional<AppointmentEntity> optionalAppointment = appointmentRepository.findById(appointmentId);

            if (optionalAppointment.isPresent()) {
                AppointmentEntity existingAppointment = optionalAppointment.get();

                // Check if trying to reschedule a cancelled appointment
                if (existingAppointment.getAppointmentStatus() == AppointmentStatus.CANCELLED) {
                    response.setStatusCode(400);
                    response.setMessage("Cannot reschedule a cancelled appointment. Please book a new one.");
                    return response;
                }

                existingAppointment.setTime(appointment.getTime());
                AppointmentEntity updatedAppointment = appointmentRepository.save(existingAppointment);
                AppointmentDto appointmentDto = DtoConverter.convertAppointmentEntityToAppointmentDto(updatedAppointment);

                response.setStatusCode(200);
                response.setMessage("Your appointment has been rescheduled to " +
                    (appointment.getTime()));
                response.setAppointmentDto(appointmentDto);
            } else {
                response.setStatusCode(404);
                response.setMessage("We couldn't find the appointment you're trying to reschedule");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("We couldn't process your rescheduling request. Please try again later.");
        }
        return response;
    }


    @Override
    public Response getAppointmentsForPatient(Long patientId) {
        Response response = new Response();
        try {
            List<AppointmentEntity> appointments = appointmentRepository.findByPatientId(patientId);

            if (appointments.isEmpty()) {
                response.setStatusCode(200);
                response.setMessage("You don't have any upcoming appointments");
                return response;
            }

            response.setStatusCode(200);
            response.setMessage("Your appointments were retrieved successfully");
            response.setAppointmentDtos(DtoConverter.convertAppointmentEntityListToAppointmentDtoList(appointments));
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("We're having trouble retrieving your appointments. Please try again later.");
        }
        return response;
    }

    @Override
    public Response getAppointmentsForDoctor(String email) {

        Response response = new Response();
        try {
            DoctorEntity doctor=doctorRepository.findByUserEmail(email);

            List<AppointmentEntity> appointments = appointmentRepository.findByDoctorId(doctor.getId());

            if (appointments.isEmpty()) {
                response.setStatusCode(200);
                response.setMessage("You don't have any scheduled appointments");
                return response;
            }

            response.setStatusCode(200);
            response.setMessage("Your patient appointments were retrieved successfully");
            response.setAppointmentDtos(DtoConverter.convertAppointmentEntityListToAppointmentDtoList(appointments));
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("We're having trouble retrieving your schedule. Please try again later.");
        }
        return response;
    }

    @Override
    public Response getAppointmentsByStatus(AppointmentStatus status) {
        Response response = new Response();
        try {
            List<AppointmentEntity> appointments = appointmentRepository.findByAppointmentStatus(status);

            if (appointments.isEmpty()) {
                response.setStatusCode(200);
                response.setMessage("No " + status.toString().toLowerCase() + " appointments found");
                return response;
            }

            response.setStatusCode(200);
            response.setMessage(status.toString().toLowerCase() + " appointments retrieved successfully");
            response.setAppointmentDtos(DtoConverter.convertAppointmentEntityListToAppointmentDtoList(appointments));
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("We're having trouble retrieving the appointments. Please try again later.");
        }
        return response;
    }

    @Override
    public Response getAllAppointments() {
        Response response = new Response();
        try {
            List<AppointmentEntity> appointments = appointmentRepository.findAll();

            if (appointments.isEmpty()) {
                response.setStatusCode(200);
                response.setMessage("You don't have any  appointments");
                response.setAppointmentDtos(Collections.emptyList());
                return response;
            }

            response.setStatusCode(200);
            response.setMessage("Your patient appointments were retrieved successfully");
            response.setAppointmentDtos(DtoConverter.convertAppointmentEntityListToAppointmentDtoList(appointments));
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("We're having trouble retrieving appointments. Please try again later.");
        }
        return response;
    }

    @Override
    public AppointmentEntity getAppointmentById(Long id) {
        Optional<AppointmentEntity> appointment = appointmentRepository.findById(id);
        return appointment.orElse(null);

    }

    @Override
    public Response completeAppointment(Long id) {
        Response response = new Response();

        try {
            Optional<AppointmentEntity> optionalAppointment = appointmentRepository.findById(id);

            if (optionalAppointment.isPresent()) {
                AppointmentEntity appointment = optionalAppointment.get();

                appointment.setAppointmentStatus(AppointmentStatus.COMPLETED);
                appointmentRepository.save(appointment);

                response.setStatusCode(200);
                response.setMessage(" appointment has been successfully completed");
                response.setAppointmentDto(DtoConverter.convertAppointmentEntityToAppointmentDto(appointment));
            } else {
                response.setStatusCode(404);
                response.setMessage("We couldn't find the appointment you're trying to cancel");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("We couldn't process your cancellation request. Please try again later.");
        }
        return response;
    }


}
