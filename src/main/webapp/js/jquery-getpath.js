jQuery.fn.extend({getPath: function (path) {
    if (typeof path == 'undefined') {
        path = '';
    }

    if (this.is('html')) {
        return 'html' + path;
    }

    var cur = this.get(0).nodeName.toLowerCase();
    var id = this.attr('id'), clazz = this.attr('class');

    if (typeof id != 'undefined') {
        cur += '#' + id;
    }

    if (typeof clazz != 'undefined') {
        cur += '.' + clazz.split(/[\s\n]+/).join('.');
    }

    return this.parent().getPath(' > ' + cur + path);
}});