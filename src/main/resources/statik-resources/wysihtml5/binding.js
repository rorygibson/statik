$(document).ready(function () {
    var txt = document.querySelector('#textarea-preload').innerHTML;
    document.querySelector('textarea').innerHTML = txt;

    var editor = new wysihtml5.Editor("wysihtml5-textarea", {
        name: "statik",
        toolbar: "wysihtml5-toolbar",
        stylesheets: ["/statik-resources/wysihtml5/editor.css"],
        parserRules: wysihtml5ParserRules,
        html: true,
        style: true
    });

    editor.on("load", function () {
        if (!editor.isCompatible()) {
            return;
        }

        var doc = editor.composer.sandbox.getDocument();
        var link = doc.createElement("link");
        link.href = "/statik-resources/wysihtml5/editor.css";
        link.rel = "stylesheet";
        doc.querySelector("head").appendChild(link);

        debugger;
    });

    document.editor = editor;

    $('#editorForm').ajaxForm(function () {
        window.location.reload();
    });
});