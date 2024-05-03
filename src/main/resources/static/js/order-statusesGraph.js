var chart1 = new CanvasJS.Chart("chartContainer", {
    theme: "light2",
    title:{
        text: "Воронка статусов заказов"
    },
    data: [{
        type: "funnel",
        indexLabel: "{label} {y}%",
        neckHeight: 0,
        toolTipContent: "{label} - {y}%",
        dataPoints: [] // Пустой массив, будет заполнен данными с сервера
    }]
});

// Отправка запроса на сервер для получения данных
fetch('/order-status-count')
    .then(response => response.json())
    .then(data => {
        // Создаем массив объектов для данных воронки
        var funnelData = [];
        for (var status in data) {
            funnelData.push({ y: data[status], label: status });
        }

        // Обновляем данные диаграммы
        chart1.options.data[0].dataPoints = funnelData;

        // Перерисовываем диаграмму
        chart1.render();
    })
    .catch(error => {
        console.error('Error fetching data:', error);
    });
