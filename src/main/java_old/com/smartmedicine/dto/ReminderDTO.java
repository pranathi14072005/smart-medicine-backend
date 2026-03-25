package com.smartmedicine.dto;

import com.smartmedicine.model.Reminder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderDTO {

    private Long id;

    @NotBlank(message = "Reminder title is required")
    private String title;

    private String notes;

    @NotNull(message = "Frequency is required")
    private Reminder.Frequency frequency;

    private List<LocalTime> reminderTimes;

    private List<Reminder.DayOfWeek> daysOfWeek;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    private Double doseAmount;

    private String doseUnit;

    private Reminder.NotificationType notificationType;

    private boolean active;

    private boolean completed;

    private LocalDateTime lastTakenAt;

    private Integer snoozeMinutes;

    private Long userId;

    private Long medicineId;

    private String medicineName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
