fetch('/delivered_categories')
    .then(response => response.json())
    .then(data => {
        console.log(data); // Проверяем данные в консоли браузера
        renderChart2(data);
    })
    .catch(error => {
        console.error('Error fetching data:', error);
    });


// Функция для построения круговой диаграммы
function renderChart2(data) {
    var ctx = document.getElementById('deliveredProductsChart').getContext('2d');

    if (window.deliveredProductsChart instanceof Chart) {
        // Если да, уничтожаем его
        window.deliveredProductsChart.destroy();
    }

    window.deliveredProductsChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: Object.keys(data),
            datasets: [{
                data: Object.values(data),
                backgroundColor: Object.keys(data).map(key => getRandomColor()),
            }]
        },
        options: {
            responsive: true, // Разрешить масштабирование
            width: 100, // Задать ширину диаграммы
            height: 100 // Задать высоту диаграммы
        }
    });
    var chartTitle = document.getElementById('chart2Title');
    chartTitle.innerText = "Количество приобретенных товаров по категориям";
}


function getRandomColor() {
    var letters = '0123456789ABCDEF';
    var color = '#';
    for (var i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;



}