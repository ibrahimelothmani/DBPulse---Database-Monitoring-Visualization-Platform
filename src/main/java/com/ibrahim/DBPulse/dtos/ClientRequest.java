package com.ibrahim.DBPulse.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating and updating clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Size(max = 20, message = "Phone number should not exceed 20 characters")
    private String phone;

    @Size(max = 255, message = "Address should not exceed 255 characters")
    private String address;

    @Size(max = 100, message = "City should not exceed 100 characters")
    private String city;

    @Size(max = 50, message = "Country should not exceed 50 characters")
    private String country;
}
