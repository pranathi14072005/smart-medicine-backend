package com.smartmedicine.controller;

import com.smartmedicine.dto.ApiResponse;
import com.smartmedicine.dto.MedicineDTO;
import com.smartmedicine.model.User;
import com.smartmedicine.service.AuthService;
import com.smartmedicine.service.MedicineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
@Tag(name = "Medicines", description = "Medicine management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class MedicineController {

    private final MedicineService medicineService;
    private final AuthService authService;

    @PostMapping
    @Operation(summary = "Add a new medicine")
    public ResponseEntity<ApiResponse<MedicineDTO>> addMedicine(
            @Valid @RequestBody MedicineDTO dto) {
        User currentUser = authService.getCurrentUser();
        MedicineDTO created = medicineService.addMedicine(dto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Medicine added successfully", created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medicine by ID")
    public ResponseEntity<ApiResponse<MedicineDTO>> getMedicine(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                medicineService.getMedicineById(id, currentUser.getId())));
    }

    @GetMapping
    @Operation(summary = "Get all medicines for current user")
    public ResponseEntity<ApiResponse<Page<MedicineDTO>>> getMyMedicines(Pageable pageable) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                medicineService.getUserMedicines(currentUser.getId(), pageable)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search medicines by name")
    public ResponseEntity<ApiResponse<List<MedicineDTO>>> searchMedicines(
            @RequestParam String name) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                medicineService.searchMedicines(name, currentUser.getId())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a medicine")
    public ResponseEntity<ApiResponse<MedicineDTO>> updateMedicine(
            @PathVariable Long id,
            @Valid @RequestBody MedicineDTO dto) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Medicine updated successfully",
                medicineService.updateMedicine(id, dto, currentUser)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a medicine")
    public ResponseEntity<ApiResponse<Void>> deleteMedicine(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        medicineService.deleteMedicine(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Medicine deleted successfully", null));
    }

    @GetMapping("/expired")
    @Operation(summary = "Get expired medicines")
    public ResponseEntity<ApiResponse<List<MedicineDTO>>> getExpiredMedicines() {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                medicineService.getExpiredMedicines(currentUser.getId())));
    }

    @GetMapping("/expiring-soon")
    @Operation(summary = "Get medicines expiring soon")
    public ResponseEntity<ApiResponse<List<MedicineDTO>>> getExpiringSoon(
            @RequestParam(defaultValue = "30") int days) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                medicineService.getExpiringSoon(currentUser.getId(), days)));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock medicines")
    public ResponseEntity<ApiResponse<List<MedicineDTO>>> getLowStockMedicines() {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                medicineService.getLowStockMedicines(currentUser.getId())));
    }

    @PatchMapping("/{id}/quantity")
    @Operation(summary = "Update medicine quantity")
    public ResponseEntity<ApiResponse<MedicineDTO>> updateQuantity(
            @PathVariable Long id,
            @RequestParam int quantity) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Quantity updated",
                medicineService.updateQuantity(id, quantity, currentUser.getId())));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get medicine statistics for current user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMedicineStats() {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                medicineService.getMedicineStats(currentUser.getId())));
    }
}
