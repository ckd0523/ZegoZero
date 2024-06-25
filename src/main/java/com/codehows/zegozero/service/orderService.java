package com.codehows.zegozero.service;

import com.codehows.zegozero.dto.Order_Dto;
import com.codehows.zegozero.dto.savePurchaseMaterial_Dto;
import com.codehows.zegozero.entity.Orders;
import com.codehows.zegozero.entity.Purchase_matarial;
import com.codehows.zegozero.repository.OrdersRepository;
import com.codehows.zegozero.repository.PurchaseMatarialRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final PurchaseMatarialRepository purchaseMatarialRepository;

    public void save(Order_Dto Orderdata) {

        Orders orders = new Orders();
        orders.setProduct_name(Orderdata.getProduct_name());
        orders.setQuantity(Orderdata.getQuantity());
        orders.setUsed_inventory(Orderdata.getUsed_inventory());
        orders.setProduction_quantity(Orderdata.getProduction_quantity());
        orders.setOrder_date(LocalDateTime.now());
        orders.setExpected_shipping_date(Orderdata.getExpected_shipping_date());
        orders.setCustomer_name(Orderdata.getCustomer_name());
        orders.setDelivery_address(Orderdata.getDelivery_address());
        orders.setDeletable(true);

        ordersRepository.save(orders);
    }

    public List<Orders> findAll() {
        return ordersRepository.findAll();
    }

    //게시글 삭제
    @Transactional
    public void deleteOrder(Integer order_id) {
        Orders order = ordersRepository.findById(order_id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id: " + order_id));
        ordersRepository.delete(order);
    }

    public List<Orders> findByDeletable(Boolean deletable){
        return ordersRepository.findByDeletable(deletable);
    }

    public List<Purchase_matarial> findByDelivery_status(String deliveryStatus){

        return purchaseMatarialRepository.findByDeliveryStatusJPQL(deliveryStatus);
    }



//    public void updateOrderDeletable(Integer orderId) {
//        Orders updateOrders = ordersRepository.findByOrderId(orderId);
//        updateOrders.
//    }

    public void savePurchaseMaterial(savePurchaseMaterial_Dto saveRequest) {
        Orders orders = ordersRepository.findById(saveRequest.getOrder_id())
                .orElseThrow(EntityNotFoundException::new);
        orders.setDeletable(false);
        purchaseMatarialRepository.save(saveRequest.toEntity(orders));
    }

    public void findByPurchase_material_id(Integer Purchase_material_id){
        String deliveryOk = "배송완료";
        Purchase_matarial purchaseMatarial = purchaseMatarialRepository.findByPurchaseMaterialId(Purchase_material_id);
        purchaseMatarial.setDelivery_status(deliveryOk);

        System.out.println(purchaseMatarial.getDelivery_status());
        purchaseMatarialRepository.save(purchaseMatarial);
    }

//    public List<Orders> delivered(Boolean deletable){
//        return ordersRepository.findByDeletable(deletable);
//    }


}
