$(document).ready(function () {
    var calendarEl = document.getElementById('calendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
        headerToolbar: {
            left: 'prev,next today', // 왼쪽에 표시될 버튼
            center: 'title', // 중앙에 표시될 제목
            right: 'dayGridMonth,timeGridWeek,timeGridDay' // 오른쪽에 표시될 버튼
        },
        locale: 'ko',
        selectable: false,
        editable: false,
        events: '/api/fullCalendar'
        // eventSources: [
        //     'MOCK_DATA4.json'
        // ]
    });
    calendar.render();
});