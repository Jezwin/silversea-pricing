+function($) {
   'use strict';

    $.fn.cTab = function() {
        this.each(function(){
            var _self = $(this),
            _data = {
                link : '.c-tab__link',
                content : '.c-tab__content',
                nav : '.c-tab__link'
            },
            _links = _self.find('.c-tab__nav').first().children(_data.link),
            _orphan_link = $('.c-cruise__descr a.bound, a[href^="#"]'),
            _contents = _self.find('.c-tab__body').first().find(_data.content),
            _showTab = function (tab) {
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
                var id = $(this).attr('href');

                $(_data.nav).each(function() {
                    var that = $(this);
                    if (that.find('a').attr('href') === id) {
                        _showTab(that);
                    }
                });
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
                var activeTab = $('.c-tab__link[data-state="active"]')[0];
                if (activeTab)
                    _showTab(activeTab);
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