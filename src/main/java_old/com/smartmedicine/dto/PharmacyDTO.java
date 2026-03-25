package com.smartmedicine.dto;

import com.smartmedicine.model.Pharmacy;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyDTO {

    private Long id;

    @NotBlank(message = "Pharmacy name is required")
    private String name;

    private String description;

    @NotBlank(message = "Address is required")
    private String address;

    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phoneNumber;
    private String email;
    private String website;
    private Double latitude;
    private Double longitude;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private boolean open24Hours;
    private boolean openWeekends;
    private boolean hasDelivery;
    private boolean hasOnlineOrdering;
    private Double rating;
    private Integer totalReviews;
    private String imageUrl;
    private Pharmacy.PharmacyStatus status;
    private String licenseNumber;
    private Double distanceKm;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
