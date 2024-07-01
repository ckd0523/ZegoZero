package com.codehows.zegozero.controller;

import com.codehows.zegozero.dto.Order_Dto;
import com.codehows.zegozero.dto.System_time_Dto;
import com.codehows.zegozero.entity.System_time;
import com.codehows.zegozero.service.TimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class timeApiController {

    private final TimeService timeService;


    @GetMapping("/time")
    public String getTimeFromDB() {
        return timeService.getTimeFromDB();
    }

    // 시간 저장
    @PostMapping("/timesave")
    public ResponseEntity<?> saveOrUpdateTime(@RequestBody System_time_Dto systemTimeDto) throws IOException {
        timeService.saveOrUpdate(systemTimeDto);
        return ResponseEntity.ok().body("시간이 저장되었습니다.");
    }

}
