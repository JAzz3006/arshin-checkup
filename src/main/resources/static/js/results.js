function getReport(){

    const storedData = sessionStorage.getItem("verificationResults");
    const tableBody = document.getElementById("report-table-body");
    const stats = document.getElementById("stats");

    if (!storedData) {
            result.textContent = "Нет данных о результатах проверки";
            return;
    }

    const data = JSON.parse(storedData);
    tableBody.innerHTML = "";

    data.results.forEach(r => {
        const row = document.createElement("tr");

        const idCell = document.createElement("td");
            idCell.textContent = r.id ?? "-";
            row.appendChild(idCell);
        const modelCell = document.createElement("td");
            modelCell.textContent = r.model ?? "-";
            row.appendChild(modelCell);
        const numberCell = document.createElement("td");
            numberCell.textContent = r.serialNumber ?? "-";
            row.appendChild(numberCell);
        const statusCell = document.createElement("td");
            statusCell.textContent = r.status ?? "-";
            row.appendChild(statusCell);
        const msgCell = document.createElement("td");
            msgCell.textContent = r.message ?? "-";
            row.appendChild(msgCell);
        const prevDateCell = document.createElement("td");
            prevDateCell.textContent = r.previousValidDate ?? "-";
            row.appendChild(prevDateCell);
        const actDateCell = document.createElement("td");
            actDateCell.textContent = r.arshinValidDate ?? "-";
            row.appendChild(actDateCell);
        const resultCountCell = document.createElement("td");
            resultCountCell.textContent = r.resultCount ?? "-";
            row.appendChild(resultCountCell);
        const organizationCell = document.createElement("td");
            organizationCell.textContent = r.organization ?? "-";
            row.appendChild(organizationCell);
        const siteCell = document.createElement("td");
            siteCell.textContent = r.site ?? "-";
            row.appendChild(siteCell);
        const settlementCell = document.createElement("td");
            settlementCell.textContent = r.settlement ?? "-";
            row.appendChild(settlementCell);
        const addressCell = document.createElement("td");
            addressCell.textContent = r.address ?? "-";
            row.appendChild(addressCell);
        tableBody.appendChild(row);
    });

    let summaryText = `Проверено в ГИС Аршин ${data.results.length} СИ, из них:\n`;

    Object.entries(data.summary).forEach(([status, count]) => {
        summaryText += `${status}: ${count}\n`;
    });
    stats.textContent = summaryText;
}

document.addEventListener("DOMContentLoaded", () => {
    getReport();
});