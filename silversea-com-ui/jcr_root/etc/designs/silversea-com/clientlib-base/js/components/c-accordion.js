+function ($) {
    'use strict';

    $.fn.cAccordion = function() {

        var _self = this;
        
        var _data = {
            elem: '.c-accordion__elem',
            link: '.c-accordion__header',
            content: '.c-accordion__body'
        };
        var _elems = _self.find(_data.elem);
        var _links = _self.find(_data.link);
        var _contents = _self.find(_data.content);

        /*
        ** Link Click Event to show content
        */
        _elems.children(".c-accordion__header").click(function (e) {
            e.preventDefault();

            var elem = $(this).parent().children(".c-accordion__body");
            var isCollapsed = (elem.attr("data-collapsed") == 'true');
            elem.attr("data-collapsed", !isCollapsed);
            $(this).attr("data-state", !isCollapsed);

        });
    };

    
    $(function() {
        /*
        ** Set Plugin cTab to the button
        */
        $(".c-accordion").cAccordion();
    });
}(jQuery);