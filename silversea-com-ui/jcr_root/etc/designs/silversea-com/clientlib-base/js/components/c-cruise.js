$(function() {
    $('.c-cruise .request-quote').on('click', function(e) {
        e.stopPropagation();
    })

    // Force reinit tab when it was inside a hidden suite tab
    $('div[id^="suitelist-collapse"]').on('show.bs.collapse', function(e) {
        $(this).find('.c-tab__content[data-state="active"]').trigger('ctabcontent-shown');
    });

    // Force reinit slider
    $('.c-suitelist').find('.c-tab__content').on('ctabcontent-shown', function() {
        // load image inside slider first
        $(this).find('.lazy').lazy({
            afterLoad: function(element) {
                $(window).trigger('resize');
            }
        });

        // reinit slider with resize event
        setTimeout(function() {
            $(window).trigger('resize');
        }, 50);
    });

    // call lazy load : load image inside the current tab actived (the one opened on page load)
    $('.c-tab--cruise > .c-tab__body > .c-tab__content[data-state="active"]').find('.lazy').lazy();

    $('.c-cruise .c-tab__content').on('ctabcontent-shown', function() {
        // Force reinit slider for cruise for Enrichments and key people slider
        $(this).find('.c-slider.slick-initialized:not(.c-slider--for, .c-slider--nav)').slick('unslick').slick(settingSlider);

       // call lazy load : load image inside the current tab opened
        $(this).find('.lazy:visible').lazy();
    });

    $('.c-cruise div[id^="suitelist-collapse"]').on('shown.bs.collapse', function(e) {
        var $currentCollapse = $(this);
        var $item = $('div[id^="suitelist-collapse"]').not($currentCollapse);
        $item.prev('.c-suitelist__heading').addClass('opacity');

        // Scroll page to content
        var scrollTargetOffsetTop = $.viewportDetect() === 'xs' ? $currentCollapse.offset().top - 20 : $currentCollapse.prev('.c-suitelist__heading').offset().top - 10;
        $('html, body').animate({
            scrollTop : scrollTargetOffsetTop - $('.c-header').height() - $('.c-main-nav__container').height()
        }, 500);

    }).on('hide.bs.collapse', function(e) {
        var $item = $('div[id^="suitelist-collapse"]').not($(this));
        $item.prev('.c-suitelist__heading').removeClass('opacity');
    });

    // Open location tab on click event on tab
    $('.c-cruise a[data-toggle="location-tab"]').on('click', function(e) {
        e.preventDefault();
        var $tab = $(this).closest('.c-tab ').find('a[href^="#suite-location"]').closest('.c-tab__link').trigger('click');
    })

    // Force Scroll top for highlight container on resize
    $(window).on('resize', function() {
        var viewport = $.viewportDetect();
        if (viewport !== 'md' || viewport !== 'lg') {
            $('.c-cruise-highlights__content').scrollTop(0);
        }
    });

    // Toggle Highlight (small viewport)
    var $container = $('.c-cruise-highlights__content');
    $container.find('a[data-toggle-text]').on('click', function(e) {
        e.preventDefault();
        $container.toggleClass('open');
        $('html, body').animate({
            scrollTop : $('#highlight').offset().top - $('.c-header').height()
        }, 500);
    });

    // keypeople tab/carousel
    $('.c-cruise-keypeople').find('[data-keypeople-toggle]').on('click', function() {
        var $currentTrigger = $(this);

        // highlight current
        $('.c-cruise-keypeople').find('[data-keypeople-toggle]').removeClass('active');
        $currentTrigger.addClass('active');

        // show current details
        $('.keypeople__details__inner-wrapper').hide();
        $('#' + $currentTrigger.data('keypeople-toggle') + '').show();

        // Close details
        $('.btn-close').on('click', function(e) {
            e.preventDefault();
            $(this).closest('.keypeople__details__inner-wrapper').hide();
        });
    });

    /***************************************************************************
     * Open tab from link other than tab
     **************************************************************************/
    $('.c-cruise a[data-tab-target]').on('click', function(e) {
        e.preventDefault();
        var $link = $(this);
        // Scroll to top first
        $('html, body').animate({
            scrollTop : 0
        }, 0);

        // Open the target tab
        $('a[href="' + $link.data('tab-target') + '"]').closest('li').trigger('click');

        // Open collapse if collpase Id
        if ($link.data('collpase-id') !== 'undefined') {
            $('[data-target="#' + $link.data('collpase-id') + '"]').trigger('click');
        }
    });

    /***************************************************************************
     * Scroll to content
     **************************************************************************/
    $('.c-cruise a[data-scroll-target]').on('click', function(e) {
        e.preventDefault();
        $('html, body').animate({
            scrollTop : $($(this).data('scroll-target')).offset().top - $('.c-header').height() - 80
        }, 500);
    });

    /***************************************************************************
     * Scroll to content expanded
     **************************************************************************/
    $('.c-cruise [role="tabpanel"]:not(.c-suitelist__collapse)').on('shown.bs.collapse', function(e) {
        e.stopPropagation();
        $('html, body').animate({
            scrollTop : $(this).prev('[role="button"]').offset().top - $('.c-header').height() - $('.c-main-nav__bottom').height()
        }, 500);
    });

    /***************************************************************************
     * Cruise fare addiction collapse
     **************************************************************************/
    $('.c-list--cruise-fare-additions').each(function() {
        var $list = $(this);
        var $wrapper = $list.closest('.c-list--cruise-fare-additions__wrapper');
        var $items = $list.find('li');
        var isMobile = $.viewportDetect() === 'xs';

        // Split list into 2 lists
        var length = $items.length;
        var halfLength = Math.round(length / 2);
        var $lastHalf = $($items.splice(halfLength, length - 1)).remove();
        $list.after($('<ul class="' + $list.attr('class') + '"></ul>').html($lastHalf));

        // Hide content according to viewport
        var hideContent = (function hideContent() {
            isMobile = $.viewportDetect() === 'xs';

            $wrapper.find('li').removeClass('hidden');
            if (isMobile) {
                $wrapper.find('li:gt(7)').addClass('hidden');
            } else {
                $('.c-list--cruise-fare-additions').each(function() {
                    $(this).find('li:gt(3)').addClass('hidden');
                });
            }

            return hideContent;
        })();

        $('body').on('trigger.viewport.changed', function() {
            hideContent();
        })

        // Action show/hide
        var $trigger = $wrapper.next('.c-list__expand').find('a');

        $trigger.on('click', function(e) {
            e.preventDefault();

            // Set height just before animation
            var isClosed = $wrapper.hasClass('closed');
            $wrapper.css('height', isClosed ? getMaxHeight('closed') : getMaxHeight('opened'));

            // Animate for slide effect
            if (isClosed) {
                // open
                $wrapper.toggleClass('closed opened');

                $wrapper.animate({
                    'height' : getMaxHeight('opened')
                }, 300, function() {
                    // Remove inline height
                    $wrapper.css('height', '');
                });
            } else {
                // close
                $wrapper.animate({
                    'height' : getMaxHeight('closed')
                }, 300, function() {
                    $wrapper.toggleClass('closed opened');
                    // Remove inline height
                    $wrapper.css('height', '');
                });
            }
        });

        // function get max Height
        function getMaxHeight(state) {
            var totalHeight = [];
            var isMobile = $.viewportDetect() === 'xs';
            $wrapper.find('.c-list--cruise-fare-additions').each(function() {
                var $list = $(this);
                var sum = 0;
                var items;

                if (state === 'closed') {
                    if (isMobile) {
                        $items = $list.find('li:lt(8)');
                    } else {
                        $items = $list.find('li:lt(4)');
                    }
                } else {
                    $items = $list.find('li');
                }

                $items.each(function() {
                    sum += $(this).outerHeight(true);
                });

                totalHeight.push(sum);
            });

            // is mobile return height of both column, is not mobile return only the heighest
            return isMobile ? totalHeight[0] + totalHeight[1] : Math.max.apply(Math, totalHeight);
        }
    });

    /***************************************************************************
     * Read more
     **************************************************************************/
    var readMore = (function readMore() {
        $('.c-cruise-ship-info__item .variationcontent__descr').each(function(i, description) {
            var $description = $(description);
            // Re-init hmtl markup
            $description.attr('data-original-height', '');
            $description.removeClass('clipped');
            $description.removeClass('opened');

            if ($description.height() > 137) {
                var $descriptionToggle = $description.next('.variationcontent__descr__expand');
                var $moreBtn = $descriptionToggle.find('.read_more');
                var $lessBtn = $descriptionToggle.find('.read_less');

                $description.attr('data-original-height', $description.height());
                $description.addClass('clipped');

                // Expand
                $moreBtn.on('click', function(e) {
                    e.preventDefault();
                    $description.addClass('opened');
                    $description.css('height', $description.data('original-height'))
                });

                // Collapse
                $lessBtn.on('click', function(e) {
                    e.preventDefault();
                    $description.removeClass('opened');
                    $description.css('height', '')
                });
            }
        });

        return readMore;
    })();

    // Init function on resize
    $('body').on('trigger.viewport.changed', function() {
        readMore();
    });
    

    /*********************************************************************************
     * Automatic collapse all suite and fares boxes if the user click outside the list
     *********************************************************************************/
    $('body').on('click', function(event) {
    	var hideBox = !($(event.target).hasClass('c-suitelist__collapse') || $(event.target).parents('.c-suitelist__collapse').length != 0);
    	if (hideBox) {
    		$(".c-suitelist").find('.collapse').collapse('hide');
    		if(!$('body').hasClass('modal-open')){
    			clearVirtualTourCruise();
    		}
    	}
    });

    
    /***************************************************************************
     * Scroll down when Suite And Fares button is clicked (mobile only)
     **************************************************************************/
    $('[data-tab-target="#suitenfare"]').on('click', function(event) {
		var target = $("#suitenfare");

		if( target && target.length ) {
			event.preventDefault();
			$('html, body').stop().animate({
				scrollTop: target.offset().top - 130
			}, 600); //1000 = 1s speed
		}
	});
    /* AB - Test suite and fare button B version*/
    $('[data-tab-target="#suitenfare-b-version"]').on('click', function(event) {
		var target = $("#suitenfare-b-version");

		if( target && target.length ) {
			event.preventDefault();
			$('html, body').stop().animate({
				scrollTop: target.offset().top - 130
			}, 600); //1000 = 1s speed
		}
	});
    
    
    /***************************************************************************
     * Open directly the first element on the modal photo and video
     * link related to ab test v2
     **************************************************************************/
    $(".c-cruise__itineraries .cruise-itinerary-link").on('click', function(event) {
    	event.preventDefault();
    	var mediaElement = $(".c-cruise__gallery__item.automatic-gallery-modal");
    	if (mediaElement.length > 0 ) {
    		mediaElement[0].click();
    	}
    });
    
    $("#cruise-itinerary-suite-and-fares-mobile-btn-v2").on('click', function(event) {
    	event.preventDefault();
    	var mediaElement = $('[href="#overview"]');
    	if (mediaElement.length > 0) {
    		if ($('[href="#overview"]').parent().attr('data-state') !== "active") {
    			mediaElement.click();
    			var intervalDiv = setInterval(function(){ 
    				if ($('[href="#overview"]').parent().attr('data-state') == "active") {
    					clearInterval(intervalDiv);
    					intervalDiv = null;
    					var mediaElement2 = $('[data-tab-target="#suitenfare-b-version"]');
    					mediaElement2.click();
    				}
    			}, 500);
    		} else {
    			var mediaElement3 = $('[data-tab-target="#suitenfare-b-version"]');
    			mediaElement3.click();
    		}
    	}
    });
    
    
    /***************************************************************************
     * Create virtual tour in every suite on cruise page when
     * the use click on VIRTUAL TOUR tab
     **************************************************************************/
    $(".cruise-suite-virtual-tour").on('click', function(event) {
    	event.preventDefault();
    	
    	var thisElement = $(this);
    	var idParent =  $(this).attr('href');
    	var idContainer = idParent + "-container";
    	var imagePath = $(idContainer).attr("data-image");
    	
    	if (thisElement.attr('data-virtual-tour-exists') == null) { //make sure to not create the virtual tour again
    		if (window.hasOwnProperty('virtualTour') && window.virtualTour != null) {
    			window.virtualTour.destroy();
    			window.virtualTour = null;
    		}
    		
    		if (window.hasOwnProperty('virtualTourID') && window.virtualTourID != null) {
    			$(virtualTourID).empty();
    		}
    		
    		var intervalDiv = setInterval(function(){
    			var active = $(idParent).attr("data-state");
    			if (active == "active") {
    				clearInterval(intervalDiv);
    				thisElement.attr('data-virtual-tour-exists','true');
    				intervalDiv = null;
    				if (imagePath != null && idContainer != null) {
    					window.virtualTourID = idContainer;
    					window.virtualTour = PhotoSphereViewer({
    						container: idContainer.replace("#",""),
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
    		},500);
    	}
    });
    
    
    /****************************************************************************
    * Clear all virtual tour to release memory and DOM when the use click
    * on close button or on tab that are not the virtual tour
    **************************************************************************/
    $(".suitelist-collapse-close").on('click', function(event) {
    	event.preventDefault();
    	clearVirtualTourCruise();
    });
    
    
    /****************************************************************************
     * Function to clear all virtual tour to release memory and DOM
     * used when the use click on close button or outside the div
     **************************************************************************/
    function clearVirtualTourCruise(){

    	if(window.hasOwnProperty('virtualTourType') &&  window.virtualTourType == "cruise-gallery-virtual-tour") {
    		return;
    	}
    	
    	if (window.hasOwnProperty('virtualTour') && window.virtualTour != null) {
         	window.virtualTour.destroy();
         	window.virtualTour = null;
        }
    	
    	if (window.hasOwnProperty('virtualTourID') && window.virtualTourID != null) {
    		var virtualTourTab = window.virtualTourID.replace("-container","");
    		var descrptionTab = virtualTourTab.replace("virtual-tour", "description");
    		var descriptionTabElement = $("[href='"+descrptionTab+"']") ;
    		$("[href='"+virtualTourTab+"']").removeAttr('data-virtual-tour-exists');
    		if ( descriptionTabElement != null) {
    			descriptionTabElement.click();
    		}
         	$(virtualTourID).empty();
         	window.virtualTourID = null;
        }
    }
    
    
   
    
    /***************************************************************************
     * Build modal for suite detail
     **************************************************************************/
    $(window).on('hashchange', function (event) {
        if(window.location.hash != "#modal") {
        	window.backNavigation = true;
            $('.modal').modal('hide');
        }
    });
    
    $(window).on("resize", function() {
    	//check if it is the cruise page
    	if ($('header').nextAll().hasClass("c-cruise") && $(".modal-content").hasClass("modal-content--transparent-suite")) {
    		var template = null;
            var desktop = $(window).width() > 768;
    		if (desktop && (window.suiteDesktop === false)) { //make the switch from mobile to desktop
    			window.suiteDesktop = true;
     	    	template = window.templateSuiteDetail;
    		} else if (!desktop && (window.suiteDesktop === true)) { //make the switch from desktop to mobile
    			window.suiteDesktop = false;
            	template = window.templateSuiteDetailMobile;
    		} 
    		if (template !=null && window.cruiseIDShowed != null) {
    			//$('body > .modal .modal-content.modal-content--transparent-suite .close').click();
                $('.modal').modal('hide');

    			var intervalDiv = setInterval(function(){
	    			if (!$("body").hasClass("modal-open")) {
	    				clearInterval(intervalDiv);
	    				setTimeout(function() {
	    					createSuiteDetailLightbox(template);
	    				},200);
	    			} 
	    		},200);
    		}
    	}
	});
   
    
    $('.automatic-modal-suite-detail').on('click', function(e) {
        e.preventDefault();
        window.cruiseIDShowed = $(this).attr('id');
        var template = window.templateSuiteDetail;
        var desktop = $(window).width() > 768;
        if (desktop) { //check if not mobile
        	window.suiteDesktop = true;
        	template = window.templateSuiteDetail;
        } else {
        	window.suiteDesktop = false;
        	template = window.templateSuiteDetailMobile;
        }
        createSuiteDetailLightbox(template)
    });
    
    $('.automatic-modal-suite-detail a.btn-request-a-quote').on('click', function(e) {
        e.preventDefault();
        document.location.href = $(this).attr("href"); 
        e.stopPropagation();
    });
    
    function createSuiteDetailLightbox(template) {
		$('html').addClass("no-scroll-html");
		// Activate Modal
        $('body').addClass("no-scroll-body");

        
    	var myThis = $("#"+window.cruiseIDShowed);
    	
        if (window.hasOwnProperty("cruiseItem") && window.cruiseItem != null) {
        	var id = myThis.attr('id');
        	//label i18n
        	var navDescription = (myThis.parent().data('nav-description') != null) ? myThis.parent().data('nav-description')   : "";
        	var navPlan = (myThis.parent().data('nav-plan') != null) ? myThis.parent().data('nav-plan')   : "";
        	var navFeatures = (myThis.parent().data('nav-features') != null) ? myThis.parent().data('nav-features')   : "";
        	var navLocation = (myThis.parent().data('nav-location') != null) ? myThis.parent().data('nav-location')   : "";
        	var navVirtualTour = (myThis.parent().data('nav-virtual-tour') != null) ? myThis.parent().data('nav-virtual-tour')  : "";
        	
        	var note = (myThis.parent().data('note') != null) ? myThis.parent().data('note')   : "";
        	var requestQuote = (myThis.parent().data('request-quote') != null) ? myThis.parent().data('request-quote')   : "";
        	var earlyBonus = (myThis.parent().data('early-bonus') != null) ? myThis.parent().data('early-bonus')   : "";
        	var waitlist = (myThis.parent().data('waitlist') != null) ? myThis.parent().data('waitlist')   : "";
        	var deckLabel = (myThis.parent().data('deck') != null) ? myThis.parent().data('deck')   : "";
        	var viewAll = (myThis.parent().data('view-all') != null) ? myThis.parent().data('view-all')   : "";
        	var viewLess = (myThis.parent().data('view-less') != null) ? myThis.parent().data('view-less')   : "";
        	var close = (myThis.parent().data('close') != null) ? myThis.parent().data('close')   : "";
        	var from = "";
        	
        	//suite values
        	var title = (window.cruiseItem[id].title != null) ? window.cruiseItem[id].title : "";
        	var assetSelectionReference = (window.cruiseItem[id].assetSelectionReference != "") ? window.cruiseItem[id].assetSelectionReference : null;
        	var longDescription = (window.cruiseItem[id].longDescription != null) ? window.cruiseItem[id].longDescription : "";
        	var bedroomsInformation = (window.cruiseItem[id].bedroomsInformation != null) ? window.cruiseItem[id].bedroomsInformation : "";
        	var virtualTourImage = (window.cruiseItem[id].virtualTour != "") ? window.cruiseItem[id].virtualTour : null;
        	
        	var currency = (myThis.data('currency') != null) ? myThis.data('currency')   : "";
        	var raqLink = $("#"+id).find(".link-request-quote-card").attr("href");
        	var early = window.cruiseItem[id].early;
        	var priceEarlyBookingBonus = (window.cruiseItem[id].priceBookingBonus != null) ? currency + " " + window.cruiseItem[id].priceBookingBonus : "";
        	var suitePlan =  (window.cruiseItem[id].plan != "") ? window.cruiseItem[id].plan : null;
        	var features =  (window.cruiseItem[id].features != null) ? window.cruiseItem[id].features : "";
        	var locationImage =  (window.cruiseItem[id].locationImage != "") ? window.cruiseItem[id].locationImage : null;
        	var deck =(window.cruiseItem[id].deck != null) ? window.cruiseItem[id].deck : "";
        	
        	var price = waitlist;
        	
        	if (window.cruiseItem[id].price != null) {
        		price = currency + " " + window.cruiseItem[id].price;
        		from =  (myThis.parent().data('from') != null) ? myThis.parent().data('from')   : "";
        	}
        	
        	//description tab
			var assetSelectionToRender = "";
    		if (assetSelectionReference != null) {
				var assetSelectionReferenceList = assetSelectionReference.split("#next#");
				for(var i = 0; i < assetSelectionReferenceList.length; i++) {
					assetSelectionToRender += '<div class="slider-item">';
					assetSelectionToRender += '<div class="ratio lazy" style="display:block;background-size: cover;background-position: center;background-repeat: no-repeat;background-image:url('+ assetSelectionReferenceList[i] +'?wid=1200&fit=hfit,1)"></div>';
					if (i == 0) {
						assetSelectionToRender += '<div class="c-suite-loader"></div>'; 
					}
					assetSelectionToRender += '</div>'; 
				}
			}
    		
    		//features tab
    		var featuresToRender = "";
    		if (navFeatures != null) {
    			var featuresList = features.split("#next#");
    			for(var i=0; i < featuresList.length; i++) {
    				featuresToRender += "<li>"+featuresList[i]+"</li>";
    			}
    		}
    		
    		//location tab
			var locationImageToRender = "";
			var deckToRender = "";
    		if (locationImage != null) {
    			var deckList = deck.split("#next#");

    			for(var i=0; i < deckList.length; i++) {
    				var classItem = "btn btn-thin show-deck-image";
    				var idItem = "deck-"+ i;
    				if (i == 0) {
    					classItem += " activeDeck";
    				}
    				deckToRender += '<a id="'+idItem+'" class="'+classItem+'" href="#"> <span> '+ deckLabel + ' ' +deckList[i] +'</span></a>';

    			}
    			var locationImageList = locationImage.split("#next#");
    			var style="";
    			for(var i=0; i < locationImageList.length; i++) {
    				var idItem = "deck-"+i+"-image";
    				if (i != 0) {
    					style = "style='display:none'";
    				}
    				locationImageToRender += '<img id="'+idItem+'" src="'+ locationImageList[i] +'"?hei=930&wid=930&fit=constrain" alt="Suite plan" title="Suite plan" class="o-img"' + style + '/>';
    			}
    		} 
    		
    		//virtual tour tab
    		var virtualTourToRender = "";
    		if (virtualTourImage != null) {
    			virtualTourToRender = '<div id="c-suite-detail-modal-virtual-tour-containerDiv" class="c-suite-detail-modal-virtual-tour-container" data-image="'+virtualTourImage+'"></div>';
    		}
    		
        var $modalContent = template;
        		
        //HTML layout label 
		$modalContent  = $modalContent.replace("c-suitelist-nav-plan-placeholder", navPlan);
		$modalContent  = $modalContent.replace("c-suitelist-nav-virtual-tour-placeholder", navVirtualTour);
		$modalContent  = $modalContent.replace("c-suitelist-note-placeholder", note);
		$modalContent  = $modalContent.replace("c-suitelist-raq-placeholder", requestQuote);
		$modalContent  = $modalContent.replace("c-suitelist-from-placeholder", from);
		$modalContent  = $modalContent.replace("c-suitelist-raq-placeholder-mobile", requestQuote);
		$modalContent  = $modalContent.replace("c-suitelist-from-placeholder-mobile", from);
		$modalContent  = $modalContent.replace("c-suitelist-view-all-placeholder", viewAll);
		$modalContent  = $modalContent.replace("c-suitelist-view-less-placeholder", viewLess);
		$modalContent  = $modalContent.replace("c-suitelist-close-placeholder", close);


		// HTML layout values
		$modalContent  = $modalContent.replace("c-suitelist-title-placeholder", title);
		$modalContent  = $modalContent.replace("c-suitelist-longDescription-placeholder", longDescription);
		$modalContent  = $modalContent.replace("c-suitelist-bedroomsInformation-placeholder", bedroomsInformation);
		$modalContent  = $modalContent.replace("c-suitelist-raq-link-placeholder", raqLink);
		$modalContent  = $modalContent.replace("c-suitelist-raq-link-placeholder-mobile", raqLink);

		$modalContent  = $modalContent.replace("c-suitelist-price-placeholder", price);
		$modalContent  = $modalContent.replace("c-suitelist-price-placeholder-mobile", price);

		$modalContent  = $modalContent.replace("c-suitelist-price-booking-bonus-placeholder", priceEarlyBookingBonus);
		$modalContent  = $modalContent.replace("c-suitelist-price-booking-bonus-placeholder-mobile", priceEarlyBookingBonus);

		if (early) {
			$modalContent  = $modalContent.replace("c-suitelist-early-bonus-placeholder", earlyBonus);
			$modalContent  = $modalContent.replace("c-suitelist-early-bonus-placeholder-mobile", earlyBonus);

		} else {
			$modalContent  = $modalContent.replace("c-suitelist-early-bonus-placeholder", "");
			$modalContent  = $modalContent.replace("c-suitelist-early-bonus-placeholder-mobile", "");

		}
		
		//description tab
		if (assetSelectionReference != null) {
			$modalContent  = $modalContent.replace("c-suitelist-description-placeholder", assetSelectionToRender);
			$modalContent  = $modalContent.replace("c-suitelist-nav-description-placeholder", navDescription);
		}
		
		//suitetab
		if (suitePlan != null) {
			var suitePlanToRender ='<img src="'+ suitePlan + '?hei=930&wid=930&fit=constrain" alt="Suite plan" title="Suite plan" class="o-img" />';
			$modalContent  = $modalContent.replace("c-suitelist-plan-placeholder", suitePlanToRender);
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


        $('body').addClass('modal-open');
        $(myThis.data('target')).modal('show');
         
		$('.modal').on('shown.bs.modal', function(e) {
			if(window.scrollSupport != null && window.scrollSupport) { 
				window.iNoBounce.enable();
			}
            
			var urlReplace = "#" + $(this).attr('id'); // make the hash the id of the modal shown
			history.pushState(null, null, urlReplace); // push state that hash into the url
			
		  var $modal = $(this);
            $modal.off('shown.bs.modal'); //fix bug with modal gallery
		    
			$(this).find('.modal-dialog').empty().append($modalContent);
			$(".modal-dialog").css("padding-top","0%"); //remove padding-top only on this modal
			if (window.suiteDesktop == false) {
				$(".modal-content--transparent-suite").parent().parent().css("display", "block"); //remove when modal is close
				$(".modal-content--transparent-suite").parent().parent().css("overflow-y", "hidden"); //remove when modal is close
				$(".modal-content--transparent-suite .modal-body").css("overflow-y", "scroll");
				$(".modal-content--transparent-suite .modal-body").css("max-height", "100vh");
			}
			//logic to put active tab
			var numTab = 0; 
			if (assetSelectionReference != null) {
				if (window.suiteDesktop) {
					$(".c-suite-detail-modal #descr-tab").removeClass("hidden");
					$(".c-suite-detail-modal #descr-tab").addClass("active");
					$(".c-suite-detail-modal #suite-description.tab-pane").addClass("active");
					numTab++;
				} else {
	        		$(".c-suite-detail-modal #suite-description").removeClass("hidden");

				}
			}
			if (suitePlan != null) {
				if (window.suiteDesktop) {
					$(".c-suite-detail-modal #suite-tab").removeClass("hidden");
					if (numTab == 0) {
						$(".c-suite-detail-modal #suite-tab").addClass("active");
						$(".c-suite-detail-modal #suite-suite-plan.tab-pane").addClass("active");
					}
					numTab++;
				} else {
	        		$(".c-suite-detail-modal #suite-suite-plan").removeClass("hidden");
				}
    		}
			if (navFeatures != null) {
				if (window.suiteDesktop) {
					$(".c-suite-detail-modal #features-tab").removeClass("hidden");
					if (numTab == 0) {
						$(".c-suite-detail-modal #features-tab").addClass("active");
						$(".c-suite-detail-modal #suite-features.tab-pane").addClass("active");
					}
					numTab++;
				} else {
	        		$(".c-suite-detail-modal #suite-features").removeClass("hidden");
				}
    		}
			
    		if (locationImage != null) {
    			var deckList = deck.split("#next#");
    			if (deckList.length > 4 && !window.suiteDesktop) {
    				$(".c-suite-detail-modal-deck").css("float", "left");
    			}
				if (window.suiteDesktop) {
    				$(".c-suite-detail-modal #location-tab").removeClass("hidden");
    				if (numTab == 0) {
    					$(".c-suite-detail-modal #location-tab").addClass("active");
						$(".c-suite-detail-modal #suite-location.tab-pane").addClass("active");
    				}
    				numTab++;
    			} else {
	        		$(".c-suite-detail-modal #suite-location").removeClass("hidden");
    			}
    		}
    		
    		if (virtualTourImage != null) {
				if (window.suiteDesktop) {
    				$(".c-suite-detail-modal #virtualtour-tab").removeClass("hidden");
    				if (numTab == 0) {
    					$(".c-suite-detail-modal #virtualtour-tab").addClass("active");
						$(".c-suite-detail-modal .tab-pane #suite-virtual-tour").addClass("active");
    				}
    				numTab++;
    			}
    		}
    		
    		//logic to use all space in tab
    		//$(".c-suite-detail-modal-navtabs li").css("width", (100 / numTab) + "%");
			
    		//create slider
			 var $slideFor = $(this).find('.c-slider').slick({
               slidesToShow : 1,
               slidesToScroll : 1
            });
			// Show / calc counter
            var slideTotalItem = $slideFor.find('.slick-slide:not(.slick-cloned)').length;
            	$('.modal').find('.c-suite-detail-modal-description__counter .slide-item-current').html(1);
            	// Set total number of slide
            	$('.modal').find('.c-suite-detail-modal-description__counter .slide-item-total').html(slideTotalItem);
			if (slideTotalItem == 1) {
				$(".c-suite-detail-modal-description__counter").css("display", "none");
			}
            
            $slideFor.on('beforeChange', function(event, slick, currentSlide, nextSlide) {
                var $slider = $(this);
                // Set counter according to the current slide
                $('.modal').find('.c-suite-detail-modal-description__counter .slide-item-current').html(nextSlide + 1);
             });
            
            
            //attached all jquery event inside the modal
            
            //show deck image
   			$(this).find(".show-deck-image").on("click", function(e) {	
		    	e.preventDefault();
		    	
		    	var idDeck = $(this).attr("id");
		    	var idDeckImage = idDeck + "-image";
		    	
		    	$("#suite-location a:not(#"+idDeck+")").removeClass("activeDeck");
		    	$("#suite-location #"+idDeck).addClass("activeDeck");
		    	
		    	$("#suite-location img:not(#"+idDeckImage+")").css("display","none");
		    	$("#suite-location #"+idDeckImage).css("display", "block");
		    	
		    });
			
   			//workaround for bug virtual tour full screen
			$(this).find("#descr-tab").on("click", function(e) {
				if ($slideFor != null) {
					$slideFor.slick('slickGoTo', 0);
				}
			});
			
			//action for view all view less features
			$(this).find(".suite-features-expand a.view_all").on("click", function(e) {
				e.preventDefault();
				//$("#suite-features ul").removeClass("expand-ul");
				$('.expand-ul li:nth-of-type(1n+10)').stop().css('display','list-item').hide().slideDown();
				$(".suite-features-expand a.view_less").show();
				$(this).hide();
			});
			
			$(this).find(".suite-features-expand a.view_less").on("click", function(e) {
				
				e.preventDefault();
				//$("#suite-features ul").addClass("expand-ul");
				$(".suite-features-expand a.view_all").show();
				$('.expand-ul li:nth-of-type(1n+10)').stop().slideUp();
				$(this).hide();
		        /*$('.modal-body').animate({
		            scrollTop: $('.suite-features-title').offset().top - 10
		        }, 500);*/
			});
			
			
			$(this).find(".close").on("click", function(e) {
				
				e.preventDefault();
				 $('.modal').modal('hide');
			});
			
			if (window.suiteDesktop == true) {
				
				$(this).find("#features-tab.c-navtab__link--button a").on("click", function (e) {
	        		 $(".modal-body .tab-content").addClass("tab-content-grey");
	        	});
	    	  
				$(this).find(".c-navtab__link--button:not(#features-tab) a").on("click", function (e) {
	        		$(".modal-body .tab-content").removeClass("tab-content-grey");
	        	});
			}
			
			//create virtual tour on desktop
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
		    		},500);
				}
		    });
		});
      }
    }//createSuiteDetaiLightbox
    
});