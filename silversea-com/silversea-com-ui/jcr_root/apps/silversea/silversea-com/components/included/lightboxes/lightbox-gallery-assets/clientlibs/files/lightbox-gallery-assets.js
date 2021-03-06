$(function () {
    /***************************************************************************
     * Lightbox Gallery Assets for "cruise page" 2018"
     **************************************************************************/
    $(".open-lightbox-gallery-assets").on("click", function (e) {
        e.preventDefault();
        e.stopPropagation();
        var gotoSlide = $(this).data("go-to");
        var id = $(this).attr("id");
        var labelData = $(this).data("label");
        var $link = "",
            ajaxContentPath = "",
            modalTarget = "",
            $modalContent = "";

        try {
             $link = $("#" + id);
                ajaxContentPath = $link.closest('[' + labelData + ']').data(labelData.replace("data-", ""));
                modalTarget = $link.data('target');
                $modalContent = $(modalTarget);
        }catch(e){
             $link = $("#lb-mainGallery");
                ajaxContentPath = $link.closest('[data-lightbox-gallery-path]').data('lightbox-gallery-path');
                modalTarget = $link.data('target');
                $modalContent = $(modalTarget);
        }
        console.log(ajaxContentPath);
        $modalContent.find(".modal-content").addClass("modal-content--transparent");
        $modalContent.addClass("lightbox-no-scroll");
        $modalContent.find(".modal-dialog").addClass("custom-lightbox-width");
        $modalContent.find(".modal-dialog").addClass("lightbox-width-1200");
        $modalContent.modal('show');

        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function (e) {
            e.preventDefault();
            e.stopPropagation();
            var $modal = $(this);
            $modal.off('shown.bs.modal');
            // Append html response inside modal
            $modal.find('.modal-content').load(ajaxContentPath, function (e) {
                createLigthboxGallerySlider($modal, $link, gotoSlide);
                setHeight();
                setTopLightboxModal(0);
            });
            history.replaceState(null, null, location.href.replace(location.hash, "") + "#" + $link.attr("id"));
        });
        $modalContent.on('hide.bs.modal', function (e) {
            history.replaceState(null, null, location.href.replace(location.hash, ""));
        });
    });

    function setHeight() {
        var $modalLightbox = $(".modal.lightbox");
        var windowHeight = $(window).height();
        if (windowHeight < $modalLightbox.find(".modal-content").height()) {
            $modalLightbox.find(".main-slider .ratio").css('padding-bottom', (windowHeight - 250) + 'px');
        }
    }

    function createLigthboxGallerySlider($modal, $link, gotoSlide) {
        var $mainSlider = $modal.find('.main-slider').slick({
            slidesToShow: 1,
            slidesToScroll: 1,
            asNavFor: '.lightbox-gallery-assets .navigation-slider'
        });

        var $navigationSlider = $modal.find('.navigation-slider').slick({
            slidesToShow: 6,
            slidesToScroll: 5,
            asNavFor: '.lightbox-gallery-assets .main-slider',
            focusOnSelect: true,
            arrows: false
        });

        // Init video on click
        $('.lightbox-gallery-assets .video-link').on('click', function (e) {
            e.preventDefault();
            e.stopPropagation();
            if (typeof s7viewers !== 'undefined') {
                $(this).next('.video-icon').initVideo();
            }
        });

        $modal.find(".ga-virtual-tour").on('click', function (event) {
            createVirtualTour(this, event);
        });

        $mainSlider.on('beforeChange', function (event, slick, currentSlide, nextSlide) {
            $(".lightbox-gallery-assets .ga-label").hide();
            $(".lightbox-gallery-assets .ga-credits").hide();
            var $slider = $(this);
            // Kill video if current slide contains video
            var $video = $slider.find('.slick-current .video-icon');
            $video.find('.s7playpausebutton[selected="false"]').trigger('click');
            $video.attr('class', 'video-icon').empty();
            // Call lazy load for 2 previous and 2 next slides
            destroyVirtualTour();
        }).on('afterChange', function (event, slick, currentSlide) {
            createInfoAssetSection($(this));
            // Call lazy load for 2 previous and 2 next slides
            loadLazyImage($(this));
        });

        loadLazyImage($mainSlider);
        createInfoAssetSection($mainSlider);
        if (gotoSlide) {
            var split = gotoSlide.split("/");
            var image = split[split.length - 1];

            var $image = $(".ga-slider--main img[data-src*='" + image + "']");

            $($mainSlider).slick('slickGoTo', $image.closest("[data-slick-index]").data("slick-index"), true);
        }

    };//createLigthboxGallerySlider

    function loadLazyImage($slider) {
        var $sliderActiveMain = $slider.closest('.lightbox-gallery-assets').find('.ga-slider--main .slick-active');
        var $sliderActiveNav = $slider.closest('.lightbox-gallery-assets ').find('.ga-slider--nav .slick-active');
        $(".lightbox-gallery-assets .main-slider").slick("slickSetOption", "draggable", true, false);
        $(".lightbox-gallery-assets .main-slider").slick("slickSetOption", "swipe", true, false);

        lazyLazy($sliderActiveMain);
        lazyLazy($sliderActiveNav);
        lazyLazy($sliderActiveNav.prev());
        lazyLazy($sliderActiveNav.next());

        setTimeout(function () {
            lazyLazy($sliderActiveMain)
        }, 50);

    };//loadLazyImage

    function lazyLazy(slider) {
        if (slider != null) {
            slider.find('.lazy').lazy();
            // call lazy loading for 2 previous and next images active
            slider.prev().find('.lazy').lazy();
            slider.prev().prev().find('.lazy').lazy();
            slider.next().find('.lazy').lazy();
            slider.next().next().find('.lazy').lazy();
        }
    }

    function createInfoAssetSection($slider) {
        var $sliderActive = $slider.find(".slick-slide.slick-current.slick-active div");
        var $assetLabel = $(".lightbox-gallery-assets #ga-label-text-label");
        var $assetCredits = $(".lightbox-gallery-assets #ga-label-text-credits");
        var $assetSectionLabel = $(".lightbox-gallery-assets .ga-label");
        var $assetSectionCredits = $(".lightbox-gallery-assets .ga-credits");
        var label = $sliderActive.data("asset-label");
        var credits = $sliderActive.data("asset-credits");
        if (label != null && label.length > 0) {
            $assetLabel.html(label);
            $assetSectionLabel.show();
        } else {
            $assetLabel.html("");
            $assetSectionLabel.hide();
        }
        if (credits != null && credits.length > 0) {
            $assetCredits.html(credits);
            $assetSectionCredits.show();
        } else {
            $assetCredits.html("");
            $assetSectionCredits.hide();
        }
    };//createInfoAssetSection

    function destroyVirtualTour() {
        if (window.hasOwnProperty('virtualTour') && window.virtualTour != null) {
            window.virtualTour.destroy();
            window.virtualTour = null;
        }

        if (window.hasOwnProperty('virtualTourID') && window.virtualTourID != null) {
            $(window.virtualTourID).empty();
            $(window.virtualTourID).css("height", "0px");
            window.virtualTourID = null;
        }

        if (window.hasOwnProperty('virtualTourImage') && window.virtualTourImage != null) {
            $(window.virtualTourImage).css("display", "block");
            window.virtualTourImage = null;
        }

        if (window.hasOwnProperty('virtualTourType') && window.virtualTourType != null) {
            window.virtualTourType = null;
        }
    };//destroyVirtualTour


    function createVirtualTour(myThis, event) {
        event.preventDefault();
        event.stopPropagation();
        var imagePath = $(myThis).data('image-virtual-tour');
        var idContainerImage = $(myThis).attr('id');
        var idContainerVirtualTour = $(myThis).attr('id') + "-container";

        destroyVirtualTour();

        if (imagePath != null && idContainerVirtualTour != null) {
            $("#" + idContainerVirtualTour).css("height", "550px");
            $("#" + idContainerImage).css("display", "none");
            var intervalDiv = setInterval(function () {
                if ($("#" + idContainerVirtualTour).height() > 0) {
                    clearInterval(intervalDiv);
                    window.virtualTourID = "#" + idContainerVirtualTour;
                    window.virtualTourImage = "#" + idContainerImage;
                    window.virtualTourType = "ga-virtual-tour";
                    window.virtualTour = PhotoSphereViewer({
                        container: idContainerVirtualTour,
                        panorama: imagePath,
                        anim_speed: '0.4rpm',
                        move_speed: 1.0,
                        mousemove: true, //disable move to face slick swipe
                        time_anim: '1000',
                        min_fov: 10,
                        default_fov: 179,
                        usexmpdata: true, //it needs to be true to work in mobile
                        navbar: [
                            'autorotate',
                            'zoom',
                            'spacer-1',
                            'caption',
                            'gyroscope',
                            'fullscreen'
                        ]
                    });
                }
                $(".lightbox-gallery-assets .main-slider").slick("slickSetOption", "draggable", false, false);
                $(".lightbox-gallery-assets .main-slider").slick("slickSetOption", "swipe", false, false);
            }, 500);
        }
    };//createVirtualTour
});