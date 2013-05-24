
$(document).ready(function () {
    $('body').append('<script type="text/javascript" src="/statik-resources/authenticated.js"></script>');

    setupScripts();
    addControlBoxToScreen();
    setupContextMenu();
    setupEditorContainer();
    addSitesListToScreen();

    var editableElements = $('section, a, p, li');
    var pagePath = window.location.pathname;

    $.each(editableElements, function (index, item) {
        addHoverState(item);
        addContextMenuTo(item, pagePath);
    });

    $('#publish').click(function () {
        $.ajax({
            type: 'POST',
            url: '/statik/make-it-so',
            data: {
                domain: window.location.hostname,
                path: pagePath
            },
            success: function (msg) {
                if (!msg) {
                    console.error('publish failure');
                }
            }
        });
        return false;
    });
});
