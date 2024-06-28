package com.codehows.zegozero.controller;

import com.codehows.zegozero.dto.*;

import com.codehows.zegozero.entity.Material_details;
import com.codehows.zegozero.entity.Orders;
import com.codehows.zegozero.entity.Plans;
import com.codehows.zegozero.entity.Purchase_matarial;
import com.codehows.zegozero.repository.MaterialDetailsRepository;
import com.codehows.zegozero.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;
    private final finishedProductService finishedProductService;
    private final MaterialDetailsService materialDetailsService;

    private static final Logger logger = Logger.getLogger(OrderApiController.class.getName());
    private final PlanService planService;
    private final TimeService timeService;

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

        if (finishedProductService.totalProduct(Orderdata.getProduct_name()) < Orderdata.getUsed_inventory()) {
            return ResponseEntity.badRequest().body("필요한 재고가 부족합니다.");
        }

        if (Orderdata.getUsed_inventory() > 0) {

            finishedProductService.orderProductsave(Orderdata);

        }
        orderService.save(Orderdata);

        orderService.updatePlanOrderId();

        // 원하는 작업 후 데이터를 담은 객체를 반환
        return ResponseEntity.ok().body("Order saved successfully");
    }

    @GetMapping("/progressorder")
    public Map<String, Object> progressApiOrder() throws IOException {

        Map<String, Object> Order_DtoList1 = new HashMap<String, Object>();

        List<Order_Dto> Order_DtoList = orderService.findAllByShippingDateIsNull().stream()
                .map(a -> new Order_Dto(a))
                .collect(Collectors.toList());

        Order_DtoList1.put("data", Order_DtoList);

        // 원하는 작업 후 데이터를 담은 객체를 반환
        return Order_DtoList1;
    }

    @GetMapping("/completedorder")
    public Map<String, Object> completedApiOrder() throws IOException {

        Map<String, Object> Order_DtoList1 = new HashMap<String, Object>();

        List<Order_Dto> Order_DtoList = orderService.findAllByShippingDateIsNotNull().stream()
                .map(a -> new Order_Dto(a))
                .collect(Collectors.toList());

        Order_DtoList1.put("data", Order_DtoList);

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


    @GetMapping("/orderPlan")
    public Map<String, Object> orderPlan() throws IOException {
        Boolean deletable = true;
        Map<String, Object> orderPlan = new HashMap<String, Object>();

        List<Order_Dto> orderPlan1 = orderService.findByDeletable(deletable)
                .stream()
                .map(a -> new Order_Dto(a))
                .collect(Collectors.toList());

        System.out.println("123");

        orderPlan.put("data", orderPlan1);

        System.out.println("12333");
        // 원하는 작업 후 데이터를 담은 객체를 반환
        return orderPlan;
    }


//    @PostMapping("/updateOrderDeletable")
//    public ResponseEntity<String> updateOrderDeletable(@RequestBody OrderUpdateRequest_Dto request) {
//        try {
//            orderService.updateOrderDeletable(request.getOrderIds());
//            return ResponseEntity.ok("Deletable 속성이 업데이트되었습니다.");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업데이트 중 오류 발생");
//        }
//
//    }


    //원자재 구매 테이블에서 orders의 데이터를 가져오기

//    @GetMapping("/delivered")
//    public ResponseEntity<?> delivered() throws IOException {
//
//        //원자재 리포지토리에서 배송중인 값을 찾아서, 가져온 값을 매핑.
//
//
//          String deliveryStatus = "배송중";
//        Map<String, Object> delivered = new HashMap<String, Object>();
//
//        List<responsePurchaseMaterial_Dto> delivered1 = orderService.findByDelivery_status(deliveryStatus)
//                .stream()
//                .map(a ->new responsePurchaseMaterial_Dto(a))
//                .collect(Collectors.toList());
//
//        System.out.println("안녕하세요"+delivered1);
//
//        delivered.put("data",delivered1);
////
////        // 원하는 작업 후 데이터를 담은 객체를 반환
////
//        return new ResponseEntity<>(delivered1, HttpStatus.OK);
//    }


    @GetMapping("/delivered")
    public Map<String, Object> delivered() throws IOException {

        String deliveryStatus = "배송중";
        Map<String, Object> delivered = new HashMap<String, Object>();

        List<responsePurchaseMaterial_Dto> delivered1 = orderService.findByDelivery_status(deliveryStatus)
                .stream()
                .map(a -> new responsePurchaseMaterial_Dto(a))
                .collect(Collectors.toList());

        if(delivered1.isEmpty()){
            responsePurchaseMaterial_Dto dto = new responsePurchaseMaterial_Dto();
            dto.setOrder_id(null);
            dto.setPurchase_matarial_id(null);
            dto.setRaw_material(null);
            dto.setOrder_quantity(null);
            dto.setDelivery_completion_date(null);
            dto.setDelivery_status(null);
            dto.setPurchase_date(null);
        }

        delivered.put("data", delivered1);

        return delivered;
    }

    @PostMapping("savePurchaseMaterial")
    public ResponseEntity<?> savePurchaseMaterial(@RequestBody List<savePurchaseMaterial_Dto> saveList) {

        //deletable로 변환하는 매서드 추가
        Boolean deletable = false;

        for (savePurchaseMaterial_Dto save : saveList) {
            orderService.savePurchaseMaterial(save);

        }
        return ResponseEntity.ok().body("All materials saved successfully");
    }


    //1.발주번호를 바탕으로 '배송중'을 '배송완료'로 변경한다.
    //2.원자재 내역 테이블에 발주번호를 등록한다.
    //3. 원자재 입고량을 구하는 방법- '주문량'(원자재발주tbl)을 가져와 '입고량'(원자재내역tbl)으로 등록한다.
    //4.dto에 현재 날짜를 등록하여 함께 저장한다.
    @PostMapping("deliveryOk")
    public ResponseEntity<?> deliveryOk(@RequestBody Integer[] deliveryOk) {
        //deliveryOk는 발주번호를 가진 배열


        // 배열 길이를 체크하고 각 값을 출력
        if (deliveryOk != null && deliveryOk.length > 0) {

            for (int i = 0; i < deliveryOk.length; i++) {

                orderService.findByPurchase_material_id(deliveryOk[i]); //
            }
        } else {
            return ResponseEntity.badRequest().body("No materials provided");
        }
        return ResponseEntity.ok().body("All materials saved successfully");
    }

    @GetMapping("/showInventory")
    public Map<String, Object> showInventory() throws IOException {

        Map<String, Object> detail = new HashMap<String, Object>();


        //matarial_details 테이블 생성 후 값을 가져오기
        List<Material_details> all = orderService.findAllMaterialDetail();


        System.out.println(all);
        detail.put("data", all);
        System.out.println(all);
        return detail;
    }

//    @GetMapping("/getPackagingData")
//    public List<PackagingData_Dto> getPackagingData() {
//        // 여기에 데이터베이스에서 데이터를 가져오는 로직을 작성합니다.
//         orderService.PackagingData();
//
//
//         return
//    }


//    @GetMapping("/getPackagingData")
//    public ResponseEntity<PackagingData_Dto> getPackagingData() {
//
//        PackagingData_Dto packagingData = orderService.getPackagingData();
//
//
//        System.out.println(packagingData.getPackaging());
//        System.out.println(packagingData.getBox());
//
//        return ResponseEntity.ok().body(packagingData);
//        //박스, 포장지의 현재 개수를 포함한 dto
//    }

    @GetMapping("/getPackagingData")
    public ResponseEntity<PackagingData_Dto> getPackagingData() {

        try {
            PackagingData_Dto packagingData = orderService.getPackagingData();

            if (packagingData == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }

            System.out.println(packagingData.getPackaging());
            System.out.println(packagingData.getBox());

            return ResponseEntity.ok().body(packagingData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/cleaning")
    public String getMaterialDetailsByPurchaseId(@RequestBody CleaningDto cleaningDto) {
        // Debugging information
        System.out.println("Received planId: " + cleaningDto.getPlanId());
        System.out.println("Received shipped_quantity: " + cleaningDto.getShipped_quantity());

        Plans plan = planService.findById(cleaningDto.getPlanId());

        // planId와 shipped_quantity를 가져옵니다.
        Integer planId = cleaningDto.getPlanId();
        Integer shippedQuantity = plan.getPlanned_quantity();
        int material1 = shippedQuantity * 4;
        int material2 = (int) Math.ceil(shippedQuantity * 0.15);
        int material3 = shippedQuantity * 125;
        int material4 = (int) Math.ceil(shippedQuantity * 0.05);
        // 시간
        LocalDateTime time = timeService.getDateTimeFromDB().getTime();

        // planId를 이용하여 관련된 주문을 찾습니다.
        Orders order = planService.getOrderByPlanId(planId);
        List<Purchase_matarial> purchaseMatarials = materialDetailsService.findByOrderId(order.getOrderId());

        List<Material_details> materialDetailsList = new ArrayList<>();



        for (Purchase_matarial purchaseMatarial : purchaseMatarials) {
            Material_details material_details = new Material_details();
            material_details.setPurchase_matarial(purchaseMatarial);
            material_details.setShipped_date(time);

            switch (purchaseMatarial.getRaw_material()) {
                case "양배추":
                case "흑마늘":
                    material_details.setShipped_quantity(material1);
                    break;
                case "꿀":
                    material_details.setShipped_quantity(material2);
                    break;
                case "석류":
                case "매실":
                    material_details.setShipped_quantity(material3);
                    break;
                case "콜라겐":
                    material_details.setShipped_quantity(material4);
                    break;
                default:
                    // 만약 예상치 못한 값이 들어왔을 경우 기본값을 설정할 수도 있습니다.
                    material_details.setShipped_quantity(0);
                    break;
            }

            // 리스트에 추가
            materialDetailsList.add(material_details);
        }

                // 생성된 Material_details 객체들을 데이터베이스에 저장합니다.
                for (Material_details details : materialDetailsList) {
                    materialDetailsService.saveMaterialDetails(details);
                }

        // 반환할 값이 있다면 설정해줍니다.
                if (materialDetailsList.isEmpty()) {
                    return "Not Found";
                } else {
                    return "Success";
                }
    }

    @PostMapping("PackOrder")
    public ResponseEntity<?> PackOrder() {
        orderService.packagingOrder();

        return ResponseEntity.ok().body("주문 완료");
    }





}


