requirejs.config({
    "baseUrl": "/statik-resources/js",
    "paths": {
      "appjs": "/statik-resources/appjs"
    },
    "shim": {
        "jquery.contextmenu": ["jquery"],
        "jquery.form": ["jquery"],
        "bootstrap": ["jquery"],
        "wysihtml5": ["bootstrap"],
        "bootstrap-wysihtml5": ["bootstrap","wysihtml5"]
    }
});


require(["jquery"], function ($) {

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

    function changeLanguage(e) {
        var lang = $(this).attr('data-lang');

        $('a.language').removeClass('active');
        $(this).addClass('active');

        parent.window.location = parent.window.location.pathname + "?language=" + lang;
        e.preventDefault();
    }

    $('#publish').click(publish);
    $('#logout').click(logout);
    $('#users').click(users);
    $('a.site').click(openSite);
    $('a.language').click(changeLanguage)
});

