$(function() {
    $('.request-quote').on('click', function(e) {
        e.stopPropagation();
    })

    // Force reinit slider when it was inside a hidden tab
    $('div[id^="suitelist-collapse"]').on('show.bs.collapse', function(e) {
        $(this).find('.c-tab__content[data-state="active"]').trigger('ctabcontent-shown');
    });

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

    // Open location tab on click event on ""
    $('a[data-toggle="location-tab"]').on('click', function(e) {
        e.preventDefault();
        var $tab = $(this).closest('.c-tab ').find('a[href^="#suite-location"]').closest('.c-tab__link').trigger('click');
    })
});