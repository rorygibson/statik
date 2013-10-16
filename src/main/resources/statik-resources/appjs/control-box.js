requirejs.config({
    "baseUrl": "/statik-resources/js",
    "paths": {
        "appjs": "/statik-resources/appjs"
    },
    "shim": {
        "jquery.contextmenu": ["jquery"],
        "jquery.form": ["jquery"],
        "jquery.uploadify": ["jquery"],
        "bootstrap": ["jquery"],
        "wysihtml5": ["bootstrap"],
        "bootstrap-wysihtml5": ["bootstrap", "wysihtml5"]
    }
});


require(["jquery", "bootstrap", "jquery.uploadify"], function ($) {

    function logout(e) {
        $.get("/statik/logout").done(function () {
            var element = parent.document.getElementById("control-box");
            element.parentNode.removeChild(element);
        });

        e.preventDefault();
    }

    function publish(e) {
        $.ajax({
            type: 'POST',
            url: '/statik/make-it-so',
            data: {
                domain: parent.window.location.hostname,
                path: parent.window.location.pathname
            },
            success: function (msg) {
                if (!msg) {
                    console.error('publish failure');
                }
            }
        });

        e.preventDefault();
    }

    function users(e) {
        parent.window.location = "/statik/users";
        e.preventDefault();
    }

    function openSite(e) {
        parent.window.location = $(this).attr('href');
        e.preventDefault();
    }

    function loadUploader() {
        var url = "/statik-resources/uploader.html";

        parent.$("#statik-uploader-dialog-container").load(url, function () {
            var modal = parent.$("#statik-uploader-dialog");
            $(modal).on('shown', function() {

            });

            $(modal).attr('class', 'modal').modal("show");
        });
    };


    function changeLanguage(e) {
        var lang = $(this).attr('data-lang');

        $('a.language').removeClass('active');
        $(this).addClass('active');

        parent.window.location = parent.window.location.pathname + "?language=" + lang;
        e.preventDefault();
    }

    $('#publish').click(publish);
    $('#upload').click(loadUploader);
    $('#logout').click(logout);
    $('#users').click(users);
    $('a.site').click(openSite);
    $('a.language').click(changeLanguage)
});

