$(function() {
    $(window).on('resize load', function() {
        var $col = $('.c-hero-banner__content');
        var $row = $('.slick-active .c-hero-banner__row');

        if ($col.length > 0 && $row.length) {
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

    // On viewport change
    $('body').on('trigger.viewport.changed', function() {
        setBackgroundImage();
    });

    // On page load
    setBackgroundImage();

    function setBackgroundImage() {
        var $imageBannerList = $('.c-hero-banner__image, .c-search-cruise__image, .c-combo-cruise-header__image').find('.o-img');

        $imageBannerList.each(function() {
            var $image = $(this);
            var src = $image.prop("currentSrc") || $image.prop("src");
            $image.closest('div[class*=__image]').css('background-image', 'url(' + src + ')');
            $image.css('visibility', 'hidden');
        });
    }

    // init Video after modal load
    $('.modal').on('shown.bs.modal', function(event) {
        $(this).find('.c-video').initVideo();
    });
});