/*
* Function DISABLE - SIL-1305
* Function from AdWords, we need it for Google Forwarding Number
(function(aGoogle,eGoogle,cGoogle,fGoogle,gGoogle,hGoogle,bGoogle,dGoogle){
	var market = "US";
	var kGoogle={ak:"1014943127",cl:"6sX6CLfmsFwQl5v74wM"};
	if (window.dataLayer != null && window.dataLayer[0] != null) {
		market = window.dataLayer[0].user_geo_adwords;
	}
	switch (market) {
		case "US":
			kGoogle={ak:"1014943127",cl:"6sX6CLfmsFwQl5v74wM"};
			break;
		case "LAM":
			kGoogle={ak:"985293017",cl:"19tjCL3os1wQ2cHp1QM"};
			break;
		case "EMEA":
			kGoogle={ak:"974176588",cl:"uPZUCPPpsFwQzILD0AM"};
			break;
		case "UK":
			kGoogle={ak:"958324608",cl:"I_N0CIiOsFwQgL_7yAM"};
			break;
		case "AP":
			kGoogle={ak:"974446676",cl:"4DSFCNLmsFwQ1MDT0AM"};
			break;
	}
	aGoogle[cGoogle]=aGoogle[cGoogle]||function(){
		(aGoogle[cGoogle].q=aGoogle[cGoogle].q||[]).push(arguments)
	};
	aGoogle[gGoogle]||(aGoogle[gGoogle]=kGoogle.ak);
	bGoogle=eGoogle.createElement(hGoogle);
	bGoogle.async=1;
	bGoogle.src="//www.gstatic.com/wcm/loader.js";
	dGoogle=eGoogle.getElementsByTagName(hGoogle)[0];
	dGoogle.parentNode.insertBefore(bGoogle,dGoogle);
	aGoogle[fGoogle]=function(bGoogle_2,dGoogle_2,eGoogle_2){
		aGoogle[cGoogle](2,bGoogle_2,kGoogle,dGoogle_2,null,new Date,eGoogle_2)
	};
	aGoogle[fGoogle]()
})(window,document,"_googWcmImpl","_googWcmGet","_googWcmAk","script");
*/
(function () {
    setTimeout(function(){
 	if(typeof s !== 'undefined'){
			if (s.eVar34 != null && s.eVar34.length > 0) {
				var date = new Date();
				date.setTime(date.getTime() + 365 * 24 * 60 * 60 * 1000);
				document.cookie = 'marketingEffortValue=' + encodeURIComponent(s.eVar34) + '; expires=' + date.toGMTString() + '; path=/';
			}
	}
	
}, 1000);
					
					
				})();

/*
 * Floating Bar behavior
 */
/*$(window).scroll(function() {
	if($(window).width() > 767){
	    if ($(window).scrollTop() > 100) {
	        $(".c-floating-bar:not(.editor)").fadeIn("slow");
	    }
	    else {
	        $(".c-floating-bar:not(.editor)").fadeOut("fast");
	    }
	}
});*/

/*
 * Sticky Filter FYC 
 */
/*$(window).scroll(function() {
	if($(".c-fyc__header").length > 0){
    var DistTop = $(".c-fyc__header").offset().top - 180;
    var LastResElPosTop = $(".c-fyc__result").last().offset().top;

var scroll = $(window).scrollTop();
if($(window).width() > 767){
	if (scroll >= DistTop && scroll < LastResElPosTop) {
	    $(".c-find-your-cruise-filter").addClass("c-find-your-cruise-filter-sticky");
	            $(".c-fyc-filter__header").addClass("c-fyc-filter__header-sticky");
	} else {
	    $(".c-find-your-cruise-filter").removeClass("c-find-your-cruise-filter-sticky");
	            $(".c-fyc-filter__header").removeClass("c-fyc-filter__header-sticky");
	}
}
	}
});*/


/*
 * Hack for tab - open and autoscroll support
 */
