$(function () {

    /***************************************************************************
     * Modal : Global modal code applied for every modal
     **************************************************************************/
    $(document).on('show.bs.modal', function (e) {
        $(".lightbox-prev-link, .lightbox-next-link").hide();
        var $body = $('body');
        var $html = $('html');
        var $modalLightbox = $("#modalLightbox");
        var $modalBodyLightbox = $modalLightbox.find(".modal-body");

        if (!$body.hasClass("no-scroll-body")) {
            $body.addClass('no-scroll-body');
        }
        if (!$html.hasClass("no-scroll-html")) {
            $html.addClass('no-scroll-html');
        }

        $modalLightbox.find(".modal-dialog").find(".lightbox-close .lightbox-close-div").off("click");
        $modalLightbox.find(".modal-dialog").find(".lightbox-close .lightbox-close-div").on("click", function (e) {
            e.preventDefault();
            $('#modalLightbox').modal('hide');
            //history.back();
        });



    });

    /***************************************************************************
     * Modal : Global Clean modal content on close event
     **************************************************************************/
    $(document).on('hide.bs.modal', function (e) {
        if (window.location.hash.indexOf("#lb-") != -1) {
            history.back();
            //  window.location.hash =  "";
            //    window.backNavigation = false;
        }

        $(e.target).removeData('bs.modal');
        var $body = $('body');
        var $html = $('html');
        var $modal = $(".modal");
        var $modalLightbox = $("#modalLightbox");
        var $modalBody = $modal.find(".modal-body");
        var $modalDialog = $modal.find(".modal-dialog");
        var $modalContent = $modal.find(".modal-content");
        var $modalDialogLightbox = $modalLightbox.find(".modal-dialog");


        if ($body.hasClass("modal-open")) {
            $body.removeClass('modal-open');
        }
        if ($body.hasClass("no-scroll-body")) {
            $body.removeClass('no-scroll-body');
        }
        if ($html.hasClass("no-scroll-html")) {
            $html.removeClass('no-scroll-html');
        }
        if ($modal.hasClass("lightbox")) {
            if ($modalLightbox.hasClass("lightbox-no-scroll")) {
                $modalLightbox.removeClass("lightbox-no-scroll");
            }

            if ($modalContent.hasClass("lightbox-suite-content")) {
                $modalContent.removeClass("lightbox-suite-content");
            }

            if ($modalDialogLightbox.hasClass("custom-lightbox-width")) {
                $modalDialogLightbox.removeClass("custom-lightbox-width");
                var clazzList = $modalDialogLightbox.attr("class").split(/\s+/);
                for (i in clazzList) {
                    if (clazzList[i].lastIndexOf("lightbox-width-")==0) {
                        $modalDialogLightbox.removeClass(clazzList[i]);
                    }
                }
            }
        }

        if (window.iNoBounce != null) {
            try {
                window.iNoBounce.disable();
            } catch (error) {
            }
        }

        if ($('header').nextAll().hasClass("c-cruise") && $(".modal-content").hasClass("modal-content--transparent-suite")) {
            if (!window.backNavigation) {
                history.back();
            }
            setTimeout(function () {
                window.backNavigation = false;
            }, 200);
            if (window.suiteDesktop == false) {
                $(".modal-content--transparent-suite").parent().parent().css("overflow-y", "auto"); //remove when modal is close
            }
        }


        if ($modalBody.hasClass("automatic-modal-body-modal-detail")) {
            if (window.$slickSlider != null) {
                window.$slickSlider.slick("unslick");
            }
            if (!window.backNavigation) {
                history.back();
            }
            setTimeout(function () {
                window.backNavigation = false;
            }, 200);
            if ($modalBody.hasClass("automatic-modal-body-modal-detail-mobile")) {
                $(".automatic-modal-body-modal-detail-mobile").parent().parent().parent().css("overflow-y", "auto");
                $(".automatic-modal-body-modal-detail-mobile").parent().parent().parent().css("top", "9%");
            }
        }

        var $modalContent = $('body > .modal .modal-content');
        $modalContent.empty();

        // Force to default class
        $modalContent.attr('class', 'modal-content');


        if (window.hasOwnProperty('virtualTour') && window.virtualTour != null) {
            try {
                window.virtualTour.destroy();
            } catch (error) {
            } finally {
                window.virtualTour = null;
            }
        }



    });

    /***************************************************************************
     * Build modal for image
     **************************************************************************/
    $(document).on('click', '.automatic-modal', function (e) {
        e.preventDefault();

        $('body').addClass('modal-open');

        // HTML layout
        var $modalContent = $('<div class="modal-content modal-content--transparent modal-content--single">'
            + '<div class="modal-header"><button class="close c-btn--close" type="button" data-dismiss="modal" aria-label="Close"></button></div>'
            + '<div class="modal-body automatic-modal-body"><img class="o-img" /></div>' + '</div>');

        // Activate Modal
        $($(this).data('target')).modal('show');

        // Append image inside Modal
        var imagePath = $(this).attr('href');
        $('.modal').on('shown.bs.modal', function (e) {
            $(this).find('.modal-dialog').empty().append($modalContent);
            $(this).find('img').attr('src', imagePath);
        });
    });


    /***************************************************************************
     * Modal virtual tour
     **************************************************************************/
    $('.modal-virtual-tour').on('click', function (e) {
        e.preventDefault();

        $('body').addClass('modal-open');

        // HTML layout
        var $modalContent = $('<div class="modal-content modal-content--transparent modal-content--single modal-content__virtual-tour">'
            + '<div class="modal-header modal-header__virtual-tour"><button class="close c-btn--close" type="button" data-dismiss="modal" aria-label="Close"></button></div>'
            + '<div class="modal-body"><div class="modal__virtual-tour-container" id="virtual-tour-container"></div></div>' + '</div>');

        // Activate Modal
        $($(this).data('target')).modal('show');

        // Append image inside Modal
        var imagePath = $(this).attr('href');
        var captionTitle = $(this).attr('caption-title');

        $('.modal-dialog').empty().append($modalContent);
        $('.modal').off('shown.bs.modal');

        $('.modal').on('shown.bs.modal', function (e) {
            $('.modal').off('shown.bs.modal');
            window.virtualTour = PhotoSphereViewer({
                container: 'virtual-tour-container',
                panorama: imagePath,
                anim_speed: '0.4rpm',
                move_speed: 1.0,
                time_anim: '1000',
                min_fov: 10,
                default_fov: 179,
                usexmpdata: false,
                caption: captionTitle,
                navbar: [
                    'autorotate',
                    'zoom',
                    'spacer-1',
                    'caption',
                    'gyroscope',
                    'fullscreen'
                ]
            });
        });

    });

    /***************************************************************************
     * Modal Gallery for "cruise page" and "inline gallery component"
     **************************************************************************/
    $(document).on('click', '.automatic-gallery-modal', function (e) {
        e.preventDefault();
        var $link = $(this),
            ajaxContentPath = $link.closest('[data-gallery-path]').data('gallery-path'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);

        // Activate Modal
        $modalContent.modal('show');

        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function (e) {
            var $modal = $(this);
            $modal.off('shown.bs.modal');

            // Append html response inside modal
            $modal.find('.modal-dialog').load(ajaxContentPath, function () {
                // Activate gallery
                var $slideFor = $modal.find('.c-slider--for').slick({
                    slidesToShow: 1,
                    slidesToScroll: 1,
                    asNavFor: '.modal .c-slider--nav'
                });

                var $slideNav = $modal.find('.c-slider--nav').slick({
                    slidesToShow: 6,
                    slidesToScroll: 5,
                    asNavFor: '.modal .c-slider--for',
                    focusOnSelect: true
                });

                $modal.find(".cruise-gallery-virtual-tour").on('click', function(event) {
                	createVirtualTour(this, event);
                });

                // Init video on click
                $('.video-link').on('click', function(e) {
                    e.preventDefault();
                    $(this).next('.c-video').initVideo();
                })

                // Code only for gallery with category
                if($('.c-gallery__tab').length) {

                    // Tab gallery
                    var $sliderTab = $('.c-gallery__tab__link');
                    $modal.find('.c-gallery__tab__link').on('click', function(e) {
                        e.preventDefault();
                        var $linkCategory = $(this), targetSlideIndex = $slideFor.find('.slick-slide:not(".slick-cloned")[data-category-target="' + $linkCategory.data('category') + '"]').index() - 1;

                        // Slide to the first image of the current category
                        $slideFor.slick('slickGoTo', targetSlideIndex);
                        $(".c-slider--for").slick("slickSetOption", "draggable", true, false);
                        $(".c-slider--for").slick("slickSetOption", "swipe", true, false);
                    });

                    // Update category tab according to the current slide
                    $slideFor.on('beforeChange', function(event, slick, currentSlide, nextSlide) {
                        var currentCategory = $(this).find('.slick-slide:not(".slick-cloned")').eq(nextSlide).data('category-target');
                        $('.c-gallery__tab__link:visible').removeClass('active');
                        $('.c-gallery__tab__link:visible[data-category="' + currentCategory + '"]').addClass('active');

                        //Related to virtual tour, see c-cruise.js
                        if (window.hasOwnProperty('virtualTour') && window.virtualTour != null) {
                         	window.virtualTour.destroy();
                         	window.virtualTour = null;
                        }
                    	
                    	if (window.hasOwnProperty('virtualTourID') && window.virtualTourID != null) {
                         	$(window.virtualTourID).empty();
                         	$(window.virtualTourID).css("height","0px");
                         	window.virtualTourID = null;
                        }
                    	
                    	if (window.hasOwnProperty('virtualTourImage') && window.virtualTourImage != null) {
                          	$(window.virtualTourImage).css("display","block");
                          	 window.virtualTourImage = null;
                        }
                    });
                }

                // Show / calc counter
                var slideTotalItem = $slideFor.find('.slick-slide:not(.slick-cloned)').length;
                // Set total number of slide
                $slideFor.closest('.c-gallery__wrappertop').find('.c-gallery__counter .slide-item-total').html(slideTotalItem);
                $slideFor.on('beforeChange', function(event, slick, currentSlide, nextSlide) {
                    var $slider = $(this);
                    // Set counter according to the current slide
                    $slider.closest('.c-gallery__wrappertop').find('.c-gallery__counter .slide-item-current').html(nextSlide + 1);

                    // Kill video if current slide contains video
                    var $video = $slider.find('.slick-current .c-video');
                    $video.find('.s7playpausebutton[selected="false"]').trigger('click');
                    $video.attr('class', 'c-video').empty();

                    // Call lazy load for 2 previous and 2 next slides
                    loadLazyImage($(this));
                }).on('afterChange', function(event, slick, currentSlide) {
                    // Call lazy load for 2 previous and 2 next slides
                    loadLazyImage($(this));
                });

                // Scroll to the target image
                var currentImagePath = $link.attr('href');
                $(".c-slider--for").slick("slickSetOption", "draggable", true, false);
                $(".c-slider--for").slick("slickSetOption", "swipe", true, false);
                $slideFor.slick('slickGoTo', $slideFor.find('.slick-slide:not(".slick-cloned")[data-image="' + currentImagePath + '"]').first().data('slick-index'), false);
            });


        });
    });

/*Changes for modal full-screen*/
$('.automatic-gallery-modal-fullscreen').on('click', function(e) {

        e.preventDefault();
        var $link = $(this),
            ajaxContentPath = $link.closest('[data-gallery-path]').data('gallery-path'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);

        // Activate Modal
        $modalContent.modal('show');

        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function(e) {
            var $modal = $(this);
            $modal.addClass('modal-dialog-fullscreen');
            $modal.off('shown.bs.modal');

            // Append html response inside modal
            $modal.find('.modal-dialog').load(ajaxContentPath, function() {
                // Activate gallery
                var $slideFor = $modal.find('.c-slider--for').slick({
                    slidesToShow : 1,
                    slidesToScroll : 1
                    //variableWidth: true
                    //asNavFor : '.modal .c-slider--nav'
                    //dots:true
                });

               /* var $slideNav = $modal.find('.c-slider--nav').slick({
                    slidesToShow : 6,
                    slidesToScroll : 5,
                    asNavFor : '.modal .c-slider--for',
                    focusOnSelect : true
                });*/

                $modal.find(".cruise-gallery-virtual-tour").on('click', function(event) {
                	createVirtualTour(this, event);
                });

                $('#steveandsilversea-modal-dialog-close').on('click', function(e) {
                    if (document.exitFullscreen) {
                        document.exitFullscreen()
                    } else if (document.webkitCancelFullScreen) {
                        document.webkitCancelFullScreen()
                    } else if (document.mozCancelFullScreen) {
                        document.mozCancelFullScreen()
                    } else if (document.msExitFullscreen) {
                        document.msExitFullscreen()
                    }

                });


                // Init video on click
                $('.video-link').on('click', function (e) {
                    e.preventDefault();
                    $(this).next('.c-video').initVideo();
                })

                // Code only for gallery with category
                if ($('.c-gallery__tab').length) {

                    // Tab gallery
                    var $sliderTab = $('.c-gallery__tab__link');
                    $modal.find('.c-gallery__tab__link').on('click', function (e) {
                        e.preventDefault();
                        var $linkCategory = $(this),
                            targetSlideIndex = $slideFor.find('.slick-slide:not(".slick-cloned")[data-category-target="' + $linkCategory.data('category') + '"]').index() - 1;

                        // Slide to the first image of the current category
                        $slideFor.slick('slickGoTo', targetSlideIndex);
                        $(".c-slider--for").slick("slickSetOption", "draggable", true, false);
                        $(".c-slider--for").slick("slickSetOption", "swipe", true, false);
                    });

                    // Update category tab according to the current slide
                    $slideFor.on('beforeChange', function (event, slick, currentSlide, nextSlide) {
                        var currentCategory = $(this).find('.slick-slide:not(".slick-cloned")').eq(nextSlide).data('category-target');
                        $('.c-gallery__tab__link:visible').removeClass('active');
                        $('.c-gallery__tab__link:visible[data-category="' + currentCategory + '"]').addClass('active');

                        //Related to virtual tour, see c-cruise.js
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
                    });
                }

                // Show / calc counter
                var slideTotalItem = $slideFor.find('.slick-slide:not(.slick-cloned)').length;
                // Set total number of slide
                $slideFor.closest('.c-gallery__wrappertop').find('.c-gallery__counter .slide-item-total').html(slideTotalItem);
                $slideFor.on('beforeChange', function (event, slick, currentSlide, nextSlide) {
                    var $slider = $(this);
                    // Set counter according to the current slide
                    $slider.closest('.c-gallery__wrappertop').find('.c-gallery__counter .slide-item-current').html(nextSlide + 1);

                    // Kill video if current slide contains video
                    var $video = $slider.find('.slick-current .c-video');
                    $video.find('.s7playpausebutton[selected="false"]').trigger('click');
                    $video.attr('class', 'c-video').empty();

                    // Call lazy load for 2 previous and 2 next slides
                    loadLazyImage($(this));
                }).on('afterChange', function (event, slick, currentSlide) {
                    // Call lazy load for 2 previous and 2 next slides
                    loadLazyImage($(this));
                });

                // Scroll to the target image
                var currentImagePath = $link.attr('href');
                $(".c-slider--for").slick("slickSetOption", "draggable", true, false);
                $(".c-slider--for").slick("slickSetOption", "swipe", true, false);
                $slideFor.slick('slickGoTo', $slideFor.find('.slick-slide:not(".slick-cloned")[data-image="' + currentImagePath + '"]').first().data('slick-index'), false);
            });


        });
      var modeldialog = document.getElementsByClassName("modal-dialog")[0];
        if(modeldialog.requestFullscreen){
            modeldialog.requestFullscreen(Element.ALLOW_KEYBOARD_INPUT);
        } else if(modeldialog.mozRequestFullScreen){
            modeldialog.mozRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
        } else if(modeldialog.webkitRequestFullScreen){
            modeldialog.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
        } else if(modeldialog.msRequestFullscreen){
           modeldialog.msRequestFullscreen(Element.ALLOW_KEYBOARD_INPUT);
        }

    });

    function loadLazyImage($slider) {
        var $sliderActive = $slider.closest('.c-gallery').find('.slick-active');
        $(".c-slider--for").slick("slickSetOption", "draggable", true, false);
        $(".c-slider--for").slick("slickSetOption", "swipe", true, false);
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
    }

    function createVirtualTour(myThis, event) {
        event.preventDefault();
        var imagePath = $(myThis).attr('data-image-virtual-tour');
        var idContainerImage = $(myThis).attr('id');
        var idContainerVirtualTour = $(myThis).attr('id') + "-container";

        if (window.hasOwnProperty('virtualTour') && window.virtualTour != null) {
            window.virtualTour.destroy();
            window.virtualTour = null;
        }

        if (window.hasOwnProperty('virtualTourID') && window.virtualTourID != null) {
            $(virtualTourID).empty();
            window.virtualTourID = null;
        }

        if (window.hasOwnProperty('virtualTourImage') && window.virtualTourImage != null) {
            window.virtualTourImage = null;
        }

        if (window.hasOwnProperty('virtualTourType') && window.virtualTourType != null) {
            window.virtualTourType = null;
        }

        if (imagePath != null && idContainerVirtualTour != null) {
            $("#" + idContainerVirtualTour).css("height", "550px");
            $("#" + idContainerImage).css("display", "none");
            var intervalDiv = setInterval(function () {
                if ($("#" + idContainerVirtualTour).height() > 0) {
                    clearInterval(intervalDiv);
                    window.virtualTourID = "#" + idContainerVirtualTour;
                    window.virtualTourImage = "#" + idContainerImage;
                    window.virtualTourType = "cruise-gallery-virtual-tour";
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
                $(".c-slider--for").slick("slickSetOption", "draggable", false, false);
                $(".c-slider--for").slick("slickSetOption", "swipe", false, false);
            }, 500);
        }
    }
});