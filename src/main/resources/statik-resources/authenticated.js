
function addControlBoxToScreen() {
  $('body').append('<iframe style="position:absolute; top:20px; right:20px; width: 180px" id="control-box" src="/statik/control-box" width="200px" height="300px" />');
}

function setupContextMenu() {
    $('body').append('<div id="editMenu" class="contextMenu"></div>');
    $('#editMenu').append('<ul></ul>');
    $('#editMenu ul').append('<li id="edit"> <span class="ui-icon ui-icon-pencil"></span> Edit </li>')
        .append('<li id="copy"> <span class="ui-icon ui-icon-plusthick"></span> Copy </li>');

    $('#editMenu span').css('display', 'inline-block');
}

function setupEditorContainer() {
    $('body').append('<div id="statik-editor-dialog"></div>');
}

function setupScripts() {
    $('head').append('<script type="text/javascript" src="/statik-resources/dom.js"></script>')
        .append('<script type="text/javascript" src="/statik-resources/getpath.js"></script>')
        .append('<script type="text/javascript" src="/statik-resources/jquery.contextmenu.r2.packed.js"></script>');
}


function hasCopyAbility(item) {
    return (item.tagName === "LI") ||
        (item.tagName === "P");
}

function copy(item, path) {
    var theCopy = $(item).clone();
    $(item).parent().append(theCopy);

    var content = $(item).html();
    var selector = getPath(theCopy.get(0));

    addHoverState(theCopy);
    addContextMenuTo(theCopy.get(0), path);

    $.ajax({
        type: 'POST',
        url: '/statik/content',
        data: {
            domain: window.location.hostname,
            path: path,
            content: content,
            selector: selector
        },
        success: function (msg) {
            if (!msg) {
                console.error('update failure');
            }
        }
    });
}

function loadEditor(item, path) {
    var selector = getPath(item);
    var content = $(item).html();

    var encodedDomain = encodeURIComponent(window.location.hostname);
    var encodedSelector = encodeURIComponent(selector);
    var encodedPath = encodeURIComponent(path);
    var encodedContent = encodeURIComponent(content);

    loadEditorIntoDialog(encodedSelector, encodedDomain, encodedPath, encodedContent);
}

function addContextMenuTo(item, path) {
    $(item).contextMenu('editMenu', {
        bindings: {
            'edit': function (t) {
                loadEditor(item, path);
            },
            'copy': function (t) {
                copy(item, path);
            }
        },
        onShowMenu: function (e, menu) {
            if (!hasCopyAbility(item)) {
                menu.find("#copy").hide();
            } else {
                menu.find("#copy").show();
            }
            return menu;
        },
        menuStyle: {
            width: '120px',
            'font-size': '10pt'
        }
    });
}

function addHoverState(item) {
    $(item).hover(
        function (e) {
            if (e.target === this) {
                $(item).data('pre-hover', $(item).css('background-color'));
                $(item).css("background-color", "lightgreen");
            }
        },
        function (e) {
            if (e.target === this) {
                $(item).css("background-color", $(item).data('pre-hover'));
                $(item).data('pre-hover', '');
            }
        }
    );
}


function loadEditorIntoDialog(encodedSelector, encodedDomain, encodedPath, encodedContent, encodedParentSelector) {
    $.ajax({
        url: "/statik/editor?selector=" + encodedSelector + "&domain=" + encodedDomain + "&path=" + encodedPath + "&content=" + encodedContent,
        success: function (data) {
            $("#statik-editor-dialog").html(data);

            $("#statik-editor-dialog").dialog(
                {
                    title: "Edit",
                    bgiframe: true,
                    autoOpen: true,
                    height: 250,
                    width: 500,
                    modal: true
                }
            );
        }
    });
}



