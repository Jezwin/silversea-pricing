$(function() {
    /***************************************************************************
     * Lightbox Gallery Assets for "cruise page" 2018"
     **************************************************************************/
    $(".open-lightbox-gallery-assets").on("click",function(e) {
        e.preventDefault();
        e.stopPropagation();
        var $link = $(this),
            ajaxContentPath = $link.closest('[data-lightbox-gallery-path]').data('lightbox-gallery-path'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);
        $modalContent.modal('show');

        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function(e) {
            e.preventDefault();
            e.stopPropagation();
            var $modal = $(this);
            $modal.off('shown.bs.modal');
            // Append html response inside modal
            $modal.find('.modal-dialog').load(ajaxContentPath, function(e) {
                createLigthboxGallerySlider($modal, $link);
            });
        });
    });

    function createLigthboxGallerySlider($modal, $link) {
        var $mainSlider = $modal.find('.main-slider').slick({
            slidesToShow : 1,
            slidesToScroll : 1,
            asNavFor : '.lightbox-gallery-assets .navigation-slider'
        });

        var $navigationSlider = $modal.find('.navigation-slider').slick({
            slidesToShow : 6,
            slidesToScroll : 5,
            asNavFor : '.lightbox-gallery-assets .main-slider',
            focusOnSelect : true,
            arrows: false
        });

        // Init video on click
        $('.lightbox-gallery-assets .video-link').on('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).next('.c-video').initVideo();
        });

        $mainSlider.on('beforeChange', function(event, slick, currentSlide, nextSlide) {
            var $slider = $(this);
            // Kill video if current slide contains video
            var $video = $slider.find('.slick-current .c-video');
            $video.find('.s7playpausebutton[selected="false"]').trigger('click');
            $video.attr('class', 'c-video').empty();
             // Call lazy load for 2 previous and 2 next slides
            loadLazyImage($(this));
        }).on('afterChange', function(event, slick, currentSlide) {
            createInfoAssetSection($(this));
            // Call lazy load for 2 previous and 2 next slides
            loadLazyImage($(this));
        });

        var $firstElement = $mainSlider.find('[data-slick-index="0"] > div');
        createInfoAssetSection($firstElement);

    };//createLigthboxGallerySlider

    function loadLazyImage($slider) {
        var $sliderActive = $slider.closest('.lightbox-gallery-assets').find('.slick-active');
        $(".lightbox-gallery-assets .main-slider").slick("slickSetOption", "draggable", true, false);
        $(".lightbox-gallery-assets .main-slider").slick("slickSetOption", "swipe", true, false);
        // call lazy loading for active image
        $sliderActive.find('.lazy').lazy();

        // call lazy loading for 2 previous and next images active
        $sliderActive.prev().find('.lazy').lazy();
        $sliderActive.prev().prev().find('.lazy').lazy();
        $sliderActive.next().find('.lazy').lazy();
        $sliderActive.next().next().find('.lazy').lazy();
        setTimeout(function() {
            $sliderActive.find('.lazy').lazy();
        }, 50);

    };//loadLazyImage

    function createInfoAssetSection(element) {
        var $assetLabel = $(".lightbox-gallery-assets #ga-label-text-label");
        var $assetCredits = $(".lightbox-gallery-assets #ga-label-text-credits");
        var $assetSectionLabel = $(".lightbox-gallery-assets .ga-label");
        var $assetSectionCredits = $(".lightbox-gallery-assets .ga-credits");
        var label = element.data("asset-label");
        var credits = element.data("asset-credits");
        if (label != null && label.length > 0){
            $assetLabel.html(label);
            $assetSectionLabel.show();
        } else {
            $assetLabel.html("");
            $assetSectionLabel.hide();
        }
        if (credits != null && credits.length > 0){
            $assetCredits.html(credits);
            $assetSectionCredits.show();
        } else {
            $assetCredits.html("");
            $assetSectionCredits.hide();
        }
    };//createInfoAssetSection
});