package com.smartmedicine.repository;

import com.smartmedicine.model.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    List<Pharmacy> findByCity(String city);

    List<Pharmacy> findByStatus(Pharmacy.PharmacyStatus status);

    Page<Pharmacy> findByStatus(Pharmacy.PharmacyStatus status, Pageable pageable);

    @Query("SELECT p FROM Pharmacy p WHERE p.name LIKE %:keyword% OR p.city LIKE %:keyword% OR p.address LIKE %:keyword%")
    List<Pharmacy> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Pharmacy p WHERE p.hasDelivery = true AND p.status = 'ACTIVE'")
    List<Pharmacy> findPharmaciesWithDelivery();

    @Query("SELECT p FROM Pharmacy p WHERE p.open24Hours = true AND p.status = 'ACTIVE'")
    List<Pharmacy> find24HourPharmacies();

    /**
     * Find pharmacies within a radius using the Haversine formula.
     * NOTE: :limit parameter removed — use Pageable or slice in service layer.
     */
    @Query(value = """
            SELECT *, (6371 * acos(
                cos(radians(:lat)) * cos(radians(latitude)) *
                cos(radians(longitude) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(latitude))
            )) AS distance
            FROM pharmacies
            WHERE status = 'ACTIVE'
            HAVING distance < :radiusKm
            ORDER BY distance
            """, nativeQuery = true)
    List<Pharmacy> findNearbyPharmacies(@Param("lat") Double latitude,
                                         @Param("lng") Double longitude,
                                         @Param("radiusKm") Double radiusKm);

    boolean existsByLicenseNumber(String licenseNumber);
}