$(document).ready(function(){
    if(window.location.hash != "") {
        var hash = $.trim( window.location.hash );
        if (hash){
        	$('.c-tab__nav a[href$="'+hash+'"]').trigger('click');
        	if($(hash).length > 0){
                $("html,body").animate({scrollTop: $(hash).offset().top - 120},"slow");
        	}
        }
    }
});



/*
 * TY BRO CUSTOM MESSAGE
 */
if($(".brochurethankyou").length > 0){
	var cookie = getCookie('userInfo');
	var userInf = JSON.parse(cookie);
	var type = userInf.requestsource;
	
	if(type == "BRO"){
		$('h5').hide();
		$('h5.c-BroDesc').show();

	}

    try {
        dataLayer[0].track_brochuretype = type;
    }catch(e){

    }
}


/*
 * Auto click on SFO checkbox
 */
if($("input[name='subscribeemail-custom']").length > 0){
	if(!$("input[name='subscribeemail-custom']").is(":checked") && dataLayer[0].user_country!="CA"){
		$("input[name='subscribeemail-custom']").parent().click();
	}
}

/*
 * Cookie helper
 */

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for(var i = 0; i <ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}

/*
 * Smooth scroll for ship page
 */
if($('.ship .c-hero-banner__buttons a.btn-default').length > 0){
	if($('.ship .c-hero-banner__buttons a.btn-default').attr('href').indexOf("#voyages") >= 0){
$(document).on('click', '.ship .c-hero-banner__buttons a.btn-default', function(event){
    event.preventDefault();
	//href should contain #voyages
    $('html, body').animate({
        scrollTop: $( '#' + $.attr(this, 'href').split('#')[1] ).offset().top -120
    }, 500);
});
	}
}

/*
 * Smooth scroll for all hero banner landing
 */
if($('.c-hero-banner-landing__buttons.anchorlink a.btn').length > 0){
	$(".c-hero-banner-landing__buttons.anchorlink a.btn").each(function( index ) {
	if($($(".c-hero-banner-landing__buttons.anchorlink a.btn")[index]).attr('href').indexOf("#") >= 0){
$(this).on('click', function(event){
    event.preventDefault();
	//href should contain #voyages
    $('html, body').animate({
        scrollTop: $( '#' + $.attr(this, 'href').split('#')[1] ).offset().top -120
    }, 500);
});
	}
	});
}

/*
 * Hot Fix - Block form submit
 */
function blockMultiSubmit (){
	if($(".request-quote-form").length > 0){
		$(".request-quote-form").on('submit', function (){
			if(!$('.has-error').length > 0){
				$(".request-quote-form button").attr("disabled", "disabled");
				setTimeout(function() { $(".request-quote-form button").removeAttr("disabled"); }, 3500);
			}
		});
	}

	if($(".c-signupforoffer").length > 0){
		$(".c-signupforoffer").on('submit', function (){
			if(!$('.has-error').length > 0){
				$(".c-signupforofferbutton").attr("disabled", "disabled");
				setTimeout(function() { $(".c-signupforoffer button").removeAttr("disabled"); }, 3500);
			}
		});
	}
}

blockMultiSubmit();

$(document).ajaxStop(function() { 
	blockMultiSubmit();


		if($(".brochurerequest #InputTelephoneNumber").length > 0){
		$('.brochurerequest #InputTelephoneNumber').removeAttr('required');
		}
		
		if($("input[name='subscribeemail-custom']").length > 0 ){
			if(!$("input[name='subscribeemail-custom']").is(":checked") && dataLayer[0].user_country!="CA"){
				$("input[name='subscribeemail-custom']").parent().click();
			}
		}
});

/*
 * OVERRIDE DATALAYER VALUE AFTER AJAX REQUEST
 */

function overrideDatalayerFYCValue(){
	$resultWrapper = $('.c-fyc__result-wrapper');
	$cruise = $resultWrapper.find('.c-fyc__result:first');
	window.dataLayer[0].track_destination_name = $cruise.find('.cruise-destination').data('destinationname');
	window.dataLayer[0].track_voyage_type = $cruise.find('.cruise-type').data('cruisetype');
	window.dataLayer[0].track_shipname = $cruise.find('.cruise-ship').data('shipname');
}

