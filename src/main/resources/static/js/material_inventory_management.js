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
            // {data: 'purchase_matarial.order_id.orderId', title: '수주 번호'},
            {
                data: 'purchase_matarial.order_id.orderId',
                render: function(data, type, row) {
                    return data !== null && data !== undefined ? data : '박스 및 포장지';
                },
                title: '수주번호'
            },

            {
                data: 'purchase_matarial.purchase_matarial_id', title: '발주 번호',
                render: function (data, type, row) {
                    return data !== null && data !== undefined ? data : 'null';
                },
            },
            {
                data: 'purchase_matarial.raw_material', title: '원자재',
                render: function (data, type, row) {
                    return data !== null && data !== undefined ? data : 'null';
                },
            },
            {
                data: 'received_quantity',
                title: '입고량',
                // render: function(data, type, row) {
                //     var unit='';
                //
                //     if (row.raw_material === '양배추' || row.raw_material === '흑마늘') {
                //             unit = 'kg';
                //     } else if (row.raw_material === '콜라겐' || row.raw_material === '벌꿀') {
                //             unit = 'ml';
                //     }else if(row.raw_material === '박스' || row.raw_material === '포장지'){
                //             unit = '개';
                //     }
                //         return data + ' ' + unit; // 단위를 포함하여 반환
                // }
                // },
                render: function(data, type, row) {
                    // raw_material에 따라 단위를 설정
                    var unit = '';
                    if (row.purchase_matarial.raw_material === '양배추' || row.purchase_matarial.raw_material === '흑마늘') {
                        unit = 'kg';
                    } else if (row.purchase_matarial.raw_material === '콜라겐' || row.purchase_matarial.raw_material === '벌꿀') {
                        unit = 'ml';
                    }else if(row.purchase_matarial.raw_material === '박스' || row.purchase_matarial.raw_material === '포장지'){
                        unit = '개';
                    }
                    return data + ' ' + unit; // 단위를 포함하여 반환
                }
            },
            { data: 'received_date', title: '입고 날짜' },
            {
                data: 'shipped_quantity',
                title: '출고량',
                render: function(data, type, row){
                    // raw_material에 따라 단위를 설정
                    var unit = '';
                    if (row.purchase_matarial.raw_material === '양배추' || row.purchase_matarial.raw_material === '흑마늘') {
                        unit = 'kg';
                    } else if(row.purchase_matarial.raw_material === '콜라겐' || row.purchase_matarial.raw_material === '벌꿀') {
                        unit = 'ml';
                    }else if(row.purchase_matarial.raw_material === '박스' || row.purchase_matarial.raw_material === '포장지'){
                        unit = '개';
                    }
                    return data + ' ' + unit; // 단위를 포함하여 반환
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
                '<td>' + data.packaging + '개' + '</td>' +
                '<td>' + data.box + '개' + '</td>' +
                '</tr>';
            tbody.append(row);
        },
        error: function(error) {
            console.error('Error fetching data:', error);
        }
    });





    window.fetchData = function() {
        const inputValue = $('#inputNumber').val(); // 입력된 수를 가져옴

        $.ajax({
            url: `/api/selectedOrderNum/${inputValue}`, // URL을 동적으로 생성
            type: 'GET',
            success: function(response) {
                // 테이블 데이터 초기화
                table.clear();
                console.log(response);
                //전달받은 데이터가 존재하고 길이가 0이상인 경우(값이 잘 도착한 경우)
                if (response.Data && response.Data.length > 0) {
                    // 데이터를 테이블에 추가
                    response.Data.forEach(function(item) {
                        table.row.add(item);

                    });

                    //
                    table.draw();

                }
                else {
                    // 서버에서 받은 메시지 표시
                    alert(response.message || '값이 존재하지 않습니다.');
                }

            },
            //error를 보내어 도착한 내용을 alert창으로 표기
            error: function(jqXHR, textStatus, errorThrown) {
                console.error('Error fetching data:', errorThrown);

                // 서버에서 반환된 에러 메시지
                const errorMessage = jqXHR.responseJSON ? jqXHR.responseJSON.message : 'An unknown error occurred';

                // 에러 메시지를 alert로 표시
                alert(`Error: ${errorMessage}`);
            }
        });
    };


});
