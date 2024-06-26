$(document).ready(function() {
    //원자재 발주 관리 페이지 테이블
    const table = $('#materialTable').DataTable({
        paging: true,
        lengthChange: false,
        searching: false,
        select: false,
        info: false,
        ajax: {
            url: '/api/showInventory',
            dataSrc: 'data',
            type: 'GET'
        },
        responsive: true,
        orderMulti: true,
        columns: [
            {data: 'purchase_matarial.order_id.orderId', title: '수주 번호'},
            {data: 'purchase_matarial.purchase_matarial_id', title: '발주 번호'},
            {data: 'purchase_matarial.raw_material', title: '원자재'},
            {
                data: 'received_quantity',
                title: '입고량',
                render: function(data, type, row) {
                    if (row.purchase_matarial.raw_material === '벌꿀' || row.purchase_matarial.raw_material === '콜라겐') {
                        return data + ' ml'; // '벌꿀' 또는 '콜라겐'인 경우 'ml' 단위 추가
                    } else {
                        return data + ' kg'; // 기타 경우에는 'kg' 단위 추가
                    }
                }
            },
            { data: 'received_date', title: '입고 날짜' },
            {
                data: 'shipped_quantity',
                title: '출고량',
                render: function(data, type, row) {
                    if (row.purchase_matarial.raw_material === '벌꿀' || row.purchase_matarial.raw_material === '콜라겐') {
                        return data + ' ml'; // '벌꿀' 또는 '콜라겐'인 경우 'ml' 단위 추가
                    } else {
                        return data + ' kg'; // 기타 경우에는 'kg' 단위 추가
                    }
                }
            },
            { data: 'shipped_date', title: '출고 날짜' }
        ]
    });


    // 페이지가 로드될 때 API를 호출하여 데이터를 가져옵니다.
    $.ajax({
        url: '/api/getPackagingData',
        type: 'GET',
        success: function(data) {
            // 데이터가 성공적으로 반환되면 테이블에 데이터를 삽입합니다.
            var tbody = $('#box tbody');
            tbody.empty(); // 기존 데이터를 지웁니다.

            var row = '<tr>' +
                '<td>' + data.packaging + '</td>' +
                '<td>' + data.box + '</td>' +
                '</tr>';
            tbody.append(row);
        },
        error: function(error) {
            console.error('Error fetching data:', error);
        }
    });
});