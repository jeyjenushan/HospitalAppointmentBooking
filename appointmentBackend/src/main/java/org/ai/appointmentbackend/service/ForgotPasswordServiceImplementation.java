package org.ai.appointmentbackend.service;

import org.ai.appointmentbackend.entity.ForgotPasswordToken;
import org.ai.appointmentbackend.entity.UserEntity;
import org.ai.appointmentbackend.repository.ForgotPasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ForgotPasswordServiceImplementation implements ForgotPasswordService{
    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;



    @Override
    public ForgotPasswordToken createToken(UserEntity user, String id, String otp, String sendTo) {
        ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
        forgotPasswordToken.setUserEntity(user);
        forgotPasswordToken.setOtp(otp);
        forgotPasswordToken.setId(id);
        forgotPasswordToken.setEmail(sendTo);
        return forgotPasswordRepository.save(forgotPasswordToken);
    }

    @Override
    public ForgotPasswordToken findById(String id) {
       Optional<ForgotPasswordToken> forgotPasswordToken = forgotPasswordRepository.findById(id);
       return forgotPasswordToken.orElse(null);
    }

    @Override
    public ForgotPasswordToken findByUser(Long userId) {
        return forgotPasswordRepository.findByUserEntity_id(userId);
    }

    @Override
    public void deleteToken(ForgotPasswordToken token) {
        forgotPasswordRepository.delete(token);

    }
}
