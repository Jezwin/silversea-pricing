$(function () {
    "use strict";

    /***************************************************************************
     * Lightbox Land Shorex Hotel detail
     **************************************************************************/
    $('.open-lightbox-land-shorex-hotel').on('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        $(".lightbox-prev-link, .lightbox-next-link").hide();
        var $link = $(this),
            ajaxContentPath = $link.attr('href'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);
        $modalContent.find(".modal-content").addClass("lightbox-land-shorex-hotel-content");
        $modalContent.addClass("lightbox-no-scroll");

        // Activate Modal
        $modalContent.modal('show');
        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function (e) {
            e.preventDefault();
            e.stopPropagation();
            history.pushState(null, null, "#" + $link.attr("id")); // push state that hash into the url
            //avoid ios issue
            if (window.scrollSupport != null && window.scrollSupport) {
                window.iNoBounce.enable();
            }
            var $modal = $(this);
            $modal.off('shown.bs.modal');
            var itineraryId = $link.data('itinerary-id');
            var excursionId = $link.data('excursion-id');
            setModalContent($modal, itineraryId, excursionId, ajaxContentPath, false, '')
            // Append html response inside modal
        });
        $modalContent.on('hide.bs.modal', function (e) {
            history.replaceState(null, null, location.href.replace(location.hash, ""));
        });
    });

    $('.lightbox-prev-link, .lightbox-next-link').on('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        var $this = $(this);
        var itineraryId = $this.data('itinerary-id');
        var excursionId = $this.data('excursion-id');
        var $modal = $('#' + $this.data('target'));
        setModalContent($modal, itineraryId, excursionId, $this.attr('href'), true, $this.hasClass('lightbox-prev-link') ? 'right' : 'left');
    });

    function setModalContent($modal, itineraryId, excursionId, ajaxContentPath, animation, direction) {

        if (itineraryId && excursionId) {
            if ($(window).width() > 1024 && setModalNavigation($modal, ajaxContentPath, itineraryId, excursionId)) {
                $(".lightbox-prev-link, .lightbox-next-link").show();
            }
        }
        var $modalContent = $modal.find('.modal-content');
        var loadContent = function (callback) {
            $modalContent.load(ajaxContentPath + '.' + itineraryId + '.' + excursionId + ".html", function (e) {
                setTopLightboxModal(432);
                createSlider($modal);
                //avoid ios issue
                if (window.scrollSupport != null && window.scrollSupport) {
                    window.iNoBounce.enable();
                }
                if ($("body").hasClass("viewport-sm")) {
                    $(".modal.lightbox").css("padding-left", "0px");
                }
                if ($(".lsh-title") != null && $(".lsh-title").html() != null) {
                    $(".lsh-title").html($(".lsh-title").html().toLowerCase());
                }
                $(".lazy:visible").lazy();
                callback && callback();
            });
        };
        if (animation) {
            var opposite = direction === 'left' ? 'right' : 'left';
            (function c(direction, opposite) {
                var obj = {};
                obj[direction] = '-100vw';
                $('.lightbox-close').hide();
                $modalContent.animate(obj, 400, function () {//this move it out of view from one side
                    loadContent(function () {
                        $modalContent.animate({left: '', right: ''}, 500, function () {//this bring back to view from the other side
                            $modalContent.css('left', '');//this is just to rest left && right because previous line put them to 0
                            $modalContent.css('right', '');
                            $('.lightbox-close').fadeIn();
                        });
                    });
                    $modalContent.css(opposite, '-100vw');//this move it on the other side of the screen
                    $modalContent.css(direction, '');//this remove the first set
                });
            })(direction, opposite);
        } else {
            loadContent();
        }
    }


    function setModalNavigation($modal, uri, itineraryId, excursionId) {
        try {
            var excursion = window['it' + itineraryId][excursionId];
            if (excursion.prevId == excursionId) {
                return false;
            }
            var subUri = uri.substring(0, uri.lastIndexOf('.') + 1);
            var prev = $modal.find('.lightbox-prev-label');
            var next = $modal.find('.lightbox-next-label');
            var nextLink = $modal.find('.lightbox-next-link');
            var prevLink = $modal.find('.lightbox-prev-link');
            prev.html(excursion.prevLabel);
            prev.attr('title', excursion.prevLabel);
            next.html(excursion.nextLabel);
            next.attr('title', excursion.nextLabel);

            prevLink.data('target', $modal.attr('id'));
            prevLink.data('itinerary-id', itineraryId);
            prevLink.data('excursion-id', excursion.prevId);

            var prevUri = subUri + excursion.prevKind;
            prevLink.attr('href', prevUri);

            nextLink.data('target', $modal.attr('id'));
            nextLink.data('itinerary-id', itineraryId);
            nextLink.data('excursion-id', excursion.nextId);

            var nextUri = subUri + excursion.nextKind;
            nextLink.attr('href', nextUri);
        } catch (e) {

        }
        return true;
    }

    function createSlider($modal) {
        var $mainSlider = $modal.find('.lightbox-land-shorex-hotel .lsh-asset-slider').slick({
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
        $('.lightbox-land-shorex-hotel .lsh-asset-gallery .video-link').on('click', function (e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).next('.c-video').initVideo();
        });

        $modal.find(".lightbox-land-shorex-hotel .lsh-virtual-tour").on('click', function (event) {
            createVirtualTour(this, event);
        });


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
        createLineProgressBarGallery($modal);

    };//createLigthboxGallerySlider

    function loadLazyImage($slider) {
        var $sliderActive = $slider.closest('.lightbox-land-shorex-hotel').find('.slick-active');
        $(".lightbox-land-shorex-hotel .lsh-asset-slider").slick("slickSetOption", "draggable", true, false);
        $(".lightbox-land-shorex-hotel .lsh-asset-slider").slick("slickSetOption", "swipe", true, false);
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
                $(".lightbox-land-shorex-hotel .lsh-asset-slider").slick("slickSetOption", "draggable", false, false);
                $(".lightbox-land-shorex-hotel .lsh-asset-slider").slick("slickSetOption", "swipe", false, false);
            }, 500);
        }
    };//createVirtualTour

    function createLineProgressBarGallery($modal) {
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
    };//createLineProgressBarGallery

    var wdest = $(window).width();

    $(window).resize(function () {
        if ($(window).width() == wdest) return;
        wdest = $(window).width();
        var $modal = $(".modal.lightbox");
        createLineProgressBarGallery($modal);
    });
});