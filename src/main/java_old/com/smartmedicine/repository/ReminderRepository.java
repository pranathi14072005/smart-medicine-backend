package com.smartmedicine.repository;

import com.smartmedicine.model.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByUserId(Long userId);

    Page<Reminder> findByUserId(Long userId, Pageable pageable);

    Optional<Reminder> findByIdAndUserId(Long id, Long userId);

    List<Reminder> findByUserIdAndActive(Long userId, boolean active);

    List<Reminder> findByMedicineId(Long medicineId);

    @Query("SELECT r FROM Reminder r WHERE r.user.id = :userId AND r.active = true AND r.startDate <= :today AND (r.endDate IS NULL OR r.endDate >= :today)")
    List<Reminder> findActiveRemindersForToday(@Param("userId") Long userId, @Param("today") LocalDate today);

    @Query("SELECT r FROM Reminder r WHERE r.active = true AND r.startDate <= :today AND (r.endDate IS NULL OR r.endDate >= :today)")
    List<Reminder> findAllActiveRemindersForToday(@Param("today") LocalDate today);

    @Query("SELECT COUNT(r) FROM Reminder r WHERE r.user.id = :userId AND r.active = true")
    long countActiveRemindersByUserId(@Param("userId") Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
