import { formatDate } from "./utils.js"
function getReport(){

    const storedData = sessionStorage.getItem("verificationResults");
    const tableBody = document.getElementById("report-table-body");
    const stats = document.getElementById("stats");

    if (!storedData) {
            stats.textContent = "Нет данных о результатах проверки";
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
            prevDateCell.textContent = formatDate(r.previousValidDate);
            row.appendChild(prevDateCell);
        const actDateCell = document.createElement("td");
            actDateCell.textContent = formatDate(r.arshinValidDate);
            row.appendChild(actDateCell);

        const resultCountCell = document.createElement("td");
        const resultCountButton = document.createElement("button");
            resultCountButton.type = "button";
            resultCountButton.className = "result-count-button";
            const count = document.createElement("span");
            count.textContent = r.resultCount ?? "-";
            const arrow = document.createElement("span");
            arrow.className = "result-count-arrow";
            arrow.textContent = "⌄";
            resultCountButton.appendChild(count);
            resultCountButton.appendChild(arrow);
            resultCountButton.disabled = !r.options?.length;
            resultCountCell.appendChild(resultCountButton);
            row.appendChild(resultCountCell);
        const optionsRow = document.createElement("tr");
            optionsRow.hidden = true;
            const optionsCell = document.createElement("td");
            optionsCell.colSpan = 12;
            if(r.options?.length){
                optionsCell.appendChild(createOptionsList(r.options, r.id));
            }
            optionsRow.appendChild(optionsCell);
            resultCountButton.addEventListener("click", () =>{
                optionsRow.hidden = !optionsRow.hidden
                arrow.textContent = optionsRow.hidden ? "⌄" : "⌃";
            });


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
        tableBody.appendChild(optionsRow);
    });

    let summaryText = `Проверено в ГИС Аршин ${data.results.length} СИ, из них:\n`;

    Object.entries(data.summary).forEach(([status, count]) => {
        summaryText += `${status}: ${count}\n`;
    });
    stats.textContent = summaryText;
}

function createOptionsList(options, instrumentId){

    let selectedOption = null;

    const container = document.createElement("div");
    container.className = "options-container";

    const title = document.createElement("h4");
    title.textContent = "Записи, найденные в ГИС 'Аршин'";
    container.appendChild(title);
    options.forEach((option) => {
        const label = document.createElement("label");
        label.className = "verification-option";

        const radio = document.createElement("input");
        radio.type = "radio";
        radio.name = `verification-${instrumentId}`;
        radio.value = option["vri_id"];

            radio.addEventListener("change", () => {
                selectedOption = option;
                selectedDate.textContent = formatDate(option["valid_date"]);
                applyButton.disabled = false;
            })

        const description = document.createElement("div");
        const verificationDate = document.createElement("strong");
        verificationDate.textContent = `Поверка от ${formatDate(option["verification_date"])}`;

        const details = document.createElement("p");
        details.textContent = `Действует до ${formatDate(option.valid_date)} ·` +
        `Обозначение типа СИ: ${option["mi.mitype"] ?? "-"} · ` +
        `Модификация СИ: ${option["mi.modification"] ?? "-"} · ` +
        `Заводской № ${option["mi.number"]} · ` +
        `Регистрационный номер типа СИ ${option["mi.mitnumber"]}`;

        description.appendChild(verificationDate);
        description.appendChild(details);

        label.appendChild(radio);
        label.appendChild(description);

        container.appendChild(label);
    });

    const actionPanel = document.createElement("div");
    actionPanel.className = "verification-action-panel";

    const dateBlock = document.createElement("div");
    dateBlock.className = "verification-date-block";

    const dateLabel = document.createElement("span");
    dateLabel.className = "verification-date-label";
    dateLabel.textContent = "Новая дата действительности";

    const selectedDate = document.createElement("strong");
    selectedDate.className = "verification-selected-date";
    selectedDate.textContent = "Выберите поверку";

    dateBlock.appendChild(dateLabel);
    dateBlock.appendChild(selectedDate);

    const applyButton = document.createElement("button");
    applyButton.type = "button";
    applyButton.className = "apply-verification-button";
    applyButton.textContent = "✓ Применить";
    applyButton.disabled = true;

    actionPanel.appendChild(dateBlock);
    actionPanel.appendChild(applyButton);
    container.appendChild(actionPanel);

    applyButton.addEventListener("click", async () => {
        if(!selectedOption) return;
        const optionToApply = selectedOption;
        applyButton.disabled = true;
        try{
            const response = await fetch(`/api/mi/${instrumentId}`, {
                method: "PUT",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    validDate: optionToApply["valid_date"],
                    verificationDate: optionToApply["verification_date"]
                })
            })
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }
            selectedDate.textContent = `Дата действительности СИ ${optionToApply["mi.mitype"]} · № ${optionToApply["mi.number"]} обновлена`;
        }catch(error){
            console.error(error);
            selectedDate.textContent = "Ошибка обновления даты";
            applyButton.disabled = false;
        }
    });

    return container;
}

document.addEventListener("DOMContentLoaded", () => {
    getReport();
    document.getElementById("go-back").addEventListener("click", (event) => {
        event.preventDefault();
        const page = sessionStorage.getItem("returnPage") ?? "0";
        window.location.href = `/checkup.html?page=${page}`;
    });

});