
function addHoverState(item) {
    $(item).hover(
        function () {
            $(item).data('pre-hover', $(item).css('border'));
            $(item).css("border", "solid 1px lightgreen");
        },
        function () {
            $(item).css("border", $(item).data('pre-hover'));
            $(item).data('pre-hover', '');
        }
    );
}

function addPostBehaviour(item, path) {
    $(item).dblclick(function (e) {
        var selector = getPath(item);
        var content = $(item).text();

        var encodedSelector = encodeURIComponent(selector);
        var encodedPath = encodeURIComponent(path);
        var encodedContent = encodeURIComponent(content);

        loadEditorIntoDialog(encodedSelector, encodedPath, encodedContent);
    });
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



