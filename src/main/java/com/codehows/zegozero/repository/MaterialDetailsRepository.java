package com.codehows.zegozero.repository;

import com.codehows.zegozero.entity.Material_details;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialDetailsRepository  extends JpaRepository<Material_details, Integer> {
}
