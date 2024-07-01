package com.codehows.zegozero.service;


import com.codehows.zegozero.entity.Material_details;
import com.codehows.zegozero.entity.Purchase_matarial;
import com.codehows.zegozero.repository.MaterialDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MaterialDetailsService {

    private final MaterialDetailsRepository materialDetailsRepository;

    public Optional<Material_details> findByPurchaseId(Integer id) {
        return Optional.ofNullable(materialDetailsRepository.findByPurchaseId(id));
    }

    public List<Purchase_matarial> findByOrderId(Integer orderId) {
        return materialDetailsRepository.findByOrderId(orderId);
    }

    public Material_details saveMaterialDetails(Material_details materialDetails) {
        return materialDetailsRepository.save(materialDetails);
    }



}
