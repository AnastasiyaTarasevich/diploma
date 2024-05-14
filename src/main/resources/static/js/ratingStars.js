function fetchSupplierRatings() {
    // Отправляем GET запрос на сервер для получения данных о рейтингах
    fetch('/ratingStarts') // Исправленный URL
        .then(response => response.json()) // Преобразуем ответ в JSON
        .then(data => {
            // После получения данных вызываем функцию для их отображения
            showSupplierRatings(data);

        })
        .catch(error => console.error('Ошибка при получении данных:', error));
}

function showSupplierRatings(data) {
    var container = document.getElementById("supplierRatings");
    var row = document.createElement("div");
    row.classList.add("row");
    container.appendChild(row);

    for (var supplier in data) {
        if (data.hasOwnProperty(supplier)) {
            var ratingBlock = document.createElement("div");
            ratingBlock.classList.add("supplier-rating", "col-md-auto", "mb-3", "text-center");

            var nameElement = document.createElement("span");
            nameElement.classList.add("supplier-name", "font-weight-bold");
            nameElement.textContent = supplier;
            ratingBlock.appendChild(nameElement);

            var ratingElement = document.createElement("div");
            ratingElement.classList.add("rating", "text-warning");
            ratingElement.innerHTML = '<i class="fas fa-star"></i>' + data[supplier];

            ratingBlock.appendChild(ratingElement);
            row.appendChild(ratingBlock);
        }
    }
}


// Пример использования функции
document.addEventListener("DOMContentLoaded", function() {
    // Вызываем функцию для получения данных о рейтингах с сервера
    fetchSupplierRatings();
});
