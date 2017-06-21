$(function() {
    // toggle style for active/inactive tab
    $('.select-segment dd a').on('click', function(e) {
        e.preventDefault();
        var $tab = $(e.target);

        $tab.closest('dd').addClass('active');
        $tab.closest('dd').siblings('dd.active').toggleClass('active');

        // init slider inside current tab content
        var $currentSlider;
        $tab.on('shown.bs.tab', function() {
            $currentSlider = $($(this).attr('href')).find('.c-slider')
            $currentSlider.slick('unslick').slick(settingSlider);
        });
    })
});