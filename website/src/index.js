require('./mystyles.scss');
const fillTable = require("./table.js")

window.onload = function() {
    document.getElementById("deleteInfo").addEventListener('click', function(event) {
        console.log(event.target.parentNode.parentNode.remove())
    });

    // users can supply a region name that will be listed at the top
    const topRegion = getParameterByName('region')
    
    fillTable(topRegion)
}

/**
 * Source: https://stackoverflow.com/questions/901115/how-can-i-get-query-string-values-in-javascript?rq=1
 */

function getParameterByName(name, url = window.location.href) {
    name = name.replace(/[\[\]]/g, '\\$&');
    var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
}