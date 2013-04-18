function focusOnFieldNamed(name) {
    window.document.getElementsByName(name).item(0).focus();
}

function cacheOriginalValue(element) {
    $(element).data('before', $(element).html());
}

function makeEditable(item) {
    $(item).attr('contenteditable', 'true');
}
