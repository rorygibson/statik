function addControlBoxToScreen() {
    $('body').append('<div id="wrap" style="width:200px; position:absolute; top:20px; right:20px; background-color: lightgrey"></div>');
    $('#wrap').append('<iframe seamless="seamless" style="width: 180px;margin-left:10px; border:none" id="control-box" src="/statik/control-box" width="200px" height="330px" />');
}

function setupContextMenu() {
    $('body').append('<div id="editMenu" class="contextMenu tw-bs"></div>');
    $('#editMenu').append('<ul></ul>');
    $('#editMenu ul').append('<li id="edit"> <i class="icon-pencil"></i> Edit </li>')
        .append('<li id="copy"> <i class="icon-plus"></i> Copy </li>');

    $('#editMenu span').css('display', 'inline-block');
}

function setupEditorContainer() {
    $('body').append('<div id="statik-editor-container" class="tw-bs"></div>');

    $('head')
        .append('<script type="text/javascript" src="/statik-resources/wysihtml5/jquery.form.js"></script>');

    $('head').append('<link rel="stylesheet" type="text/css" href="/statik-resources/bootstrap-wysihtml5-0.0.2/libs/css/namespaced-bootstrap.css" />')
        .append('<link rel="stylesheet" type="text/css" href="/statik-resources/bootstrap-wysihtml5-0.0.2/bootstrap-wysihtml5-0.0.2.css" />')
        .append('<link rel="stylesheet" type="text/css" href="/statik-resources/wysihtml5/editor.css" />');
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

function loadEditor(item, path, language) {
    var selector = getPath(item);
    var content = $(item).html();

    var encodedDomain = encodeURIComponent(window.location.hostname);
    var encodedSelector = encodeURIComponent(selector);
    var encodedPath = encodeURIComponent(path);
    var encodedContent = encodeURIComponent(content);
    var encodedLanguage = encodeURIComponent(language);

    window.statik = {};
    window.statik.item = item;
    window.statik.path = path;
    window.statik.language = language;

    loadEditorIntoDialog(encodedSelector, encodedDomain, encodedPath, encodedContent, encodedLanguage);
}


function addContextMenuTo(item, path) {
    $(item).contextMenu('editMenu', {
        bindings: {
            'edit': function (t) {
                loadEditor(item, path, "en");
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

            menu.addClass("tw-bs");

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


function loadEditorIntoDialog(encodedSelector, encodedDomain, encodedPath, encodedContent, encodedLanguage) {
    var url = "/statik/editor?selector=" + encodedSelector + "&language=" + encodedLanguage + "&domain=" + encodedDomain + "&path=" + encodedPath + "&content=" + encodedContent;

    $("#statik-editor-container").load(url, function() {
         $("#statik-editor-dialog").attr('class','modal').modal("show");
    });
}



