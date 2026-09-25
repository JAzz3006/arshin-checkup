    async function getAllMIVerificationNeeded() {

        const result = document.getElementById("result-unverified");
        const tableBody = document.getElementById("mi-table-body");

             result.textContent = "Ожидайте...";

        try {
            const response = await fetch(`/api/mi/v-needed`, {
                method: "GET"
            });

            const data = await response.json();

            if (!response.ok) {
                result.textContent = data.message ?? "Ответ содержит ошибку";
                return;
            }

            tableBody.innerHTML = "";

            result.textContent = `Найдено записей: ${data.totalElements}`;

            data.miResponses    .forEach(mi => {
                const row = document.createElement("tr");

                const boxCell = document.createElement("td");
                    const checkbox = document.createElement("input");
                    checkbox.type = "checkbox";
                    checkbox.value = mi.id;
                    boxCell.appendChild(checkbox)
                row.appendChild(boxCell);

                const idCell = document.createElement("td");
                idCell.textContent = mi.id ?? "-";
                row.appendChild(idCell);

                const modelCell = document.createElement("td");
                modelCell.textContent = mi.model ?? "-";
                row.appendChild(modelCell);

                const numberCell = document.createElement("td");
                numberCell.textContent = mi.serialNumber ?? "-";
                row.appendChild(numberCell);

                const verificationCell = document.createElement("td");
                verificationCell.textContent = mi.verificationDate ?? "-";
                row.appendChild(verificationCell);

                const validCell = document.createElement("td");
                validCell.textContent = mi.validDate ?? "-";
                row.appendChild(validCell);

                const organizationCell = document.createElement("td");
                organizationCell.textContent = mi.organization ?? "-";
                row.appendChild(organizationCell);

                const siteCell = document.createElement("td");
                siteCell.textContent = mi.site ?? "-";
                row.appendChild(siteCell);

                const settlementCell = document.createElement("td");
                settlementCell.textContent = mi.settlement ?? "-";
                row.appendChild(settlementCell);

                const addressCell = document.createElement("td");
                addressCell.textContent = mi.address ?? "-";
                row.appendChild(addressCell);

                tableBody.appendChild(row);
            });

        } catch (error) {
            result.textContent = "Ошибка обращения к серверу";
        }
    }
function checkSelectedMI() {

    const selectedCheckboxes = document.querySelectorAll(
        '#mi-table-body input[type="checkbox"]:checked'
    );

    const selectedIds = Array.from(selectedCheckboxes)
        .map(checkbox => Number(checkbox.value));

    if (selectedIds.length === 0) {
        alert("Выберите средства измерения для проверки");
        return;
    }

    console.log(selectedIds);
}

document.addEventListener("DOMContentLoaded", () => {
    getAllMIVerificationNeeded();
});