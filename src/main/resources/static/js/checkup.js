import { formatDate } from "./utils.js";
let pageNumber = 0;
let pageCount = 0;
async function getAllMIVerificationNeeded(page = 0) {

    const result = document.getElementById("result-unverified");
    const tableBody = document.getElementById("mi-table-body");
    const pageNumberElement = document.getElementById("page-number");
    const pageCountElement = document.getElementById("total-page-number");

    tableBody.innerHTML = "";
    result.textContent = "Ожидайте...";
    pageNumberElement.textContent = "";
    pageCountElement.textContent = "";

    try {
         const response = await fetch(`/api/mi/v-needed?page=${page}`, {
             method: "GET",
             cache: "no-store"
         });

         const data = await response.json();

         if (!response.ok) {
             result.textContent = data.message ?? "Ответ содержит ошибку";
             return;
         }

         result.textContent = `Найдено записей: ${data.totalElements}`;
         pageNumber = data.page;
         pageCount = data.totalPages;
         pageNumberElement.textContent = pageCount === 0 ? 0 : pageNumber + 1;
         pageCountElement.textContent = pageCount;

         updatePaginationButtons()

         data.miResponses.forEach(mi => {
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
             verificationCell.textContent = formatDate(mi.verificationDate);
             row.appendChild(verificationCell);

             const validCell = document.createElement("td");
             validCell.textContent = formatDate(mi.validDate);
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

async function checkSelectedMI() {

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

    const response = await fetch(`/api/mi/verify-group`,{
        method: "POST",
        headers:{
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            ids: selectedIds
        })
    });

    const data = await response.json();
    console.log(data)

    sessionStorage.setItem(
        "verificationResults",
        JSON.stringify(data),
    );
    sessionStorage.setItem(
            "returnPage",
            pageNumber
    );

    window.location.href = "/results.html";
}

function nextPage(){
    if(pageNumber < pageCount - 1){
        getAllMIVerificationNeeded(pageNumber + 1)
    }
}

function previousPage(){
    if(pageNumber > 0){
        getAllMIVerificationNeeded(pageNumber - 1)
    }
}

function updatePaginationButtons() {
    const backward = document.getElementById("page-backward");
    const forward = document.getElementById("page-forward");

    backward.classList.toggle("disabled", pageNumber === 0);
    forward.classList.toggle("disabled", pageNumber >= pageCount - 1);
}

document.addEventListener("DOMContentLoaded", () => {
    //console.log("DOMContentLoaded сработал");
    document.getElementById("check-selected")
        .addEventListener("click", checkSelectedMI);

    document.getElementById("page-backward")
        .addEventListener("click", (event) => {
            event.preventDefault();
            if (event.currentTarget.classList.contains("disabled")) return;
            previousPage();
        });


    document.getElementById("page-forward")
        .addEventListener("click", (event) =>{
            event.preventDefault();
            if (event.currentTarget.classList.contains("disabled")) return;
            nextPage();
        });
});

window.addEventListener("pageshow", () => {
    const params = new URLSearchParams(window.location.search);
    const page = Number(params.get("page") ?? 0);
    getAllMIVerificationNeeded(
        Number.isInteger(page) && page >=0 ? page : 0
    );
});