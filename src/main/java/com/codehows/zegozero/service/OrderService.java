package com.codehows.zegozero.service;

import com.codehows.zegozero.dto.*;
import com.codehows.zegozero.entity.Material_details;
import com.codehows.zegozero.entity.Orders;
import com.codehows.zegozero.entity.Purchase_matarial;
import com.codehows.zegozero.repository.*;
import jakarta.persistence.EntityNotFoundException;
import com.codehows.zegozero.entity.Plans;
import com.codehows.zegozero.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final PurchaseMatarialRepository purchaseMatarialRepository;
    private final MaterialDetailsRepository materialDetailsRepository;

    private final TimeService timeService;
    private final PlanService planService;
    private final PlansRepository plansRepository;

    public void save(Order_Dto Orderdata) {
        int maxPlanId = plansRepository.getMaxPlanId();
        LocalDateTime date = timeService.getDateTimeFromDB().getTime();
        Plans plans = plansRepository.findByPlanId(maxPlanId);

        LocalDateTime expectedShippingDate = plans.getCompletion_date();

        Orders orders = new Orders();
        orders.setProduct_name(Orderdata.getProduct_name());
        orders.setQuantity(Orderdata.getQuantity());
        orders.setUsed_inventory(Orderdata.getUsed_inventory());
        orders.setProduction_quantity(Orderdata.getProduction_quantity());
        orders.setOrder_date(date);
        orders.setExpected_shipping_date(expectedShippingDate);
        orders.setCustomer_name(Orderdata.getCustomer_name());
        orders.setDelivery_address(Orderdata.getDelivery_address());
        orders.setDeletable(true);
        orders.setDelivery_available(false);

        ordersRepository.save(orders);
    }

    public void updatePlanOrderId(){
        int maxOrderId = ordersRepository.findMaxOrderId();
        List<Plans> plansWithoutOrder = plansRepository.findAllByOrderIsNull();

        for (Plans plan : plansWithoutOrder) {
            plan.setOrder(ordersRepository.findById(maxOrderId).orElse(null));
            plansRepository.save(plan);
        }
    }

    public Orders findById(Integer id) {
        return ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));
    }

    public List<Orders> findAll() {
        return ordersRepository.findAll();
    }

    // shipping_date가 null인 Orders를 찾는 메서드
    public List<Orders> findAllByShippingDateIsNull() {
        return ordersRepository.findAllByShippingDateIsNull();
    }

    // shipping_date가 null이 아닌 Orders를 찾는 메서드
    public List<Orders> findAllByShippingDateIsNotNull() {
        return ordersRepository.findAllByShippingDateIsNotNull();
    }

    //게시글 삭제
    @Transactional
    public void deleteOrder(Integer order_id) {
        Orders order = ordersRepository.findById(order_id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id: " + order_id));
        ordersRepository.delete(order);
    }

    // 출하날짜를 수정후 저장
    public void update(Shipment_management_dto shippingProduct) {
        Optional<Orders> optionalOrder = ordersRepository.findById(shippingProduct.getOrder_id());

        LocalDateTime date = timeService.getDateTimeFromDB().getTime();

        if (optionalOrder.isPresent()) {
            Orders order = optionalOrder.get();

            // 수정할 필드만 설정
            order.setShipping_date(date);

            // 저장
            ordersRepository.save(order);
        } else {
            throw new RuntimeException("Order not found with id");
        }
    }

    // 출하가능하도록 변경
    public void update2(Finished_product_management_Dto finishedProductManagementDto) {
        Optional<Orders> optionalOrder = ordersRepository.findById(finishedProductManagementDto.getOrder_id());

        if (optionalOrder.isPresent()) {
            Orders order = optionalOrder.get();

            // 수정할 필드만 설정
            order.setDelivery_available(true);

            // 저장
            ordersRepository.save(order);
        } else {
            throw new RuntimeException("Order not found with id");
        }

    }

    public List<Orders> findByDeletable(Boolean deletable) {
        return ordersRepository.findByDeletable(deletable);
    }

    public List<Purchase_matarial> findByDelivery_status(String deliveryStatus) {

        return purchaseMatarialRepository.findByDeliveryStatusJPQL(deliveryStatus);
    }


    public void savePurchaseMaterial(savePurchaseMaterial_Dto saveRequest) {
        Orders orders = ordersRepository.findById(saveRequest.getOrder_id())
                .orElseThrow(EntityNotFoundException::new);
        orders.setDeletable(false);
        purchaseMatarialRepository.save(saveRequest.toEntity(orders));
    }


    public void findByPurchase_material_id(Integer Purchase_material_id) {

        //발주번호로 데이터를 조회하고, '배송중'->'배송 완료'로 변경하여 저장
        String deliveryOk = "배송완료";
        Purchase_matarial purchaseMatarial = purchaseMatarialRepository.findByPurchaseMaterialId(Purchase_material_id);
        purchaseMatarial.setDelivery_status(deliveryOk);
        purchaseMatarialRepository.save(purchaseMatarial);

        //발주번호로 데이터를 조회한 값의 '주문량'을 가져와 '입고량'으로 등록한다.
        //발주번호로 조회한 원자재발주tbl을 연관관계 매핑된 필드에 등록한다.
        //시각을 할당한다.
        //db에 저장한다.
        LocalDateTime date = timeService.getDateTimeFromDB().getTime();

        Material_details materialDetails = new Material_details();
        materialDetails.setReceived_quantity(purchaseMatarial.getOrder_quantity());
        materialDetails.setPurchase_matarial(purchaseMatarial);
        materialDetails.setReceived_date(date);
        materialDetailsRepository.save(materialDetails);

    }

    public List<Material_details> findAllMaterialDetail() {
        return materialDetailsRepository.findAll();
    }


//    //디티오로 보내
//    public Material_details PackagingData() {
//Integer[] list;
//
//        //원자재명이 박스, 포장지인 데이터를 가져와 발주번호를 추출한다.
//        //해당 발주번호 중 출고 가장 최근의 입고량에서
//
//        List<Purchase_matarial> pack = purchaseMatarialRepository.findByRawMaterial("포장지");
//        for(int i=0;i<=pack.toArray().length;i++){
//            list[i] = pack[i].getPurchase_Material_Id;
//
//        }
//
//        //pack의 발주번호를 모두 추출
//        //발주번호로 원자재내역 테이블에서 입고량과 출고량을 모두 더한 값을 도출
//
//
//
//
//        materialDetailsRepository.findBy
//
//        return;
//    }

    public PackagingData_Dto getPackagingData() {

        PackagingData_Dto dto = new PackagingData_Dto();



        String boxName="박스";

        int usableBox=0;// 사용가능한 박스
        int usedBox=0;//사용된 박스
        int stockOfBox=0;//현재 박스 재고

        List<Purchase_matarial> Box = purchaseMatarialRepository.findByRawMaterial(boxName);

        // Order ID를 저장할 리스트
        List<Integer> purchaseIds  = new ArrayList<>();

        for (Purchase_matarial pm : Box) {
            purchaseIds.add(pm.getPurchase_matarial_id());
        }

        // 각 purchaseId의 값을 모두 더하기
        for (Integer purchaseId : purchaseIds) {
           Material_details materialDetails= materialDetailsRepository.findByPurchaseId(purchaseId);

                usableBox += materialDetails.getReceived_quantity();
                usedBox += materialDetails.getShipped_quantity();


        }

        stockOfBox= usableBox-usedBox;
        dto.setBox(String.valueOf(stockOfBox));


        String packName="포장지";

        int usablePack=0;// 사용가능한 포장지
        int usedPack=0;//사용된 포장지
        int stockOfPack=0;//현재 포장지 재고


        List<Purchase_matarial> Pack = purchaseMatarialRepository.findByRawMaterial(packName);

        // Order ID를 저장할 리스트
        List<Integer> purchaseIdsOfBox  = new ArrayList<>();

        for (Purchase_matarial pm : Pack) {
            purchaseIdsOfBox.add(pm.getPurchase_matarial_id());
        }

        // 각 purchaseId의 값을 모두 더하기
        for (Integer purchaseId : purchaseIdsOfBox) {
            Material_details materialDetails = materialDetailsRepository.findByPurchaseId(purchaseId);
            usablePack += materialDetails.getReceived_quantity();
            usedPack += materialDetails.getShipped_quantity();
        }

        stockOfPack= usablePack-usedPack;
        dto.setPackaging(String.valueOf(stockOfPack));


        return dto;
    }
}


