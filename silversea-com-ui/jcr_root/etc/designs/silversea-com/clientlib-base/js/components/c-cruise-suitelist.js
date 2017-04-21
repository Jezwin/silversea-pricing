$(function() {
    $('.request-quote').on('click', function(e) {
        e.stopPropagation();
    })

    $('div[id^="suitelist-collapse"]').on('shown.bs.collapse', function(e) {
        var $item = $('div[id^="suitelist-collapse"]').not($(this));
        $item.prev('.c-suitelist__heading').addClass('opacity');
    }).on('hide.bs.collapse', function(e) {
        var $item = $('div[id^="suitelist-collapse"]').not($(this));
        $item.prev('.c-suitelist__heading').removeClass('opacity');
    });
});