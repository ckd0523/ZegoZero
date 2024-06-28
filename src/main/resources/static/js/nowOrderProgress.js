$(document).ready(function() {



    //원자재 발주 관리 페이지 테이블
    const aa = $('#example').DataTable({
        paging: true,
        lengthChange: false,
        searching: false,
        select: false,
        info: false,
        // ajax: {
        //     url: '/api/delivered',
        //     dataSrc: 'data',
        //     type: 'GET'
        // },
        order: [[3, 'asc']],
        responsive: true,
        orderMulti: true,
        columns: [
            {data: 'order_id', title: '수주번호'},
            {data: 'product_name', title: '제품명'},
            {data: 'equipment', title: '설비'},
            // {
            //     data: 'equipment_id', title: '설비',
                // render: function(data, type, row) {
                //     var unit = '';
                //     if (row.equipment_id === 1) {
                //         unit = '발주';
                //     } else if (row.equipment_id === 2) {
                //         unit = '세척';
                //     } else if (row.equipment_id === 3) {
                //         unit = '추출기1';
                //     } else if (row.equipment_id === 4) {
                //         unit = '추출기2';
                //     } else if (row.equipment_id === 5) {
                //         unit = '살균기1';
                //     } else if (row.equipment_id === 6) {
                //         unit = '살균기2';
                //     } else if (row.equipment_id === 7) {
                //         unit = '혼합기1';
                //     } else if (row.equipment_id === 8) {
                //         unit = '냉각';
                //     } else if (row.equipment_id === 9) {
                //         unit = '여과기';
                //     } else if (row.equipment_id === 10) {
                //         unit = '충진기1,2(즙)';
                //     } else if (row.equipment_id === 11) {
                //         unit = '충진기3,4(젤리)';
                //     } else if (row.equipment_id === 12) {
                //         unit = 'Box포장기';
                //     } else if (row.equipment_id === 13) {
                //         unit = '검사기';
                //     }
                //     return unit; // 단위를 포함하여 반환
                // }
            // },
            {data: 'estimated_start_date', title:'시작 예정 시간'},
            {data: 'estimated_end_date', title:'종료 예정 시간'},
            {
                data: 'nowProcessing', title: '현재 진행 상태',
                render: function(data, type, row) {
                    var unit = '';
                    if (row.start_date == null) {
                        unit = '대기';
                    } else if (row.end_date == null) {
                        unit = '진행중'+"("+row.nowProcessing+"%"+")";
                    } else {
                        unit = '완료';
                    }
                    return unit;
                }
            },
        ],
    });


    // window.fetchData = function() {
    //     const inputValue = $('#inputNumber').val(); // 입력된 수를 가져옴
    //
    //
    //     $.ajax({
    //         url: `/api/nowOrderProgress/${inputValue}`, // URL을 동적으로 생성
    //         type: 'GET',
    //         success: function(response) {
    //             // 테이블 데이터 초기화
    //             aa.clear();
    //
    //             // 데이터를 테이블에 추가
    //             response.Data.forEach(function(item) {
    //                 aa.row.add(item).draw();
    //             });
    //         },
    //
    //         error: function(jqXHR, textStatus, errorThrown) {
    //             console.error('Error fetching data:', errorThrown);
    //
    //             // 서버에서 반환된 에러 메시지
    //             const errorMessage = jqXHR.responseJSON ? jqXHR.responseJSON.message : 'An unknown error occurred';
    //
    //             // 에러 메시지를 alert로 표시
    //             alert(`Error: ${errorMessage}`);
    //         }
    //     });
    // };

    window.fetchData = function() {
        const inputValue = $('#inputNumber').val(); // 입력된 수를 가져옴

        $.ajax({
            url: `/api/nowOrderProgress/${inputValue}`, // URL을 동적으로 생성
            type: 'GET',
            success: function(response) {
                // 테이블 데이터 초기화
                aa.clear();
                //전달받은 데이터가 존재하고 길이가 0이상인 경우(값이 잘 도착한 경우)
                if (response.Data && response.Data.length > 0) {
                    // 데이터를 테이블에 추가
                    response.Data.forEach(function(item) {
                        aa.row.add(item).draw();
                    });
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
