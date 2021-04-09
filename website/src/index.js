require('./mystyles.scss');
const fillTable = require("./table.js")
import '@fortawesome/fontawesome-free/js/fontawesome'
import '@fortawesome/fontawesome-free/js/solid'

window.onload = function() {
    document.getElementById("deleteInfo").addEventListener('click', function(event) {
        console.log(event.target.parentNode.parentNode.remove())
    });

    fillTable()
}