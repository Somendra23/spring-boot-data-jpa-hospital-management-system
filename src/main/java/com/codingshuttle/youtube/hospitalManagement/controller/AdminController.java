package com.codingshuttle.youtube.hospitalManagement.controller;

import com.codingshuttle.youtube.hospitalManagement.dto.DoctorResponseDto;
import com.codingshuttle.youtube.hospitalManagement.dto.OnboardDoctorRequestDto;
import com.codingshuttle.youtube.hospitalManagement.dto.PatientResponseDto;
import com.codingshuttle.youtube.hospitalManagement.entity.Doctor;
import com.codingshuttle.youtube.hospitalManagement.entity.User;
import com.codingshuttle.youtube.hospitalManagement.service.DoctorService;
import com.codingshuttle.youtube.hospitalManagement.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PatientService patientService;
    private final DoctorService doctorService;

    @GetMapping("/patients")
    public ResponseEntity<List<PatientResponseDto>> getAllPatients(
            @RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize
    ) {

        //you can get hold of security context holder from anywhere in the app once it's set
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return ResponseEntity.ok(patientService.getAllPatients(pageNumber, pageSize));
    }
    @PostMapping("/onboard")
    public ResponseEntity<DoctorResponseDto> onboardNewDoctor(@RequestParam OnboardDoctorRequestDto requestDto){

        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.onboardNewDoctor(requestDto));

    }
}
