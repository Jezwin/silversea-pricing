$(function() {
    /***************************************************************************
     * Modal
     **************************************************************************/
    // Clean modal content on close event
    $(document).on('hide.bs.modal', function(e) {
        $(e.target).removeData('bs.modal');
        var $modalContent = $('body > .modal .modal-content');
        $modalContent.empty();

        // Force to default class
        $modalContent.attr('class', 'modal-content');
    });

    // Build modal for image
    $('.automatic-modal, .virtual-tour-modal').on('click', function(e) {
        e.preventDefault();

        // HTML layout
        var $modalContent = $('<div class="modal-content modal-content--transparent">'
                + '<div class="modal-header"><button class="close c-btn--close" type="button" data-dismiss="modal" aria-label="Close"></button></div>'
                + '<div class="modal-body automatic-modal-body"><img class="o-img" /></div>' + '</div>');

        // Activate Modal
        $($(this).data('target')).modal('show');

        // Append image inside Modal
        var imagePath = $(this).attr('href');
        $('.modal').on('shown.bs.modal', function(e) {
            $(this).find('.modal-dialog').empty().append($modalContent);
            $(this).find('img').attr('src', imagePath);
        });
    });

    // Modal Gallery for cruise page
    $('.automatic-gallery-modal').on('click', function(e) {
        e.preventDefault();
        var $link = $(this), ajaxContentPath = $link.closest('[data-gallery-path]').data('gallery-path'), modalTarget = $link.data('target'), $modalContent = $(modalTarget);

        // Activate Modal
        $modalContent.modal('show');

        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function(e) {
            var $modal = $(this);
            $modal.off('shown.bs.modal');

            // Append html response inside modal
            $modal.find('.modal-dialog').load(ajaxContentPath, function() {
                // Activate gallery
                var $slideFor = $modal.find('.c-slider--for').slick({
                    slidesToShow : 1,
                    slidesToScroll : 1,
                    asNavFor : '.modal .c-slider--nav'
                });

                var $slideNav = $modal.find('.c-slider--nav').slick({
                    slidesToShow : 6,
                    slidesToScroll : 1,
                    asNavFor : '.modal .c-slider--for',
                    focusOnSelect : true
                });

                // Init video on click
                $('.video-link').on('click', function(e) {
                    e.preventDefault();
                    $(this).next('.c-video').initVideo();
                })

                // Show / calc counter
                var slideTotalItem = $slideFor.find('.slick-slide:not(.slick-cloned)').length;
                // Set total number of slide
                $slideFor.closest('.c-gallery__wrappertop').find('.c-gallery__counter .slide-item-total').html(slideTotalItem);
                $slideFor.on('beforeChange', function(event, slick, currentSlide, nextSlide) {
                    var $slide = $(this);
                    // Set counter according to the current slide
                    $slide.closest('.c-gallery__wrappertop').find('.c-gallery__counter .slide-item-current').html(nextSlide + 1);

                    // Kill video if current slide contains video
                    var $video = $slide.find('.slick-current .c-video');
                    $video.find('.s7playpausebutton[selected="false"]').trigger('click');
                    //$video.attr('class', 'c-video').empty();
                });

                // Tab gallery
                var $sliderTab = $('.c-gallery__tab__link');
                $modal.find('.c-gallery__tab__link').on('click', function(e) {
                    e.preventDefault();
                    var $linkCategory = $(this), targetSlideIndex = $slideFor.find('.slick-slide:not(".slick-cloned")[data-category-target="' + $linkCategory.data('category') + '"]').index() - 1;

                    // Slide to the first image of the current category
                    $slideFor.slick('slickGoTo', targetSlideIndex);
                });

                // Update category tab according to the current slide
                $slideFor.on('beforeChange', function(event, slick, currentSlide, nextSlide) {
                    var currentCategory = $(this).find('.slick-slide:not(".slick-cloned")').eq(nextSlide).data('category-target');
                    $('.c-gallery__tab__link:visible').removeClass('active');
                    $('.c-gallery__tab__link:visible[data-category="' + currentCategory + '"]').addClass('active');
                });

                // Scroll to the target image
                var currentImagePath = $link.attr('href');
                $slideFor.slick('slickGoTo', $slideFor.find('.slick-slide:not(".slick-cloned")[data-image="' + currentImagePath + '"]').first().data('slick-index'), true);
            });
        });
    });
});