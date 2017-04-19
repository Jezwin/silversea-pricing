+function ($) {
    'use strict';

    $.fn.cTab = function() {

        var _self = this;
        var _data = {
            link: '.c-tab__link',
            content: '.c-tab__content'
        };
        var _links = _self.find(_data.link);
        var _contents = _self.find(_data.content);

        /*
        ** Link Click Event to show content
        */
        _links.click(function (e) {
            e.preventDefault();
            var id = $(this).children('a').attr('href');

            $(_contents).removeAttr("data-state", null);
            $(_links).removeAttr("data-state", null);

            $(this).attr("data-state", "active");
            $(_data.content + id).attr("data-state", "active");
        });

        if (_self.hasClass('c-tab__accordion')) {

            $('body').on('trigger.viewport.changed', function() {
                if($.viewportDetect() === 'xs') {
                    _self.removeClass('open');
                    _self.children('.c-tab__nav').attr('aria-expanded', 'false');
                }
            });
        }
    };

    $(function() {
        /*
        ** Set Plugin cTab to the button
        */
        $(".c-tab").cTab();
    });
}(jQuery);