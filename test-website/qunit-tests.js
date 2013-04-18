
module("DOM stuff");
test("should focus on named field", function () {
    var $fixture = $("#qunit-fixture");
    $fixture.append("<input /> <input /> <input id=\"id-of-my-input\" name=\"my-input\" />");

    focusOnFieldNamed("my-input");

    var active = document.activeElement;
    equal(active.getAttribute("id"), "id-of-my-input", "Should have focussed on the correct input");
});


module("Editing");
test("Caches original data", function() {
    var $fixture = $("#qunit-fixture");
    $fixture.append("<p>original content</p>");

    cacheOriginalValue($('#qunit-fixture p'));
    $('#qunit-fixture p').text('new content');

    var valueInDataAttribute = $('#qunit-fixture p').data('before');
    equal("original content", valueInDataAttribute, "Should have cached the initial value in the daa attribute");
})
