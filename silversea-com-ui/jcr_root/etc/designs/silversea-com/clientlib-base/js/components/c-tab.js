+function($) {
    'use strict';

    $.fn.cTab = function() {
        this.each(function() {
            var _self = $(this),
            _data = {
                link : '.c-tab__link',
                content : '.c-tab__content',
                nav : '.c-tab__link'
            },
            _links = _self.find('.c-tab__nav').first().children(_data.link),
            _orphan_link = $('.c-cruise__descr a.bound, .parbase a[href^="#"]'),
            _contents = _self.find('.c-tab__body').first().children(_data.content),
            _showTab = function(tab) {
                var id = $(tab).children('a').attr('href');
                $(_contents).removeAttr('data-state', null);
                $(_links).removeAttr('data-state', null);

                $(tab).attr('data-state', 'active');
                $(_data.content + id).attr('data-state', 'active').trigger('ctabcontent-shown');
            };

            /*
             * * Orphan Link Click Event to show content
             */
            _orphan_link.click(function(e) {
                e.preventDefault();
                var tab = _self.find('.c-tab__link a[href="' + $(this).attr('href') + '"]');
                if (tab.length > 0)
                    _showTab($(tab[0]).parent());
            });

            /*
             * * Link Click Event to show content
             */
            _links.click(function(e) {
                e.preventDefault();
                _showTab(this);
            });

            if (_self.hasClass('c-tab__accordion')) {
                $('body').on('trigger.viewport.changed', function() {
                    if ($.viewportDetect() === 'xs') {
                        _self.removeClass('open');
                        _self.children('.c-tab__nav').attr('aria-expanded', 'false');
                    }
                });
            }

            if (!_self.hasClass('c-tab__edit')) {
                var activeTab = _self.find('.c-tab__nav').first().children(_data.link + '[data-state="active"]')[0];
                if (activeTab) {
                    _showTab(activeTab);
                }
            }
        });
    };

    $(function() {
        /*
         * * Set Plugin cTab to the button
         */
        $('.c-tab').cTab();
    });
}(jQuery);