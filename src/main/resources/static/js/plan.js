$(document).ready(function () {

    // 주문수량, 재고사용을 바꿀때마다 작동
    $('#productName, #orderQuantity, #stockUsage').on('input change', function () {
        calculateExpectedShipmentDate();
    });

    $('#confirm').on('click', function () {
        // 저장 전에 계획이 유효한지 확인
        if (window.productionPlans) {
            var newPlan = window.productionPlans.newPlan;
            var totalProductionQuantity = window.productionPlans.totalProductionQuantity;
            var productName = window.productionPlans.productName;

            if(productName == "양배추즙" || productName == "흑마늘즙"){
                if(newPlan == 1){
                    id34Plan(productName, 3, 4, totalProductionQuantity);
                }else if(newPlan == 2){
                    id34Plan(productName, 3, 4, 333);
                    setTimeout(function() {
                        id34Plan(productName, 3, 4, totalProductionQuantity);
                    }, 500);
                }else if(newPlan == 3){
                    id34Plan(productName, 3, 4, 333);
                    setTimeout(function() {
                        id34Plan(productName, 3, 4, 333);
                    }, 500);
                    setTimeout(function() {
                        id34Plan(productName, 3, 4, totalProductionQuantity);
                    }, 1000);
                }else if(newPlan == 4){
                    id34Plan(productName, 3, 4, 333);
                    setTimeout(function() {
                        id34Plan(productName, 3, 4, 333);
                    }, 500);
                    setTimeout(function() {
                        id34Plan(productName, 3, 4, 333);
                    }, 1000);
                    setTimeout(function() {
                        id34Plan(productName, 3, 4, totalProductionQuantity);
                    }, 1500);
                }
            }else {
                if (newPlan == 1) {
                    zeliPlan(productName, totalProductionQuantity);
                } else if (newPlan == 2) {
                    zeliPlan(productName, 160);
                    setTimeout(function () {
                        zeliPlan(productName, totalProductionQuantity);
                    }, 500);
                } else if (newPlan == 3) {
                    zeliPlan(productName, 160);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 500);
                    zeliPlan(function () {
                        zeliPlan(productName, totalProductionQuantity);
                    }, 1000);
                } else if (newPlan == 4) {
                    zeliPlan(productName, 160);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 500);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 1000);
                    setTimeout(function () {
                        zeliPlan(productName, totalProductionQuantity);
                    }, 1500);
                } else if (newPlan == 5) {
                    zeliPlan(productName, 160);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 400);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 800);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 1200);
                    setTimeout(function () {
                        zeliPlan(productName, totalProductionQuantity);
                    }, 1600);
                } else if (newPlan == 6) {
                    zeliPlan(productName, 160);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 300);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 600);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 900);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 1200);
                    setTimeout(function () {
                        zeliPlan(productName, totalProductionQuantity);
                    }, 1500);
                } else if (newPlan == 7) {
                    zeliPlan(productName, 160);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 250);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 500);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 750);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 1000);
                    setTimeout(function () {
                        zeliPlan(productName, 160);
                    }, 1250);
                    setTimeout(function () {
                        zeliPlan(productName, totalProductionQuantity);
                    }, 1500);
                }
            }
        }
    });

    function calculateExpectedShipmentDate() {
        var orderQuantity = parseInt($('#orderQuantity').val()) || 0; // 숫자로 파싱하며, 실패할 경우 0으로 설정
        var stockUsage = parseInt($('#stockUsage').val()) || 0; // 숫자로 파싱하며, 실패할 경우 0으로 설정
        var productionQuantity = Math.ceil((orderQuantity - stockUsage) * 1.031); // 1.031을 곱해서 올림 처리
        var productName = $('#productName').val();

        console.log(productName);

        if (!isNaN(productionQuantity)) {
            $.ajax({
                url: '/api/calculateProductionQuantity',
                type: 'GET',
                data: {
                    productName: productName,
                    productionQuantity: productionQuantity
                },
                success: function (data) { // 999
                    var numberOfPlans = data[0]; // 3
                    var totalProductionQuantity = data[1]; // 333

                    var newPlan = numberOfPlans; // 새로운 계획을 새워야 하는지 여부
                    var quantityPlan = totalProductionQuantity; // 실제작수량
                    // 각 값 콘솔에 출력
                    console.log("만들어야 하는 생산계획 개수: " + numberOfPlans);
                    console.log("마지막 생산계획에 넣어야하는 수량: " + totalProductionQuantity);

                    // 생산계획을 saveNewPlan 함수 내에서 처리할 수 있도록 전역 변수로 설정
                    window.productionPlans = {
                        newPlan: newPlan,
                        totalProductionQuantity: totalProductionQuantity,
                        productName: productName
                    };
                },
                error: function (xhr, status, error) {
                    console.error('에러 발생:', error);
                }
            });
        } else {
            console.log("유효하지 않은 생산 수량입니다.");
        }

    }

    // 착즙기 선택 메서드
    function id34Plan(productName, equipmentId3, equipmentId4, id34Input) {
        $.ajax({
            url: '/api/earliestEndDate',
            type: 'GET',
            data: {
                productName: productName,
                equipmentId3: equipmentId3,
                equipmentId4: equipmentId4,
                input: id34Input
            },
            success: function (data) {
                // DTO를 컨트롤러로 전달하고, 콘솔에 출력
                var equipmentDto = data; // 예상되는 DTO 형식에 맞게 데이터를 가공

                // 시간대로 변환하여 출력
                var startDateKoreanTime = convertToKoreanTime(equipmentDto.estimatedStartDate);
                var endDateKoreanTime = convertToKoreanTime(equipmentDto.estimatedEndDate);

                console.log("설비번호: " + equipmentDto.equipmentId);
                console.log("착즙기 시작 예상 시간: " + startDateKoreanTime);
                console.log("착즙기 종료 예상 시간: " + endDateKoreanTime);
                console.log("착즙기 투입량: " + equipmentDto.input);
                console.log("착즙기 산출량: " + equipmentDto.output);

                // 예상 시작 날짜를 설정
                id34StartDate = equipmentDto.estimatedStartDate;

                // 예상 종료 날짜를 설정
                id34EndDate = equipmentDto.estimatedEndDate;

                // 여과기 투입량 계산
                id9Input = equipmentDto.output;

                // equipmentDto.input을 0.75로 나눈 후 올림하여 id2Input에 저장
                var id2Input = Math.ceil(equipmentDto.input / 0.75);

                // id2 세척 계획 잡기
                id2Plan(id34StartDate, id2Input);

                // id9 여과기 계획 잡기
                id9Plan(id34EndDate, id9Input);
            },
            error: function (xhr, status, error) {
                console.error('에러 발생:', error);
            }
        });
    }

    // 혼합기 계획 잡기
    function zeliPlan(productName, zeliInput){
        $.ajax({
            url: '/api/zeliPlan',
            type: 'GET',
            data: {
                productName: productName,
                input: zeliInput
            },
            success: function (data) {
                saveNewPlan2();
            },
            error: function (xhr, status, error) {
                console.error('에러 발생:', error);
            }
        });

    }

    // 세척 계획 잡기
    function id2Plan(id34StartDate, id2Input) {
        // 산출량 계산
        var id2Output = Math.floor(id2Input * 0.75);
        // 세척 공정에 걸리는 시간 계산
        var cleaningTimeHours = (id2Input / 1000) * 2; // 1000 투입량당 2시간
        var cleaningTimeMinutes = Math.ceil(cleaningTimeHours * 60); // 시간을 분으로 변환하여 올림 처리

        // id34StartDate를 LocalDateTime으로 변환
        var id34StartDateTime = new Date(id34StartDate);

        // 세척 공정 시작 시간을 계산 (id34StartDateTime에서 cleaningTimeMinutes를 빼기)
        var cleaningStartDateTime = new Date(id34StartDateTime.getTime() - cleaningTimeMinutes * 60000);

        // 10분 단위로 내림 처리
        var minutes = cleaningStartDateTime.getMinutes();
        cleaningStartDateTime.setMinutes(minutes - minutes % 10);
        cleaningStartDateTime.setSeconds(0);
        cleaningStartDateTime.setMilliseconds(0);

        // 한국 표준시(KST, GMT+09:00)로 변환
        var koreanTimezoneOffset = 9 * 60; // 한국 표준시의 UTC 오프셋 (분 단위)
        var localTimezoneOffset = cleaningStartDateTime.getTimezoneOffset(); // 현재 지역의 UTC 오프셋 (분 단위)
        var utcTime = cleaningStartDateTime.getTime() + (localTimezoneOffset * 60000); // 현재 시간을 UTC로 변환
        var koreanTime = new Date(utcTime + (koreanTimezoneOffset * 60000)); // 한국 표준시로 변환된 시간 객체

        // 한국 표준시 기준으로 9시간을 더한 ISO 8601 문자열 생성
        var isoStringWithKoreanTime = new Date(koreanTime.getTime() + (9 * 60 * 60 * 1000)).toISOString();

        // AJAX 요청을 통해 데이터를 백엔드로 전송하고 응답을 받음
        $.ajax({
            url: '/api/id2Plan',
            type: 'GET',
            data: {
                id2Input: id2Input,
                id2Output: id2Output,
                cleaningTimeMinutes: cleaningTimeMinutes,
                cleaningStartDateTime: isoStringWithKoreanTime // 한국 표준시 기준으로 9시간을 더한 ISO 문자열 전송
            },
            success: function (data) {
                // 세척 계획 시작 시간을 한국 시간대로 변환하여 출력
                var startDateKoreanTime = convertToKoreanTime(data.estimatedStartDate);
                // 세척 계획 종료 시간을 한국 시간대로 변환하여 출력
                var endDateKoreanTime = convertToKoreanTime(data.estimatedEndDate);

                // 응답 데이터 콘솔 출력
                console.log("설비 번호: " + data.equipmentId);
                console.log("세척 계획 시작 시간: " + startDateKoreanTime);
                console.log("세척 계획 종료 시간: " + endDateKoreanTime);
                console.log("세척 공정 투입량: " + data.input);
                console.log("세척 공정 산출량: " + data.output);

                id2StartDate = data.estimatedStartDate;
                id1Input = data.input;

                // 발주 계획 잡기
                id1Plan(id2StartDate, id1Input);
            },
            error: function (xhr, status, error) {
                console.error('Error:', error);
            }
        });
    }

    // 발주 계획 잡기
    function id1Plan(id2StartDate, id1Input) {

        var id1EndDate; // 발주 종료 시간을 저장할 변수

        // 세척 공정 시작 시간(id2StartDate)을 Date 객체로 변환
        var startDate = new Date(id2StartDate);
        startDate.setTime(startDate.getTime() + (9 * 60 * 60 * 1000)); // 9시간을 더함

        // 세척 공정 시작 날짜의 오전 9시 시간
        var cleaningStart9AM = new Date(startDate);
        cleaningStart9AM.setHours(9, 0, 0, 0); // 세척 공정 시작일의 09:00:00

        // 세척 공정 시작 날짜의 1일 전 오전 9시 시간
        var cleaningStartYesterday9AM = new Date(cleaningStart9AM);
        cleaningStartYesterday9AM.setDate(cleaningStart9AM.getDate() - 1); // 전날 09:00:00

        // 설비2가 시작하는 시간이 오늘 오전 9시 이전이면
        if (startDate < cleaningStart9AM) {
            id1EndDate = new Date(cleaningStartYesterday9AM.getTime()); // 전날 09:00:00에서
        } else {
            id1EndDate = new Date(cleaningStart9AM.getTime()); // 오늘 09:00:00에서
        }

        // 발주 시작 시간은 발주 종료 시간에서 1일 21시간 후의 시간으로 설정
        var id1StartDate = new Date(id1EndDate.getTime() - (24 * 60 * 60 * 1000) - (21 * 60 * 60 * 1000));

        // 날짜를 ISO 8601 형식으로 변환
        var isoEstimatedStartDate = id1StartDate.toISOString();
        var isoEstimatedEndDate = id1EndDate.toISOString();

        // AJAX 요청을 통해 데이터를 백엔드로 전송하고 응답을 받음
        $.ajax({
            url: '/api/id1Plan',
            type: 'GET',
            data: {
                estimatedStartDate: isoEstimatedStartDate,
                estimatedEndDate: isoEstimatedEndDate,
                output: id1Input // 발주 산출량 설정
            },
            success: function (data) {

                // 콘솔에 출력
                console.log("발주 계획");
                console.log("설비 번호: " + data.equipmentId);
                console.log("발주 시작 시간: " + convertToKoreanTime(data.estimatedStartDate));
                console.log("발주 종료 시간: " + convertToKoreanTime(data.estimatedEndDate));
                console.log("발주 산출량: " + data.output);
            },
            error: function (xhr, status, error) {
                console.error('Error:', error);
            }
        });
    }

    // id9 여과기 계획 잡기
    function id9Plan(id34EndDate, id9Input) {
        // AJAX 요청 설정
        $.ajax({
            url: '/api/id9Plan',
            type: 'GET',
            data: {
                id34EndDate: id34EndDate,
                id9Input: id9Input
            },
            success: function(data) {
                // 성공적으로 데이터를 받았을 때 실행되는 함수
                console.log("여과기 계획");
                console.log("설비 번호: " + data.equipmentId);
                console.log("여과기 시작 시간: " + convertToKoreanTime(data.estimatedStartDate));
                console.log("여과기 종료 시간: " + convertToKoreanTime(data.estimatedEndDate));
                console.log("여과기 투입량: " + data.input);
                console.log("여과기 산출량: " + data.output);

                id56EndDate = data.estimatedEndDate;
                id56Input = data.output;

                id56Plan(id56EndDate, id56Input);
            },
            error: function(xhr, status, error) {
                // AJAX 요청 중 에러가 발생했을 때 실행되는 함수
                console.error('AJAX Error:', status, error);
            }
        });
    }

    // id56 살균기 계획 잡기
    function id56Plan(id56EndDate, id56Input) {
        // AJAX 요청 설정
        $.ajax({
            url: '/api/id56Plan',
            type: 'GET',
            data: {
                id56EndDate: id56EndDate,
                id56Input: id56Input
            },
            success: function(data) {
                // 성공적으로 데이터를 받았을 때 실행되는 함수
                console.log("살균기 계획");
                console.log("설비 번호: " + data.equipmentId);
                console.log("살균기 시작 시간: " + convertToKoreanTime(data.estimatedStartDate));
                console.log("살균기 종료 시간: " + convertToKoreanTime(data.estimatedEndDate));
                console.log("살균기 투입량: " + data.input);
                console.log("살균기 산출량: " + data.output);

                id10Input = data.output;
                id10StartDate = data.estimatedEndDate

                // 충진기1,2 계획
                id10Plan(id10StartDate, id10Input);
            },
            error: function(xhr, status, error) {
                // AJAX 요청 중 에러가 발생했을 때 실행되는 함수
                console.error('AJAX Error:', status, error);
            }
        });
    }

    // 충진기1,2(즙) 계획
    function id10Plan(id10StartDate, id10Input) {
        $.ajax({
            url: '/api/id10Plan',
            type: 'GET',
            data: {
                id10StartDate: id10StartDate,
                id10Input: id10Input
            },
            success: function(data) {
                // 성공적으로 데이터를 받았을 때 실행되는 함수
                console.log("충진기 계획");
                console.log("설비 번호: " + data.equipmentId);
                console.log("충진기 시작 시간: " + convertToKoreanTime(data.estimatedStartDate));
                console.log("충진기 종료 시간: " + convertToKoreanTime(data.estimatedEndDate));
                console.log("충진기 투입량: " + data.input);
                console.log("충진기 산출량: " + data.output);
                
                id13Input = data.output;
                id13StartDate = data.estimatedEndDate;
                
                // 검사기 계획
                id13Plan(id13StartDate, id13Input);
            },
            error: function(xhr, status, error) {
                // AJAX 요청 중 에러가 발생했을 때 실행되는 함수
                console.error('AJAX Error:', status, error);
            }
        });
    }

    // 설비13 계획
    function id13Plan(id13StartDate, id13Input){
        $.ajax({
            url: '/api/id13Plan',
            type: 'GET',
            data: {
                id13StartDate: id13StartDate,
                id13Input: id13Input
            },
            success: function(data) {
                // 성공적으로 데이터를 받았을 때 실행되는 함수
                console.log("검사기 계획");
                console.log("설비 번호: " + data.equipmentId);
                console.log("검사기 시작 시간: " + convertToKoreanTime(data.estimatedStartDate));
                console.log("검사기 종료 시간: " + convertToKoreanTime(data.estimatedEndDate));
                console.log("검사기 투입량: " + data.input);
                console.log("검사기 산출량: " + data.output);

                id12Input = data.output;
                id12StartDate = data.estimatedEndDate;

                // Box포장기 계획
                id12Plan(id12StartDate, id12Input);
            },
            error: function(xhr, status, error) {
                // AJAX 요청 중 에러가 발생했을 때 실행되는 함수
                console.error('AJAX Error:', status, error);
            }
        });
    }

    // Box포장기 계획
    function id12Plan(id12StartDate, id12Input){
        $.ajax({
            url: '/api/id12Plan',
            type: 'GET',
            data: {
                id12StartDate: id12StartDate,
                id12Input: id12Input
            },
            success: function(data) {
                // 성공적으로 데이터를 받았을 때 실행되는 함수
                console.log("Box포장기 계획");
                console.log("설비 번호: " + data.equipmentId);
                console.log("Box포장기 시작 시간: " + convertToKoreanTime(data.estimatedStartDate));
                console.log("Box포장기 종료 시간: " + convertToKoreanTime(data.estimatedEndDate));
                console.log("Box포장기 투입량: " + data.input);
                console.log("Box포장기 산출량: " + data.output);

                saveNewPlan();
            },
            error: function(xhr, status, error) {
                // AJAX 요청 중 에러가 발생했을 때 실행되는 함수
                console.error('AJAX Error:', status, error);
            }
        });
    }

    // UTC를 한국 시간대로 변환하는 함수
    function convertToKoreanTime(utcDateTime) {
        var date = new Date(utcDateTime);
        var koreanTime = new Date(date.getTime() + (9 * 60 * 60 * 1000)); // 9시간을 밀리초로 계산하여 더함
        return koreanTime.toLocaleString('ko-KR', { timeZone: 'Asia/Seoul' });
    }
    
    // 즙 생산계획 저장
    function saveNewPlan() {
        $.ajax({
            url: '/api/saveNewPlan',
            type: 'POST',
            success: function () {
                console.log("생산계획이 성공적으로 저장되었습니다.");
                saveAllEquipmentPlans();
            },
            error: function (xhr, status, error) {
                console.error('Error:', error);
            }
        });
    }

    // 즙 설비계획 저장
    function saveAllEquipmentPlans() {
        $.ajax({
            url: '/api/saveAllEquipmentPlans',
            type: 'POST',
            success: function () {
                console.log("설비계획이 성공적으로 저장되었습니다.");
            },
            error: function (xhr, status, error) {
                console.error('Error:', error);
            }
        });
    }

    // 스틱 생산계획 저장
    function saveNewPlan2() {
        $.ajax({
            url: '/api/saveNewPlan',
            type: 'POST',
            success: function () {
                console.log("생산계획이 성공적으로 저장되었습니다.");
                saveAllEquipmentPlans2();
            },
            error: function (xhr, status, error) {
                console.error('Error:', error);
            }
        });
    }

    // 스틱 설비계획 저장
    function saveAllEquipmentPlans2() {
        $.ajax({
            url: '/api/saveAllEquipmentPlans2',
            type: 'POST',
            success: function () {
                console.log("설비계획이 성공적으로 저장되었습니다.");
            },
            error: function (xhr, status, error) {
                console.error('Error:', error);
            }
        });
    }
});
