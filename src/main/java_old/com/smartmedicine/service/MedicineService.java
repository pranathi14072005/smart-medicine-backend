package com.smartmedicine.service;

import com.smartmedicine.dto.MedicineDTO;
import com.smartmedicine.exception.AccessDeniedException;
import com.smartmedicine.exception.ResourceNotFoundException;
import com.smartmedicine.model.Medicine;
import com.smartmedicine.model.User;
import com.smartmedicine.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public MedicineDTO addMedicine(MedicineDTO dto, User currentUser) {
        Medicine medicine = modelMapper.map(dto, Medicine.class);
        medicine.setUser(currentUser);
        medicine.setId(null);

        // Auto-set status based on conditions
        updateMedicineStatus(medicine);

        medicine = medicineRepository.save(medicine);
        log.info("Medicine added: {} by user: {}", medicine.getName(), currentUser.getUsername());
        return toDTO(medicine);
    }

    public MedicineDTO getMedicineById(Long id, Long userId) {
        Medicine medicine = medicineRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", id));
        return toDTO(medicine);
    }

    public Page<MedicineDTO> getUserMedicines(Long userId, Pageable pageable) {
        return medicineRepository.findByUserId(userId, pageable).map(this::toDTO);
    }

    public List<MedicineDTO> searchMedicines(String name, Long userId) {
        return medicineRepository.searchByNameAndUserId(name, userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public MedicineDTO updateMedicine(Long id, MedicineDTO dto, User currentUser) {
        Medicine medicine = medicineRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", id));

        modelMapper.map(dto, medicine);
        medicine.setId(id);
        medicine.setUser(currentUser);
        updateMedicineStatus(medicine);

        medicine = medicineRepository.save(medicine);
        log.info("Medicine updated: {} by user: {}", medicine.getName(), currentUser.getUsername());
        return toDTO(medicine);
    }

    @Transactional
    public void deleteMedicine(Long id, Long userId) {
        if (!medicineRepository.existsByIdAndUserId(id, userId)) {
            throw new ResourceNotFoundException("Medicine", "id", id);
        }
        medicineRepository.deleteById(id);
        log.info("Medicine {} deleted by user {}", id, userId);
    }

    public List<MedicineDTO> getExpiredMedicines(Long userId) {
        return medicineRepository.findExpiredMedicines(userId, LocalDate.now())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<MedicineDTO> getExpiringSoon(Long userId, int daysAhead) {
        return medicineRepository.findExpiringSoon(userId, LocalDate.now(),
                LocalDate.now().plusDays(daysAhead))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<MedicineDTO> getLowStockMedicines(Long userId) {
        return medicineRepository.findLowStockMedicines(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public MedicineDTO updateQuantity(Long id, int quantity, Long userId) {
        Medicine medicine = medicineRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Medicine", "id", id));
        medicine.setQuantity(quantity);
        updateMedicineStatus(medicine);
        return toDTO(medicineRepository.save(medicine));
    }

    public Map<String, Object> getMedicineStats(Long userId) {
        long total = medicineRepository.countByUserId(userId);
        long active = medicineRepository.countByUserIdAndStatus(userId, Medicine.MedicineStatus.ACTIVE);
        long expired = medicineRepository.findExpiredMedicines(userId, LocalDate.now()).size();
        long lowStock = medicineRepository.findLowStockMedicines(userId).size();

        return Map.of(
                "total", total,
                "active", active,
                "expired", expired,
                "lowStock", lowStock,
                "expiringSoon", medicineRepository
                        .findExpiringSoon(userId, LocalDate.now(), LocalDate.now().plusDays(30)).size()
        );
    }

    private void updateMedicineStatus(Medicine medicine) {
        if (medicine.isExpired()) {
            medicine.setStatus(Medicine.MedicineStatus.EXPIRED);
        } else if (medicine.getQuantity() != null && medicine.getQuantity() == 0) {
            medicine.setStatus(Medicine.MedicineStatus.OUT_OF_STOCK);
        } else if (medicine.isLowStock()) {
            medicine.setStatus(Medicine.MedicineStatus.LOW_STOCK);
        } else {
            medicine.setStatus(Medicine.MedicineStatus.ACTIVE);
        }
    }

    private MedicineDTO toDTO(Medicine medicine) {
        MedicineDTO dto = modelMapper.map(medicine, MedicineDTO.class);
        dto.setUserId(medicine.getUser().getId());
        dto.setUsername(medicine.getUser().getUsername());
        dto.setExpired(medicine.isExpired());
        dto.setLowStock(medicine.isLowStock());
        return dto;
    }
}