$(document).ready(function(){
    if($('.c-fyc__result').length > 0) {
    	$(document).ajaxComplete(function(){
    		overrideDatalayerFYCValue();
    	});
    	
    	overrideDatalayerFYCValue();
    }
});

/*
 * FIX SHOREX ICON (HIDE ON VOYAGE NOT PART OF PROMO)
 */
function hideFreeShorexIcon(){
	var includedCruise = ['2804','2806','2807','2809','2811','2812','6805','6806','3801','3802','3803','3804','3805','3806','3807','3808','3809','3810','3811'];
	if($('.icon-shorex').length > 0) {
		if(includedCruise.indexOf(window.dataLayer[0].track_voyage_id) == -1){
			$('.icon-shorex').hide();
            if($('.cruise-2018-itineraries-port-excursions-excursion-features').length > 0) {
                $('.icon-shorex').parent().hide();
                $(".cruise-2018-itineraries-port-excursions-moredetails").on("click", function() {
                    setTimeout(function(){
                        $(".lsh-icon-list .icon-shorex").hide();
                        $(".lsh-icon-list .icon-shorex").parent().hide();
                        $(".lsh-icon .icon-shorex").parent().hide();
                    }, 500);
                    setTimeout(function(){
                        $(".lsh-icon-list .icon-shorex").hide();
                        $(".lsh-icon-list .icon-shorex").parent().hide();
                        $(".lsh-icon .icon-shorex").parent().hide();
                    }, 1000);
                    setTimeout(function(){
                        $(".lsh-icon-list .icon-shorex").hide();
                        $(".lsh-icon-list .icon-shorex").parent().hide();
                        $(".lsh-icon .icon-shorex").parent().hide();
                    }, 1500);
                    setTimeout(function(){
                        $(".lsh-icon-list .icon-shorex").hide();
                        $(".lsh-icon-list .icon-shorex").parent().hide();
                        $(".lsh-icon .icon-shorex").parent().hide();
                    }, 2000);
                    setTimeout(function(){
                        $(".lsh-icon-list .icon-shorex").hide();
                        $(".lsh-icon-list .icon-shorex").parent().hide();
                        $(".lsh-icon .icon-shorex").parent().hide();
                    }, 3000);
                });
            }else {
                $('.icon-shorex').parent().parent().hide();
            }
		}
	}
	}

$(document).ready(function(){
	hideFreeShorexIcon();
});

/*
 * Function called in googleforwardingnumber.html
 * real_number: real number to change with google number
 * idGoogleText: element id to apply the new google number (formatted_number)
 * idGoogleHref: element id with href to apply the new google number (mobile_number)
 * classGoogleText: element class to apply the new google number (use in localizedPhoneDisplay)
 * */
function callGoogleForwardingNumber(real_number, idGoogleText, idGoogleHref, classGoogleText) {
	/*Function DISABLE - SIL-1305
		window._googWcmGet(function(formatted_number, mobile_number){
			var e = null;
			if (idGoogleText != null && formatted_number != null) {
				e = document.getElementById(idGoogleText);
				e.innerHTML = "";
				e.appendChild(document.createTextNode(formatted_number));
			}
			if (idGoogleHref != null && mobile_number != null) {
				e = document.getElementById(idGoogleHref);
				e.href = "tel:" + mobile_number;
			}
			if (classGoogleText != null && formatted_number != null) {
				var element = document.getElementsByClassName(classGoogleText);
				for (var i=0; i < element.length; i++) {
					e = element[i];
					e.innerHTML = "";
					e.appendChild(document.createTextNode(formatted_number));
				}
			}
		}, real_number);
	*/
}

/*
 * Cool anchor slide
 */
