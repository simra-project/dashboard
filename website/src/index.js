require('./mystyles.scss');
const fillTable = require("./table.js")

window.onload = function() {
    document.getElementById("deleteInfo").addEventListener('click', function(event) {
        console.log(event.target.parentNode.parentNode.remove())
    });

    fillTable()
}