package com.codehows.zegozero.controller;

import com.codehows.zegozero.dto.Order_Dto;
import com.codehows.zegozero.entity.Orders;
import com.codehows.zegozero.service.OrderService;
import com.codehows.zegozero.service.finishedProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;
    private final finishedProductService finishedProductService;
    private static final Logger logger = Logger.getLogger(OrderApiController.class.getName());

    @PostMapping("/order")
    public ResponseEntity<?> registApiOrder(@Valid @RequestBody Order_Dto Orderdata, BindingResult bindingResult) throws IOException {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append("; "));
            return ResponseEntity.badRequest().body(errorMessage.toString());
        }

        if (Orderdata.getProduction_quantity() < 0) {
            return ResponseEntity.badRequest().body("주문수량보다 재고가 많을수는 없습니다.");
        }

        if(finishedProductService.totalProduct(Orderdata.getProduct_name()) < Orderdata.getUsed_inventory()){
            return ResponseEntity.badRequest().body("필요한 재고가 부족합니다.");
        }

        if (Orderdata.getUsed_inventory() > 0) {

            finishedProductService.orderProductsave(Orderdata);

        }
        orderService.save(Orderdata);

        // 원하는 작업 후 데이터를 담은 객체를 반환
        return ResponseEntity.ok().body("Order saved successfully");
    }

    @GetMapping("/progressorder")
    public Map<String, Object> progressApiOrder() throws IOException {

        Map<String, Object> Order_DtoList1 = new HashMap<String, Object>();

        List<Order_Dto> Order_DtoList = orderService.findAllByShippingDateIsNull().stream()
                        .map(a ->new Order_Dto(a))
                        .collect(Collectors.toList());

        Order_DtoList1.put("data",Order_DtoList);

        // 원하는 작업 후 데이터를 담은 객체를 반환
        return Order_DtoList1;
    }

    @GetMapping("/completedorder")
    public Map<String, Object> completedApiOrder() throws IOException {

        Map<String, Object> Order_DtoList1 = new HashMap<String, Object>();

        List<Order_Dto> Order_DtoList = orderService.findAllByShippingDateIsNotNull().stream()
                .map(a ->new Order_Dto(a))
                .collect(Collectors.toList());

        Order_DtoList1.put("data",Order_DtoList);

        // 원하는 작업 후 데이터를 담은 객체를 반환
        return Order_DtoList1;
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteOrder(@RequestBody Map<String, Integer> request) {
        Integer orderId = request.get("order_id");
        if (orderId != null) {
            // orderId를 사용하여 삭제 로직을 처리
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok().body("Order deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Order ID is missing");
        }
    }




}
