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
            $('.c-links .c-list__accordion--content').collapse('hide');
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

            var cookieValues = [ 'title', 'firstname', 'lastname', 'email', 'phone', 'comments', 'requestsource', 'att02', 'workingwithagent', 'postaladdress','postalcode','city', 'country', 'voyagename', 'departuredate', 'voyagelength', 'shipname', 'suitecategory', 'suitevariation', 'price'];
            var pos = document.cookie.indexOf("userInfo=");

            // Set cookie if not created
            if (pos <= 0) {
                $.CookieManager.setCookie('userInfo', JSON.stringify(cookieValues));
            }
            var leadApiData = {}, currentData = JSON.parse($.CookieManager.getCookie('userInfo'));

            // Browse the form fields and extract values to leadApiData
            for ( var i in cookieValues) {
                if (elem[cookieValues[i]] && elem[cookieValues[i]].value !== undefined) {
                    if( elem[cookieValues[i]].name != "isbooked"){
                        leadApiData[cookieValues[i]] = elem[cookieValues[i]].value;
                    }
                    /*if( elem[cookieValues[i]].name == "isbooked" ){
                        var checkbox = document.querySelector('.c-signupforoffer [name="isbooked"]');
                        leadApiData[elem[cookieValues[i]].name] = checkbox.checked;
                    }*/
                }
            }

            $.ajax({
                type : "POST",
                url : "/content/silversea/ab.lead.json",
                data : JSON.stringify(leadApiData),
                contentType : "application/json",
                dataType : "json",
                success : function(data) {
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

                    if (elem.className.match(/c-formcookie--redirect/) !== null) {
                        $.CookieManager.setCookie('api_indiv_id', data);
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