+function ($) {
    'use strict';

    $.fn.cAccordion = function() {

        var _self = this;
        
        var _data = {
            elem: '.c-accordion__elem',
            link: '.c-accordion__header',
            content: '.c-accordion__body',
            action: '.c-accordion__action',
            more: 'See More&nbsp;<i class="fa fa-angle-down"></i>',
            less: 'Close&nbsp;<i class="fa fa-angle-up"></i>'
        };
        var _elems = _self.find(_data.elem);
        var _links = _self.find(_data.link);
        var _contents = _self.find(_data.content);
        var _multiple = (_self.attr('data-multiview') == 'true') || false;
        /*
        ** Link Click Event to show content
        */
        _elems.children('.c-accordion__header').click(function (e) {
            e.preventDefault();

            var elem = $(this).parent().children('.c-accordion__body');
            var isCollapsed = (elem.attr('data-collapsed') == 'true');
            var value = (!isCollapsed) ? _data.less : _data.more;

            if (!_multiple) {
                _contents.attr('data-collapsed', 'false');
                _links.attr('data-state', 'false');
            }
            elem.attr('data-collapsed', !isCollapsed);
            $(this).attr('data-state', !isCollapsed);
            $(this).children(_data.action).html(value);

        });
    };

    
    $(function() {
        /*
        ** Set Plugin cTab to the button
        */
        $(".c-accordion").cAccordion();
    });
}(jQuery);