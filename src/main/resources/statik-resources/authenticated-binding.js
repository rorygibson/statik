
$(document).ready(function () {
    setupScripts();
    setupContextMenu();
    setupEditorContainer();
    addControlBoxToScreen();

    var editableElements = $('section, a, p, li');
    var pagePath = window.location.pathname;

    $.each(editableElements, function (index, item) {
        addHoverState(item);
        addContextMenuTo(item, pagePath);
    });
});
