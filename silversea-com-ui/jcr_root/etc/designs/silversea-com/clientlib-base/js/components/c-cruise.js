$(function() {
    $('.c-cruise .request-quote').on('click', function(e) {
        e.stopPropagation();
    })

    // Force reinit tab when it was inside a hidden suite tab
    $('div[id^="suitelist-collapse"]').on('show.bs.collapse', function(e) {
        $(this).find('.c-tab__content[data-state="active"]').trigger('ctabcontent-shown');
    });

    // Force reinit slider
    $('.c-suitelist').find('.c-tab__content').on('ctabcontent-shown', function() {
        // load image inside slider first
        $(this).find('.lazy').lazy();

        // reinit slider with resize event
        setTimeout(function() {
            $(window).trigger('resize');
        }, 50);
    });

    // call lazy load : load image inside the current tab actived (the one opened on page load)
    $('.c-tab--cruise > .c-tab__body > .c-tab__content[data-state="active"]').find('.lazy').lazy();

    $('.c-cruise .c-tab__content').on('ctabcontent-shown', function() {
        // Force reinit slider for cruise for Enrichments and key people slider
        $(this).find('.c-slider.slick-initialized:not(.c-slider--for, .c-slider--nav)').slick('unslick').slick(settingSlider);

       // call lazy load : load image inside the current tab opened
        $(this).find('.lazy:visible').lazy();
    });

    $('.c-cruise div[id^="suitelist-collapse"]').on('shown.bs.collapse', function(e) {
        var $currentCollapse = $(this);
        var $item = $('div[id^="suitelist-collapse"]').not($currentCollapse);
        $item.prev('.c-suitelist__heading').addClass('opacity');

        // Scroll page to content
        var scrollTargetOffsetTop = $.viewportDetect() === 'xs' ? $currentCollapse.offset().top - 20 : $currentCollapse.prev('.c-suitelist__heading').offset().top - 10;
        $('html, body').animate({
            scrollTop : scrollTargetOffsetTop - $('.c-header').height() - $('.c-main-nav__container').height()
        }, 500);

    }).on('hide.bs.collapse', function(e) {
        var $item = $('div[id^="suitelist-collapse"]').not($(this));
        $item.prev('.c-suitelist__heading').removeClass('opacity');
    });

    // Open location tab on click event on tab
    $('.c-cruise a[data-toggle="location-tab"]').on('click', function(e) {
        e.preventDefault();
        var $tab = $(this).closest('.c-tab ').find('a[href^="#suite-location"]').closest('.c-tab__link').trigger('click');
    })

    // Force Scroll top for highlight container on resize
    $(window).on('resize', function() {
        var viewport = $.viewportDetect();
        if (viewport !== 'md' || viewport !== 'lg') {
            $('.c-cruise-highlights__content').scrollTop(0);
        }
    });

    // Toggle Highlight (small viewport)
    var $container = $('.c-cruise-highlights__content');
    $container.find('a[data-toggle-text]').on('click', function(e) {
        e.preventDefault();
        $container.toggleClass('open');
        $('html, body').animate({
            scrollTop : $('#highlight').offset().top - $('.c-header').height()
        }, 500);
    });

    // keypeople tab/carousel
    $('.c-cruise-keypeople').find('[data-keypeople-toggle]').on('click', function() {
        var $currentTrigger = $(this);

        // highlight current
        $('.c-cruise-keypeople').find('[data-keypeople-toggle]').removeClass('active');
        $currentTrigger.addClass('active');

        // show current details
        $('.keypeople__details__inner-wrapper').hide();
        $('#' + $currentTrigger.data('keypeople-toggle') + '').show();

        // Close details
        $('.btn-close').on('click', function(e) {
            e.preventDefault();
            $(this).closest('.keypeople__details__inner-wrapper').hide();
        });
    });

    /***************************************************************************
     * Open tab from link other than tab
     **************************************************************************/
    $('.c-cruise a[data-tab-target]').on('click', function(e) {
        e.preventDefault();
        var $link = $(this);
        // Scroll to top first
        $('html, body').animate({
            scrollTop : 0
        }, 0);

        // Open the target tab
        $('a[href="' + $link.data('tab-target') + '"]').closest('li').trigger('click');

        // Open collapse if collpase Id
        if ($link.data('collpase-id') !== 'undefined') {
            $('[data-target="#' + $link.data('collpase-id') + '"]').trigger('click');
        }
    });

    /***************************************************************************
     * Scroll to content
     **************************************************************************/
    $('.c-cruise a[data-scroll-target]').on('click', function(e) {
        e.preventDefault();
        $('html, body').animate({
            scrollTop : $($(this).data('scroll-target')).offset().top - $('.c-header').height()
        }, 500);
    });

    /***************************************************************************
     * Scroll to content expanded
     **************************************************************************/
    $('.c-cruise [role="tabpanel"]:not(.c-suitelist__collapse)').on('shown.bs.collapse', function(e) {
        e.stopPropagation();
        $('html, body').animate({
            scrollTop : $(this).prev('[role="button"]').offset().top - $('.c-header').height() - $('.c-main-nav__bottom').height()
        }, 500);
    });

    /***************************************************************************
     * Cruise fare addiction collapse
     **************************************************************************/
    $('.c-list--cruise-fare-additions').each(function() {
        var $list = $(this);
        var $wrapper = $list.closest('.c-list--cruise-fare-additions__wrapper');
        var $items = $list.find('li');
        var isMobile = $.viewportDetect() === 'xs';

        // Split list into 2 lists
        var length = $items.length;
        var halfLength = Math.round(length / 2);
        var $lastHalf = $($items.splice(halfLength, length - 1)).remove();
        $list.after($('<ul class="' + $list.attr('class') + '"></ul>').html($lastHalf));

        // Hide content according to viewport
        var hideContent = (function hideContent() {
            isMobile = $.viewportDetect() === 'xs';

            $wrapper.find('li').removeClass('hidden');
            if (isMobile) {
                $wrapper.find('li:gt(7)').addClass('hidden');
            } else {
                $('.c-list--cruise-fare-additions').each(function() {
                    $(this).find('li:gt(3)').addClass('hidden');
                });
            }

            return hideContent;
        })();

        $('body').on('trigger.viewport.changed', function() {
            hideContent();
        })

        // Action show/hide
        var $trigger = $wrapper.next('.c-list__expand').find('a');

        $trigger.on('click', function(e) {
            e.preventDefault();

            // Set height just before animation
            var isClosed = $wrapper.hasClass('closed');
            $wrapper.css('height', isClosed ? getMaxHeight('closed') : getMaxHeight('opened'));

            // Animate for slide effect
            if (isClosed) {
                // open
                $wrapper.toggleClass('closed opened');

                $wrapper.animate({
                    'height' : getMaxHeight('opened')
                }, 300, function() {
                    // Remove inline height
                    $wrapper.css('height', '');
                });
            } else {
                // close
                $wrapper.animate({
                    'height' : getMaxHeight('closed')
                }, 300, function() {
                    $wrapper.toggleClass('closed opened');
                    // Remove inline height
                    $wrapper.css('height', '');
                });
            }
        });

        // function get max Height
        function getMaxHeight(state) {
            var totalHeight = [];
            var isMobile = $.viewportDetect() === 'xs';
            $wrapper.find('.c-list--cruise-fare-additions').each(function() {
                var $list = $(this);
                var sum = 0;
                var items;

                if (state === 'closed') {
                    if (isMobile) {
                        $items = $list.find('li:lt(8)');
                    } else {
                        $items = $list.find('li:lt(4)');
                    }
                } else {
                    $items = $list.find('li');
                }

                $items.each(function() {
                    sum += $(this).outerHeight(true);
                });

                totalHeight.push(sum);
            });

            // is mobile return height of both column, is not mobile return only the heighest
            return isMobile ? totalHeight[0] + totalHeight[1] : Math.max.apply(Math, totalHeight);
        }
    });
});