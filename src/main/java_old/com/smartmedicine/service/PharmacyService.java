package com.smartmedicine.service;

import com.smartmedicine.dto.PharmacyDTO;
import com.smartmedicine.exception.BadRequestException;
import com.smartmedicine.exception.ResourceNotFoundException;
import com.smartmedicine.model.Pharmacy;
import com.smartmedicine.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PharmacyService {

    private final PharmacyRepository pharmacyRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public PharmacyDTO createPharmacy(PharmacyDTO dto) {
        if (dto.getLicenseNumber() != null &&
                pharmacyRepository.existsByLicenseNumber(dto.getLicenseNumber())) {
            throw new BadRequestException(
                    "Pharmacy with license number '" + dto.getLicenseNumber() + "' already exists");
        }

        Pharmacy pharmacy = modelMapper.map(dto, Pharmacy.class);
        pharmacy.setId(null);
        pharmacy.setStatus(Pharmacy.PharmacyStatus.ACTIVE);
        pharmacy = pharmacyRepository.save(pharmacy);
        log.info("Pharmacy created: {}", pharmacy.getName());
        return modelMapper.map(pharmacy, PharmacyDTO.class);
    }

    public PharmacyDTO getPharmacyById(Long id) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", "id", id));
        return modelMapper.map(pharmacy, PharmacyDTO.class);
    }

    public Page<PharmacyDTO> getAllActivePharmacies(Pageable pageable) {
        return pharmacyRepository.findByStatus(Pharmacy.PharmacyStatus.ACTIVE, pageable)
                .map(p -> modelMapper.map(p, PharmacyDTO.class));
    }

    public Page<PharmacyDTO> getAllPharmacies(Pageable pageable) {
        return pharmacyRepository.findAll(pageable)
                .map(p -> modelMapper.map(p, PharmacyDTO.class));
    }

    public List<PharmacyDTO> searchPharmacies(String keyword) {
        return pharmacyRepository.searchByKeyword(keyword)
                .stream()
                .map(p -> modelMapper.map(p, PharmacyDTO.class))
                .collect(Collectors.toList());
    }

    public List<PharmacyDTO> getNearbyPharmacies(Double lat, Double lng, Double radiusKm) {
        return pharmacyRepository.findNearbyPharmacies(lat, lng, radiusKm)
                .stream()
                .limit(20)
                .map(pharmacy -> {
                    PharmacyDTO dto = modelMapper.map(pharmacy, PharmacyDTO.class);
                    dto.setDistanceKm(calculateDistance(lat, lng,
                            pharmacy.getLatitude(), pharmacy.getLongitude()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<PharmacyDTO> getPharmaciesWithDelivery() {
        return pharmacyRepository.findPharmaciesWithDelivery()
                .stream()
                .map(p -> modelMapper.map(p, PharmacyDTO.class))
                .collect(Collectors.toList());
    }

    public List<PharmacyDTO> get24HourPharmacies() {
        return pharmacyRepository.find24HourPharmacies()
                .stream()
                .map(p -> modelMapper.map(p, PharmacyDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public PharmacyDTO updatePharmacy(Long id, PharmacyDTO dto) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", "id", id));

        dto.setId(id);
        modelMapper.map(dto, pharmacy);
        pharmacy = pharmacyRepository.save(pharmacy);
        log.info("Pharmacy updated: {}", pharmacy.getName());
        return modelMapper.map(pharmacy, PharmacyDTO.class);
    }

    @Transactional
    public void deletePharmacy(Long id) {
        if (!pharmacyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pharmacy", "id", id);
        }
        pharmacyRepository.deleteById(id);
        log.info("Pharmacy {} deleted", id);
    }

    @Transactional
    public PharmacyDTO updateStatus(Long id, Pharmacy.PharmacyStatus status) {
        Pharmacy pharmacy = pharmacyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacy", "id", id));
        pharmacy.setStatus(status);
        return modelMapper.map(pharmacyRepository.save(pharmacy), PharmacyDTO.class);
    }

    private Double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 100.0) / 100.0;
    }
}