
test("should focus on named field", function () {
    var $fixture = $("#qunit-fixture");
    $fixture.append("<input /> <input /> <input id=\"id-of-my-input\" name=\"my-input\" />");

    focusOnFieldNamed("my-input");

    var active = document.activeElement;
    equal(active.getAttribute("id"), "id-of-my-input", "Should have focussed on the correct input");
});