package com.EventHive.entity;

import com.EventHive.dto.UserResponseDto;
import com.EventHive.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    public User(UserResponseDto userdto){
        this.id=userdto.getId();
        this.firstName=userdto.getFirstName();
        this.lastName=userdto.getLastName();
        this.email=userdto.getEmail();
        this.role=userdto.getRole();
    }


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;

    @Enumerated(value = EnumType.STRING)
    private Role role;
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}