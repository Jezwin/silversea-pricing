$(function () {
    "use strict";

    /***************************************************************************
     * Lightbox Suite detail
     **************************************************************************/
    $('.cruise-2018-itineraries-container').on('click',".open-lightbox-port", function (e) {
        e.preventDefault();
        e.stopPropagation();
        var $link = $(this),
            ajaxContentPath = $link.attr('href'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);
        $modalContent.find(".modal-content").addClass("lightbox-port-content");
        $modalContent.addClass("lightbox-no-scroll");

        // Activate Modal
        $modalContent.modal('show');

        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function (e) {
            e.preventDefault();
            e.stopPropagation();
            history.pushState(null, null, location.href.replace(location.hash, "") + "#" + $link.attr("id"));
            //avoid ios issue
            if (window.scrollSupport != null && window.scrollSupport) {
                window.iNoBounce.enable();
            }
            var $modal = $(this);
            $modal.off('shown.bs.modal');
            // Append html response inside modal
            $modal.find('.modal-content').load(ajaxContentPath, function (e) {
                setTopLightboxModal();
                createSlider($modal, $link);

            });
        });
        $modalContent.on('hide.bs.modal', function (e) {
            history.replaceState(null, null, location.href.replace(location.hash, ""));
        });
    });

    function createSlider($modal, $link) {
        var $mainSlider = $modal.find('.lightbox-port .lg-asset-slider').slick({
            slidesToShow: 1,
            slidesToScroll: 1,
            dots: false,
            responsive: [
                {
                    breakpoint: 480,
                    settings: {
                        arrows: true
                    }
                }
            ]
        });

        // Init video on click
        $('.lightbox-port .lg-asset-gallery .video-link').on('click', function (e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).next('.c-video').initVideo();
        });

        $mainSlider.on('beforeChange', function (event, slick, currentSlide, nextSlide) {
            var $slider = $(this);
            // Kill video if current slide contains video
            var $video = $slider.find('.slick-current .c-video');
            $video.find('.s7playpausebutton[selected="false"]').trigger('click');
            $video.attr('class', 'c-video').empty();
        }).on('afterChange', function (event, slick, currentSlide) {
            // Call lazy load for 2 previous and 2 next slides
            loadLazyImage($(this));
        });

        loadLazyImage($mainSlider);
        createLineProgressBarPortGallery($modal);

    };//createLigthboxGallerySlider

    function loadLazyImage($slider) {
        var $sliderActive = $slider.closest('.lightbox-port').find('.slick-active');
        $(".lightbox-suite .lg-asset-slider").slick("slickSetOption", "draggable", true, false);
        $(".lightbox-suite .lg-asset-slider").slick("slickSetOption", "swipe", true, false);
        // call lazy loading for active image
        $sliderActive.find('.lazy').lazy();

        // call lazy loading for 2 previous and next images active
        $sliderActive.prev().find('.lazy').lazy();
        $sliderActive.prev().prev().find('.lazy').lazy();
        $sliderActive.next().find('.lazy').lazy();
        $sliderActive.next().next().find('.lazy').lazy();
        setTimeout(function () {
            $sliderActive.find('.lazy').lazy();
        }, 50);

    };//loadLazyImage


    function createLineProgressBarPortGallery($modal) {
        var widthSlider = $modal.find(".slick-dots").width();
        if ($("body").hasClass("viewport-md") || $("body").hasClass("viewport-lg")) {
            if (widthSlider > 930) {
                widthSlider = 930;
            }
        } else if ($("body").hasClass("viewport-sm")) {
            if (widthSlider > 768) {
                widthSlider = 630;
            }
        } else if ($("body").hasClass("viewport-xs")) {
            widthSlider = widthSlider;
        }

        var liItem = $modal.find("ul.slick-dots li").length;
        var liWidth = ((widthSlider / liItem) - 1);

        if ($("body").hasClass("viewport-md") || $("body").hasClass("viewport-lg")) {
            $modal.find("ul.slick-dots li").css("width", liWidth + "px");
        } else if ($("body").hasClass("viewport-sm")) {
            $modal.find("ul.slick-dots li").css("width", liWidth + "px");
        } else if ($("body").hasClass("viewport-xs")) {
            $modal.find("ul.slick-dots li").css("width", liWidth + "px");
        }
    };//createLineProgressBarSuiteGallery

    var wdest = $(window).width();

    $(window).resize(sscThrottled(function () {
        if ($(window).width() == wdest) return;
        wdest = $(window).width();
        var $modal = $(".modal.lightbox");
        /*if ($("body").hasClass("viewport-sm")) {
            $modal.css("padding-left", "0px");
        }*/
        createLineProgressBarPortGallery($modal);
    }));
});