+function ($) {
    'use strict';

    $.fn.cAccordion = function() {

        var _self = this;
        
        var _data = {
            content: '.c-accordion__body',
            action: '.c-accordion__action',
            more: _self.attr('data-more') || '<i class="fa fa-angle-down"></i>',
            close: _self.attr('data-close') || '<i class="fa fa-angle-up"></i>'
        },
        _elems = _self.children('.c-accordion__elem'),
        _links = _elems.children('.c-accordion__header'),
        _contents = _self.find(_data.content),
        _multiple = (_self.attr('data-multiview') === 'true') || false;

        /*
        ** Link Click Event to show content
        */
        _links.click(function (e) {
            e.preventDefault();

            if($(e.target).hasClass('c-accordion__header--unclickable') || $(e.target).parent().hasClass('c-accordion__header--unclickable')){
                return;
            }

            var elem = $(this).parent().children(_data.content);
            var isCollapsed = ($(this).attr('data-state') === 'true');
            var currentAction = (!isCollapsed) ? _data.close : _data.more;

            if (!_multiple) {
                _contents.attr('data-collapsed', 'false');
                _links.children(_data.action).html(_data.more);
                _links.attr('data-state', 'false');
            }
            elem.attr('data-collapsed', !isCollapsed);
            $(this).attr('data-state', !isCollapsed);
            $(this).find(_data.action).html(currentAction);

        });
    };

    
    $(function() {
        /*
        ** Set Plugin cTab to the button
        */
        $(".c-accordion").each(function (i) {
            $(this).cAccordion();
        });
    });
}(jQuery);