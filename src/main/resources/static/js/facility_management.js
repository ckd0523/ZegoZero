$(document).ready(function() {

    var table;

    // 초기화 함수 정의
    function initializeDataTable(url) {
        if (table) {
            table.destroy(); // 기존 DataTable 객체가 있으면 파괴
        }

        // DataTable 초기화
        table = $('#myTable').DataTable({
            ajax: {
                url: url, // JSON 데이터 URL
                type: 'GET',
                dataSrc: 'data',
                error: function(xhr, error, thrown) {
                    console.log('Ajax error:', xhr.responseText);
                }
            },
            responsive: true,
            orderMulti: true,
            columns: [
                {
                    // 체크박스 열
                    orderable: false,
                    render: function(data, type, full, meta) {
                        return '<input class="checkbox" type="checkbox" value="' + full.id + '">';
                    }
                },
                { data: 'equipment_plan_id' },
                { data: 'product_name' },
                { data: 'input' },
                { data: 'output' },
                { data: 'estimated_start_date' },
                { data: 'estimated_end_date' },
                { data: 'start_date' },
                { data: 'end_date' }
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

        // 체크박스 클릭 이벤트 설정
        $('#myTable').on('click', '.checkbox', function() {
            var isChecked = $(this).prop('checked');

            // 모든 체크박스의 체크 상태 해제
            $('.checkbox').prop('checked', false);

            // 클릭된 체크박스의 체크 상태 설정
            $(this).prop('checked', isChecked);
        });

        // 열 선택 버튼 클릭 시 이벤트 처리
        $('#myTable').on('click', '.colVisButton', function() {
            table.buttons(['.buttons-colvis']).trigger();
        });
    }

    // 셀렉트 박스 변경 이벤트 리스너
    $('#equipmentSelect').on('change', function() {
        var selectedValue = $(this).val();

        // 선택된 값에 따라 JSON 데이터 URL 변경
        var jsonUrl = '/api/equipment/' + selectedValue;

        // DataTable 초기화 함수 호출
        initializeDataTable(jsonUrl);
    });

    var selectedValue1 = $(equipmentSelect).val();
    // 페이지 로드 시 초기화
    initializeDataTable('/api/equipment/'+ selectedValue1 ); // 초기에는 기본 데이터로 초기화

    // START 버튼 클릭 시 이벤트 처리
    $('#startButton').on('click', function() {
        // 체크된 체크박스 가져오기
        var checkedBox = $('.checkbox:checked');

        if (checkedBox.length === 1) {
            var selectedRow = checkedBox.closest('tr');
            var rowData = table.row(selectedRow).data();
            var selectedValue = $('#equipmentSelect').val();

            console.log('선택된 행의 데이터:', rowData);
            console.log('선택된 설비 start :', selectedValue);

            // 여기서 선택된 데이터에 대한 추가적인 동작을 수행할 수 있습니다.
            // 예: 선택된 데이터를 서버로 전송하여 처리하는 등의 작업
            

        } else {
            alert('하나의 행을 선택해야 합니다.');
        }
    });

    // STOP 버튼 클릭 시 이벤트 처리
    $('#stopButton').on('click', function() {
        // 체크된 체크박스 가져오기
        var checkedBox = $('.checkbox:checked');

        if (checkedBox.length === 1) {
            var selectedRow = checkedBox.closest('tr');
            var rowData = table.row(selectedRow).data();
            var selectedValue = $('#equipmentSelect').val();

            console.log('선택된 행의 데이터:', rowData);
            console.log('선택된 설비 stop :', selectedValue);

            // 여기서 선택된 데이터에 대한 추가적인 동작을 수행할 수 있습니다.
            // 예: 선택된 데이터를 서버로 전송하여 처리하는 등의 작업

            if (selectedValue == '12') {
                var finishData = {
                    order_id: rowData.equipment_plan_id,
                    product_name: rowData.product_name,
                    received_quantity: rowData.quantity
                };

                $.ajax({
                    type: "POST",
                    url: "/api/receive",
                    data: JSON.stringify(finishData),
                    contentType: "application/json",
                    success: function(response) {
                        alert("박스 포장 종료, 완재품 재고로 입고되었습니다.");
                    },
                    error: function(xhr, status, error) {
                        alert("시간 저장 중 오류가 발생했습니다.");
                    }
                });
            }
        } else {
            alert('하나의 행을 선택해야 합니다.');
        }
    });

});
