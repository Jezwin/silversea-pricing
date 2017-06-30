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
            $currentSlider = $($(this).attr('href')).find('.c-slider')
            $currentSlider.slick('unslick').slick(settingSlider);
        });
    })

    // Overview tab : open suite/route tab for the current item
    $('.c-combo-cruise #overview [data-tab-target]').on('click', function() {
        var $trigger = $(this);
        // Open route tab
        var tabId = $trigger.data('tab-target');
        $('a[href="' + tabId + '"]').closest('li').trigger('click');

        // scroll to tab
        $('html').stop().animate({
            scrollTop: $('.c-combo-cruise .c-combo-cruise-tab').offset().top - $('.c-header').height() - 24
        }, 500, function() {
            if (tabId == '#suitenfare') {
                // Open current suite only (force close others suite)
                $(tabId).find('.panel:eq(' + $trigger.index() + ')').find('[role="tab"]').trigger('click');
            } else {
                // Open current segment
                $(tabId).find('.select-segment dd:eq(' + $trigger.closest('[data-slick-index]').data('slick-index') + ') a').trigger('click');
            }
        });
    });
});