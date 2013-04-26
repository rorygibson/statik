
module("DOM stuff");
test("should focus on named field", function () {
    var $fixture = $("#qunit-fixture");
    $fixture.append("<input /> <input /> <input id=\"id-of-my-input\" name=\"my-input\" />");

    focusOnFieldNamed("my-input");

    var active = document.activeElement;
    equal(active.getAttribute("id"), "id-of-my-input", "Should have focussed on the correct input");
});

test("Caches original data", function() {
    var $fixture = $("#qunit-fixture");
    $fixture.append("<p>original content</p>");

    cacheOriginalValue($('#qunit-fixture p'));
    $('#qunit-fixture p').text('new content');

    var valueInDataAttribute = $('#qunit-fixture p').data('before');
    equal("original content", valueInDataAttribute, "Should have cached the initial value in the daa attribute");
});

test("Makes element editable", function() {
    var $fixture = $("#qunit-fixture");
    $fixture.append("<ul><li>text of li</li></ul>");

    makeEditable($('li'));

    equal('true', $('li').attr('contenteditable'), "LI should have been made contenteditable");
});


module("CSS selectors");
test("Using ID", function() {
    var $fixture = $("#qunit-fixture");
    $fixture.append("<ul><li id=\"first\">one</li><li>two</li><li>three</li></ul>");

    var el = $('ul > li')[0];
    var selector = getPath(el);

    equal("html > body > div#qunit-fixture > ul > li#first", selector, "Should have created a selector that relies on the ID");
});

test("Using n-of-type", function() {
    var $fixture = $("#qunit-fixture");
    $fixture.append("<ul><li>one</li><li>two</li><li>three</li></ul>");

    var el = $('ul > li')[1];
    var selector = getPath(el);

    equal("html > body > div#qunit-fixture > ul > li:nth-of-type(2)", selector, "Should have created a selector with nth-of-type");
});


module("Editing", {
    setUp: function () {
        this.server = sinon.fakeServer.create();
    },

    tearDown: function () {
        this.server.restore();
    }
});