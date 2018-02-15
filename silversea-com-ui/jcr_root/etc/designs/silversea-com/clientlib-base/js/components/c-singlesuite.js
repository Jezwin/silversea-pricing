$(function() {
// Gallery expander
    $('.c-single-suite .c-suite__item-expander a').on('click', function(e) {
        e.preventDefault();
        var $trigger = $(this), $wrapper = $trigger.closest('.c-suite-gallery');
        var totalHeight = 0;

        if ($wrapper.hasClass('open')) {
            $wrapper.css('height', $wrapper.outerHeight());

            // Animate for slide effect (slide up)
            $wrapper.animate({
                'height' : $wrapper.find('> div').first().outerHeight()
            }, 600, function() {
                var defaultItem = $wrapper.closest('.c-cruise-ship-info').length ? 1 : 0;
                $wrapper.css('height', $wrapper.height()).find('> div:gt(' + defaultItem + '):not(.c-suite__item-expander)').hide();

                $(this).css('height', '').removeClass('open');

                // After animation, launch scrollTo
                $('html, body').animate({
                    scrollTop : $wrapper.offset().top - $('.c-header').height()
                }, 0);
            });
        } else {
            // show hidden image group
            $wrapper.css('height', $wrapper.height()).find('> div').show();

            // Calculate full height
            $wrapper.children().each(function() {
                totalHeight = totalHeight + $(this).outerHeight(true);
            });

            // Call lazy plugin
            $wrapper.find('.lazy').lazy();

            // Animate for slide effect (slide down)
            $wrapper.animate({
                'height' : totalHeight
            }, 1000, function() {
                $(this).css('height', '').addClass('open');
            });
        }
    });

   $('.c-suite-gallery').find(".lazy").lazy();  
});