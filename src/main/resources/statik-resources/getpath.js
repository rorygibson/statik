var getPath = function(el) {
    if (!(el instanceof Element)) return;

    var path = [];
    while (el.nodeType === Node.ELEMENT_NODE) {
        var selector = el.nodeName.toLowerCase();
        if (el.id) {
            selector += '#' + el.id;
        } else {
            var sib = el, nth = 1, necessary = false;

            while (sib = sib.previousSibling) {
              if (sib.nodeType === Node.ELEMENT_NODE && sib.tagName === el.tagName) {
                nth++;
                necessary = true;
              }
            }

            if (necessary) {
                selector += ":nth-of-type("+nth+")";
            }
        }

        path.unshift(selector);
        el = el.parentNode;
    }

    return path.join(" > ");
};