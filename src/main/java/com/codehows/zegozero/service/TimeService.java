package com.codehows.zegozero.service;


import com.codehows.zegozero.dto.System_time_Dto;

import com.codehows.zegozero.entity.System_time;
import com.codehows.zegozero.repository.SystemTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TimeService {

    private final SystemTimeRepository systemTimeRepository;

    public String getTimeFromDB() {
        // 예시: id가 1인 데이터 가져오기
        System_time timeEntity = systemTimeRepository.findById(1L).orElse(null);

        if (timeEntity != null) {
            // LocalDateTime을 HTML datetime-local 형식으로 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            return timeEntity.getTime().format(formatter);
        } else {
            return "No data found";
        }
    }

    public System_time getDateTimeFromDB() {
        System_time timeEntity = systemTimeRepository.findById(1L).orElse(null);
        return timeEntity;
    }

    public void saveOrUpdate(System_time_Dto systemTimeDto) {
        // 모든 시간을 가져와서 존재하는지 확인
        Optional<System_time> existingTime = systemTimeRepository.findFirstByOrderById();

        if (existingTime.isPresent()) {
            System_time system_time = existingTime.get();
            system_time.setTime(systemTimeDto.getTime());
            systemTimeRepository.save(system_time);
        } else {
            System_time system_time = new System_time();
            system_time.setTime(systemTimeDto.getTime());
            systemTimeRepository.save(system_time);
        }
    }

}
