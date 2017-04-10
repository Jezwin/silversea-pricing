$(function() {
    $(window).on('resize load', function() {
        var $col = $('.c-hero-banner__content');
        var $row = $('.c-hero-banner__row');

        if ($col.length > 0) {
            var offsetLeft = $row.offset().left;

            if ($.viewportDetect() === 'sm' || $.viewportDetect() === 'md') {
                if (offsetLeft < 60) {
                    $col.css('left', 60 - offsetLeft);
                } else {
                    $col.css('left', 'auto');
                }
            } else {
                $col.css('left', 'auto');
            }
        }
    });

    $('body', window).on('trigger.viewport.changed load', function() {
        var $imageBannerList = $('.c-hero-banner .o-img');
        $imageBannerList.each(function() {
            var $imageBanner = $(this);
            $imageBanner.closest('.c-hero-banner__image').css('background-image', 'url(' + $imageBanner.prop("currentSrc") + ')');
            $imageBanner.css('visibility', 'hidden');
        });
    });
});