$(document).ready(function(){
	jQuery("a.coolanchor").click(function(){
		//check if it has a hash (i.e. if it's an anchor link)
		if(this.hash){
			var hash = this.hash.substr(1);
			var $toElement = jQuery("[id="+hash+"]");
			var toPosition = $toElement.offset().top - 90;
			//scroll to element
			jQuery("body,html").animate({
				scrollTop : toPosition
			},1000)
			return false;
		}
	});
	
	jQuery("a.coolanchorminus").click(function(){
		//check if it has a hash (i.e. if it's an anchor link)
		if(this.hash){
			var hash = this.hash.substr(1);
			var $toElement = jQuery("[id="+hash+"]");
			var toPosition = $toElement.offset().top - 200;
			//scroll to element
			jQuery("body,html").animate({
				scrollTop : toPosition
			},1000)
			return false;
		}
	});
});


(function(global) {
    // Stores the Y position where the touch started
    var startY = 0;

    // Store enabled status
    var enabled = false;

    var handleTouchmove = function(evt) {
        // Get the element that was scrolled upon
        var el = evt.target;

        // Check all parent elements for scrollability
        while (el !== document.body && el !== document) {
            // Get some style properties
            var style = window.getComputedStyle(el);

            if (!style) {
                // If we've encountered an element we can't compute the style for, get out
                break;
            }

            // Ignore range input element
            if (el.nodeName === 'INPUT' && el.getAttribute('type') === 'range') {
                return;
            }

            var scrolling = style.getPropertyValue('-webkit-overflow-scrolling');
            var overflowY = style.getPropertyValue('overflow-y');
            var height = parseInt(style.getPropertyValue('height'), 10);

            // Determine if the element should scroll
            var isScrollable = scrolling === 'touch' && (overflowY === 'auto' || overflowY === 'scroll');
            var canScroll = el.scrollHeight > el.offsetHeight;

            if (isScrollable && canScroll) {
                // Get the current Y position of the touch
                var curY = evt.touches ? evt.touches[0].screenY : evt.screenY;

                // Determine if the user is trying to scroll past the top or bottom
                // In this case, the window will bounce, so we have to prevent scrolling completely
                var isAtTop = (startY <= curY && el.scrollTop === 0);
                var isAtBottom = (startY >= curY && el.scrollHeight - el.scrollTop === height);

                // Stop a bounce bug when at the bottom or top of the scrollable element
                if (isAtTop || isAtBottom) {
                    evt.preventDefault();
                }

                // No need to continue up the DOM, we've done our job
                return;
            }

            // Test the next parent
            el = el.parentNode;
        }

        // Stop the bouncing -- no parents are scrollable
        evt.preventDefault();
    };

    var handleTouchstart = function(evt) {
        // Store the first Y position of the touch
        startY = evt.touches ? evt.touches[0].screenY : evt.screenY;
    };

    var enable = function() {
        // Listen to a couple key touch events
        window.addEventListener('touchstart', handleTouchstart, false);
        window.addEventListener('touchmove', handleTouchmove, false);
        enabled = true;
    };

    var disable = function() {
        // Stop listening
        window.removeEventListener('touchstart', handleTouchstart, false);
        window.removeEventListener('touchmove', handleTouchmove, false);
        enabled = false;
    };

    var isEnabled = function() {
        return enabled;
    };

    // Enable by default if the browser supports -webkit-overflow-scrolling
    // Test this by setting the property with JavaScript on an element that exists in the DOM
    // Then, see if the property is reflected in the computed style
    var testDiv = document.createElement('div');
    document.documentElement.appendChild(testDiv);
    testDiv.style.WebkitOverflowScrolling = 'touch';
    window.scrollSupport = 'getComputedStyle' in window && window.getComputedStyle(testDiv)['-webkit-overflow-scrolling'] === 'touch';
    document.documentElement.removeChild(testDiv);

    // A module to support enabling/disabling iNoBounce
    var iNoBounce = {
        enable: enable,
        disable: disable,
        isEnabled: isEnabled
    };
    
    window.iNoBounce = iNoBounce;

    if (typeof module !== 'undefined' && module.exports) {
        // Node.js Support
        module.exports = iNoBounce;
    }
    if (typeof global.define === 'function') {
        // AMD Support
        (function(define) {
            define('iNoBounce', [], function() { return iNoBounce; });
        }(global.define));
    }
    else {
        // Browser support
        global.iNoBounce = iNoBounce;
    }
}(this));