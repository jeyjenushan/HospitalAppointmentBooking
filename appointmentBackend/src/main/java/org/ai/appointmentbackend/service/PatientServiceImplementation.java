package org.ai.appointmentbackend.service;

import lombok.AllArgsConstructor;
import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.entity.PatientEntity;
import org.ai.appointmentbackend.entity.UserEntity;
import org.ai.appointmentbackend.mapper.DtoConverter;
import org.ai.appointmentbackend.repository.PatientRepository;
import org.ai.appointmentbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImplementation implements PatientService{

    private final PatientRepository patientRepository;

    private final UserRepository userRepository;

    public PatientServiceImplementation(PatientRepository patientRepository, UserRepository userRepository) {
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }


    @Override
    public Response getAllPatients() {
       Response response=new Response();
       try {
           List<PatientEntity> patients = patientRepository.findAll();
           if (patients.isEmpty()) {
               response.setMessage("No patients found in the system");
               response.setStatusCode(404);
           } else {
               response.setPatientDtos(DtoConverter.convertPatientEntityListToPatientDtoList(patients));
               response.setMessage("Patients retrieved successfully");
               response.setStatusCode(200);
           }
       }catch (Exception e) {
           response.setStatusCode(500);
           response.setMessage("Unable to retrieve patients at this time. Please try again later.");
       }
       return response;
    }

    @Override
    public Response getPatientById(Long id) {
        Response response=new Response();
        try {
            if (id == null || id <= 0) {
                response.setStatusCode(400);
                response.setMessage("Invalid patient ID provided");
                return response;
            }
            Optional<PatientEntity> patients=patientRepository.findById(id);
            if(patients.isPresent()) {
                response.setPatientDto(DtoConverter.convertPatientEntityToPatientDto(patients.get()));
                response.setStatusCode(200);
                response.setMessage("Patient details retrieved successfully");
            }else{
                response.setStatusCode(500);
                response.setMessage("Patient with ID " + id + " not found");
            }

        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving patient information. Please try again.");
        }
        return response;
    }

    @Override
    public Response updatePatient(Long id,PatientEntity updatedPatient) {
        Response response=new Response();
        try {
            if (id == null || id <= 0) {
                response.setStatusCode(400);
                response.setMessage("Invalid patient ID provided");
                return response;
            }
            PatientEntity existingPatient = patientRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

            if (updatedPatient.getAge() != 0) {
                if (updatedPatient.getAge() < 0 || updatedPatient.getAge() > 120) {
                    response.setStatusCode(400);
                    response.setMessage("Invalid age value provided");
                    return response;
                }
                existingPatient.setAge(updatedPatient.getAge());
            }
            if (updatedPatient.getGender() != null) {
                existingPatient.setGender(updatedPatient.getGender());
            }
            if (updatedPatient.getContactNumber() != null) {
                String trimmedNumber = updatedPatient.getContactNumber().trim();
                if (!trimmedNumber.matches("^[0-9\\-\\+\\s]+$")) {
                    response.setStatusCode(400);
                    response.setMessage("Invalid contact number format");
                    return response;
                }
                existingPatient.setContactNumber(trimmedNumber);
            }
            if (updatedPatient.getAddress() != null) {
                existingPatient.setAddress(updatedPatient.getAddress().trim());
            }
            if (updatedPatient.getMedicalHistory() != null) {
                existingPatient.setMedicalHistory(updatedPatient.getMedicalHistory().trim());
            }
            if(updatedPatient.getUser()!=null) {
                UserEntity existingUser = existingPatient.getUser();
                UserEntity updatedUser = updatedPatient.getUser();

                if (updatedUser.getName() != null && !updatedUser.getName().isEmpty()) {
                    String trimmedName = updatedUser.getName().trim();
                    if (!trimmedName.matches("^[a-zA-Z\\s]+$")) {
                        response.setStatusCode(400);
                        response.setMessage("Name can only contain letters and spaces");
                        return response;
                    }
                    existingUser.setName(trimmedName);
                }
            }

           PatientEntity patientEntity=patientRepository.save(existingPatient);
            response.setPatientDto(DtoConverter.convertPatientEntityToPatientDto(patientEntity));
           response.setStatusCode(200);
            response.setMessage("Patient information updated successfully");
        }catch (RuntimeException e) {
            response.setStatusCode(404);
            response.setMessage("Patient with ID " + id + " not found");
        }



        catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Unable to update patient information. Please try again later.");
        }
        return response;
    }

    @Override
    public Response deletePatient(Long id) {
        Response response=new Response();
        try{
            if (id == null || id <= 0) {
                response.setStatusCode(400);
                response.setMessage("Invalid patient ID provided");
                return response;
            }
            if (!patientRepository.existsById(id)) {
                response.setStatusCode(404);
                response.setMessage("Patient with ID " + id + " not found");
                return response;
            }
            patientRepository.deleteById(id);
            response.setStatusCode(200);
            response.setMessage("Successfully deleted patient with id "+id);

        }catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Unable to delete patient account at this time. Please try again later.");
        }
return response;
    }

    @Override
    public Response fetchPatientMedicalHistory(Long id) {
        Response response=new Response();
        try {
            if (id == null || id <= 0) {
                response.setStatusCode(400);
                response.setMessage("Invalid patient ID provided");
                return response;
            }

            PatientEntity patient = patientRepository.findById(id).orElseThrow(() -> new RuntimeException("Patient not found"));

        }catch (RuntimeException e) {
            response.setStatusCode(404);
            response.setMessage("Patient with ID " + id + " not found");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Unable to retrieve medical records. Please try again later.");
        }
return response;
    }

    @Override
    public PatientEntity findByEmail(String email) {
        return patientRepository.findByUserEmail(email);
    }

    @Override
    public boolean isPatientOwnedByEmail(Long patientId, String email) throws Exception {
        Response response = new Response();

        PatientEntity patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new Exception("Patient not found"));

        // Get the user associated with this patient
        UserEntity user = userRepository.findById(patient.getUser().getId())
                .orElseThrow(() -> new Exception("User not found"));

        // Verify email matches
        return user.getEmail().equals(email);

    }


}
