package com.codingshuttle.youtube.hospitalManagement.service;

import com.codingshuttle.youtube.hospitalManagement.dto.DoctorResponseDto;
import com.codingshuttle.youtube.hospitalManagement.dto.OnboardDoctorRequestDto;
import com.codingshuttle.youtube.hospitalManagement.entity.Doctor;
import com.codingshuttle.youtube.hospitalManagement.entity.User;
import com.codingshuttle.youtube.hospitalManagement.entity.UserRepository;
import com.codingshuttle.youtube.hospitalManagement.entity.type.RoleType;
import com.codingshuttle.youtube.hospitalManagement.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final ModelMapper modelMapper;
    private final UserRepository  userRepository;

    public List<DoctorResponseDto> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(doctor -> modelMapper.map(doctor, DoctorResponseDto.class))
                .collect(Collectors.toList());
    }


    @Transactional
    public DoctorResponseDto onboardNewDoctor(OnboardDoctorRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + requestDto.getUserId()));

        if(doctorRepository.existsById(user.getId())){
            throw new IllegalArgumentException("Doctor already exists for user with ID: " + requestDto.getUserId());
        }

        Doctor doctor = Doctor.builder()
                .name(requestDto.getName())
                .specialization(requestDto.getSpecialization())
                .user(user)
                .build();
        user.getRoles().add(RoleType.DOCTOR);
        userRepository.save(user);
        return modelMapper.map(doctorRepository.save(doctor), DoctorResponseDto.class);
    }
}
