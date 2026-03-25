package com.smartmedicine.service;

import com.smartmedicine.dto.ReminderDTO;
import com.smartmedicine.exception.ResourceNotFoundException;
import com.smartmedicine.model.Medicine;
import com.smartmedicine.model.Reminder;
import com.smartmedicine.model.User;
import com.smartmedicine.repository.MedicineRepository;
import com.smartmedicine.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final MedicineRepository medicineRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ReminderDTO createReminder(ReminderDTO dto, User currentUser) {
        Reminder reminder = new Reminder();
        reminder.setTitle(dto.getTitle());
        reminder.setNotes(dto.getNotes());
        reminder.setFrequency(dto.getFrequency());
        reminder.setReminderTimes(dto.getReminderTimes());
        reminder.setDaysOfWeek(dto.getDaysOfWeek());
        reminder.setStartDate(dto.getStartDate());
        reminder.setEndDate(dto.getEndDate());
        reminder.setDoseAmount(dto.getDoseAmount());
        reminder.setDoseUnit(dto.getDoseUnit());
        reminder.setNotificationType(dto.getNotificationType() != null
                ? dto.getNotificationType() : Reminder.NotificationType.PUSH);
        reminder.setSnoozeMinutes(dto.getSnoozeMinutes() != null ? dto.getSnoozeMinutes() : 10);
        reminder.setActive(true);
        reminder.setUser(currentUser);

        if (dto.getMedicineId() != null) {
            Medicine medicine = medicineRepository.findByIdAndUserId(
                    dto.getMedicineId(), currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Medicine", "id", dto.getMedicineId()));
            reminder.setMedicine(medicine);
        }

        reminder = reminderRepository.save(reminder);
        log.info("Reminder created: {} for user: {}", reminder.getTitle(), currentUser.getUsername());
        return toDTO(reminder);
    }

    public ReminderDTO getReminderById(Long id, Long userId) {
        Reminder reminder = reminderRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", id));
        return toDTO(reminder);
    }

    public Page<ReminderDTO> getUserReminders(Long userId, Pageable pageable) {
        return reminderRepository.findByUserId(userId, pageable).map(this::toDTO);
    }

    public List<ReminderDTO> getActiveReminders(Long userId) {
        return reminderRepository.findByUserIdAndActive(userId, true)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ReminderDTO> getTodayReminders(Long userId) {
        return reminderRepository.findActiveRemindersForToday(userId, LocalDate.now())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public ReminderDTO updateReminder(Long id, ReminderDTO dto, User currentUser) {
        Reminder reminder = reminderRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", id));

        reminder.setTitle(dto.getTitle());
        reminder.setNotes(dto.getNotes());
        reminder.setFrequency(dto.getFrequency());
        if (dto.getReminderTimes() != null) reminder.setReminderTimes(dto.getReminderTimes());
        if (dto.getDaysOfWeek() != null) reminder.setDaysOfWeek(dto.getDaysOfWeek());
        if (dto.getStartDate() != null) reminder.setStartDate(dto.getStartDate());
        reminder.setEndDate(dto.getEndDate());
        if (dto.getDoseAmount() != null) reminder.setDoseAmount(dto.getDoseAmount());
        if (dto.getDoseUnit() != null) reminder.setDoseUnit(dto.getDoseUnit());
        if (dto.getNotificationType() != null) reminder.setNotificationType(dto.getNotificationType());
        if (dto.getSnoozeMinutes() != null) reminder.setSnoozeMinutes(dto.getSnoozeMinutes());

        if (dto.getMedicineId() != null) {
            Medicine medicine = medicineRepository.findByIdAndUserId(
                    dto.getMedicineId(), currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Medicine", "id", dto.getMedicineId()));
            reminder.setMedicine(medicine);
        }

        reminder = reminderRepository.save(reminder);
        return toDTO(reminder);
    }

    @Transactional
    public ReminderDTO markAsTaken(Long id, Long userId) {
        Reminder reminder = reminderRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", id));
        reminder.setLastTakenAt(LocalDateTime.now());
        reminder = reminderRepository.save(reminder);
        log.info("Reminder {} marked as taken by user {}", id, userId);
        return toDTO(reminder);
    }

    @Transactional
    public ReminderDTO toggleActive(Long id, Long userId) {
        Reminder reminder = reminderRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", id));
        reminder.setActive(!reminder.isActive());
        return toDTO(reminderRepository.save(reminder));
    }

    @Transactional
    public void deleteReminder(Long id, Long userId) {
        if (!reminderRepository.existsByIdAndUserId(id, userId)) {
            throw new ResourceNotFoundException("Reminder", "id", id);
        }
        reminderRepository.deleteById(id);
        log.info("Reminder {} deleted by user {}", id, userId);
    }

    /**
     * Scheduled task: check for due reminders every minute
     */
    @Scheduled(fixedDelay = 60000)
    public void checkDueReminders() {
        List<Reminder> activeReminders =
                reminderRepository.findAllActiveRemindersForToday(LocalDate.now());
        log.debug("Checking {} active reminders for notifications", activeReminders.size());
        // Notification dispatch logic would go here (push/email/SMS)
    }

    private ReminderDTO toDTO(Reminder reminder) {
        ReminderDTO dto = new ReminderDTO();
        dto.setId(reminder.getId());
        dto.setTitle(reminder.getTitle());
        dto.setNotes(reminder.getNotes());
        dto.setFrequency(reminder.getFrequency());
        dto.setReminderTimes(reminder.getReminderTimes());
        dto.setDaysOfWeek(reminder.getDaysOfWeek());
        dto.setStartDate(reminder.getStartDate());
        dto.setEndDate(reminder.getEndDate());
        dto.setDoseAmount(reminder.getDoseAmount());
        dto.setDoseUnit(reminder.getDoseUnit());
        dto.setNotificationType(reminder.getNotificationType());
        dto.setActive(reminder.isActive());
        dto.setCompleted(reminder.isCompleted());
        dto.setLastTakenAt(reminder.getLastTakenAt());
        dto.setSnoozeMinutes(reminder.getSnoozeMinutes());
        dto.setUserId(reminder.getUser().getId());
        dto.setCreatedAt(reminder.getCreatedAt());
        dto.setUpdatedAt(reminder.getUpdatedAt());
        if (reminder.getMedicine() != null) {
            dto.setMedicineId(reminder.getMedicine().getId());
            dto.setMedicineName(reminder.getMedicine().getName());
        }
        return dto;
    }
}
