// // 전역 변수와 초기 객체 선언
// var selectedData = [];
// var aggregatedQuantities = {};
//
// var newObject = {
//     '벌꿀': 0,
//     '양배추': 0,
//     '흑마늘': 0,
//     '석류농축액': 0,
//     '매실농축액': 0,
//     '콜라겐': 0
// };
var table2;
var table3; // 전역 변수로 선언
var table4;
var allData;    //발주 등록의 모든 수주 데이터
// 양배추즙 1개당 필요한 재료의 양
const CABBAGEorGarlic_PER_JUICE = 4; // 1 양배추즙 당 1kg 양배추
const HONEY_PER_JUICE = 0.15; // 1 양배추즙 당 0.1kg 벌꿀
const PER_JUICE = 0.125; // ML
const COLAGEN_PER_JUICE = 0.05; // ML

var selectedRows=[];


const result = [];// 수주번호 + 주문해야할 원자재의 배열 값, DB에 저장할 값



$(document).ready(function() {

    //원자재 발주 관리 페이지 테이블
    const aa = $('#example').DataTable({
        paging: true,
        lengthChange: false,
        searching: false,
        select: {
            style: 'multi'
        },
        info: false,
        ajax: {
            url: '/api/delivered',
            dataSrc: 'data',
            type: 'GET'
        },
        responsive: true,
        orderMulti: true,
        columns: [
            {
                data: 'order_id',
                render: function(data, type, row) {
                    return data !== null && data !== undefined ? data : '박스 및 포장지';
                },
                title: '수주번호'
            }, // order_id 내부의 orderId
            {data: 'purchase_matarial_id'},
            {data: 'raw_material'}, // purchase_matarial의 raw_material
            {
                data: 'order_quantity',
                render: function (data, type, row) {
                    // raw_material에 따라 단위를 설정
                    var unit = '';
                    if (row.raw_material === '양배추' || row.raw_material === '흑마늘') {
                        unit = 'kg';
                    } else if (row.raw_material === '콜라겐' || row.raw_material === '벌꿀' || row.raw_material === '석류농축액' || row.raw_material === '매실농축액') {
                        unit = 'L';
                    }else if(row.raw_material === '박스' || row.raw_material === '포장지'){
                        unit = '개';
                    }
                    return data + ' ' + unit; // 단위를 포함하여 반환
                },

            },
            {data: 'purchase_date'}, // order_id 내부의 production_quantity
            {data: 'delivery_status'},
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


    //발주 계획 조회 버튼 클릭
    $('#openRegistrationPopup').click(function () {
        if (!table2) {
            //발주 계획 페이지 테이블
            table2 = $('#example1').DataTable({
                paging: false,
                lengthChange: false,
                searching: false,
                select: {
                    style: 'multi'
                },
                info: false,
                ajax: {
                    url: '/api/orderPlan',
                    dataSrc: 'data',
                    type: 'GET'
                },
                responsive: true,
                orderMulti: true,
                columns: [
                    {data: 'order_id'},
                    {data: 'product_name'},
                    {data: 'production_quantity'},
                    {data: 'expected_shipping_date'},
                ]
            });
        }
        openPopup("registrationPopup");
    })

    $('#deliveryOk').click(function () {

        const deliveryOkOrder = []; //발주 번호를 배열형태로 저장

        //선택한 행의 정보를 배열형태로 저장
        const deliveryOk = aa.rows('.selected').data().toArray();

        console.log(deliveryOk);


        deliveryOk.forEach(function(deliveryOk) {
            console.log(deliveryOk.purchase_matarial_id); // 각 선택된 행의 order_id 내부의 orderId 속성
            deliveryOkOrder.push(deliveryOk.purchase_matarial_id);
        });

        console.log(deliveryOkOrder);

        //1.발주번호를 바탕으로 '배송중'을 '배송완료'로 변경한다.
        //2.원자재 내역 테이블에 발주번호를 등록한다.
        //3. 원자재 입고량을 구하는 방법- '주문량'(원자재발주tbl)을 가져와 '입고량'(원자재내역tbl)으로 등록한다.
        //4.dto에 현재 날짜를 등록하여 함께 저장한다.



        // fetch('/api/deliveryOk', {
        //     method: 'POST',
        //     headers: {
        //         'Content-Type': 'application/json'
        //     },
        //     body: JSON.stringify(deliveryOkOrder)
        // })
        //     .then(response => response.text())
        //     // .then(response => {
        //     //     if (!response.ok) {
        //     //         throw new Error('Network response was not ok');
        //     //     }
        //     //     return response.text();
        //     // })
        //     .then(data => {
        //         console.log('Success:', data);
        //         alert('Success:'+ data);
        //     })
        //     .catch((error) => {
        //         // console.error('Error:', "error");
        //         alert('에러'+ error.message)
        //     });
        //
        //
        // location.reload();

        //비동기통신이 끝나기 전에 페이지를 로드하여 문제 발생
        //통신이 끝난 후 페이지를 로드할 수 있도록 then 뒤에 매서드 실행하니 문제 해결

        fetch('/api/deliveryOk', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(deliveryOkOrder)
        })
            .then(response => response.text())
            .then(data => {
                console.log('Success:', data);
                alert('배송완료 처리되었습니다.');
                location.reload();  // 비동기 통신이 끝난 후 페이지 리로드
            })
            .catch((error) => {
                alert('에러: ' + error.message);
            });


    });

    $("#PackOrder").click(function () {
        fetch('/api/PackOrder', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(data => {
                console.log('Success:', data); // 서버에서 반환한 응답 데이터 출력
                // 성공 시 추가적인 클라이언트 측 로직을 추가할 수 있음
            })
            .catch(error => {
                console.error('Error:', error);
                // 오류 발생 시 처리할 로직을 추가할 수 있음
            });

        location.reload(); // 페이지 새로고침
    });


//     //체크박스 클릭 시 이벤트
//     $('#example1').on('click', '.checkbox', function() {
//
//         //1.가장 가까운 행의 정보를 rowData에 저장한다.
//         var rowData = table2.row($(this).closest('tr')).data();
//
//         // 2.체크박스가 체크되었다면 선택된 데이터를 selectedData에 추가한다
//         if ($(this).is(':checked')) {
//             selectedData.push(rowData); // 선택된 데이터를 리스트에 추가
//
//         // 3. 체크가 해제된 경우 [is (:checked)]가 아닌 경우에 else 구문 실행
//         //    data(selectedData)와 rowData가 같으면 false리턴
//         //    => false가 리턴된 정보에 대해서는 selectedData에서 삭제됨
//         } else {
//             selectedData = selectedData.filter(function(data) {
//                 return data.order_id !== rowData.order_id;
//             });
//         }
//     });



    //발주버튼 클릭 이벤트
    $("#openConfirmationPopup").click(function () {

        // 페이지가 로드될 때 API를 호출하여 데이터를 가져옵니다.
        $.ajax({
            url: '/api/getPackagingData',
            type: 'GET',
            success: function(data) {
                // 데이터가 성공적으로 반환되면 테이블에 데이터를 삽입합니다.
                var tbody = $('#box2 tbody');
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

        //선택된 행의 데이터를 배열형태로 저장/
        //forEach함수 사용을 위해 배열형대로 변환
        selectedRows = table2.rows('.selected').data().toArray();


        if (selectedRows.length > 0) {
            if (confirm('선택된 항목을 발주하시겠습니까?')) {

                //map = 배열의 각 요소에 대해 주어진 함수를 호출하여 그 결과로 새로운 배열을 생성
                // //selectedRows를 가공하여 order_id 값을 가지는 배열로 생성
                // orderIds = selectedRows.map(function(rowData) {
                //     return rowData.order_id;
                // });

                closePopup("registrationPopup");
                openPopup2("confirmationPopup");

            }
        } else {
            alert('발주할 항목을 선택해 주세요.');
        }


    });


    document.getElementById("regist").addEventListener("click", async function () {

        try {
            const response = await fetch('/api/savePurchaseMaterial', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(result) // JSON 배열 전송
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();
            console.log('Success:', data.message);


        } catch (error) {
            console.error('Error:', error);
        }

        closePopup("confirmationPopup");
        location.reload();

    });

    $('#cancel').click(function () {
        closePopup("registrationPopup");
    });


    // $("#regist").click(function () {
    //
    //     fetch('/api/savePurchaseMaterial', {
    //         method: 'POST',
    //         headers: {
    //             'Content-Type': 'application/json'
    //         },
    //         body: JSON.stringify({ data: result })
    //     })
    //         .then(response => response.json())
    //         .then(data => console.log('Success:', data))
    //         .catch((error) => console.error('Error:', error));
    // });


    // });




    //1번째 팝업 오픈 매서드
    window.openPopup = function (popupId) {
        $("#" + popupId).fadeIn();
    }

    //2번째 팝업 오픈 매서드
    //orderIds에 해당하는 값을 출력
    window.openPopup2 = function (popupId) {
        $("#" + popupId).fadeIn();
        if (!table3) {
            table3 = $('#example2').DataTable({
                lengthChange: false,
                paging: false,
                selected:false,
                searching: false,
                select: {
                    style: 'multi'
                },
                info: false,
                data: selectedRows,
                responsive: true,
                orderMulti: true,
                columns: [
                    {data: 'order_id'},
                    {data: 'product_name'},
                    {
                        data: 'production_quantity',
                        render: function(data, type, row) {
                            return Math.ceil(data * 1.031); // 1.03을 곱하고 소수점 두 자리로 반올림
                        }
                    }
                ],

            })
        }

        divideOrders(selectedRows);
        console.log(result[0]);
        console.log(result[1]);


    }


    window.closePopup = function (popupId) {
        $("#" + popupId).fadeOut();
    }

    // function processOrderData(allData) {
    //     allData.forEach(function(index) {
    //         // 수주번호
    //         var orderId = index.order_id;
    //         var product_name = index.product_name;
    //         var production_quantity = index.production_quantity;


    function divideOrders(selectedRows) {
        selectedRows.forEach(function (rowData) {

            var orderId = rowData.order_id;
            var product_name = rowData.product_name;
            var production_quantity =  Math.ceil(rowData.production_quantity * 1.031);

            console.log(orderId);
            console.log(product_name);
            console.log(production_quantity);

            // const CABBAGEorGarlic_PER_JUICE = 4; // 1 양배추즙 당 1kg 양배추
            // const HONEY_PER_JUICE = 0.15; // 1 양배추즙 당 0.1kg 벌꿀
            // const PER_JUICE = 150; // ML
            // const COLAGEN_PER_JUICE = 0.05; // ML



            //양배추즙일 경우
            if('양배추즙'===product_name){
                console.log("양배추즙")

                // 주문해야 할 양배추와 벌꿀의 양 계산
                var cabbageRequired = Math.ceil(production_quantity * CABBAGEorGarlic_PER_JUICE);
                var honeyRequired = Math.ceil(production_quantity * HONEY_PER_JUICE);

                // 객체로 만들어 배열에 추가
                const data1 = {
                    order_id: orderId,
                    raw_material: '양배추',
                    order_quantity: cabbageRequired
                };

                const data2 = {
                    order_id: orderId,
                    raw_material: '벌꿀',
                    order_quantity: honeyRequired
                };

                result.push(data1);
                result.push(data2);



                //흑마늘즙일 경우
            }else if('흑마늘즙'===product_name) {
                console.log("흑마늘즙")

                // 주문해야 할 양배추와 벌꿀의 양 계산
                var garlicRequired = Math.ceil(production_quantity * CABBAGEorGarlic_PER_JUICE);
                var honeyRequired2 = Math.ceil(production_quantity * HONEY_PER_JUICE);

                // 객체로 만들어 배열에 추가
                const data1 = {
                    order_id: orderId,
                    raw_material: '흑마늘',
                    order_quantity: garlicRequired
                };

                const data2 = {
                    order_id: orderId,
                    raw_material: '벌꿀',
                    order_quantity: honeyRequired2
                };

                result.push(data1);
                result.push(data2);



                //석류 젤리일 경우
            }else if('석류젤리'===product_name) {
                console.log("석류젤리")

                // 주문해야 할 양배추와 벌꿀의 양 계산
                var pomegranateJelly = Math.ceil(production_quantity * PER_JUICE);
                var collagenRequired = Math.ceil(production_quantity * COLAGEN_PER_JUICE);

                // 객체로 만들어 배열에 추가
                const data1 = {
                    order_id: orderId,
                    raw_material: '석류농축액',
                    order_quantity: pomegranateJelly
                };

                const data2 = {
                    order_id: orderId,
                    raw_material: '콜라겐',
                    order_quantity: collagenRequired
                };

                result.push(data1);
                result.push(data2);

                //매실 젤리인 경우
            }else if('매실젤리'===product_name) {
                console.log("매실젤리")

                // 주문해야 할 양배추와 벌꿀의 양 계산
                var plumJelly = Math.ceil(production_quantity * PER_JUICE);
                var collagenRequired2 = Math.ceil(production_quantity * COLAGEN_PER_JUICE);

                // 객체로 만들어 배열에 추가
                const data1 = {
                    order_id: orderId,
                    raw_material: '매실농축액',
                    order_quantity: plumJelly
                };

                const data2 = {
                    order_id: orderId,
                    raw_material: '콜라겐',
                    order_quantity: collagenRequired2
                };

                result.push(data1);
                result.push(data2);
            }

        });

        if (!table4) {
            table4 = $('#example4').DataTable({
                lengthChange: false,
                paging:false,
                searching: false,
                select: false,
                info: false,
                data: result,
                responsive: true,
                orderMulti: true,
                columns: [
                    {data: 'order_id'},
                    {data: 'raw_material'},
                    {
                        data: 'order_quantity',
                        render: function(data, type, row) {
                            // raw_material에 따라 단위를 설정
                            var unit = '';
                            if (row.raw_material === '양배추' || row.raw_material === '흑마늘') {
                                unit = 'kg';
                            } else if (row.raw_material === '콜라겐' || row.raw_material === '벌꿀' || row.raw_material === '석류농축액' || row.raw_material === '매실농축액') {
                                unit = 'L';
                            }else if(row.raw_material === '박스' || row.raw_material === '포장지'){
                                unit = '개';
                            }
                            return data + ' ' + unit; // 단위를 포함하여 반환
                        }
                    },
                ],

            })
        }


    }
})



//값을 바탕으로 양배추 벌꿀 흑마늘...의 양을 매개변수로 받는 테이블 생성 함수 만들기.

//등록하기 버튼 클릭 시 DB 저장하기.
