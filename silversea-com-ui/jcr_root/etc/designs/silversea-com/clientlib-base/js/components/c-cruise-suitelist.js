$(function() {
    $('.request-quote').on('click', function(e) {
        e.stopPropagation();
    })

    $('div[id^="suitelist-collapse"]').on('shown.bs.collapse', function(e) {
        var $item = $('div[id^="suitelist-collapse"]').not($(this));
        $item.prev('.c-suitelist__heading').addClass('opacity');

        $('html, body').animate({
            scrollTop: $(this).prev('.c-suitelist__heading').offset().top - $('.c-header').height()
        }, 500);
    }).on('hide.bs.collapse', function(e) {
        var $item = $('div[id^="suitelist-collapse"]').not($(this));
        $item.prev('.c-suitelist__heading').removeClass('opacity');
    });
});