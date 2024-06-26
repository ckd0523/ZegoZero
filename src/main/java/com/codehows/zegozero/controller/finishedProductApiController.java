package com.codehows.zegozero.controller;

import com.codehows.zegozero.dto.Finished_product_management_Dto;
import com.codehows.zegozero.dto.Order_Dto;
import com.codehows.zegozero.dto.Shipment_management_dto;
import com.codehows.zegozero.service.OrderService;
import com.codehows.zegozero.service.finishedProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class finishedProductApiController {

    private final finishedProductService productService;
    private final OrderService orderService;

    // 입고
    @PostMapping("/receive")
    public ResponseEntity<?> receive(@RequestBody Finished_product_management_Dto finishedProduct) throws IOException {

        orderService.update2(finishedProduct);
        productService.receivesave(finishedProduct);
        return ResponseEntity.ok().body("saved successfully");
    };

    // 출고
    @PostMapping("/shipping")
    public ResponseEntity<?> shipping(@RequestBody Shipment_management_dto shippingProduct) throws IOException {

        orderService.update(shippingProduct);
        productService.shippingsave(shippingProduct);
        return ResponseEntity.ok().body("saved successfully");

    };

    // 전체
    @GetMapping("/finish1")
    public Map<String, Object> finish1() throws IOException {

        Map<String, Object> Finish_DtoList1 = new HashMap<String, Object>();

        List<Finished_product_management_Dto> finishedProductManagementDto = productService.findAll().stream()
                .map(a ->new Finished_product_management_Dto(a))
                .collect(Collectors.toList());

        Finish_DtoList1.put("data",finishedProductManagementDto);

        return Finish_DtoList1;
    }

    // 입고
    @GetMapping("/finish2")
    public Map<String, Object> finish2() throws IOException {
        Map<String, Object> Finish_DtoList1 = new HashMap<String, Object>();

        List<Finished_product_management_Dto> finishedProductManagementDto = productService.findAllWithReceivedDateNotNull().stream()
                .map(a ->new Finished_product_management_Dto(a))
                .collect(Collectors.toList());

        Finish_DtoList1.put("data",finishedProductManagementDto);

        return Finish_DtoList1;

    }

    // 출고
    @GetMapping("/finish3")
    public Map<String, Object> finish3() throws IOException {
        Map<String, Object> Finish_DtoList1 = new HashMap<String, Object>();

        List<Finished_product_management_Dto> finishedProductManagementDto = productService.findAllWithShippedDateNotNull().stream()
                .map(a ->new Finished_product_management_Dto(a))
                .collect(Collectors.toList());

        Finish_DtoList1.put("data",finishedProductManagementDto);

        return Finish_DtoList1;

    }


}
