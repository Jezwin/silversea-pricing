$(function() {
    $('.request-quote').on('click', function(e) {
        e.stopPropagation();
    })

    // Force reinit slider when it was inside a hidden tab
    $('.c-suitelist').find('.c-tab__content').on('ctabcontent-shown', function() {
        var $currentSlider = $(this).find('.c-slider');
        $(window).trigger('resize');
        $currentSlider.slick('unslick').slick();
    });

    $('div[id^="suitelist-collapse"]').on('shown.bs.collapse', function(e) {
        var $currentCollapse = $(this);
        var $item = $('div[id^="suitelist-collapse"]').not($currentCollapse);
        $item.prev('.c-suitelist__heading').addClass('opacity');

        // Scroll page to content
        var scrollTargetOffsetTop = $.viewportDetect() === 'xs' ? $currentCollapse.offset().top - 20 : $currentCollapse.prev('.c-suitelist__heading').offset().top - 10;
        $('html, body').animate({
            scrollTop : scrollTargetOffsetTop - $('.c-header').height()
        }, 500);
    }).on('hide.bs.collapse', function(e) {
        var $item = $('div[id^="suitelist-collapse"]').not($(this));
        $item.prev('.c-suitelist__heading').removeClass('opacity');
    });
});