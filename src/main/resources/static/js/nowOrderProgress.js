$(document).ready(function() {

    //원자재 발주 관리 페이지 테이블
    const aa = $('#example').DataTable({
        paging: true,
        lengthChange: false,
        searching: true,
        select: false,
        info: false,
        ajax: {
            url: '/api/delivered',
            dataSrc: 'data',
            type: 'GET'
        },
        responsive: true,
        orderMulti: true,
        columns: [
            {data: 'order_id.orderId'}, // order_id 내부의 orderId
            {data: 'purchase_matarial_id'},
            {data: 'raw_material'}, // purchase_matarial의 raw_material
            {data: 'purchase_date'},
            {data: 'order_id.order_date'}, // order_id 내부의 production_quantity
            {data: 'delivery_status'},
        ]
    });
});
