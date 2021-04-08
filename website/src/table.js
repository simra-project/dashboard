let sorttable = require("./sorttable.js")

function generateTableHead(table, tableMeta) {
    const thead = table.createTHead();
    const row = thead.insertRow();
    for (const [key, value] of Object.entries(tableMeta)) {
        const th = document.createElement("th");
        const abbr = document.createElement("abbr")
        abbr.title = value.abbr
        const text = document.createTextNode(value.text);
        abbr.appendChild(text)
        th.appendChild(abbr);
        row.appendChild(th);
    }
}

function generateTable(table, dashboard, tableMeta) {
    for (const region of dashboard.regions) {
        const row = table.insertRow();
        // name column
        const th = document.createElement("td")
        th.setAttribute("data-label", region.name)
        th.className = "has-text-centered has-text-weight-bold"
        const a = document.createElement("a")
        a.href = region.map
        const textName = document.createTextNode(region.name);
        a.appendChild(textName)
        th.appendChild(a)
        row.appendChild(th)

        // stat columns
        for (const [key, value] of Object.entries(region)) {
            if (["rides", "incidents", "scaryIncidents", "km"].includes(key)) {
                generateStatColumn(row, key, value, tableMeta);
            }
        }
    }
}

// key = rides, value = [100, 1]
function generateStatColumn(row, key, value, tableMeta) {
    // create cell and append already to row
    const cell = document.createElement("td")
    cell.setAttribute("data-label", tableMeta[key].text);
    row.appendChild(cell)

    // create div
    const div = document.createElement("div")
    div.className = "is-flex is-align-items-center";

    // create total span
    const totalSpan = document.createElement("span")
    totalSpan.className = "tag is-medium mr-1 is-flex-grow-1"
    // create total span text
    const totalSpanText = document.createTextNode(value[0].toLocaleString())

    // create diff span
    const divSpan = document.createElement("span")
    const icon = document.createElement("i")
    if (value[1] > 0) {
        divSpan.className = "tag " + tableMeta[key].tag
        icon.className = "fas fa-arrow-circle-up"
    } else {
        divSpan.className = "tag"
        icon.className = "fas fa-arrow-circle-right"
    }
    const iconText = document.createElement("span")
    iconText.className = "icon-text is-flex-wrap-nowrap"
    const iconSpan = document.createElement("span")
    iconSpan.className = "icon mr-0"
    const diffValue = document.createElement("span")
    diffValue.appendChild(document.createTextNode(value[1].toLocaleString()))

    // append elements in reverse creation order to cell
    iconSpan.appendChild(icon)
    iconText.appendChild(iconSpan)
    iconText.appendChild(diffValue)
    divSpan.appendChild(iconText)

    totalSpan.appendChild(totalSpanText)
    div.appendChild(totalSpan)
    div.appendChild(divSpan)
    cell.appendChild(div)
}

async function fillTable() {
    let table = document.getElementById("regionTable");

    let [r1, r2] = await Promise.all([
        fetch("./resources/tableMeta.json"),
        fetch("./resources/dashboard.json")
    ])

    let tableMeta = await r1.json()
    let dashboard = await r2.json()

    generateTable(table, dashboard, tableMeta);
    generateTableHead(table, tableMeta);

    sorttable.makeSortable(table)
}

module.exports = fillTable;