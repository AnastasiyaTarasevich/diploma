fetch('/supplier-defects')
    .then(response => response.json())
    .then(data => {
        // Данные успешно получены, используйте их для построения диаграммы
        console.log(data); // Проверьте данные в консоли браузера
        renderChart(data);
    })
    .catch(error => {
        console.error('Error fetching data:', error); // В случае ошибки выведите сообщение об ошибке
    });

// Функция для генерации случайного цвета
function getRandomColor() {
    var letters = '0123456789ABCDEF';
    var color = '#';
    for (var i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

// Функция для построения диаграммы


function renderChart(data) {
    var suppliers = Object.keys(data);
    var allMonths = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
    var monthsWithData = [];

    // Определяем месяцы, для которых есть данные по всем поставщикам
    suppliers.forEach(function (supplier) {
        Object.keys(data[supplier]).forEach(function (month) {
            var formattedMonth = month.charAt(0).toUpperCase() + month.slice(1).toLowerCase();
            if (!monthsWithData.includes(formattedMonth)) {
                monthsWithData.push(formattedMonth);
            }
        });
    });

    var chartData = {
        labels: monthsWithData,
        datasets: suppliers.map(function (supplier) {
            var defectCounts = monthsWithData.map(function (month) {
                return data[supplier][month.toUpperCase()] || 0;
            });

            return {
                label: supplier,
                backgroundColor: getRandomColor(),
                borderColor: 'rgb(255, 99, 132)',
                data: defectCounts
            };
        })
    };

    var options = {
        scales: {
            yAxes: [{
                ticks: {
                    beginAtZero: true
                }
            }]
        }
    };

    var ctx = document.getElementById('defectsChart').getContext('2d');

    var defectsChart = new Chart(ctx, {
        type: 'bar',
        data: chartData,
        options: options
    });
}