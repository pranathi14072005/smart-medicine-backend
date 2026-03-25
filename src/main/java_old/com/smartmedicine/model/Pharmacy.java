package com.smartmedicine.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "pharmacies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private String address;

    @Column
    private String city;

    @Column
    private String state;

    @Column(name = "zip_code")
    private String zipCode;

    @Column
    private String country;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column
    private String email;

    @Column
    private String website;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "is_24_hours")
    @Builder.Default
    private boolean open24Hours = false;

    @Column(name = "is_open_weekends")
    @Builder.Default
    private boolean openWeekends = false;

    @Column(name = "has_delivery")
    @Builder.Default
    private boolean hasDelivery = false;

    @Column(name = "has_online_ordering")
    @Builder.Default
    private boolean hasOnlineOrdering = false;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "total_reviews")
    @Builder.Default
    private Integer totalReviews = 0;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private PharmacyStatus status = PharmacyStatus.ACTIVE;

    @Column(name = "license_number")
    private String licenseNumber;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PharmacyStatus {
        ACTIVE, INACTIVE, TEMPORARILY_CLOSED, PERMANENTLY_CLOSED
    }
}
