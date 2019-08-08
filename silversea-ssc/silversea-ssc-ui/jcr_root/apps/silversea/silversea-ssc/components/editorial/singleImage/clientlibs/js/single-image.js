'use strict';

var _createClass = function () {
    function defineProperties(target, props) {
        for (var i = 0; i < props.length; i++) {
            var descriptor = props[i];
            descriptor.enumerable = descriptor.enumerable || false;
            descriptor.configurable = true;
            if ("value" in descriptor) descriptor.writable = true;
            Object.defineProperty(target, descriptor.key, descriptor);
        }
    }
    return function (Constructor, protoProps, staticProps) {
        if (protoProps) defineProperties(Constructor.prototype, protoProps);
        if (staticProps) defineProperties(Constructor, staticProps);
        return Constructor;
    };
}();

function _classCallCheck(instance, Constructor) {
    if (!(instance instanceof Constructor)) {
        throw new TypeError("Cannot call a class as a function");
    }
}

var ResponsiveBackgroundImage = function () {
    function ResponsiveBackgroundImage(element) {
        var _this = this;

        _classCallCheck(this, ResponsiveBackgroundImage);

        this.element = element;
        this.img = element.querySelector('img');
        this.src = '';

        this.img.addEventListener('load', function () {
            _this.update();
        });

        if (this.img.complete) {
            this.update();
        }
    }

    _createClass(ResponsiveBackgroundImage, [{
        key: 'update',
        value: function update() {
            var src = typeof this.img.currentSrc !== 'undefined' ? this.img.currentSrc : this.img.src;
            if (this.src !== src) {
                this.src = src;
                this.element.style.backgroundImage = 'url("' + this.src + '")';
            }
        }
    }]);

    return ResponsiveBackgroundImage;
}();

$(window).load(function () {
    var SINGLE_IMAGE_CONTAINER = ".single-image-container";
    if ($(SINGLE_IMAGE_CONTAINER).length > 0) {
        var elements = document.querySelectorAll(SINGLE_IMAGE_CONTAINER);
        for (var i = 0; i < elements.length; i++) {
            new ResponsiveBackgroundImage(elements[i]);
        }
    }
});
