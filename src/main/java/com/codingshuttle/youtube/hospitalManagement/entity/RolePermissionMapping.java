package com.codingshuttle.youtube.hospitalManagement.entity;

import com.codingshuttle.youtube.hospitalManagement.entity.type.RoleType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.management.relation.Role;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RolePermissionMapping {
    private static final Map<RoleType, Set<PermissionType>> rolePermissionMap =
            Map.of(RoleType.PATIENT, Set.of(PermissionType.PATIENT_READ, PermissionType.APPOINTMENT_READ, PermissionType.APPOINTMENT_WRITE),
                    RoleType.DOCTOR, Set.of(PermissionType.DOCTOR_READ, PermissionType.APPOINTMENT_READ, PermissionType.APPOINTMENT_WRITE),
                    RoleType.ADMIN, Set.of(PermissionType.PATIENT_READ, PermissionType.PATIENT_WRITE, PermissionType.DOCTOR_READ,
                            PermissionType.DOCTOR_WRITE, PermissionType.APPOINTMENT_READ, PermissionType.APPOINTMENT_WRITE,
                            PermissionType.USER_MANAGEMENT, PermissionType.REPORT_VIEW)
            );

    public static Set<SimpleGrantedAuthority> getAuthoritiesForRole(RoleType roleType) {
        return rolePermissionMap.get(roleType).stream()
                .map(permissionType -> new SimpleGrantedAuthority(permissionType.getPermission()))
                .collect(Collectors.toSet());
    }
}
