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

});

