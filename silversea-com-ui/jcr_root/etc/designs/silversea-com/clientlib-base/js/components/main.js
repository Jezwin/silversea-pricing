$(function() {
    /***************************************************************************
     * Mobile detect
     **************************************************************************/
    var isMobile = false; //initiate as false
     // device detection
     if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|ipad|iris|kindle|Android|Silk|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i.test(navigator.userAgent) 
         || /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(navigator.userAgent.substr(0,4))) isMobile = true;
     $('body').toggleClass('mobile', isMobile);

     $.fn.mobileDetect = function() {
        return isMobile;
    };

    /***************************************************************************
     * Use viewportDetect : allow sync between css breakpoint and javascript
     * function call
     **************************************************************************/
    // On page load event, set viewport class name :
    var viewportBootstrap = 'viewport-' + $.viewportDetect(), $body = $('body');
    $body.addClass(viewportBootstrap);

    // On widow resize event, set viewport class name :
    $.viewportDetect(function(vp) {
        viewportBootstrap = 'viewport-' + vp;
        $body.removeClass('viewport-xs viewport-sm viewport-md viewport-lg viewport-xl').addClass(viewportBootstrap);
        $body.trigger('trigger.viewport.changed');
    });

    /***************************************************************************
     * Chosen (custom <select> look and feel)
     **************************************************************************/
    // Activate chosen plugin
    if (!$.fn.mobileDetect()) {
        $('.chosen:not(.chosen-with-search)').chosen({
            'disable_search' : true
        });

        $('.chosen.chosen-with-search').chosen({
            'disable_search' : false
        });
    } else {
        // Call function on page load
        mobileSelectStyle();

        // Call function on modal shown
        $('.modal').on('shown.bs.modal', function() {
            mobileSelectStyle();
        });
    }

    function mobileSelectStyle() {
        // Behavior for select on mobile device
        var $mobileSelect = $('body.mobile select:not(.mobile-select-ready):not(#countryCode)');

        $mobileSelect.each(function(i, select) {
            $(select).addClass('mobile-select-ready');
            $(select).after('<i class="fa fa-angle-down"></i>');
        });

        $mobileSelect.on('change', function() {
            var $currentSelect = $(this);
            $currentSelect.parent().toggleClass('active', $currentSelect.val() !== 'all' || '')
        });
    }

    /***************************************************************************
     * iCheck (custom <input type="checkbox"> look and feel)
     **************************************************************************/
    $('.custom-checkbox').iCheck({
        checkboxClass : 'icheckbox_minimal'
    });

    /***************************************************************************
     * Form cookie value
     **************************************************************************/
    // On submit store mandatory value
    $('.c-formcookie').validator({
        focus : false,custom : {                    
            email : function($el){
            	var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            	 if(!re.test($el.val())){
            		 return "error";
            	 }
            	if(typeof prevemail !== 'undefined'){
            		if(prevemail == $el.val()){
            			return window.answerBrite;
            		}else{
                		prevemail = $el.val();
                		window.briteVerify($el.val());
                	  return window.answerBrite;
                	}
            		
            	}else{
            		prevemail = $el.val();
            		window.briteVerify($el.val());
            	  return window.answerBrite;
            	}
            }
        }
    }).off('input.bs.validator change.bs.validator focusout.bs.validator').on('submit', function(e) {
    		
        if (!e.isDefaultPrevented()) {
            $.signUp.signUpOffers(this, e);
        }
    });

    /***************************************************************************
     * Brochure teaser
     **************************************************************************/
    // Redirect to page with brochure in the current language
    $('#selectBrochureListLangId').on('change', function() {
        window.location.href = this.value;
    });

    /***************************************************************************
     * Footer link collapse behavior according to the viewport
     **************************************************************************/
    $('body').on('trigger.viewport.changed', function() {
        if ($.viewportDetect() === 'xs') {
            $('.c-links .c-list__accordion--content, #ship-overview__content--collapsed').collapse('hide');
        }
    });
});

/*******************************************************************************
 * Save data form to cookie and post data to lead API
 ******************************************************************************/
