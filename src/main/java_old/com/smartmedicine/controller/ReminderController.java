package com.smartmedicine.controller;

import com.smartmedicine.dto.ApiResponse;
import com.smartmedicine.dto.ReminderDTO;
import com.smartmedicine.model.User;
import com.smartmedicine.service.AuthService;
import com.smartmedicine.service.ReminderService;
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

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
@Tag(name = "Reminders", description = "Medication reminder APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class ReminderController {

    private final ReminderService reminderService;
    private final AuthService authService;

    @PostMapping
    @Operation(summary = "Create a new reminder")
    public ResponseEntity<ApiResponse<ReminderDTO>> createReminder(
            @Valid @RequestBody ReminderDTO dto) {
        User currentUser = authService.getCurrentUser();
        ReminderDTO created = reminderService.createReminder(dto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reminder created successfully", created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reminder by ID")
    public ResponseEntity<ApiResponse<ReminderDTO>> getReminder(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                reminderService.getReminderById(id, currentUser.getId())));
    }

    @GetMapping
    @Operation(summary = "Get all reminders for current user")
    public ResponseEntity<ApiResponse<Page<ReminderDTO>>> getMyReminders(Pageable pageable) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                reminderService.getUserReminders(currentUser.getId(), pageable)));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active reminders")
    public ResponseEntity<ApiResponse<List<ReminderDTO>>> getActiveReminders() {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                reminderService.getActiveReminders(currentUser.getId())));
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's reminders")
    public ResponseEntity<ApiResponse<List<ReminderDTO>>> getTodayReminders() {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                reminderService.getTodayReminders(currentUser.getId())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a reminder")
    public ResponseEntity<ApiResponse<ReminderDTO>> updateReminder(
            @PathVariable Long id,
            @Valid @RequestBody ReminderDTO dto) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Reminder updated",
                reminderService.updateReminder(id, dto, currentUser)));
    }

    @PostMapping("/{id}/taken")
    @Operation(summary = "Mark a dose as taken")
    public ResponseEntity<ApiResponse<ReminderDTO>> markAsTaken(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Dose marked as taken",
                reminderService.markAsTaken(id, currentUser.getId())));
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle reminder active/inactive")
    public ResponseEntity<ApiResponse<ReminderDTO>> toggleReminder(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success(
                reminderService.toggleActive(id, currentUser.getId())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reminder")
    public ResponseEntity<ApiResponse<Void>> deleteReminder(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        reminderService.deleteReminder(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Reminder deleted", null));
    }
}
