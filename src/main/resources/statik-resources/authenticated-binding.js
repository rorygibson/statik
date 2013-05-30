
$(document).ready(function () {
    $('body').append('<script type="text/javascript" src="/statik-resources/authenticated.js"></script>');

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
