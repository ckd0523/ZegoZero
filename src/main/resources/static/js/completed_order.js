$(document).ready(function () {
    var table = $('#myTable').DataTable({
        ajax: {
            url: '/api/completedorder',
            type: 'GET',
            dataSrc: 'data'
        },
        responsive: true,
        orderMulti: true,
        columns: [
            { data: 'order_id' },
            { data: 'product_name' },
            { data: 'quantity' },
            { data: 'order_date' },
            { data: 'shipping_date' },
            { data: 'customer_name' },
            { data: 'delivery_address' }
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

    // 열 선택 버튼 클릭 시 이벤트 처리
    $('#myTable').on('click', '.colVisButton', function() {
        table.buttons(['.buttons-colvis']).trigger();
    });

});