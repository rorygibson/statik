$(document).ready(function () {
    var editableElements = $('p, li');
    var pagePath = window.location.pathname;

    $.each(editableElements, function (index, item) {
        makeEditable(item, pagePath);
    });
});
