$(document).ready(function() {
    var table = $('#example1').DataTable({
        ajax: {
            url: '/api/progressorder',
            type: 'GET',
            dataSrc: 'data'
        },
        responsive: true,
        orderMulti: true,
        columns: [
            { title: "수주번호", data: 'order_id' },
            { title: "완제품명", data: 'product_name' },
            { title: "수량", data: 'quantity' },
            { title: "회사명", data: 'customer_name' },
            { title: "배송지", data: 'delivery_address' },
            { title: "출하예정날짜", data: 'expected_shipping_date' },
            {
                title: "출하",
                data: null,
                orderable: false,
                render: function (data, type, full, meta) {
                    if (full.delivery_available) {
                        return '<button class="btn btn-success btn-sm shipping-button" onclick="shipOrder(' + full.order_id + ')">출하</button>';
                    } else {
                        return '출하 준비중';
                    }
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
                text: '엑셀로 저장',
                className: 'exportCSV',
                filename: 'exported_data',
                exportOptions: {
                    columns: ':visible'
                },
                customize: function (xlsx) {
                    var sheet = xlsx.xl.worksheets['sheet1.xml'];
                    $('row c', sheet).attr('s', '25');
                }
            }
        ]
    });

    var table2 = $('#example2').DataTable({
        ajax: {
            url: '/api/completedorder',
            type: 'GET',
            dataSrc: 'data'
        },
        responsive: true,
        orderMulti: true,
        columns: [
            { title: "수주번호", data: 'order_id' },
            { title: "완제품명", data: 'product_name' },
            { title: "수량", data: 'quantity' },
            { title: "회사명", data: 'customer_name' },
            { title: "배송지", data: 'delivery_address' },
            { title: "출하예정날짜", data: 'expected_shipping_date' },
            { title: "출하날짜", data: 'shipping_date' },
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
                text: '엑셀로 저장',
                className: 'exportCSV',
                filename: 'exported_data',
                exportOptions: {
                    columns: ':visible'
                },
                customize: function (xlsx) {
                    var sheet = xlsx.xl.worksheets['sheet1.xml'];
                    $('row c', sheet).attr('s', '25');
                }
            }
        ]
    });

    // DataTables에서 생성된 테이블에 이벤트를 위임하여 출하 버튼 클릭 처리
    $('#example1').on('click', '.shipping-button', function () {
        var data = table.row($(this).parents('tr')).data();
        shipping(data.order_id);
    });

    // 출하 버튼 클릭 시 처리할 함수
    function shipping(orderId) {
        if (confirm('정말로 출하하시겠습니까?')) {
            $.ajax({
                type: "POST",
                url: "/api/shipping",
                data: JSON.stringify({ order_id: orderId }), // JSON 문자열로 변환
                contentType: "application/json",
                success: function (response) {
                    table.ajax.reload(); // 테이블 새로고침
                    table2.ajax.reload();
                },
                error: function (xhr, status, error) {
                    console.error('Error occurred during order POST:', error);
                }
            });
        }
    }

});