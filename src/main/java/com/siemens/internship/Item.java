package com.siemens.internship;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 100, message = "Name too long (max 100 chars)")
    private String name;

    @Size(max = 500, message = "Description too long (max 500 chars)")
    private String description;

    @Size(max = 500, message = "Status too long (max 500 chars)")
    private String status;

    // Add email regex validation
    @Email(message = "Invalid email format")
    private String email;
}