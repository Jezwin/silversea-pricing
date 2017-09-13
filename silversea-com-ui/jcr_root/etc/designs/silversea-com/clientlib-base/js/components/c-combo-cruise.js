$(function() {
    // Route tab : toggle style for active/inactive tab
    $('.select-segment dd a').on('click', function(e) {
        e.preventDefault();
        var $tab = $(e.target);
        var $item = $tab.closest('dd');
        $item.addClass('active');
        $item.siblings('dd.active').toggleClass('active');

        // init slider inside current tab content
        var $currentSlider;
        $tab.on('shown.bs.tab', function() {
            var $panel = $($(this).attr('href'));
            $currentSlider = $panel.find('.c-slider')
            $currentSlider.slick('unslick').slick(settingSlider);

            // Scroll to panel
            $('html, body').stop().delay(100).animate({
                scrollTop: $panel.offset().top - $('.c-header').height() - 24 - $('.c-main-nav__bottom').height()
            }, 300);
        });
    })

    // Overview tab : open suite/route tab for the current item
    $('.c-combo-cruise #overview [data-tab-target]').on('click', function() {
        var $trigger = $(this);

        // scroll to "top" page
        $('html, body').stop().animate({
            scrollTop: $('.c-combo-cruise .c-tab__nav--fixed').offset().top - $('.c-header').height() - 24
        }, 0, function() {
            // Open tab
            var tabId = $trigger.data('tab-target');
            $('a[href="' + tabId + '"]').closest('li').trigger('click');

            if (tabId == '#suitenfare') {
                if($trigger.closest('.c-suitelist').length > 0) {
                    // Open current suite only (force close others suite)
                    $(tabId).find('.panel:eq(' + $trigger.index() + ')').find('[role="tab"]').trigger('click');
                }
            } else {
                // Open current segment
                $(tabId).find('.select-segment dd:eq(' + $trigger.closest('[data-slick-index]').data('slick-index') + ') a').trigger('click');
            }
        });
    });
});