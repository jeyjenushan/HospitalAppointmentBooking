package org.ai.appointmentbackend.service;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.ai.appointmentbackend.configuration.JwtTokenProvider;
import org.ai.appointmentbackend.dto.Response;
import org.ai.appointmentbackend.dto.UserDto;
import org.ai.appointmentbackend.entity.*;
import org.ai.appointmentbackend.enumpack.Role;
import org.ai.appointmentbackend.mapper.DtoConverter;
import org.ai.appointmentbackend.repository.*;
import org.ai.appointmentbackend.request.LoginRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.function.Function;

@Service

public class AuthServiceImplementation implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder, PatientRepository patientRepository, DoctorRepository doctorRepository, AdminRepository adminRepository, JwtTokenProvider jwtTokenProvider, EmailService emailService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.adminRepository = adminRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
    }

    private final PasswordEncoder passwordEncoder;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    private final AdminRepository adminRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;

    @Override
    public Response RegisterPatient(PatientEntity patient,MultipartFile imageFile) {
        try {
            return registerUser(patient.getUser(), Role.PATIENT,imageFile, user -> {
                PatientEntity savedPatient = new PatientEntity();
                savedPatient.setUser(user);
                savedPatient.setAge(patient.getAge());
                savedPatient.setGender(patient.getGender());
                savedPatient.setAddress(patient.getAddress());
                savedPatient.setContactNumber(patient.getContactNumber());
                savedPatient.setMedicalHistory(patient.getMedicalHistory());
                savedPatient = patientRepository.save(savedPatient);

                return buildSuccessResponse(savedPatient, jwtTokenProvider.generateToken(user), "Patient");
            });
        } catch (Exception e) {
            return buildErrorResponse("Registration failed: " + e.getMessage(), 500);
        }
    }

    @Override
    public Response RegisterDoctor(DoctorEntity doctor,MultipartFile imageFile) {
        try {
            String plainTextPassword = doctor.getUser().getPassword();
            return registerUser(doctor.getUser(), Role.DOCTOR,imageFile, user -> {
                DoctorEntity savedDoctor = new DoctorEntity();

                savedDoctor.setUser(user);
                savedDoctor.setAboutDoctor(doctor.getAboutDoctor());
                savedDoctor.setDegree(doctor.getDegree());
                savedDoctor.setFees(doctor.getFees());
                savedDoctor.setExperience(doctor.getExperience());
                savedDoctor.setAddress1(doctor.getAddress1());



                savedDoctor.setSpecialization(doctor.getSpecialization());
                savedDoctor.setContactNumber(doctor.getContactNumber());
                savedDoctor.setAvailability(doctor.getAvailability());



                savedDoctor = doctorRepository.save(savedDoctor);

                try {
                    sendRegistrationEmails(user, "Doctor", plainTextPassword);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
                return buildSuccessResponse(savedDoctor, jwtTokenProvider.generateToken(user), "Doctor");
            });
        } catch (Exception e) {
            return buildErrorResponse("Registration failed: " + e.getMessage(), 500);
        }
    }



    @Override
    public Response RegisterAdmin(AdminEntity admin,MultipartFile imageFile) {
        try {
            String password1 = admin.getUser().getPassword();
            return registerUser(admin.getUser(), Role.ADMIN,imageFile,user -> {
                AdminEntity savedAdmin = new AdminEntity();
                savedAdmin.setUser(user);
                savedAdmin = adminRepository.save(savedAdmin);

                try {
                    sendRegistrationEmails(user, "Admin", password1);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
                return buildSuccessResponse(savedAdmin, jwtTokenProvider.generateToken(user), "Admin");
            });
        } catch (Exception e) {
            return buildErrorResponse("Registration failed: " + e.getMessage(), 500);
        }
    }



    private Response registerUser(UserEntity userEntity, Role role,MultipartFile imageFile, Function<UserEntity, Response> successHandler) {
        try {
            UserEntity existingUser = userRepository.findByEmail(userEntity.getEmail());
            if (existingUser != null) {
                return buildErrorResponse("User already exists with the provided email.", 400);
            }

            UserEntity newUserEntity = saveUserEntity(userEntity, role, imageFile);
            return successHandler.apply(newUserEntity);

        } catch (Exception e) {
            return buildErrorResponse("Registration failed: " + e.getMessage(), 500);
        }
    }

    private UserEntity saveUserEntity(UserEntity userEntity, Role role, MultipartFile imageFile) throws IOException {
        userEntity.setRole(role);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        if (imageFile != null && !imageFile.isEmpty()) {
            userEntity.setImage(imageFile.getBytes());
            userEntity.setImageName(imageFile.getOriginalFilename());
            userEntity.setImageType(imageFile.getContentType());
        }
        return userRepository.save(userEntity);
    }

    private void sendRegistrationEmails(UserEntity user, String role, String password) throws MessagingException {
        String userMessage = String.format(
                "Hello %s,\n\n" +
                        "Your registration as a %s is successful.\n" +
                        "Email: %s\n" +
                        "Password: %s\n\n" +
                        "Thank you!",
                user.getName(), role, user.getEmail(), password
        );
        emailService.sendEmail(user.getEmail(), "Registration Successful", userMessage);

        String adminMessage = String.format("Hello Admin,\n\nA new %s has been registered.\nName: %s\nEmail: %s\n\nThank you!",
                role, user.getName(), user.getEmail());
        emailService.sendEmail(user.getEmail(), "New " + role + " Registration", adminMessage);
    }

    private Response buildSuccessResponse(Object entity, String token, String role) {
        Response response = new Response();
        response.setStatusCode(200);
        response.setMessage(role + " registered successfully");
        response.setToken(token);

        switch (role) {
            case "Patient":
                response.setPatientDto(DtoConverter.convertPatientEntityToPatientDto((PatientEntity) entity));
                break;
            case "Doctor":
                response.setDoctorDto(DtoConverter.convertDoctorEntityToDoctorDto((DoctorEntity) entity));
                break;

            case "Admin":
                response.setAdminDto(DtoConverter.convertAdminEntityToAdminDto((AdminEntity) entity));
                break;
        }

        return response;
    }

    private Response buildErrorResponse(String message, int statusCode) {
        Response response = new Response();
        response.setStatusCode(statusCode);
        response.setMessage(message);
        return response;
    }

    @Override
    public Response LoginUser(LoginRequest loginRequest) {
        Response response = new Response();
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()
                    )
            );

            // Fetch user details
            UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail());

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(userEntity);
            Date expirationDate = jwtTokenProvider.extractExpiration(token);

            // Convert UserEntity to UserDto
            UserDto userDto = DtoConverter.convertUserEntityToUserDto(userEntity);

            // Assign role-specific IDs based on the user's role
            switch (userDto.getRole()) {
                case PATIENT:
                    PatientEntity patientEntity = patientRepository.findByUserId(userEntity.getId());
                    userDto.setPatientId(patientEntity != null ? patientEntity.getId() : null);   // Assuming you store patient-related info in the UserEntity
                    break;
                case DOCTOR:
                    DoctorEntity doctorEntity = doctorRepository.findByUserId(userEntity.getId());
                    userDto.setDoctorId(doctorEntity != null ? doctorEntity.getId() : null); // Set doctor ID
                    break;
                case ADMIN:
                    // Fetch admin data based on email or user ID
                    AdminEntity adminEntity = adminRepository.findByUserId(userEntity.getId());
                    userDto.setAdminId(adminEntity != null ? adminEntity.getId() : null); // Set admin ID  // Handle admin-related info
                    break;
                default:
                    // If no recognized role, can set to null or throw an error
                    throw new IllegalArgumentException("Invalid role.");
            }

            // Set the response attributes
            response.setStatusCode(200);
            response.setToken(token);
            response.setRole(userDto.getRole());
            response.setExpirationTime(String.valueOf(expirationDate));
            response.setUserDto(userDto);
            response.setMessage("The account has been logged in successfully.");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Login failed: " + e.getMessage());
        }

        return response;
    }


}