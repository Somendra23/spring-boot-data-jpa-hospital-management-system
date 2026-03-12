package com.codingshuttle.youtube.hospitalManagement.entity;

import com.codingshuttle.youtube.hospitalManagement.entity.type.RoleType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "app_users",
        indexes = {@Index(name = "idx_provider_id_provider_type", columnList = "provider_id, provider_type")})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "provider_type")
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    //so you store as string and not ordinal numbers
    //default fetch type is LAZY
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<RoleType> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return roles.stream().map(roleType -> new SimpleGrantedAuthority("ROLE_"+roleType)).toList();

        //add permissions also
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        roles.forEach(roleType -> {
            Set<SimpleGrantedAuthority> permissions = RolePermissionMapping.getAuthoritiesForRole(roleType);
            permissions.forEach(permission -> authorities.add(permission));
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleType));

        });

        return authorities;
    }

//    @Override
//    public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
//    }
}
