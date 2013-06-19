//
//module("CSS selectors");
//test("Using ID", function() {
//    var $fixture = $("#qunit-fixture");
//    $fixture.append("<ul><li id=\"first\">one</li><li>two</li><li>three</li></ul>");
//
//    var el = $('ul > li')[0];
//    var selector = getPath(el);
//
//    equal("html > body > div#qunit-fixture > ul > li#first", selector, "Should have created a selector that relies on the ID");
//});
//
//test("Using n-of-type", function() {
//    var $fixture = $("#qunit-fixture");
//    $fixture.append("<ul><li>one</li><li>two</li><li>three</li></ul>");
//
//    var el = $('ul > li')[1];
//    var selector = getPath(el);
//
//    equal("html > body > div#qunit-fixture > ul > li:nth-of-type(2)", selector, "Should have created a selector with nth-of-type");
//});
//
//
//module("Editing", {
//    setUp: function () {
//        this.server = sinon.fakeServer.create();
//    },
//
//    tearDown: function () {
//        this.server.restore();
//    }
//});
//
