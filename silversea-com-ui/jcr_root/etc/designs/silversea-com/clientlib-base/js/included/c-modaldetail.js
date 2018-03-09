$(function () {
    $(window).on('resize', function () {
        var $modal = $(".modal");
        var $modalBody = $modal.find(".modal-body");
        if ($modalBody.hasClass("automatic-modal-body-modal-detail")) {

            var href = $(".c-modaldetail-link").data("href");
            var target = $(".c-modaldetail-link").data("target");
            $.get(href, function (data) {
                $(target + " .modal-content").html(data);
            });
        }
    });

    $('.modal').on('shown.bs.modal', function (e) {
        var $modal = $(this);
        var modalBody = $modal.find(".modal-body");

        if (modalBody.hasClass("automatic-modal-body-modal-detail")) {
            $('html').addClass("no-scroll-html");
            $('body').addClass("no-scroll-body");
            
            $modal.off('shown.bs.modal'); //fix bug with modal gallery

            //create slider
            var $slideFor = $(this).find('.c-slider').slick({
                slidesToShow: 1,
                slidesToScroll: 1
            });
            var slideTotalItem = $slideFor.find('.slick-slide:not(.slick-cloned)').length;

            // Show / calc counter
            $('.modal').find('.c-modal-detail-modal-description__counter .slide-item-current').html(1);
            // Set total number of slide
            $('.modal').find('.c-modal-detail-modal-description__counter .slide-item-total').html(slideTotalItem);
            if (slideTotalItem == 1) {
                $(".c-modal-detail-modal-description__counter").css("display", "none");
            }
            $slideFor.on('beforeChange', function (event, slick, currentSlide, nextSlide) {
                var $slider = $(this);
                // Set counter according to the current slide
                $('.modal').find('.c-modal-detail-modal-description__counter .slide-item-current').html(nextSlide + 1);
            });

            //workaround for bug virtual tour full screen
            $(this).find("#descr-tab").on("click", function (e) {
                if ($slideFor != null) {
                    $slideFor.slick('slickGoTo', 0);
                }
            });

            //only mobile
            if (modalBody.hasClass("automatic-modal-body-modal-detail-mobile")) {
				$(".automatic-modal-body-modal-detail-mobile").parent().parent().parent().css("display", "block"); //remove when modal is close
				$(".automatic-modal-body-modal-detail-mobile").parent().parent().parent().css("overflow-y", "hidden"); //remove when modal is close
				$(".automatic-modal-body-modal-detail-mobile").css("overflow-y", "scroll");
				$(".automatic-modal-body-modal-detail-mobile").css("max-height", "100vh");
				
            	if(window.scrollSupport != null && window.scrollSupport) { 
      				window.iNoBounce.enable();
      			}
      			
                //action for view all view less features
                $(this).find(".modal-detail-features-expand a.view_all").on("click", function (e) {
                    e.preventDefault();
                    $('.expand-ul li:nth-of-type(1n+10)').stop().css('display', 'list-item').hide().slideDown();
                    $(".modal-detail-features-expand a.view_less").show();
                    $(this).hide();
                });

                $(this).find(".modal-detail-features-expand a.view_less").on("click", function (e) {
                    e.preventDefault();
                    $(".modal-detail-features-expand a.view_all").show();
                    $('.expand-ul li:nth-of-type(1n+10)').stop().slideUp();
                    $(this).hide();
                });
                $(this).find(".close").on("click", function (e) {
                    e.preventDefault();
                    $('.modal').modal('hide');
                });
            }

            //only desktop
            if (modalBody.hasClass("automatic-modal-body-modal-detail-desktop")) {
                //create virtual tour on desktop
                $(this).find(".c-modal-detail-virtual-tour-lightbox").on("click", function (e) {
                    e.preventDefault();
                    if (!window.hasOwnProperty('virtualTour') || window.virtualTour == null) {
                        var imagePath = $("#c-modal-detail-modal-virtual-tour-containerDiv").data().image;
                        var intervalDiv = setInterval(function () {
                            if ($("#virtualtour-tab").hasClass("active")) {
                                clearInterval(intervalDiv);
                                if (imagePath != null) {
                                    window.virtualTour = PhotoSphereViewer({
                                        container: 'c-modal-detail-modal-virtual-tour-containerDiv',
                                        panorama: imagePath,
                                        anim_speed: '0.4rpm',
                                        move_speed: 1.0,
                                        time_anim: '1000',
                                        min_fov: 10,
                                        usexmpdata: false,
                                        default_fov: 179,
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
                            }
                        }, 500);
                    }
                });
            }
        }
    });
});
