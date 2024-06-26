package com.codehows.zegozero.service;

import com.codehows.zegozero.dto.Finished_product_management_Dto;
import com.codehows.zegozero.dto.Order_Dto;
import com.codehows.zegozero.dto.Shipment_management_dto;
import com.codehows.zegozero.entity.Finish_product;
import com.codehows.zegozero.entity.Orders;
import com.codehows.zegozero.repository.FinishProductRepository;
import com.codehows.zegozero.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class finishedProductService {

    private final FinishProductRepository finishProductRepository;
    private final OrdersRepository ordersRepository;
    private final PlanService planService;
    private final TimeService timeService;

    public void receivesave(Finished_product_management_Dto productDto) {
        Orders order = planService.getOrderByPlanId(productDto.getPlanId());
        LocalDateTime date = timeService.getDateTimeFromDB().getTime();



           Integer production_quantity = order.getQuantity();
           Integer real_quantity = productDto.getReceived_quantity();
           Integer inventory_quantity = real_quantity - production_quantity;

           if (inventory_quantity > 0) {
               // 입고
               Finish_product finishProduct = new Finish_product();
               finishProduct.setProduct_name(productDto.getProduct_name());
               finishProduct.setOrder_id(order);
               finishProduct.setReceived_quantity(production_quantity);
               finishProduct.setReceived_date(date);
               finishProductRepository.save(finishProduct);

               Finish_product finishProduct2 = new Finish_product();
               finishProduct2.setProduct_name(productDto.getProduct_name());
               finishProduct2.setReceived_quantity(inventory_quantity);
               finishProduct2.setReceived_date(date);
               finishProductRepository.save(finishProduct2);

           } else {
               // 입고
               Finish_product finishProduct = new Finish_product();
               finishProduct.setProduct_name(productDto.getProduct_name());
               finishProduct.setOrder_id(order);
               finishProduct.setReceived_quantity(production_quantity);
               finishProduct.setReceived_date(date);
               finishProductRepository.save(finishProduct);
           }


    }

    // 출하시 출고내역 등록
    public void shippingsave(Shipment_management_dto shippingProduct) {
        Optional<Orders> optionalOrder = ordersRepository.findById(shippingProduct.getOrder_id());
        LocalDateTime date = timeService.getDateTimeFromDB().getTime();
        if (optionalOrder.isPresent()) {
            Orders order = optionalOrder.get();

                // 출고
                Finish_product finishProduct = new Finish_product();
                finishProduct.setProduct_name(order.getProduct_name());
                finishProduct.setOrder_id(order);
                finishProduct.setShipped_date(date);
                finishProduct.setShipped_quantity(order.getQuantity());
                finishProductRepository.save(finishProduct);

        }
    }

    // 수주등록시 완제품 재고 출고내역 등록
    public void orderProductsave(Order_Dto orderDto) {
        LocalDateTime date = timeService.getDateTimeFromDB().getTime();
            // 출고
            Finish_product finishProduct = new Finish_product();
            finishProduct.setProduct_name(orderDto.getProduct_name());
            finishProduct.setShipped_date(date);
            finishProduct.setShipped_quantity(orderDto.getUsed_inventory());
            finishProductRepository.save(finishProduct);
    }

    public List<Finish_product> findAll() {
        return finishProductRepository.findAll();
    }

    // 입고만 찾는 메서드
    public List<Finish_product> findAllWithReceivedDateNotNull() {
        return finishProductRepository.findAllWithReceivedDateNotNull();
    }

    // 출고만 찾는 메서드
    public List<Finish_product> findAllWithShippedDateNotNull() {
        return finishProductRepository.findAllWithShippedDateNotNull();
    }

    // 주어진 product_name과 null인 order_id에 해당하는 재고량을 반환하는 메서드
    public Integer totalProduct(String productName) {

        // shipped quantity 조회
        Integer shippedQuantity = finishProductRepository.sumShippedQuantityByProductNameAndNullOrderId(productName);

        // Null 체크 추가
        if (shippedQuantity == null) {
            shippedQuantity = 0; // 기본값으로 0을 설정하거나 다른 처리를 수행할 수 있습니다.
        }

        // received quantity 조회
        Integer receivedQuantity = finishProductRepository.sumReceivedQuantityByProductNameAndNullOrderId(productName);

        // Null 체크 추가
        if (receivedQuantity == null) {
            receivedQuantity = 0; // 기본값으로 0을 설정하거나 다른 처리를 수행할 수 있습니다.
        }

        // 총 재고량 계산
        int total = receivedQuantity - shippedQuantity;

        return total;
    }

}
