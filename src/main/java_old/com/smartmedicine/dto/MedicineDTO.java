package com.smartmedicine.dto;

import com.smartmedicine.model.Medicine;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineDTO {

    private Long id;

    @NotBlank(message = "Medicine name is required")
    private String name;

    private String description;

    @NotBlank(message = "Dosage is required")
    private String dosage;

    private String dosageUnit;

    private Medicine.MedicineType medicineType;

    private String manufacturer;

    private String batchNumber;

    private LocalDate expiryDate;

    private LocalDate purchaseDate;

    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    private String quantityUnit;

    private Integer lowStockThreshold;

    private Double price;

    private boolean prescriptionRequired;

    private String sideEffects;

    private String storageInstructions;

    private String imageUrl;

    private String barcode;

    private Medicine.MedicineStatus status;

    private Long userId;

    private String username;

    private boolean expired;

    private boolean lowStock;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
