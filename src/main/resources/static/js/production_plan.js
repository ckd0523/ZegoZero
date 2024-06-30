$(document).ready(function () {
    var calendarEl = document.getElementById('calendar');
    var calendar = new FullCalendar.Calendar(calendarEl, {
        headerToolbar: {
            left: 'prev,next today', // 왼쪽에 표시될 버튼
            center: 'title', // 중앙에 표시될 제목
            right: 'dayGridMonth,timeGridWeek,timeGridDay,listMonth' // 오른쪽에 표시될 버튼
        },
        locale: 'ko',
        navLinks: true, // can click day/week names to navigate views
        businessHours: true, // display business hours
        editable: true,
        selectable: true,
        events: '/api/fullCalendar'
    });
    calendar.render();
});
