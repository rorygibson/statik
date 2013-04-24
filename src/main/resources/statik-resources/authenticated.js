function addContextMenuTo(item, path) {
    $(item).contextMenu('editMenu', {
        bindings: {
            'edit': function (t) {
                var selector = getPath(item);
                var content = $(item).html();

                var encodedSelector = encodeURIComponent(selector);
                var encodedPath = encodeURIComponent(path);
                var encodedContent = encodeURIComponent(content);

                loadEditorIntoDialog(encodedSelector, encodedPath, encodedContent);
            }
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


function loadEditorIntoDialog(encodedSelector, encodedPath, encodedContent) {
    $.ajax({
        url: "/statik-editor?selector=" + encodedSelector + "&path=" + encodedPath + "&content=" + encodedContent,
        success: function (data) {
            $("#statik-editor-dialog").html(data);

            $("#statik-editor-dialog").dialog(
                {
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



