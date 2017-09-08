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
        focus : false
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
                    'sitelanguage', 'sitecurrency', 'isnotagent' ];
            var pos = document.cookie.indexOf("userInfo="), marketingEffortValue = $.CookieManager.getCookie('marketingEffortValue');

            // Set cookie if not created
            if (pos <= 0) {
                $.CookieManager.setCookie('userInfo', JSON.stringify(cookieValues));
            }
            var leadApiData = {},
                currentData = JSON.parse($.CookieManager.getCookie('userInfo')),
                form = $form.serializeArray();

            // Browse the form fields and extract values to leadApiData
            for (i in form) {
                index = cookieValues.indexOf(form[i].name);
                if (index > -1) {
                    leadApiData[cookieValues[index]] = form[i].value;
                }
            }

            if (marketingEffortValue) {
                leadApiData['marketingEffort'] = marketingEffortValue;
            }

            $.ajax({
                type : 'POST',
                url: '/bin/lead.json',
                data : JSON.stringify(leadApiData),
                contentType : 'application/json',
                dataType : 'json',
                success : function(data) {

                    if (typeof data !== 'string') {
                        console.log('Invalid lead API data received');
                        window.location.href = $form.attr('action');
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
                    $.CookieManager.setCookie('api_indiv_id', data);

                    if ($form.hasClass('c-formcookie--redirect')) {
                        window.location.href = $form.attr('action');
                    } else if ($form.hasClass('c-formcookie--modal')) {
                        var target = $form.data('target');

                        // Append content from ajax response inside modal
                        $(target + ' .modal-content').load($form.attr('action'), function(response, status, xhr) {
                            if (status == "success") {
                                // Open modal
                                $(target).on('shown.bs.modal', function(event) {
                                    requestForm();
                                }).modal('show');
                            }
                        });
                    }
                },
                failure : function(errMsg) {
                    console.log('error LeadAPI', errMsg);
                }
            });
        }
    }
}(jQuery);