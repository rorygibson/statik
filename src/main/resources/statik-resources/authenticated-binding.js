$(document).ready(function () {
    var editableElements = $('a, p, li');
    var pagePath = window.location.pathname;

    $.each(editableElements, function (index, item) {
        addHoverState(item);
        addContextMenuTo(item, pagePath);
    });

});
