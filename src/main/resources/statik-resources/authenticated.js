function cacheOriginalValue(element) {
    $(element).data('before', $(element).html());
}

function doPost(item, pagePath) {
    var hasChanged = $(item).data('before') !== $(item).html();

    if (hasChanged) {
        var content = $(item).text();
        var selector = getPath(item);

        $.ajax({
            type: 'POST',
            url: '/content',
            data: {
                path: pagePath,
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
}

function makeEditable(item, path) {
    $(item).attr('contenteditable', 'true');

    $(item).focus(function () {
        cacheOriginalValue(this);
    });

    $(item).live('blur', function () {
        doPost(item, path);
    });
}

$(document).ready(function () {
    var editableElements = $('p, li');
    var pagePath = window.location.pathname;

    $.each(editableElements, function (index, item) {
        makeEditable(item, pagePath);
    });
});
