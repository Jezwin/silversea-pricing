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
        setTimeout(function() {
            $(window).trigger('resize');
        }, 50);
    });

    // Force reinit slider for cruise for Enrichments and key people slider
    $('.c-cruise .c-tab__content').on('ctabcontent-shown', function() {
        $(this).find('.c-slider.slick-initialized:not(.c-slider--for, .c-slider--nav)').slick('unslick').slick(settingSlider);
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
});