package com.codingshuttle.youtube.hospitalManagement.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionType {
    PATIENT_READ("patient:read"),
    PATIENT_WRITE("patient:write"),
    APPOINTMENT_READ("appointment:read"),
    APPOINTMENT_WRITE("appointment:write"),
    APPOINTMENT_DELETE("appointment:delete"),

    USER_MANAGEMENT("user:management"),
    REPORT_VIEW("repost:view"),
    DOCTOR_READ("doctor:read"),
    DOCTOR_WRITE("doctor:write");
    ;
    private final String permission;
}
