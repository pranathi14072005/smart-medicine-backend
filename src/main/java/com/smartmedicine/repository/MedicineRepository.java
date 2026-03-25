package com.smartmedicine.repository;

import com.smartmedicine.model.Medicine;
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
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    List<Medicine> findByUserId(Long userId);

    Page<Medicine> findByUserId(Long userId, Pageable pageable);

    Optional<Medicine> findByIdAndUserId(Long id, Long userId);

    List<Medicine> findByUserIdAndStatus(Long userId, Medicine.MedicineStatus status);

    @Query("SELECT m FROM Medicine m WHERE m.user.id = :userId AND m.name LIKE %:name%")
    List<Medicine> searchByNameAndUserId(@Param("name") String name, @Param("userId") Long userId);

    @Query("SELECT m FROM Medicine m WHERE m.user.id = :userId AND m.expiryDate < :date")
    List<Medicine> findExpiredMedicines(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT m FROM Medicine m WHERE m.user.id = :userId AND m.expiryDate BETWEEN :today AND :futureDate")
    List<Medicine> findExpiringSoon(@Param("userId") Long userId,
                                    @Param("today") LocalDate today,
                                    @Param("futureDate") LocalDate futureDate);

    @Query("SELECT m FROM Medicine m WHERE m.user.id = :userId AND m.quantity <= m.lowStockThreshold")
    List<Medicine> findLowStockMedicines(@Param("userId") Long userId);

    @Query("SELECT m FROM Medicine m WHERE m.user.id = :userId AND m.medicineType = :type")
    List<Medicine> findByUserIdAndType(@Param("userId") Long userId,
                                        @Param("type") Medicine.MedicineType type);

    boolean existsByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);

    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.user.id = :userId AND m.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Medicine.MedicineStatus status);
}