+function($) {
    'use strict';
    $.signUp = {
        signUpOffers : function(elem, event) {
            var $form = $(elem);
            // Cancel synchrone submit
            event.preventDefault();

            // Custom behavior for subscribeemail : set false if nit checked, set true if checked
            if ($form.find('[name="subscribeemail-custom"]').length > 0) {
                $form.find('[name="subscribeemail"]').val($form.find('[name="subscribeemail-custom"]').is(':checked'));
            }

            var cookieValues = [ 'title', 'firstname', 'lastname', 'email', 'phone', 'comments', 'requestsource', 'requesttype', 'subscribeemail', 'workingwithagent', 'postaladdress', 'postalcode',
                    'city', 'country', 'voyagename', 'voyagecode', 'departuredate', 'voyagelength', 'shipname', 'suitecategory', 'suitevariation', 'price', 'brochurecode', 'sitecountry',
                    'sitelanguage', 'sitecurrency', 'isnotagent' , 'subject' , 'inquiry' , 'from_email' , 'bookingnumber' , 'vsnumber', 'state' ];
            var pos = document.cookie.indexOf("userInfo="), marketingEffortValue = $.CookieManager.getCookie('marketingEffortValue');

            // Set cookie if not created
            if (pos <= 0) {
                $.CookieManager.setCookie('userInfo', JSON.stringify(cookieValues));
            }
            var leadApiData = {},
                currentData = JSON.parse($.CookieManager.getCookie('userInfo')),
                form = $form.serializeArray();

            // Browse the form fields and extract values to leadApiData
            for (var i in form) {
                var index = cookieValues.indexOf(form[i].name);
                if (index > -1) {
                    leadApiData[cookieValues[index]] = form[i].value;
                }
            }

            if (marketingEffortValue) {
                leadApiData['marketingEffort'] = marketingEffortValue;
            }

            if (!$form.hasClass("c-form--rab")) {
                $.ajax({
                    type : 'POST',
                    url: '/bin/lead.json',
                    data : JSON.stringify(leadApiData),
                    contentType : 'application/json',
                    dataType : 'json',
                    success : function(data) {

                        if (typeof data !== 'string') {
                            console.log('Invalid lead API data received');
                            window.location.href = $form.attr('action') + "?did=" + dataLayer[0].track_destination_id;
                            return;
                        }

                        //set cookies for datalayer
                        var submitDate = new Date();
                        $.CookieManager.setCookie("user_status", submitDate.getTime());

                        var obj = {};

                        // Convert currentData to object
                        cookieValues.forEach(function(dat, index) {
                            if (currentData[dat] !== undefined) {
                                obj[cookieValues[index]] = currentData[dat];
                            }
                        });

                        // Merge object
                        var objs = [obj, leadApiData];
                        currentData = objs.reduce(function(r, o) {
                            Object.keys(o).forEach(function(k) {
                                r[k] = o[k];
                            });
                            return r;
                        }, {});

                        // Store object merged in the cookie
                        $.CookieManager.setCookie('userInfo', JSON.stringify(currentData));

                        var leadRes;
                        if(data !=""){
                           leadRes = JSON.parse(data).leadResponse;
                        }
                        if(leadRes != undefined){
                        	$.CookieManager.setCookie('api_indiv_id', leadRes);
                        }
                        var blockedRef;
 						if(data !=""){
                           blockedRef = JSON.parse(data).blockedReferer;
                        }
                        if(blockedRef != undefined){
                        	$.CookieManager.setCookie('api_blocked_referer', blockedRef);
                        }
						//SIL-16
						var temporaryId;
						if(data !=""){
                           temporaryId = JSON.parse(data).temporaryId;
                        }
                        if(temporaryId != undefined){
                        	$.CookieManager.setCookie('api_temporary_id', temporaryId);
                        }

                        if ($form.hasClass('c-formcookie--redirect')) {
                            window.location.href = $form.attr('action') + "?did=" + dataLayer[0].track_destination_id; //add destination id as a parameter if available
                        } else if ($form.hasClass('c-formcookie--modal')) {
                            var target = $form.data('target');

                            // Append content from ajax response inside modal
                            $(target + ' .modal-content').load($form.attr('action'), function(response, status, xhr) {
                                if (status == "success") {
                                    // Open modal
                                    $(target).modal('show');
                                    // Once modal loaded, script from c-quote-request-form.js will be launched
                                }
                            });
                        }
                    },
                    failure : function(errMsg) {
                        console.log('error LeadAPI', errMsg);
                    }
                });
            } else {
                window.location.href = $form.attr('action');
            }
        }
    }
}(jQuery);

