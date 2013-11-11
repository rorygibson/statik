requirejs.config({
    "baseUrl": "/statik-resources/js",
    "paths": {
        "appjs": "/statik-resources/appjs"
    },
    "shim": {
        "getpath": ["jquery"],
        "jquery.contextmenu": ["jquery"],
        "jquery.form": ["jquery"],
        "bootstrap": ["jquery"],
        "wysihtml5": ["bootstrap"],
        "bootstrap-wysihtml5": ["bootstrap", "wysihtml5"]
    }
});


define(["jquery", "jquery.contextmenu", "bootstrap-wysihtml5", "jquery.form", "getpath"], function ($) {

    var Editing = {
        STATIK_CONTENT_URL: '/statik/content',

        CONTROL_BOX_WRAPPER_HTML: '<div id="wrap" style="z-index:1000; position:fixed; top:20px; right:20px;"></div>',
        CONTROL_BOX_IFRAME_HTML: '<iframe seamless="seamless" style="margin-left:10px; border:none" id="control-box" src="/statik/control-box" width="200px" height="470px" />',

        EDIT_MENU_HTML: '<div id="editMenu" class="contextMenu tw-bs"><ul></ul></div>',
        EDIT_MENU_EDIT_ITEM_HTML: '<li id="edit"> <i class="icon-pencil"></i> Edit </li>',
        EDIT_MENU_COPY_ITEM_HTML: '<li id="copy"> <i class="icon-plus"></i> Copy </li>',
        EDIT_MENU_SET_IMAGE_HTML: '<li id="set-src"> <i class="icon-plus"></i> Set image </li>',
        EDITOR_CONTAINER_HTML: '<div id="statik-editor-container" class="tw-bs"></div>',
        UPLOAD_LIST_CONTAINER_HTML: '<div id="statik-upload-list-container" class="tw-bs"></div>',
        UPLOADER_DIALOG_HTML: '<div id="statik-uploader-dialog-container" class="tw-bs"></div>',

        STATIK_CSS_TAG: '<link rel="stylesheet" type="text/css" href="/statik-resources/css/statik.css" />',
        BOOTSTRAP_CSS_TAG: '<link rel="stylesheet" type="text/css" href="/statik-resources/css/namespaced-bootstrap.css" />',
        WYSIHTML5_CSS_TAG: '<link rel="stylesheet" type="text/css" href="/statik-resources/css/bootstrap-wysihtml5.css" />',
        UPLOADER_CSS_TAG: '<link rel="stylesheet" type="text/css" href="/statik-resources/css/uploadify.css" />',

        addControlBoxTo: function (parent) {
            $(parent).append(Editing.CONTROL_BOX_WRAPPER_HTML);
            $('#wrap').append(Editing.CONTROL_BOX_IFRAME_HTML);
        },

        addContextMenuMarkupTo: function (parent) {
            $(parent).append(Editing.EDIT_MENU_HTML);

            $('#editMenu ul')
                .append(Editing.EDIT_MENU_EDIT_ITEM_HTML)
                .append(Editing.EDIT_MENU_SET_IMAGE_HTML)
                .append(Editing.EDIT_MENU_COPY_ITEM_HTML);

            $('#editMenu span').css('display', 'inline-block');
        },

        addEditorContainerTo: function (parent) {
            $(parent).append(Editing.EDITOR_CONTAINER_HTML);
        },

        addUploadListContainer: function (parent) {
            $(parent).append(Editing.UPLOAD_LIST_CONTAINER_HTML);
            $(parent).append(Editing.UPLOADER_DIALOG_HTML);
        },

        addStyleTagsToHead: function () {
            $('head')
                .append(Editing.STATIK_CSS_TAG)
                .append(Editing.BOOTSTRAP_CSS_TAG)
                .append(Editing.UPLOADER_CSS_TAG)
                .append(Editing.WYSIHTML5_CSS_TAG);
        },


// Test whether copying of element is supported.
// Currently supports only P and LI tags, as they exist in conceptual sequences.
        hasCopyAbility: function (element) {
            return (element.tagName === "LI") ||
                (element.tagName === "P");
        },

// Test whether an element supports the setting of its src attr
// Currently only supports IMG tags
        hasSetSrcAbility: function (element) {
            return (element.tagName === "IMG");
        },


// Test whether an element supports text editing
        hasEditAbility: function (element) {
            return (element.tagName === "LI") ||
                (element.tagName === "P") ||
                (element.tagName === "FIGCAPTION");
        },


// Set the src attribute of an element to an uploaded file chosen from a picker
        setSrc: function (item, path) {
            var selector = getPath(item);

            var encodedDomain = encodeURIComponent(window.location.hostname);
            var encodedSelector = encodeURIComponent(selector);
            var encodedPath = encodeURIComponent(path);

            Editing.loadUploadedFileList(encodedDomain, encodedSelector, encodedPath);
        },


// Copy an element (item) and append the copy immediately after the original in the DOM.
// Sets up the appropriate hover state etc hook on the new element so it becomes immediately editable.
// Pushes the new element to the CMS backend.
        copy: function (item, path) {
            var theCopy = $(item).clone();
            $(item).parent().append(theCopy);

            var content = $(item).html();
            var selector = getPath(theCopy.get(0));

            Editing.addHoverStateTo(theCopy);
            Editing.addContextMenuTo(theCopy.get(0), path);

            $.ajax({
                type: 'POST',
                url: Editing.STATIK_CONTENT_URL,
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
        },


        addHoverStateTo: function (item) {
            $(item).hover(
                function (e) {
                    if (e.target === this) {
                        $(item).data('pre-hover-bg', $(item).css('background-color'));
                        $(item).css("background-color", "lightgreen");
                    }
                },
                function (e) {
                    if (e.target === this) {
                        $(item).css("background-color", $(item).data('pre-hover-bg'));
                        $(item).data('pre-hover-bg', '');
                    }
                }
            );
        },


        loadEditorIntoDialog: function (encodedSelector, encodedDomain, encodedPath, encodedContent, encodedLanguage) {
            var url = "/statik/editor?selector=" + encodedSelector + "&language=" + encodedLanguage + "&domain=" + encodedDomain + "&path=" + encodedPath + "&content=" + encodedContent;

            $("#statik-editor-container").load(url, function () {
                $("#statik-editor-dialog").attr('class', 'modal').modal("show");
            });
        },


        loadUploadedFileList: function (encodedDomain, encodedSelector, encodedPath) {
            var url = "/statik/upload-list-dialog?selector=" + encodedSelector + "&domain=" + encodedDomain + "&path=" + encodedPath;

            $("#statik-upload-list-container").load(url, function () {
                $("#statik-upload-list-dialog").attr('class', 'modal').modal("show");
            });
        },


// Open a WYSIHTML5 editor, with initial content determined by element.
// Causes the editor to be popped up in a modal dialog.
        openEditorFor: function (element, path, language) {
            var selector = getPath(element);
            var content = $(element).html();

            var encodedDomain = encodeURIComponent(window.location.hostname);
            var encodedSelector = encodeURIComponent(selector);
            var encodedPath = encodeURIComponent(path);
            var encodedContent = encodeURIComponent(content);
            var encodedLanguage = encodeURIComponent(language);

            window.statik = {};
            window.statik.item = element;
            window.statik.path = path;
            window.statik.language = language;
            window.statik.content = content;

            this.loadEditorIntoDialog(encodedSelector, encodedDomain, encodedPath, encodedContent, encodedLanguage);
        },

        addContextMenuTo: function (item, path) {
            $(item).contextMenu('editMenu', {
                bindings: {
                    'edit': function (t) {
                        Editing.openEditorFor(item, path, "en");
                    },
                    'copy': function (t) {
                        Editing.copy(item, path);
                    },
                    'set-src': function (t) {
                        Editing.setSrc(item, path);
                    }
                },
                onShowMenu: function (e, menu) {
                    if (!Editing.hasEditAbility(item)) {
                        menu.find("#edit").hide();
                    } else {
                        menu.find("#edit").show();
                    }

                    if (!Editing.hasCopyAbility(item)) {
                        menu.find("#copy").hide();
                    } else {
                        menu.find("#copy").show();
                    }

                    if (!Editing.hasSetSrcAbility(item)) {
                        menu.find("#set-src").hide();
                    } else {
                        menu.find("#set-src").show();
                    }

                    menu.addClass("tw-bs");

                    return menu;
                },
                menuStyle: {
                    width: '120px',
                    'font-size': '10pt'
                }
            });
        },

        prepare: function () {
            this.addStyleTagsToHead();

            var editableElements = $('p, li, figcaption, img'); // set of supported editable elements
            var pagePath = window.location.pathname;

            $.each(editableElements, function (index, item) {
                Editing.addHoverStateTo(item);
                Editing.addContextMenuTo(item, pagePath);
            });

            Editing.addContextMenuMarkupTo($('body'));
            Editing.addEditorContainerTo($('body'));
            Editing.addUploadListContainer($('body'))
            Editing.addControlBoxTo($('body'));
        }
    }

    return Editing;

});


