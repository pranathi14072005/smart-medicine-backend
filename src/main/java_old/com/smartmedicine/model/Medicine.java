package com.smartmedicine.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String dosage;

    @Column(name = "dosage_unit")
    private String dosageUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "medicine_type")
    private MedicineType medicineType;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Positive
    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "quantity_unit")
    private String quantityUnit;

    @Column(name = "low_stock_threshold")
    @Builder.Default
    private Integer lowStockThreshold = 5;

    @Column(name = "price")
    private Double price;

    @Column(name = "prescription_required")
    @Builder.Default
    private boolean prescriptionRequired = false;

    @Column(name = "side_effects", length = 2000)
    private String sideEffects;

    @Column(name = "storage_instructions")
    private String storageInstructions;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "barcode")
    private String barcode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private MedicineStatus status = MedicineStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Reminder> reminders = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum MedicineType {
        TABLET, CAPSULE, SYRUP, INJECTION, CREAM, DROPS, INHALER, PATCH, POWDER, OTHER
    }

    public enum MedicineStatus {
        ACTIVE, EXPIRED, LOW_STOCK, OUT_OF_STOCK, DISCONTINUED
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    public boolean isLowStock() {
        return quantity != null && lowStockThreshold != null && quantity <= lowStockThreshold;
    }
}
