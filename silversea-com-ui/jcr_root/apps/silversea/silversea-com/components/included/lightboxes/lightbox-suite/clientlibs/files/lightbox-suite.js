$(function () {
    "use strict";

    function showImageDeckPlan(myThat, e) {
        e && e.preventDefault();
        e && e.stopPropagation();
        var $element = $(myThat),
            srcImage = $element.data("image-src"),
            $img = $("#image-deck-plan");
        $img.attr("src", srcImage);

        $(".lightbox-suite .lg-suite-deck-number").each(function () {
            $(this).removeClass("lg-suite-active-deck");
        });

        $element.parent().addClass("lg-suite-active-deck");
    };//showImageDeckPlan

    function showSuiteFeatures(myThat, e) {
        e && e.preventDefault();
        e && e.stopPropagation();
        var $element = $(myThat);
        var $featuresList = $(".lightbox-suite .lg-suite-features .lg-suite-features-list");
        var $materialIcons = $element.find(".material-icons");
        if ($materialIcons.html() == "add") {
            $materialIcons.html("close");
            $featuresList.slideDown();
        } else {
            $materialIcons.html("add");
            $featuresList.slideUp();
        }
    };//showSuiteFeatures

    function showDescription(myThat, e) {
        e && e.preventDefault();
        e && e.stopPropagation();
        var $element = $(myThat);
        var $description = $(".lightbox-suite .lg-suite-overview .lg-suite-description");
        $description.removeClass("lg-suite-description-truncate");
        $element.hide();
    };//showDescription

    /***************************************************************************
     * Lightbox Suite detail
     **************************************************************************/
    $('.open-lightbox-suite').on('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var $link = $(this),
            ajaxContentPath = $link.attr('href'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);
        $modalContent.find(".modal-content").addClass("lightbox-suite-content");
        $modalContent.addClass("lightbox-no-scroll");

        // Activate Modal
        $modalContent.modal('show');
        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function (e) {
            e.preventDefault();
            e.stopPropagation();
            history.pushState(null, null, "#lb-detatils"); // push state that hash into the url
            //avoid ios issue
            if (window.scrollSupport != null && window.scrollSupport) {
                window.iNoBounce.enable();
            }

            var $modal = $(this);
            $modal.off('shown.bs.modal');
            // Append html response inside modal
            $modal.find('.modal-content').load(ajaxContentPath, function (e) {
                createSlider($modal, $link);

                showSuiteFeatures($(".lightbox-suite .lg-suite-features .lg-suite-features-expand"));

                $(".lightbox-suite .lg-suite-deck .lg-suite-deck-number span").on("click", function (e) {
                    showImageDeckPlan(this, e);
                });
                $(".lightbox-suite .lg-suite-features .lg-suite-features-expand").on("click", function (e) {
                    showSuiteFeatures(this, e);
                });
                $(".lightbox-suite .lg-suite-overview .lg-suite-description-expand").on("click", function (e) {
                    showDescription(this, e);
                });

                var $deckActive = $modal.find(".lightbox-suite .lg-suite-deck-number.lg-suite-active-deck span");
                if ($deckActive != null && $deckActive.length > 0) {
                    showImageDeckPlan($deckActive);
                }
                if ($("body").hasClass("viewport-sm")) {
                    $(".modal.lightbox").css("padding-left", "0px");
                }
            });
        });
    });

    function createSlider($modal, $link) {
        var $mainSlider = $modal.find('.lightbox-suite .lg-asset-slider').slick({
            slidesToShow: 1,
            slidesToScroll: 1,
            dots: true,
            responsive: [
                {
                    breakpoint: 480,
                    settings: {
                        arrows: false
                    }
                }
            ]
        });

        // Init video on click
        $('.lightbox-suite .lg-asset-gallery .video-link').on('click', function (e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).next('.c-video').initVideo();
        });

        $modal.find(".lightbox-suite .lg-virtual-tour").on('click', function (event) {
            createVirtualTour(this, event);
        })


        $mainSlider.on('beforeChange', function (event, slick, currentSlide, nextSlide) {
            var $slider = $(this);
            // Kill video if current slide contains video
            var $video = $slider.find('.slick-current .c-video');
            $video.find('.s7playpausebutton[selected="false"]').trigger('click');
            $video.attr('class', 'c-video').empty();
            destroyVirtualTour();
        }).on('afterChange', function (event, slick, currentSlide) {
            // Call lazy load for 2 previous and 2 next slides
            loadLazyImage($(this));
        });

        loadLazyImage($mainSlider);
        createLineProgressBarSuiteGallery($modal);

    };//createLigthboxGallerySlider

    function loadLazyImage($slider) {
        var $sliderActive = $slider.closest('.lightbox-suite').find('.slick-active');
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
                $(".lightbox-suite .lg-asset-slider").slick("slickSetOption", "draggable", false, false);
                $(".lightbox-suite .lg-asset-slider").slick("slickSetOption", "swipe", false, false);
            }, 500);
        }
    };//createVirtualTour

    function createLineProgressBarSuiteGallery($modal) {
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

    $(window).resize(function () {
        if ($(window).width() == wdest) return;
        wdest = $(window).width();
        var $modal = $(".modal.lightbox");
        /*if ($("body").hasClass("viewport-sm")) {
            $modal.css("padding-left", "0px");
        }*/
        createLineProgressBarSuiteGallery($modal);
    });
});