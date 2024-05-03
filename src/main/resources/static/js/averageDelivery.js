fetch('/average_delivery_time_by_supplier')
    .then(response => response.json())
    .then(data => {
        // После получения данных строим график
        renderChart3(data);
    })
    .catch(error => {
        console.error('Error fetching data:', error);
    });

// Переменная для хранения ссылки на график

// Функция для построения графика
async function renderChart3(data) {
    // Получаем контекст для рисования на элементе canvas
    var ctx = document.getElementById('deliveryTimeChart').getContext('2d');

    // Уничтожаем предыдущий график, если он существует
    if (window.deliveryTimeChart instanceof Chart) {
        // Если да, уничтожаем его
        window.deliveryTimeChart.destroy();
    }

    // Создаем новый график с полученными данными
    window.deliveryTimeChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: Object.keys(data), // Названия поставщиков
            datasets: [{
                label: 'Average Delivery Time (days)', // Название набора данных
                data: Object.values(data), // Среднее время доставки для каждого поставщика
                backgroundColor: 'rgba(54, 162, 235, 0.5)', // Цвет столбцов
                borderColor: 'rgba(54, 162, 235, 1)', // Цвет границ столбцов
                borderWidth: 1
            }]
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true // Начинать ось Y с 0
                }
            }
        }
    });
    var chartTitle = document.getElementById('chart3Title');
    chartTitle.innerText = "Среднее время доставки товаров по поставщикам";
}
