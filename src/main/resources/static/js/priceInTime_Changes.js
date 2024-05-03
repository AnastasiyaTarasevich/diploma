async function fetchSuppliers() {
    try {
        const response = await fetch('/all'); // Замените '/api/suppliers' на ваш эндпоинт
        if (!response.ok) {
            throw new Error('Failed to fetch data');
        }

        return await response.json();
    } catch (error) {
        console.error('Error fetching data:', error.message);
        return []; // Возвращаем пустой массив в случае ошибки
    }
}

// Создание кнопок для каждого поставщика и построение графиков при клике
async function createSupplierButtonsAndCharts() {
    const suppliers = await fetchSuppliers();
    const buttonsContainer = document.getElementById('buttons');

    suppliers.forEach(supplier => {
        const button = document.createElement('button');
        button.textContent = supplier.name;
        button.addEventListener('click', () => {
            buildChart('myChart', supplier);
        });
        buttonsContainer.appendChild(button);
    });
}

// Функция для построения графика
function buildChart(canvasId, supplier) {
    const ctx = document.getElementById(canvasId).getContext('2d');

    // Создаем массив для хранения всех дат изменений цен всех продуктов
    let allDates = new Set();

    // Собираем все даты изменений цен всех продуктов
    supplier.products.forEach(product => {
        product.prices.forEach(price => {
            allDates.add(new Date(price.changeDate).toISOString().slice(0, 10)); // Преобразуем строки в объекты Date
        });
    });

    // Сортируем даты по возрастанию
    allDates = Array.from(allDates).sort();

    // Создаем массив меток для оси X
    const labels = allDates;

    // Создаем набор данных для каждого продукта
    const datasets = supplier.products.map(product => {
        const productPrices = [];

        // Заполняем массив цен для каждой даты
        labels.forEach(date => {
            const price = product.prices.find(price => price.changeDate === date);
            productPrices.push(price ? price.newPrice : NaN);
        });

        return {
            label: product.name,
            data: productPrices,
            borderColor: getRandomColor(),
            borderWidth: 1
        };
    });

    // Проверяем, был ли уже создан график
    if (window.myChart instanceof Chart) {
        // Если да, уничтожаем его перед созданием нового
        window.myChart.destroy();
    }

    // Создаем новый график
    window.myChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: datasets
        },
        options: {
            scales: {
                xAxes: [{
                    type: 'time',
                    time: {
                        unit: 'month'
                    }
                }],
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            }
        }
    });
    var chartTitle = document.getElementById('chart4Title');
    chartTitle.innerText = "Изменение цен на товары у поставщиков во времени";
}




// Генерация случайного цвета
function getRandomColor() {
    const letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

// Запуск процесса создания кнопок и построения графиков при загрузке страницы
createSupplierButtonsAndCharts();