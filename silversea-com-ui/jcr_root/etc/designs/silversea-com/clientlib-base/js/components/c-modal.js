$(function() {
    /***************************************************************************
     * Modal : Clean modal content on close event
     **************************************************************************/
    $(document).on('hide.bs.modal', function(e) {
    	$(e.target).removeData('bs.modal');

        $('body').removeClass('modal-open');
        
        var $modalContent = $('body > .modal .modal-content');
        $modalContent.empty();

        // Force to default class
        $modalContent.attr('class', 'modal-content');

        if (window.hasOwnProperty('virtualTour') && window.virtualTour != null) {
        	window.virtualTour.destroy();
        	window.virtualTour = null;
        }
    });

    /***************************************************************************
     * Build modal for image
     **************************************************************************/
    $('.automatic-modal').on('click', function(e) {
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
        $('.modal').on('shown.bs.modal', function(e) {
            $(this).find('.modal-dialog').empty().append($modalContent);
            $(this).find('img').attr('src', imagePath);
        });
    });
    
    
    /***************************************************************************
     * Build modal for suite detail
     **************************************************************************/
    $('.automatic-modal-suite-detail').on('click', function(e) {
        e.preventDefault();

        if (window.hasOwnProperty("cruiseItem") && window.cruiseItem != null) {
        	var id = $(this).attr('id');
        	//label i18n
        	var navDescription = ($(this).parent().data('nav-description') != null) ? $(this).parent().data('nav-description')   : "";
        	var navPlan = ($(this).parent().data('nav-plan') != null) ? $(this).parent().data('nav-plan')   : "";
        	var navFeatures = ($(this).parent().data('nav-features') != null) ? $(this).parent().data('nav-features')   : "";
        	var navLocation = ($(this).parent().data('nav-location') != null) ? $(this).parent().data('nav-location')   : "";
        	var navVirtualTour = ($(this).parent().data('nav-virtual-tour') != null) ? $(this).parent().data('nav-virtual-tour')  : "";
        	
        	var note = ($(this).parent().data('note') != null) ? $(this).parent().data('note')   : "";
        	var requestQuote = ($(this).parent().data('request-quote') != null) ? $(this).parent().data('request-quote')   : "";
        	var earlyBonus = ($(this).parent().data('early-bonus') != null) ? $(this).parent().data('early-bonus')   : "";
        	var waitlist = ($(this).parent().data('waitlist') != null) ? $(this).parent().data('waitlist')   : "";
        	var deckLabel = ($(this).parent().data('deck') != null) ? $(this).parent().data('deck')   : "";
        	var from = "";
        	
        	//suite values
        	var title = (window.cruiseItem[id].title != null) ? window.cruiseItem[id].title : "";
        	var assetSelectionReference = (window.cruiseItem[id].assetSelectionReference != null) ? window.cruiseItem[id].assetSelectionReference : null;
        	var longDescription = (window.cruiseItem[id].longDescription != null) ? window.cruiseItem[id].longDescription : "";
        	var bedroomsInformation = (window.cruiseItem[id].bedroomsInformation != null) ? window.cruiseItem[id].bedroomsInformation : "";
        	var virtualTourImage = (window.cruiseItem[id].virtualTour != null) ? window.cruiseItem[id].virtualTour : null;
        	
        	var currency = ($(this).data('currency') != null) ? $(this).data('currency')   : "";
        	var raqLink = $("#"+id).find(".link-request-quote-card").attr("href");
        	var early = window.cruiseItem[id].early;
        	var priceEarlyBookingBonus = (window.cruiseItem[id].priceBookingBonus != null) ? currency + " " + window.cruiseItem[id].priceBookingBonus : "";
        	var suitePlan =  (window.cruiseItem[id].plan != null) ? window.cruiseItem[id].plan : null;
        	var features =  (window.cruiseItem[id].features != null) ? window.cruiseItem[id].features : "";
        	var locationImage =  (window.cruiseItem[id].locationImage != null) ? window.cruiseItem[id].locationImage : null;
        	var deck =(window.cruiseItem[id].deck != null) ? window.cruiseItem[id].deck : "";
        	
        	var price = waitlist;
        	
        	if (window.cruiseItem[id].price != null) {
        		price = currency + " " + window.cruiseItem[id].price;
        		from =  ($(this).parent().data('from') != null) ? $(this).parent().data('from')   : "";
        	}
        	
        	//description tab
			var assetSelectionToRender = "";
    		if (assetSelectionReference != null) {
				var assetSelectionReferenceList = assetSelectionReference.split("#next#");
				for(item in assetSelectionReferenceList) {
					assetSelectionToRender += '<div class="slider-item">';
					assetSelectionToRender += '<div class="ratio lazy" style="display:block;background-size: cover;background-position: center;background-repeat: no-repeat;background-image:url('+ assetSelectionReferenceList[item] +'?wid=930&fit=hfit,1)"></div>';
					assetSelectionToRender += '</div>'; 
				}
			}
    		
    		//features tab
    		var featuresToRender = "";
    		if (navFeatures != null) {
    			var featuresList = features.split("#next#");
    			for(item in featuresList) {
    				featuresToRender += "<li>"+featuresList[item]+"</li>";
    			}
    		}
    		
    		//location tab
			var locationImageToRender = "";
			var deckToRender = "";
    		if (locationImage != null) {
    			var deckList = deck.split("#next#");
    			for(item in deckList) {
    				var classItem = "btn btn-thin show-deck-image";
    				var idItem = "deck-"+item;
    				if (item == 0) {
    					classItem += " activeDeck";
    				}
    				deckToRender += '<a id="'+idItem+'" class="'+classItem+'" href="#"> <span> '+ deckLabel + ' ' +deckList[item] +'</span></a>';
    			}
    			
    			var locationImageList = locationImage.split("#next#");
    			var style="";
    			for(item in locationImageList) {
    				var idItem = "deck-"+item+"-image";
    				if (item != 0) {
    					style = "style='display:none'";
    				}
    				locationImageToRender += '<img id="'+idItem+'" src="'+ locationImageList[item] +'"?hei=930&wid=930&fit=constrain" alt="Suite plan" title="Suite plan" class="o-img"' + style + '/>';
    			}
    		} 
    		
    		//virtual tour tab
    		var virtualTourToRender = null;
    		if (virtualTourImage != null) {
    			virtualTourToRender = '<div id="c-suite-detail-modal-virtual-tour-containerDiv" class="c-suite-detail-modal-virtual-tour-container" data-image="'+virtualTourImage+'"></div>';
    		}
    		
        }
        var $modalContent = null;
        if ($('body').hasClass("viewport-md") || $('body').hasClass("viewport-lg")) { //check if not mobile
        	$modalContent = window.templateSuiteDetail;
        } else {
        	$modalContent = window.templateSuiteDetailMobile;
        }
        		
        $('body').addClass('modal-open');
        //HTML layout label 
		$modalContent  = $modalContent.replace("c-suitelist-nav-plan-placeholder", navPlan);
		$modalContent  = $modalContent.replace("c-suitelist-nav-virtual-tour-placeholder", navVirtualTour);
		$modalContent  = $modalContent.replace("c-suitelist-note-placeholder", note);
		$modalContent  = $modalContent.replace("c-suitelist-raq-placeholder", requestQuote);
		$modalContent  = $modalContent.replace("c-suitelist-from-placeholder", from);
		
		// HTML layout values
		$modalContent  = $modalContent.replace("c-suitelist-title-placeholder", title);
		$modalContent  = $modalContent.replace("c-suitelist-longDescription-placeholder", longDescription);
		$modalContent  = $modalContent.replace("c-suitelist-bedroomsInformation-placeholder", bedroomsInformation);
		$modalContent  = $modalContent.replace("c-suitelist-raq-link-placeholder", raqLink);
		$modalContent  = $modalContent.replace("c-suitelist-price-placeholder", price);
		$modalContent  = $modalContent.replace("c-suitelist-price-booking-bonus-placeholder", priceEarlyBookingBonus);
		
		if (early) {
			$modalContent  = $modalContent.replace("c-suitelist-early-bonus-placeholder", earlyBonus);
		} else {
			$modalContent  = $modalContent.replace("c-suitelist-early-bonus-placeholder", "");
		}
		
		//description tab
		if (assetSelectionReference != null) {
			$modalContent  = $modalContent.replace("c-suitelist-description-placeholder", assetSelectionToRender);
			$modalContent  = $modalContent.replace("c-suitelist-nav-description-placeholder", navDescription);
		}
		
		//suitetab
		if (suitePlan != null) {
			$modalContent  = $modalContent.replace("c-suitelist-plan-placeholder", suitePlan);
		}
		
		//features tab
		if (navFeatures != null) {
			$modalContent  = $modalContent.replace("c-suitelist-features-placeholder", featuresToRender);
    		$modalContent  = $modalContent.replace("c-suitelist-nav-features-placeholder", navFeatures);
		}
		
		//location tab
		if (locationImage != null) {
			$modalContent  = $modalContent.replace("c-suitelist-deck-placeholder", deckToRender);
			$modalContent  = $modalContent.replace("c-suitelist-location-placeholder", locationImageToRender);
    		$modalContent  = $modalContent.replace("c-suitelist-nav-location-placeholder", navLocation);
		} 
		
		//virtual tour tab
		if (virtualTourImage != null) {
			$modalContent  = $modalContent.replace("c-suitelist-virtual-tour-placeholder", virtualTourToRender);
    		$modalContent  = $modalContent.replace("c-suitelist-nav-virtual-tour-placeholder", navVirtualTour);
		}
    			
		// Activate Modal
		$($(this).data('target')).modal('show');
		
		$('.modal').on('shown.bs.modal', function(e) {
			
		  var $modal = $(this);
            $modal.off('shown.bs.modal'); //fix bug with modal gallery
		    
			$(this).find('.modal-dialog').empty().append($modalContent);
			var numTab = 0; 
			if (assetSelectionReference != null) {
    			$(".c-suite-detail-modal #descr-tab").removeClass("hidden");
    			$(".c-suite-detail-modal #descr-tab").addClass("active");
				numTab++;
			}
			if (suitePlan != null) {
    			$(".c-suite-detail-modal #suite-tab").removeClass("hidden");
    			if (numTab == 0) {
    				$(".c-suite-detail-modal #suite-tab").addClass("active");
    			}
    			numTab++;
    		}
			if (navFeatures != null) {
        		$(".c-suite-detail-modal #features-tab").removeClass("hidden");
    			if (numTab == 0) {
    				$(".c-suite-detail-modal #features-tab").addClass("active");
    			}
    			numTab++;
    		}
			
    		if (locationImage != null) {
        		$(".c-suite-detail-modal #location-tab").removeClass("hidden");
    			if (numTab == 0) {
    				$(".c-suite-detail-modal #location-tab").addClass("active");
    			}
    			numTab++;
    		}
    		
    		if (virtualTourImage != null) {
        		$(".c-suite-detail-modal #virtualtour-tab").removeClass("hidden");
    			if (numTab == 0) {
    				$(".c-suite-detail-modal #virtualtour-tab").addClass("active");
    			}
    			numTab++;
    		}
    		
    		$(".c-suite-detail-modal-navtabs li").css("width", (100 / numTab) + "%");
			
			 var $slideFor = $(this).find('.c-slider').slick({
               slidesToShow : 1,
               slidesToScroll : 1
            });
			// Show / calc counter
            var slideTotalItem = $slideFor.find('.slick-slide:not(.slick-cloned)').length;
            $('.modal').find('.c-suite-detail-modal-description__counter .slide-item-current').html(1);
            // Set total number of slide
            $('.modal').find('.c-suite-detail-modal-description__counter .slide-item-total').html(slideTotalItem);
            
            $slideFor.on('beforeChange', function(event, slick, currentSlide, nextSlide) {
                var $slider = $(this);
                // Set counter according to the current slide
                $('.modal').find('.c-suite-detail-modal-description__counter .slide-item-current').html(nextSlide + 1);

                //$video.find('.s7playpausebutton[selected="false"]').trigger('click');
                //$video.attr('class', 'c-video').empty();
                    });
                    
 
        			$(this).find(".show-deck-image").on("click", function(e) {	
		    	e.preventDefault();
		    	
		    	var idDeck = $(this).attr("id");
		    	var idDeckImage = idDeck + "-image";
		    	
		    	$("#suite-location a:not(#"+idDeck+")").removeClass("activeDeck");
		    	$("#suite-location #"+idDeck).addClass("activeDeck");
		    	
		    	$("#suite-location img:not(#"+idDeckImage+")").css("display","none");
		    	$("#suite-location #"+idDeckImage).css("display", "block");
		    	
		    });
			
			$(this).find("#descr-tab").on("click", function(e) {
				//workaround for bug virtual tour full screen
				if ($slideFor != null) {
					$slideFor.slick('slickGoTo', 0);
				}
			});
			
			$(this).find(".c-suite-detail-virtual-tour-lightbox").on("click", function(e) {
		        e.preventDefault();
		        if (!window.hasOwnProperty('virtualTour') || window.virtualTour == null) {
		        	var imagePath = $("#c-suite-detail-modal-virtual-tour-containerDiv").data().image;
		     		var intervalDiv = setInterval(function(){
		    			if ($("#virtualtour-tab").hasClass("active")) {
		    				clearInterval(intervalDiv);
		    				if (imagePath != null) {
		    					window.virtualTour = PhotoSphereViewer({
    			        			container: 'c-suite-detail-modal-virtual-tour-containerDiv',
		    						panorama: imagePath,
		    						anim_speed: '0.4rpm',
		    						move_speed: 1.0,
		    						time_anim: '1000',
		    						min_fov: 10,
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
		    		},500);
				}
		    });
		});
    });
    
    
    /***************************************************************************
     * Modal virtual tour
     **************************************************************************/
    $('.modal-virtual-tour').on('click', function(e) {
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
        
        $('.modal').on('shown.bs.modal', function(e) {
            $('.modal').off('shown.bs.modal');
        	window.virtualTour = PhotoSphereViewer({
			    container: 'virtual-tour-container',
			    panorama: imagePath,
			    anim_speed: '0.4rpm',
		        move_speed: 1.0,
		        time_anim: '1000',
		        min_fov: 10,
	            default_fov: 179,
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
    $('.automatic-gallery-modal').on('click', function(e) {
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
                    slidesToScroll : 5,
                    asNavFor : '.modal .c-slider--for',
                    focusOnSelect : true
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
    }
    
    function createVirtualTour(myThis, event) {
    	event.preventDefault();
    	var imagePath =  $(myThis).attr('data-image-virtual-tour');
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
			$("#"+idContainerVirtualTour).css("height","550px");
			$("#"+idContainerImage).css("display","none");
			var intervalDiv = setInterval(function(){
					if($("#"+idContainerVirtualTour).height() > 0) {
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
	    	},500);
		}
    }
});