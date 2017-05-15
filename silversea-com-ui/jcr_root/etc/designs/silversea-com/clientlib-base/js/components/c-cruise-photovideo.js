$(function() {
    // On viewport change
    $('body').on('trigger.viewport.changed', function() {
        setBackground();
    });

    // On page load
    setBackground();

    function setBackground() {
        var $imageList = $('.c-cruise__gallery__item .o-img');

        $imageList.each(function() {
            var $image = $(this);
            $image.closest('.c-cruise__gallery__item').css('background-image', 'url(' + $image.prop('currentSrc') + ')');
            $image.css('visibility', 'hidden');
        });
    }

    // Gallery expander
    $('.c-cruise-photovideo .expander-open, .c-cruise-photovideo .expander-close').on('click', function(e) {
        e.preventDefault();
        var $trigger = $(this), $wrapper = $trigger.closest('.c-cruise__gallery');
        var totalHeight = 0;

        if ($wrapper.hasClass('open')) {
            $wrapper.css('height', $wrapper.outerHeight());

            // Animate for slide effect (slide up)
            $wrapper.animate({
                'height' : $wrapper.find('.c-cruise__gallery__featured').first().outerHeight()
            }, 1000, function() {
                $wrapper.css('height', $wrapper.height()).find('.c-cruise__gallery__featured:not(:eq(0))').hide();
                $(this).css('height', '').removeClass('open');
            });
        } else {
            // show hidden image group
            $wrapper.css('height', $wrapper.height()).find('.c-cruise__gallery__featured').show();

            // Calculate full height
            $wrapper.children().each(function() {
                totalHeight = totalHeight + $(this).outerHeight(true);
            });

            // Animate for slide effect (slide down)
            $wrapper.animate({
                'height' : totalHeight
            }, 1000, function() {
                $(this).css('height', '').addClass('open');
            });
        }
    });
});