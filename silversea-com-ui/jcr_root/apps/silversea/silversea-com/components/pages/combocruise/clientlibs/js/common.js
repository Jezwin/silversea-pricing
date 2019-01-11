$(function () {
        function applyWidths(sameWidthSelectors) {
            function applySameWidth(selector) {
                var widest = 0;
                var allEquals = true;
                $(selector).each(function () {
                    var newValue = $(this).outerWidth(true);
                    var newWidest = Math.max(widest, newValue);
                    allEquals = allEquals && (newValue === widest || widest === 0);
                    widest = newWidest;
                });
                if (!allEquals) {
                    $(selector).outerWidth(10 + widest);
                }
                return allEquals;

            }

            var allEquals = true;
            for (var i = 0; i < sameWidthSelectors.length; i++) {
                allEquals = allEquals && applySameWidth(sameWidthSelectors[i]);
            }
            return allEquals;
        }

        function applyWithoutTransitions(callback) {
            var lazySetTransitionTo = function ($el, duration, next) {
                return function () {
                    $el.css('transition-duration', duration);
                    $el = [];
                    next();
                }
            };
            var goBack = function () {
            };
            $(".btn").filter(function (e, element) {
                return $(element).css('transition-duration') != '0s';
            }).each(function () {
                var $this = $(this);
                goBack = lazySetTransitionTo($this, $this.css('transition-duration'), goBack);
                $this.css('transition-duration', '0s');
                $this = [];
            });
            callback();
            goBack();
        }


        function fixTwoLines(selectors) {
            for (var i = 0; i < selectors.length; i++) {
                var $button = $(selectors[i]);
                var lineHeight = parseInt($button.css('line-height'));
                while ($button.height() >= (2 * lineHeight)) {
                    $button.css('padding', "12px");
                    $button.css('font-size', (parseInt($button.css('font-size')) - 1) + "px");
                }
            }
        }

        applyWithoutTransitions(function () {

            applyWidths([".cruise-2018-overview-quote .btn"]);
            fixTwoLines(["#menu-overview-raq", "#fixed-footer-2018-raq"]);
        })

    }
);

