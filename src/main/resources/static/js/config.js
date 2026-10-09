async function preProcessAll(){

    const result = document.getElementById("result-preprocess");
    const tableBody = document.getElementById("preprocess-summary-table-body");

    tableBody.innerHTML = "";
    result.textContent = "Ожидайте...";

    try{
        const response = await fetch(`/api/mi/preprocess/all`, {
        method: "POST"
        });

        const data = await response.json();

        if (!response.ok) {
            result.textContent = data.message ?? "Ответ содержит ошибку";
            return;
        }

        tableBody.innerHTML = "";
        const total = Object.values(data).reduce((sum, count) => sum + count, 0);
        result.textContent = `Обработано СИ: ${total}`;

        Object.entries(data).forEach(([status, count]) => {
            const row = document.createElement("tr");

            const statusCell = document.createElement("td");
            statusCell.textContent = status;
            row.appendChild(statusCell);

            const countCell = document.createElement("td");
            countCell.textContent = count;
            row.appendChild(countCell);

            tableBody.appendChild(row);
        });

    }catch(error){
        console.error(error);
        result.textContent = "Ошибка обращения к серверу";
    }
}

async function loadFromXlsx(){
    const result = document.getElementById("result-preprocess");
    const tableBody = document.getElementById("preprocess-summary-table-body");

    tableBody.innerHTML = "";
    result.textContent = "Ожидайте...";

    try{
        const response = await fetch(`/api/mi/fill-in-massive`, {
        method: "POST"
        });

        result.textContent = "Создано... "

    }catch(error){
        console.error(error);
        result.textContent = "Ошибка обращения к серверу";
    }
}