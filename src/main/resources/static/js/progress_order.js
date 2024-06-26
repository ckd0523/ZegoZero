$(document).ready(function () {

    var table = $('#myTable').DataTable({
        ajax: {
            url: '/api/progressorder',
            type: 'GET',
            dataSrc: 'data'
        },
        responsive: true,
        orderMulti: true,
        columns: [
            { data: 'order_id' },
            { data: 'product_name' },
            { data: 'quantity' },
            { data: 'used_inventory' },
            { data: 'production_quantity' },
            { data: 'order_date' },
            { data: 'expected_shipping_date' },
            { data: 'customer_name' },
            { data: 'delivery_address' },
            {
                // 삭제 버튼 칼럼 설정
                render: function (data, type, full, meta) {
                    return full.deletable
                        ? '<button class="btn btn-danger btn-sm delete-button">삭제</button>'
                        : '삭제불가능';
                }
            }
        ],
        "language": {
            "emptyTable": "데이터가 없어요.",
            "lengthMenu": "페이지당 _MENU_ 개씩 보기",
            "info": "현재 _START_ - _END_ / _TOTAL_건",
            "infoEmpty": "데이터 없음",
            "infoFiltered": "( _MAX_건의 데이터에서 필터링됨 )",
            "search": "검색: ",
            "zeroRecords": "일치하는 데이터가 없어요.",
            "loadingRecords": "로딩중...",
            "processing": "잠시만 기다려 주세요...",
            "paginate": {
                "next": "다음",
                "previous": "이전"
            }
        },
        dom: 'Bfrtip',
        buttons: [
            {
                extend: 'excelHtml5',
                text: 'Export Excel',
                className: 'exportCSV',
                filename: 'exported_data',
                exportOptions: {
                    columns: ':visible'
                },
                customize: function (xlsx) {
                    var sheet = xlsx.xl.worksheets['sheet1.xml'];
                    $('row c', sheet).attr('s', '25');
                }
            },
            {
                extend: 'colvis',
                text: '열 선택',
                className: 'colVisButton',
                columns: ':not(.no-export)'
            }
        ]
    });

    // DataTables에서 생성된 테이블에 이벤트를 위임하여 삭제 버튼 클릭 처리
    $('#myTable').on('click', '.delete-button', function () {
        var data = table.row($(this).parents('tr')).data();
        handleDelete(data.order_id);
    });

    // 주문 등록 팝업 열기 이벤트
    $("#openRegistrationPopup").click(function () {
        openPopup("registrationPopup");
    });

    // 주문 확인 팝업 열기 이벤트
    $("#openConfirmationPopup").click(function () {
        var productName = document.getElementById('productName').value;
        var orderQuantity = document.getElementById('orderQuantity').value;
        var stockUsage = document.getElementById('stockUsage').value;
        var productionQuantity = orderQuantity - stockUsage;
        var customer = document.getElementById('customer').value;
        var deliveryAddress = document.getElementById('deliveryAddress').value;

        // Confirm 팝업에 값 설정
        document.getElementById('confirmProductName').value = productName;
        document.getElementById('confirmOrderQuantity').value = orderQuantity;
        document.getElementById('confirmStockUsage').value = stockUsage;
        document.getElementById('confirmProductionQuantity').value = productionQuantity;
        document.getElementById('confirmCustomer').value = customer;
        document.getElementById('confirmDeliveryAddress').value = deliveryAddress;

        // 팝업 열기 및 닫기
        closePopup("registrationPopup");
        openPopup("confirmationPopup");
    });

    // 주문 확인 버튼 클릭 시 처리
    $("#confirm").click(function () {
        var productName = document.getElementById('confirmProductName').value;
        var quantity = parseInt(document.getElementById('confirmOrderQuantity').value) || 0;
        var used_inventory = parseInt(document.getElementById('confirmStockUsage').value) || 0;
        var production_quantity = quantity - used_inventory;
        var customer_name = document.getElementById('confirmCustomer').value;
        var delivery_address = document.getElementById('confirmDeliveryAddress').value;

        var Orderdata = {
            product_name: productName,
            quantity: quantity,
            used_inventory: used_inventory,
            production_quantity: production_quantity,
            customer_name: customer_name,
            delivery_address: delivery_address
        };

        setTimeout(function() {
            $.ajax({
                type: "POST",
                url: "/api/order",
                data: JSON.stringify(Orderdata),
                contentType: "application/json",
                success: function (response) {
                    closePopup("registrationPopup");
                    closePopup("confirmationPopup");
                    table.ajax.reload(); // 테이블 새로고침
                    location.reload();
                },
                error: function (xhr, status, error) {
                    // 서버로부터의 오류 메시지를 표시
                    alert('수주를 등록할 수 없습니다: ' + xhr.responseText);
                    closePopup("registrationPopup");
                    closePopup("confirmationPopup");
                }
            });
        }, 2000);
    });

    // 삭제 버튼 클릭 시 처리할 함수
    function handleDelete(orderId) {
        if (confirm('정말로 삭제하시겠습니까?')) {
            $.ajax({
                type: "POST",
                url: "/api/delete",
                data: JSON.stringify({ order_id: orderId }), // JSON 문자열로 변환
                contentType: "application/json",
                success: function (response) {
                    table.ajax.reload(); // 테이블 새로고침
                },
                error: function (xhr, status, error) {
                    console.error('Error occurred during order POST:', error);
                }
            });
        }
    }

    // 팝업 열기 함수
    function openPopup(popupId) {
        $("#" + popupId).fadeIn();
    }

    $("#registcancle").click(function (){
       closePopup("registrationPopup");
    });

    $("#confirmcancle").click(function (){
        closePopup("confirmationPopup");
    });

    // 팝업 닫기 함수
    function closePopup(popupId) {
        $("#" + popupId).fadeOut();

        // 팝업 안의 input 및 select 초기화
        $("#" + popupId).find("input[type=text], input[type=number]").val("");
        $("#" + popupId).find("select").val("양배추즙");
    }

});
