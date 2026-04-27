package com.neuroguard.userservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private String gender;
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id")
    @JsonIgnore // Prevent infinite recursion
    private User caregiver;

    @OneToMany(mappedBy = "caregiver")
    @JsonIgnore
    private java.util.List<User> patients;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @JsonIgnore
    private User doctor;

    @OneToMany(mappedBy = "doctor")
    @JsonIgnore
    private java.util.List<User> assignedPatients;

}