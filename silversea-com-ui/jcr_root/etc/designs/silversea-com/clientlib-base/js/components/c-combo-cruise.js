$(function() {
    // Route tab : toggle style for active/inactive tab
    $('.select-segment dd a').on('click', function(e) {
        e.preventDefault();
        var $tab = $(e.target);
        var $item = $tab.closest('dd');
        $item.addClass('active');
        $item.siblings('dd.active').toggleClass('active');

        // init slider inside current tab content
        var $currentSlider;
        $tab.on('shown.bs.tab', function() {
            var $panel = $($(this).attr('href'));
            $currentSlider = $panel.find('.c-slider')
            $currentSlider.slick('unslick').slick(settingSlider);

            // Scroll to panel
            $('html').delay(500).animate({
                scrollTop: $panel.offset().top - $('.c-header').height() - 24
            }, 500);
        });
    })

    // Overview tab : open suite/route tab for the current item
    $('.c-combo-cruise #overview [data-tab-target]').on('click', function() {
        var $trigger = $(this);

        // scroll to "top" page
        $('html, body').stop().animate({
            scrollTop: $('.c-combo-cruise .c-combo-cruise-tab').offset().top - $('.c-header').height() - 24
        }, 0, function() {
            // Open tab
            var tabId = $trigger.data('tab-target');
            $('a[href="' + tabId + '"]').closest('li').trigger('click');

            if (tabId == '#suitenfare') {
                // Open current suite only (force close others suite)
                $(tabId).find('.panel:eq(' + $trigger.index() + ')').find('[role="tab"]').trigger('click');
            } else {
                // Open current segment
                $(tabId).find('.select-segment dd:eq(' + $trigger.closest('[data-slick-index]').data('slick-index') + ') a').trigger('click');
            }
        });
    });

    /***************************************************************************
     * Truncate text
     **************************************************************************/
    $('.textTruncate').each(function(i, el){
        var node = $(el);
        var limit = parseInt(node.data('limit'));
        if(node.find('p span').text().length > limit){
            var trucatedText = node.find('p span').text().substr(0, limit);

            node.find('p span').text(trucatedText+'...');
        }
    });

    /***************************************************************************
     * Read more
     **************************************************************************/
    var readMore = (function readMore() {
        $('.c-cruise-ship-info__item .variationcontent__descr').each(function(i, description) {
            var $description = $(description);

            if ($description.height() > 137) {
                var $descriptionToggle = $description.next('.variationcontent__descr__expand');
                var $moreBtn = $descriptionToggle.find('.read_more');
                var $lessBtn = $descriptionToggle.find('.read_less');

                $description.attr('data-original-height', $description.height());
                $description.addClass('clipped');

                $moreBtn.on('click', function(e) {
                    e.preventDefault();
                    $description.addClass('opened');
                    $description.css('height', $description.data('original-height'))
                });

                $lessBtn.on('click', function(e) {
                    e.preventDefault();
                    $description.removeClass('opened');
                    $description.css('height', '')
                });
            }
        });

        return readMore;
    })();

    $('.c-cruise .expander-open').on('click', function(e) {
        readMore();
    });
});