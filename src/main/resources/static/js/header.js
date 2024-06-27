$(document).ready(function () {

    // 시간 불러오기
    $.ajax({
        url: "/api/time", // 시간 데이터를 가져올 엔드포인트
        type: "GET",
        success: function(response) {
            // 응답 처리
            $("#time").val(response); // 시간 입력란에 불러온 시간 채우기
        },
        error: function(xhr, status, error) {
            console.error("Error: " + error);
        }
    });

    // 시간 저장
    $('#timesave').on('click', function() {
        console.log("clicked");
        var inputTime = $('#time').val();

        var systemTimeDto = {
            time: inputTime
        };

        $.ajax({
            type: "POST",
            url: "/api/timesave",
            data: JSON.stringify(systemTimeDto),
            contentType: "application/json",
            success: function (response) {
                alert("시간이 성공적으로 저장되었습니다.");
            },
            error: function (xhr, status, error) {
                alert("시간 저장 중 오류가 발생했습니다.");
            }
        });

    });

});