$(document).ready(function () {
    var table;
    var defaultColumns = [
        {title: "수주번호", data: 'order_id'},
        {title: "완제품명", data: 'product_name'},
        {title: "입고량", data: 'received_quantity'},
        {title: "입고날짜", data: 'received_date'},
        {title: "출고량", data: 'shipped_quantity'},
        {title: "출고날짜", data: 'shipped_date'}
    ];
    var defaultUrl = '/api/finish1';

    initializeDataTable(defaultColumns, defaultUrl);

    // 초기화 함수
    function initializeDataTable(columns, url) {
        if (table) {
            table.destroy(); // 기존 DataTable 객체가 있으면 파괴
            $('#myTable').empty(); // 테이블 내용을 비웁니다
        }

        // DataTable 초기화
        table = $('#myTable').DataTable({
            ajax: {
                url: url, // JSON 데이터 URL
                type: 'GET',
                dataSrc: 'data'
            },
            responsive: true,
            orderMulti: true,
            columns: columns,
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
    }

    // 셀렉트 박스 변경 이벤트 리스너
    $('#Select').on('change', function() {
        var selectedValue = $(this).val();
        console.log(selectedValue);
        var newUrl
        var newColumns;
        if (selectedValue === '1') {
            newColumns = [
                {title: "수주번호", data: 'order_id'},
                {title: "완제품명", data: 'product_name'},
                {title: "입고량", data: 'received_quantity'},
                {title: "입고날짜", data: 'received_date'},
                {title: "출고량", data: 'shipped_quantity'},
                {title: "출고날짜", data: 'shipped_date'}
            ];
            newUrl = '/api/finish1';
        } else if (selectedValue === '2') {
            newColumns = [
                {title: "수주번호", data: 'order_id'},
                {title: "완제품명", data: 'product_name'},
                {title: "입고량", data: 'received_quantity'},
                {title: "입고날짜", data: 'received_date'}
            ];
            newUrl = '/api/finish2';
        } else if (selectedValue === '3') {
            newColumns = [
                {title: "수주번호", data: 'order_id'},
                {title: "완제품명", data: 'product_name'},
                {title: "출고량", data: 'shipped_quantity'},
                {title: "출고날짜", data: 'shipped_date'}
            ];
            newUrl = '/api/finish3';
        }

        initializeDataTable(newColumns, newUrl);
    });
});
