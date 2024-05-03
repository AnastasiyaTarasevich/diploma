var chart2 = new CanvasJS.Chart("chartContainer2", {
    theme: "light2",
    title:{
        text: "Общая сумма закупок по поставщикам"
    },
    axisY:{
        title: "Purchase Volume"
    },
    axisX:{
        title: "Supplier"
    },
    data: [{
        type: "bar",
        indexLabel: "{y}",
        indexLabelFontColor: "white",
        indexLabelPlacement: "inside",
        dataPoints: [] // Пустой массив, будет заполнен данными с сервера
    }]
});

// Отправка запроса на сервер для получения данных
fetch('/purchase-volume-by-supplier')
    .then(response => response.json())
    .then(data => {
        // Создаем массив объектов для данных горизонтальной диаграммы
        var chartData = [];
        for (var supplier in data) {
            chartData.push({ y: data[supplier], label: supplier });
        }

        // Обновляем данные диаграммы
        chart2.options.data[0].dataPoints = chartData;

        // Перерисовываем диаграмму
        chart2.render();
    })
    .catch(error => {
        console.error('Error fetching data:', error);
    });
