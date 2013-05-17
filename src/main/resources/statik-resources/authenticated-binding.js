$(document).ready(function () {
    var editableElements = $('a, p, li');
    var pagePath = window.location.pathname;

    $.each(editableElements, function (index, item) {
        addHoverState(item);
        addContextMenuTo(item, pagePath);
    });

    $('#publish').click(function() {
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
