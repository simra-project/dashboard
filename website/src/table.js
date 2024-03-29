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

function generateTable(table, dashboard, tableMeta, mapLinks, regionNames, topRegion, incidentLinks) {
    for (const region of dashboard.regions) {
        // true if the current region should be at the top when loading the page
        const atTop = region.name === topRegion

        let shownName
        try {
            shownName = regionNames[region.name]["DE"]
        } catch (err) {
            console.log(region.name + " not found in regionNames.json")
            shownName = region.name
        }

        const row = table.insertRow();
        // name column
        const th = document.createElement("td")
        th.setAttribute("data-label", shownName)
        th.className = "has-text-centered has-text-weight-bold"

        const link = mapLinks[region.name]
        const textName = document.createTextNode(shownName);
        if (link === undefined) {
            th.appendChild(textName)
        } else {
            const a = document.createElement("a")
            a.href = link
            a.appendChild(textName)
            th.appendChild(a)
        }
        row.appendChild(th)
        let diffs = true
        if (dashboard.diffDate === undefined) {
            diffs = false
        }

        // stat columns
        for (const [key, value] of Object.entries(region)) {
            if (["rides", "incidents", "scaryIncidents", "km"].includes(key)) {
                generateStatColumn(row, key, value, tableMeta, diffs, atTop, incidentLinks[region.name]);
            }
        }
    }
}

// key = rides, value = [100, 1], diffs = true/false, atTop = true/false
function generateStatColumn(row, key, value, tableMeta, diffs, atTop, incidentLink) {
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
    // create total span text.
    let totalSpanChild;
    // create a href referencing to the incident map, if "incidents"-cell is being created
    if ((key === 'incidents') && (incidentLink !== undefined)) {
        totalSpanChild = document.createElement("a")
        totalSpanChild.href = incidentLink
        let text = document.createTextNode(value[0].toLocaleString())
        totalSpanChild.appendChild(text)
    } else { // otherwise, just print the text
        totalSpanChild = document.createTextNode(value[0].toLocaleString())
    }

    if (!atTop) {
        // set sort-key of cell to value (normal sorting)
        cell.setAttribute("sorttable_customkey", value[0].toString())
    } else {
        // the given cell should be at the top, so add a very large top-key
        cell.setAttribute("sorttable_customkey", Number.MAX_SAFE_INTEGER)
    }

    // append elements
    totalSpan.appendChild(totalSpanChild)
    div.appendChild(totalSpan)

    if (diffs) {
        // create diff span
        const divSpan = document.createElement("span")
        const icon = document.createElement("span")
        icon.className = "unicode-icon"
        if (value[1] > 0) {
            divSpan.className = "tag " + tableMeta[key].tag
            icon.textContent = "⬆"
        } else {
            divSpan.className = "tag"
            icon.textContent = "➡"
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
        div.appendChild(divSpan)
    }

    cell.appendChild(div)
}

function updateTotals(dashboard) {
    document.getElementById("totalRides").innerHTML = dashboard.totalRides.toLocaleString()
    document.getElementById("totalIncidents").innerHTML = dashboard.totalIncidents.toLocaleString()
    document.getElementById("totalKm").innerHTML = dashboard.totalKm.toLocaleString()
    document.getElementById("sourceDate").innerHTML = dashboard.sourceDate

    if (dashboard.diffDate != undefined) {
        document.getElementById("diffDate").innerHTML = dashboard.diffDate
    } else {
        document.getElementById("diffText").style.display = "none";
    }
}

// topRegion can be set to the name of a region as found in the dashboard.json; this region will then be shown at the top of the table
async function fillTable(topRegion) {
    let table = document.getElementById("regionTable");
    let [r1, r2, r3, r4, r5] = await Promise.all([
        fetch("./resources/tableMeta.json"),
        fetch("./resources/dashboard.json"),
        fetch("./resources/mapLinks.json"),
        fetch("./resources/regionNames.json"),
        fetch("./resources/incidentLinks.json")
    ])

    let tableMeta = await r1.json()
    let dashboard = await r2.json()
    let mapLinks = await r3.json()
    let regionNames = await r4.json()
    let incidentLinks = await r5.json()

    updateTotals(dashboard);
    generateTable(table, dashboard, tableMeta, mapLinks, regionNames, topRegion, incidentLinks);
    generateTableHead(table, tableMeta);

    sorttable.makeSortable(table)
    // trigger the sorting by clicking the button of the #Fahrten column
    const sortButton = table.firstElementChild.firstElementChild.children[1]
    sortButton.click()
    sortButton.click()
}

module.exports = fillTable;