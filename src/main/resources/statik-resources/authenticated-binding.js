function setupContextMenu() {
    $('body').append('<div id="editMenu" class="contextMenu"></div>');
    $('#editMenu').append('<ul></ul>');
    $('#editMenu ul').append('<li id="edit"> <span class="ui-icon ui-icon-pencil"></span> Edit </li>')
        .append('<li id="copy"> <span class="ui-icon ui-icon-plusthick"></span> Copy </li>');

    $('#editMenu span').css('display', 'inline-block');
};

function setupEditorContainer() {
    $('body').append('<div id="statik-editor-dialog"></div>');
}

function setupScripts() {
    $('head').append('<link type="text/css" rel="stylesheet" href="/statik/resources/styles.css" />');
    $('head').append('<script type="text/javascript" src="/statik/resources/authenticated.js"></script>');
    $('head').append('<script type="text/javascript" src="/statik/resources/dom.js"></script>');
    $('head').append('<script type="text/javascript" src="/statik/resources/getpath.js"></script>');
    $('head').append('<script type="text/javascript" src="/statik/resources/jquery.contextmenu.r2.packed.js"></script>');
}

$(document).ready(function () {
    setupScripts();
    addControlBoxToScreen();
    setupContextMenu();
    setupEditorContainer();

    var editableElements = $('a, p, li');
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