function createCookie(name, value, days) {
	var expires;
	if (days) {
		var date = new Date();
		date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
		expires = "; expires=" + date.toGMTString();
	}
	else {
		expires = "";
	}
	document.cookie = name + "=" + value + expires + "; path=/";
}

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

//Referrer Cookie to use at lead submission level
var currentReferrer = document.referrer;
if(currentReferrer != ""){
    var a=document.createElement('a');
    a.href=currentReferrer;
    if(a.hostname.indexOf("silversea") == -1){
		createCookie("currentReferrer", currentReferrer, 1);
	}
}
window.answerBrite = "";
//BriteVerify Basic Implementation
window.briteVerify = function(email){
	var url = "https://bpi.briteverify.com/emails.json";

	 $.ajax({
		    url: url,
		    dataType: 'jsonp',
		    cache:true,
		    data: {
		      address: email,
		      username: "1847206e-0e64-45a9-bb0a-224260bd2b9a"
		    },
		    success: function(response) {
		    	var valid = response["status"];

		    	if(valid == "invalid"){
		    		window.answerBrite = "error";
		    	}else{
		    		window.answerBrite = "";
		    	}
		    	$("[name='email']").blur();
	        }
	});
	
};


//Get did parameter for TY Page destination id datalayer fetch
(function () {
	
	try {
			var match = RegExp('[?&]did=([^&]*)').exec(window.location.search);
	
			
			var did = match && decodeURIComponent(match[1].replace(/\+/g, ' '));
			if (did != null && did.length > 0) {
				dataLayer[0].track_destination_id = did;
			}
		}
		catch(error) {
		  console.error(error);
		}

})();

//Fix FYC port all caps + dest +
(function () {

    try {
        $('.c-fyc-v2__result__content__itinerary li.destination-ports .c-fyc-v2__result__content__itinerary__ports span').each(function(){
            var currentPort = $(this).text();
            currentPort = currentPort.toLowerCase();
            $(this).text(currentPort);
        });
    }
    catch(error) {
        console.error(error);
    }

})();

//Fix B version all caps
(function () {
    function lowerCaseContent(thisEl){
        var currentPort = $(thisEl).html();
        currentPort = currentPort.toLowerCase();
        $(thisEl).html(currentPort);
    }
    try {
        $('.c-destination-teaser__caption .c-destination-teaser__title').each(function () {
            lowerCaseContent(this);
        });
    }
    catch(error) {
        console.error(error);
    }

    try {
        $('.c-ship-teaser__caption .c-ship-teaser__title').each(function () {
            lowerCaseContent(this);
        });
    }
    catch(error) {
        console.error(error);
    }


    try {
        $('.c-destinationSlider-slide-small-inner__description .c-destinationSlider-slide-small-inner__description-title').each(function () {
            lowerCaseContent(this);
        });
    }
    catch(error) {
        console.error(error);
    }

    try {
        $('.c-eolist__caption .c-eolist__title').each(function () {
            lowerCaseContent(this);
        });
    }
    catch(error) {
        console.error(error);
    }

})();

// FIX Object-fit for IE 11

