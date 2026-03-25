package com.smartmedicine.controller;

import com.smartmedicine.dto.ApiResponse;
import com.smartmedicine.dto.PharmacyDTO;
import com.smartmedicine.model.Pharmacy;
import com.smartmedicine.service.PharmacyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacies")
@RequiredArgsConstructor
@Tag(name = "Pharmacies", description = "Pharmacy management APIs")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    // ===== Public Endpoints =====

    @GetMapping("/public")
    @Operation(summary = "Get all active pharmacies (public)")
    public ResponseEntity<ApiResponse<Page<PharmacyDTO>>> getActivePharmacies(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                pharmacyService.getAllActivePharmacies(pageable)));
    }

    @GetMapping("/public/{id}")
    @Operation(summary = "Get pharmacy by ID (public)")
    public ResponseEntity<ApiResponse<PharmacyDTO>> getPharmacyById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(pharmacyService.getPharmacyById(id)));
    }

    @GetMapping("/public/search")
    @Operation(summary = "Search pharmacies (public)")
    public ResponseEntity<ApiResponse<List<PharmacyDTO>>> searchPharmacies(
            @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(pharmacyService.searchPharmacies(keyword)));
    }

    @GetMapping("/public/nearby")
    @Operation(summary = "Find nearby pharmacies by location (public)")
    public ResponseEntity<ApiResponse<List<PharmacyDTO>>> getNearbyPharmacies(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "10.0") Double radiusKm) {
        return ResponseEntity.ok(ApiResponse.success(
                pharmacyService.getNearbyPharmacies(lat, lng, radiusKm)));
    }

    @GetMapping("/public/with-delivery")
    @Operation(summary = "Get pharmacies with delivery (public)")
    public ResponseEntity<ApiResponse<List<PharmacyDTO>>> getPharmaciesWithDelivery() {
        return ResponseEntity.ok(ApiResponse.success(pharmacyService.getPharmaciesWithDelivery()));
    }

    @GetMapping("/public/open-24h")
    @Operation(summary = "Get 24-hour pharmacies (public)")
    public ResponseEntity<ApiResponse<List<PharmacyDTO>>> get24HourPharmacies() {
        return ResponseEntity.ok(ApiResponse.success(pharmacyService.get24HourPharmacies()));
    }

    // ===== Admin Endpoints =====

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PHARMACIST')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create a new pharmacy (Admin/Pharmacist)")
    public ResponseEntity<ApiResponse<PharmacyDTO>> createPharmacy(
            @Valid @RequestBody PharmacyDTO dto) {
        PharmacyDTO created = pharmacyService.createPharmacy(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pharmacy created successfully", created));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Get all pharmacies with any status (Admin only)")
    public ResponseEntity<ApiResponse<Page<PharmacyDTO>>> getAllPharmacies(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(pharmacyService.getAllPharmacies(pageable)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PHARMACIST')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update pharmacy (Admin/Pharmacist)")
    public ResponseEntity<ApiResponse<PharmacyDTO>> updatePharmacy(
            @PathVariable Long id,
            @Valid @RequestBody PharmacyDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Pharmacy updated",
                pharmacyService.updatePharmacy(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete pharmacy (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deletePharmacy(@PathVariable Long id) {
        pharmacyService.deletePharmacy(id);
        return ResponseEntity.ok(ApiResponse.success("Pharmacy deleted", null));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update pharmacy status (Admin only)")
    public ResponseEntity<ApiResponse<PharmacyDTO>> updateStatus(
            @PathVariable Long id,
            @RequestParam Pharmacy.PharmacyStatus status) {
        return ResponseEntity.ok(ApiResponse.success(pharmacyService.updateStatus(id, status)));
    }
}
