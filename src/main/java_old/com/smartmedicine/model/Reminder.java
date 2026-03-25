package com.smartmedicine.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false)
    private Frequency frequency;

    @ElementCollection
    @CollectionTable(name = "reminder_times", joinColumns = @JoinColumn(name = "reminder_id"))
    @Column(name = "reminder_time")
    @Builder.Default
    private List<LocalTime> reminderTimes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "reminder_days", joinColumns = @JoinColumn(name = "reminder_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    @Builder.Default
    private List<DayOfWeek> daysOfWeek = new ArrayList<>();

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "dose_amount")
    private Double doseAmount;

    @Column(name = "dose_unit")
    private String doseUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    @Builder.Default
    private NotificationType notificationType = NotificationType.PUSH;

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    @Column(name = "is_completed")
    @Builder.Default
    private boolean completed = false;

    @Column(name = "last_taken_at")
    private LocalDateTime lastTakenAt;

    @Column(name = "snooze_minutes")
    @Builder.Default
    private Integer snoozeMinutes = 10;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Frequency {
        ONCE, DAILY, TWICE_DAILY, THREE_TIMES_DAILY, WEEKLY, MONTHLY, CUSTOM
    }

    public enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    public enum NotificationType {
        PUSH, EMAIL, SMS, ALL
    }
}