if (/MSIE|Trident/.test(navigator.userAgent)) {
    $('picture').each(function () {
        var $container = $(this),
            imgUrl = $container.find('img').prop('src');
        if($container.find('img').attr("style")){
        if($container.find('img').attr("style").indexOf("cover") != -1 && $container.find('img').attr("style").indexOf("object-fit") != -1 ) {
            if (imgUrl && imgUrl.indexOf('base64') == -1) {
                $container
                    .css('backgroundImage', 'url(' + imgUrl + ')')
                    .addClass('compat-object-fit');
                $container.find('img').remove();
            }else {
                imgUrl = $container.find('img').prop('data-src');
                if (imgUrl) {
                    $container
                        .css('backgroundImage', 'url(' + imgUrl + ')')
                        .addClass('compat-object-fit');
                    $container.find('img').remove();
                }
            }
        }
        }
    });
};


//KONAMI CODE
$(function() {

	
	var k = [38, 38, 40, 40, 37, 39, 37, 39],
	n = 0;
	$(document).keydown(function (e) {
	    if (e.keyCode === k[n++]) {
	    	console.log('yes');
	        if (n === k.length) {
	        	$('body').append("<a href='https://www.silversea.com/request-quote.html'><canvas id=q style='position: absolute;top: 0;z-index: 999999;'></a>")
	        	 for (s = window.screen,
	                     w = q.width = s.width, // on my monitor: 1920
	                     h = q.height = s.height,  // on my monitor: 1200
	                     m = Math.random,   // random number from 0-1
	                     p = [],
	                     i = 0; 

	                     // i ranges from 0 to 255, one element for each character horizontally
	                     // this is enough characters to fill the entire screen horizontally
	                     // canvas won't let you draw off the screen - so I could set this to 1000
	                     i < 256;

	                     // initialize p (the y coordinate of each character) to start at 1
	                     p[i++] = 1);

	                setInterval(
	                    // every time we call this function we draw the entire screen a very faint black (with a high transparency of 0.05)
	                    // this means every 33 milliseconds the screen is getting slightly darker
	                    // this also acts to darken and fade the green characters - when they are first printed they are dark green, then they slowly fade to black
	                    function() {
	                        // draw black (0,0,0) with alpha (transparency) value 0.05
	                        q.getContext('2d').fillStyle='rgba(0,0,0,0.05)';
	                        // fill the entire screen
	                        q.getContext('2d').fillRect(0,0,w,h);
	                        // #0f0 is a short form for color green (#00FF00)
	                        q.getContext('2d').fillStyle='#0F0';

	                        p.map(
	                            // this function will be called 256 times - once for each element of array p, 
	                            function(v,i){
	                            // map over the array p
	                            //      v is the value in the array p, which represents the y-coordinate of the text going down
	                            //      i is the index of the array p, which represents the x coordinate
	                            // start from unicode char code 30,000 (0x7530) then add a random number from 0-33
	                            // from wikipedia: http://en.wikipedia.org/wiki/List_of_CJK_Unified_Ideographs,_part_2_of_4
	                            //      U+753x 	田 	由 	甲 	申 	甴 	电 	甶 	男 	甸 	甹 	町 	画 	甼 	甽 	甾 	甿
	                            //      U+754x 	畀 	畁 	畂 	畃 	畄 	畅 	畆 	畇 	畈 	畉 	畊 	畋 	界 	畍 	畎 	畏
	                            //      U+755x 	畐
	                            randomNum = m()*33;
	                            // note how the asian characters are slightly different shades
	                            // of green, this depends on their line thickness etc, and doesn't
	                            // really happen for english characters
	                            randomAsianChar = "RAQ";//String.fromCharCode(30000 + randomNum);

	                            q.getContext('2d').fillText(
	                                randomAsianChar, 
	                                i*30,   // x coordinate - each character is 10 x 10
	                                v       // y coordinate 
	                            );
	                            // draw at least 758 characters down before reseting to the start
	                            minimumHeight=758
	                            num = minimumHeight+m()*10000;
	                            p[i] = (v>num) ? 0 : v+10   // increment the y coordinate by one character (10 pixels), reset when y-coord gets too big
	                            })
	                    },
	                    33)
	        	
	            n = 0;
	            return false;
	        }
	    }
	    else {
	        n = 0;
	    }
	});
});