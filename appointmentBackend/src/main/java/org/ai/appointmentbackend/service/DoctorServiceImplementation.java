package org.ai.appointmentbackend.service;

import org.ai.appointmentbackend.dto.AppointmentDto;
import org.ai.appointmentbackend.dto.DoctorDto;
import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.AddressEntity;
import org.ai.appointmentbackend.entity.AppointmentEntity;
import org.ai.appointmentbackend.entity.DoctorEntity;
import org.ai.appointmentbackend.mapper.DtoConverter;
import org.ai.appointmentbackend.repository.AppointmentRepository;
import org.ai.appointmentbackend.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImplementation implements DoctorService{

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;




    public DoctorServiceImplementation(DoctorRepository doctorRepository, AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Response deleteDoctor(Long id) {
        Response response = new Response();
        try {
            if (id == null || id <= 0) {
                response.setStatusCode(400);
                response.setMessage("Invalid doctor ID");
                return response;
            }

            if (!doctorRepository.existsById(id)) {
                response.setStatusCode(404);
                response.setMessage("Doctor not found with id: " + id);
                return response;
            }

            doctorRepository.deleteById(id);
            response.setStatusCode(200);
            response.setMessage("Doctor deleted successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Failed to delete doctor. Please try again later.");
        }
        return response;
    }

    @Override
    public Response fetchAllDoctors() {
        Response response = new Response();
        try {
            List<DoctorEntity> doctors = doctorRepository.findAll();

            if ( doctors.isEmpty()) {
                response.setStatusCode(404);
                response.setMessage("No doctors found in the system");
                response.setDoctorDtos(Collections.emptyList());
            } else {
                response.setDoctorDtos(DtoConverter.convertDoctorEntityListToDoctorDtoList(doctors));
                response.setStatusCode(200);
                response.setMessage("Doctors retrieved successfully");
            }

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Failed to retrieve doctors. Please try again later.");
            response.setDoctorDtos(Collections.emptyList());
        }
        return response;
    }


    @Override
    public Response updateDoctor(String email, DoctorEntity updatedDoctor) {
        Response response = new Response();

        try {

            if (email == null || email.trim().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("Doctor email cannot be empty.");
                return response;
            }

            if (updatedDoctor == null) {
                response.setStatusCode(400);
                response.setMessage("Updated doctor details cannot be null.");
                return response;
            }

            DoctorEntity existingDoctor = doctorRepository.findByUserEmail(email);

            if (existingDoctor == null) {
                response.setStatusCode(404);
                response.setMessage("Doctor with the provided email not found.");
                return response;
            }

            if (updatedDoctor.getUser() != null) {
                String name = updatedDoctor.getUser().getName();
                if (name != null && !name.trim().isEmpty()) {
                    existingDoctor.getUser().setName(name.trim());
                }

                Long userId = updatedDoctor.getUser().getId();
                if (userId != null) {
                    existingDoctor.getUser().setId(userId);
                }
            }

            if (updatedDoctor.getSpecialization() != null && !updatedDoctor.getSpecialization().trim().isEmpty()) {
                existingDoctor.setSpecialization(updatedDoctor.getSpecialization().trim());
            }

            if (updatedDoctor.getAvailability() != null) {
                existingDoctor.setAvailability(updatedDoctor.getAvailability());
            }

            if (updatedDoctor.getContactNumber() != null && !updatedDoctor.getContactNumber().trim().isEmpty()) {
                existingDoctor.setContactNumber(updatedDoctor.getContactNumber().trim());
            }

            if (updatedDoctor.getAboutDoctor() != null && !updatedDoctor.getAboutDoctor().trim().isEmpty()) {
                existingDoctor.setAboutDoctor(updatedDoctor.getAboutDoctor().trim());
            }

            if (updatedDoctor.getFees() != null) {
                existingDoctor.setFees(updatedDoctor.getFees());
            }

            if (updatedDoctor.getAddress1() != null) {
                AddressEntity existingAddress = getAddressEntity(updatedDoctor, existingDoctor);

                existingDoctor.setAddress1(existingAddress);
            }


            if (updatedDoctor.getExperience() != null) {
                existingDoctor.setExperience(updatedDoctor.getExperience());
            }

            if (updatedDoctor.getDegree() != null && !updatedDoctor.getDegree().trim().isEmpty()) {
                existingDoctor.setDegree(updatedDoctor.getDegree().trim());
            }


            DoctorEntity savedDoctor = doctorRepository.save(existingDoctor);

            response.setStatusCode(200);
            response.setMessage("Doctor details updated successfully.");
            response.setDoctorDto(DtoConverter.convertDoctorEntityToDoctorDto(savedDoctor));

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("An error occurred while updating doctor details. Please try again later.");

        }

        return response;
    }

    private static AddressEntity getAddressEntity(DoctorEntity updatedDoctor, DoctorEntity existingDoctor) {
        AddressEntity updatedAddress = updatedDoctor.getAddress1();
        AddressEntity existingAddress = existingDoctor.getAddress1();

        if (existingAddress == null) {
            existingAddress = new AddressEntity();
        }

        if (updatedAddress.getLine1() != null && !updatedAddress.getLine1().trim().isEmpty()) {
            existingAddress.setLine1(updatedAddress.getLine1().trim());
        }

        if (updatedAddress.getLine2() != null && !updatedAddress.getLine2().trim().isEmpty()) {
            existingAddress.setLine2(updatedAddress.getLine2().trim());
        }
        return existingAddress;
    }

    @Override
    public Response getDoctorsBySpecialization(String specialization) {
        Response response = new Response();
        try {
            if (specialization == null || specialization.trim().isEmpty()) {
                response.setStatusCode(400);
                response.setMessage("Please provide a specialization to search for.");
                return response;
            }

            String cleanedSpecialization = specialization.trim();
            List<DoctorEntity> matchingDoctors = doctorRepository.findBySpecializationContainingIgnoreCase(cleanedSpecialization);

            if (matchingDoctors.isEmpty()) {
                response.setStatusCode(404);
                response.setMessage("No doctors found with specialization: " + cleanedSpecialization);
            } else {
                response.setDoctorDtos(DtoConverter.convertDoctorEntityListToDoctorDtoList(matchingDoctors));
                response.setStatusCode(200);
                response.setMessage("Doctors with specialization '" + cleanedSpecialization + "' retrieved successfully.");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Failed to retrieve doctors by specialization. Please try again later.");
        }
        return response;
    }

    @Override
    public Response getDoctorAppointments(Long doctorId) {
        Response response = new Response();
        try {
            if (doctorId == null || doctorId <= 0) {
                response.setStatusCode(400);
                response.setMessage("Invalid doctor ID");
                return response;
            }

            // Check if doctor exists
            DoctorEntity doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

            // Get appointments
            List<AppointmentEntity> appointmentEntities = appointmentRepository.findByDoctorId(doctorId);

            response.setStatusCode(200);
            response.setMessage("Appointments retrieved successfully");
            List<AppointmentDto>appointmentDtoList=DtoConverter.convertAppointmentEntityListToAppointmentDtoList(appointmentEntities);
            response.setAppointmentDtos(appointmentDtoList);


        } catch (RuntimeException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Failed to retrieve appointments. Please try again later.");
        }

        return response;
    }



    public Response changeDoctorAvailability(Long doctorId) {
        Response response = new Response();
        try {
            if (doctorId == null || doctorId <= 0) {
                response.setStatusCode(400);
                response.setMessage("Invalid doctor ID");
                return response;
            }

            // Find the doctor by ID
            DoctorEntity doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));

            // Toggle the availability
            String currentAvailability = doctor.getAvailability();
            if ("available".equals(currentAvailability)) {
                doctor.setAvailability("unavailable"); // Set to unavailable
            } else {
                doctor.setAvailability("available"); // Set to available
            }

            // Save the updated doctor entity
            doctorRepository.save(doctor);

            response.setStatusCode(200);
            response.setMessage("Doctor availability changed successfully");

        } catch (RuntimeException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Failed to change doctor availability. Please try again later.");
        }

        return response;
    }



    @Override
    public Response isAuthHeader(String authHeader) {
          Response response = new Response();
        // Step 1: Extract the token from the Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatusCode(400);
            response.setMessage("Token not provided or incorrect format");
            return response;
        }
        String token = authHeader.substring(7);
        response.setStatusCode(200);
        response.setMessage("Successfully token find");
        response.setToken(token);
        return response;
    }

    @Override
    public Response fetchDoctorById(Long id) {
        Response response=new Response();
        try {
            if (id == null || id <= 0) {
                response.setStatusCode(400);
                response.setMessage("Invalid patient ID provided");
                return response;
            }
            Optional<DoctorEntity> doctor=doctorRepository.findById(id);
            if(doctor.isPresent()) {
                response.setDoctorDto(DtoConverter.convertDoctorEntityToDoctorDto(doctor.get()));
                response.setStatusCode(200);
                response.setMessage("Doctor details retrieved successfully");
            }else{
                response.setStatusCode(500);
                response.setMessage("Doctor with ID " + id + " not found");
            }

        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving Doctor information. Please try again.");
        }
        return response;
    }

    @Override
    public Response getDoctor(String email) {
        Response response = new Response();


        try {


            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be empty");
            }

            DoctorEntity doctor = doctorRepository.findByUserEmail(email);
            if (doctor == null) {

                response.setMessage("Doctor not found");
                response.setStatusCode(404);
                return response;
            }

            DoctorDto doctorDto = DtoConverter.convertDoctorEntityToDoctorDto(doctor);
             response.setDoctorDto(doctorDto);
             response.setStatusCode(200);
             response.setMessage("Doctor details retrieved successfully");
             return response;


        } catch (IllegalArgumentException e) {
            response.setMessage("failed to retrived doctor details found");
            response.setStatusCode(400);
            return response;

        } catch (Exception e) {
            response.setMessage("failed to retrived doctor details found");
            response.setStatusCode(400);
            return response;
        }
    }

    @Override
    public Response checkDoctorAvailability(Long id) {
        Response response = new Response();
        try {
            if (id == null || id <= 0) {
                response.setStatusCode(400);
                response.setMessage("Invalid doctor ID");
                return response;
            }

            DoctorEntity doctor = doctorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + id));

            if (doctor.getAvailability() == null) {
                response.setStatusCode(404);
                response.setMessage("Availability information not available for this doctor");
            } else {
                DoctorDto doctorDto =DtoConverter.convertDoctorEntityToDoctorDto(doctor);
                doctorDto.setAvailability(doctor.getAvailability());
                response.setStatusCode(200);
                response.setMessage("Doctor availability retrieved successfully");
                response.setDoctorDto(doctorDto);
            }
        } catch (RuntimeException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Failed to check doctor availability. Please try again later.");
        }
        return response;
    }


}
