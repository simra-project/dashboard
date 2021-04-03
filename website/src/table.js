let regions = [{
        name: "Berlin",
        rides: [1300, 400],
        incidents: [270, 21],
        scaryIncidents: [74, 3],
        km: [4800, 728],
        map: "https://www.google.com"
    },
    {
        name: "Ruhrgebiet",
        rides: [140021, 14320],
        incidents: [5720, 0],
        scaryIncidents: [111, 0],
        km: [1240921, 1495],
        map: "https://www.google.com"
    }
];

let tableMeta = {
    name: {
        text: "Region",
        abbr: "Region"
    },
    rides: {
        text: "#Fahrten",
        abbr: "Gesamtzahl analysierter Fahrten",
        tag: "is-success"
    },
    incidents: {
        text: "#Beinaheunfälle (gesamt)",
        abbr: "Gesamtzahl aufgezeichneter Beinaheunfälle, inkl. beängstitender Beinaheunfälle",
        tag: "is-warning"
    },
    scaryIncidents: {
        text: "#Beinaheunfälle (beängstitend)",
        abbr: "Anzahl aufgezeichneter beängstigender Beinaheunfälle",
        tag: "is-danger"
    },
    km: {
        text: "Gefahrene Kilometer",
        abbr: "Gefahrene Kilometer",
        tag: "is-success"
    },
    map: {
        text: "Auswertungskarte",
        abbr: "Auswertungskarte"
    }
}

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

function generateTable(table, regions, tableMeta) {
    for (const region of regions) {
        const row = table.insertRow();
        // name column
        const th = document.createElement("th")
        th["data-label"] = region.name
        const textName = document.createTextNode(region.name);
        th.appendChild(textName)
        row.appendChild(th)

        // stat columns
        for (const [key, value] of Object.entries(region)) {
            if (["rides", "incidents", "scaryIncidents", "km"].includes(key)) {
                generateStatColumn(row, key, value, tableMeta);
            }
        }

        // map column
        const cell = document.createElement("td")
        const a = document.createElement("a")
        a.href = region.map
        const textMap = document.createTextNode("Auswertungskarte");
        a.appendChild(textMap)
        cell.appendChild(a)
        row.appendChild(cell)
    }
}

// key = rides, value = [100, 1]
function generateStatColumn(row, key, value, tableMeta) {
    // create cell
    const cell = row.insertCell();
    cell["data-label"] = tableMeta[key]

    // create div
    const div = document.createElement("div")
    div.className = "is-flex is-align-items-center";

    // create total span
    const totalSpan = document.createElement("span")
    totalSpan.className = "tag is-medium mr-1"
    // create total span text
    const totalSpanText = document.createTextNode(value[0].toLocaleString())

    // append elements in reverse creation order
    totalSpan.appendChild(totalSpanText)
    div.appendChild(totalSpan)
    cell.appendChild(div)
}

function fillTable() {
    let table = document.getElementById("regionTable");
    generateTable(table, regions, tableMeta);
    generateTableHead(table, tableMeta);
}

module.exports = fillTable;