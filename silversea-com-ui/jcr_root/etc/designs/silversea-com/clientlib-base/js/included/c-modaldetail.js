$(function () {
	
	window.linkEventToModalDetailMobile = function linkEventToModalDetailMobile() {
		 var modalBody = $(".modal-body");
	      //only mobile
        if (modalBody.hasClass("automatic-modal-body-modal-detail-mobile")) {
			$(".automatic-modal-body-modal-detail-mobile").parent().parent().parent().css("display", "block"); //remove when modal is close
			$(".automatic-modal-body-modal-detail-mobile").parent().parent().parent().css("overflow-y", "hidden"); //remove when modal is close
			$(".automatic-modal-body-modal-detail-mobile").parent().parent().parent().css("top", "0%"); //remove when modal is close
			$(".automatic-modal-body-modal-detail-mobile").css("overflow-y", "scroll");
			$(".automatic-modal-body-modal-detail-mobile").css("max-height", "100vh");
			$(".automatic-modal-body-modal-detail-mobile").css("min-height", "100vh");
			$(".automatic-modal-body-modal-detail-mobile").parent().css("top", "0%");
			$(".automatic-modal-body-modal-detail-mobile").parent().parent().css("padding-top", "0px");
			
        	if(window.scrollSupport != null && window.scrollSupport) { 
  				window.iNoBounce.enable();
  			}
  			
            //action for view all view less features
        	modalBody.find(".modal-detail-features-expand a.view_all").on("click", function (e) {
                e.preventDefault();
                $('.expand-ul li:nth-of-type(1n+10)').stop().css('display', 'list-item').hide().slideDown();
                $(".modal-detail-features-expand a.view_less").show();
                $(this).hide();
            });

        	modalBody.find(".modal-detail-features-expand a.view_less").on("click", function (e) {
                e.preventDefault();
                $(".modal-detail-features-expand a.view_all").show();
                $('.expand-ul li:nth-of-type(1n+10)').stop().slideUp();
                $(this).hide();
            });
        	
        	if (window.hasOwnProperty('virtualTour') && window.virtualTour != null) {
            	window.virtualTour.destroy();
            	window.virtualTour = null;
            }
        }
	};//linkEventToModalDetailMobile
	
	window.linkEventToModalDetailDesktop = function() {
		var modalBody = $(".modal-body");
	    //only desktop
        if (modalBody.hasClass("automatic-modal-body-modal-detail-desktop")) {
        	modalBody.parent().parent().parent().css("top", "8%");

            //create virtual tour on desktop
        	modalBody.find(".c-modal-detail-virtual-tour-lightbox").on("click", function (e) {
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
                                    default_fov: 100,
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
        	
        	modalBody.find("#features-tab.c-navtab__link--button a").on("click", function (e) {
        		 $(".modal-body .tab-content").addClass("tab-content-grey");
        	});
    	  
        	modalBody.find(".c-navtab__link--button:not(#features-tab) a").on("click", function (e) {
        		$(".modal-body .tab-content").removeClass("tab-content-grey");
        	});
        }
	};//linkEventToModalDetailDesktop
	
    $(window).on('resize', function () {
        var $modal = $(".modal");
        var $modalBody = $modal.find(".modal-body");
        if ($modalBody.hasClass("automatic-modal-body-modal-detail")) {
        	var changeTemplate = false;
        	if ($modalBody.hasClass("automatic-modal-body-modal-detail-desktop")) {
        		if ($(window).width() <= 768) {
        			changeTemplate= true;
        		}
        	} else if ($modalBody.hasClass("automatic-modal-body-modal-detail-mobile")) {
        		if ($(window).width() > 768) {
        			changeTemplate= true;
        		}
        	}
        	if (changeTemplate) {
        		var href = $(".c-modaldetail-link").data("href");
        		var target = $(".c-modaldetail-link").data("target");
        		$(target + " .modal-content").empty();
        		$.get(href, function (data) {
        			if ( window.$slickSlider != null) {
        				 window.$slickSlider.slick("unslick");
        			}
        			$(target + " .modal-content").html(data);
        			createSlider($(target + " .modal-body"));
        		});
        	}
        }
    });
 
    $('.modal-detail-link-ajax').on('click', function(e) {
        e.preventDefault();
        $('html').addClass("no-scroll-html");
        $('body').addClass("no-scroll-body");
        var $link = $(this),
            ajaxContentPath = $link.attr('href'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);

        // Activate Modal
        $modalContent.modal('show');

        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function(e) {
            var $modal = $(this);
            $modal.off('shown.bs.modal');

            // Append html response inside modal
            $modal.find('.modal-content').load(ajaxContentPath, function() {
                var modalBody = $modal.find(".modal-body");

                 //window.location.hash = "#modal";
                 history.pushState(null, null, "#modal"); // push state that hash into the url
                 createSlider(modalBody);
            });
        });
    });
    
    
    function createSlider(modalBody){
    	//create slider
        var $slideFor = modalBody.find('.c-slider').slick({
            slidesToShow: 1,
            slidesToScroll: 1
        });
        
        window.$slickSlider = $slideFor;
        
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
        modalBody.find("#descr-tab").on("click", function (e) {
            if ($slideFor != null) {
                $slideFor.slick('slickGoTo', 0);
            }
        });
        
        
        modalBody.find(".show-deck-image").on("click", function(e) {
        	e.preventDefault();
        	var id = $(this).attr("id");
        	$("#modal-detail-location a:not(#"+id+")").removeClass("activeDeck");
        	$("#modal-detail-location img:not(#deck-"+id+")").addClass("hidden");
        	
        	$(this).addClass("activeDeck");
        	$("#deck-"+id).removeClass("hidden");
        });
        
    };//createSlider

});
