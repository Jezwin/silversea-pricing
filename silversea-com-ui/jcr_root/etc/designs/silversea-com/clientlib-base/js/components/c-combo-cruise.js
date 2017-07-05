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

    // Overview tab : open suite tab for the current suite
    $('.c-combo-cruise #overview .c-suitelist__item[data-tab-target]').on('click', function() {
        var $trigger = $(this);
        // Open suite tab
        var tabId = $trigger.data('tab-target');
        $('a[href="' + tabId + '"]').closest('li').trigger('click');

        // scroll to tab
        $('html, body').animate({
            scrollTop: $('.c-combo-cruise-tab').offset().top - $('.c-header').height() - 24
        }, 500, function() {
          // Open current suite only (force close others suite)
          $(tabId).find('.panel:eq(' + $trigger.index() + ')').find('[role="tab"]').trigger('click');
        });
    });

    // TODO : Overview tab : open route tab for the current segment
    
    
});