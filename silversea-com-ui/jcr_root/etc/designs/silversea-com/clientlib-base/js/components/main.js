$(function() {
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
    $('.chosen:not(.chosen-with-search)').chosen({
        'disable_search' : true
    });

    $('.chosen.chosen-with-search').chosen({
        'disable_search' : false
    });

    /***************************************************************************
     * iCheck (custom <input type="checkbox"> look and feel)
     **************************************************************************/
    $('.custom-checkbox').iCheck({
        checkboxClass : 'icheckbox_minimal',
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
            // Cancel synchrone submit
            event.preventDefault();

            var cookieValues = [ 'title', 'firstname', 'lastname', 'email', 'phone', 'comments', 'requestsource', 'requesttype', 'subscribeemail', 'workingwithagent', 'postaladdress','postalcode','city', 'country', 'voyagename', 'voyagecode', 'departuredate', 'voyagelength', 'shipname', 'suitecategory', 'suitevariation', 'price', 'brochurecode', 'sitecountry', 'sitelanguage', 'sitecurrency'];
            var pos = document.cookie.indexOf("userInfo="),
                marketingEffortValue = $.CookieManager.getCookie('marketingEffortValue');

            // Set cookie if not created
            if (pos <= 0) {
                $.CookieManager.setCookie('userInfo', JSON.stringify(cookieValues));
            }
            var leadApiData = {},
                currentData = JSON.parse($.CookieManager.getCookie('userInfo')),
                form = $(elem).serializeArray();

            // Browse the form fields and extract values to leadApiData
            for (i in form) {
                index = cookieValues.indexOf(form[i].name);
                if (index > -1)
                    leadApiData[cookieValues[index]] = form[i].value;
            }
            if(marketingEffortValue)
                leadApiData['marketingEffort'] = marketingEffortValue;

            $.ajax({
                type : 'POST',
                url: (window.location.href.indexOf('.html') !== -1) ? window.location.href.replace('.html', '.lead.json') : window.location.href + '.lead.json',
                data : JSON.stringify(leadApiData),
                contentType : 'application/json',
                dataType : 'json',
                success : function(data) {

                    if (typeof data !== 'string') {
                        console.log('Invalid lead API data received');
                        window.location.href = elem.action;
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

                    if (elem.className.match(/c-formcookie--redirect/) !== null) {
                        window.location.href = elem.action;
                    } else if (elem.className.match(/c-formcookie--modal/) !== null) {
                        var target = elem.dataset.target;
                        $(target + ' .modal-content').load(elem.action, function(response, status, xhr) {
                            if (status == "success") {
                                $(target).modal('show');
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