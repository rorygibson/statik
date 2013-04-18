

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

function addPostBehaviour(item, path) {
    makeEditable(item);

    $(item).focus(function () {
        cacheOriginalValue(this);
    });

    $(item).live('blur', function () {
        doPost(item, path);
    });
}

