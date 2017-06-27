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
            var $image = $(this),
            mediaSrc = $image.prop('currentSrc') || $image.prop('src');

            $image.closest('.c-cruise__gallery__item').css('background-image', 'url(' + mediaSrc + ')');
            $image.css('visibility', 'hidden');
        });
    }

    // Gallery expander
    $('.c-cruise .expander-open, .c-cruise .expander-close').on('click', function(e) {
        e.preventDefault();
        var $trigger = $(this), $wrapper = $trigger.closest('.c-cruise__gallery');
        var totalHeight = 0;

        if ($wrapper.hasClass('open')) {
            $wrapper.css('height', $wrapper.outerHeight());

            // Animate for slide effect (slide up)
            $wrapper.animate({
                'height' : $wrapper.find('> div').first().outerHeight()
            }, 600, function() {
                var defaultItem = $wrapper.closest('.c-cruise-ship-info').length ? 1 : 0;
                $wrapper.css('height', $wrapper.height()).find('> div:gt(' + defaultItem + '):not(.c-cruise__item-expander)').hide();

                $(this).css('height', '').removeClass('open');

                // After animation, launch scrollTo
                $('html, body').animate({
                    scrollTop : $wrapper.offset().top - $('.c-header').height()
                }, 1000);
            });
        } else {
            // show hidden image group
            $wrapper.css('height', $wrapper.height()).find('> div').show();

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