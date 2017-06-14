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
     * Chosen
     **************************************************************************/
    // Activate chosen plugin
    $(document).ready(function() {
        $('.chosen:not(.chosen-with-search)').chosen({
            'disable_search': true
        });

        $('.chosen.chosen-with-search').chosen({
            'disable_search': false
        });
    });
    /***************************************************************************
     * Form cookie value
     **************************************************************************/
    // On submit store mandatory value
    $('.c-formcookie').validator({
        focus : false
    }).off('input.bs.validator change.bs.validator focusout.bs.validator')
      .on('submit', function(e) {
        
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

/***************************************************************************
 * Auto fill form with data from cookie and post data to lead API
 **************************************************************************/
+function($){
    'use strict';
    $.signUp = {
        signUpOffers: function (elem, event){
            event.preventDefault();
            var cookieValues = [ 'title','firstname', 'lastname', 'email', 'phone', 'description' ];
            var pos = document.cookie.indexOf( "userInfo=" );

            // Set cookie if not created
            if( pos <= 0){
                $.CookieManager.setCookie('userInfo', JSON.stringify(cookieValues) );
            }
            var leadApiData = {},
                currentData = JSON.parse($.CookieManager.getCookie('userInfo'));

            // Browse the form fields and extract values to leadApiData
            for (var i in cookieValues) {
                if (elem[cookieValues[i]] && elem[cookieValues[i]].value !== undefined) {
                    leadApiData[cookieValues[i]] = elem[cookieValues[i]].value;
                    /*if( elem[cookieValues[i]].name == "isbooked" ){
                        var checkbox = document.querySelector('.c-signupforoffer [name="isbooked"]');
                        leadApiData[elem[cookieValues[i]].name] = checkbox.checked;
                    }*/
                }
            }

            $.ajax({
                type : "POST",
                url : "/content/silversea/data.lead.json",
                data : JSON.stringify(leadApiData),
                contentType : "application/json",
                dataType : "json",
                success : function(data) {
                    var obj = {};

                    // Convert currentData to object
                    cookieValues.forEach(function(dat, index){
                        if( currentData[dat] !== undefined ){
                            obj[cookieValues[index]] = currentData[dat];
                        }
                    });

                    // Affect leadApiData values to currentData object
                    currentData = Object.assign(obj, leadApiData);
                    $.CookieManager.setCookie('userInfo', JSON.stringify(currentData));
                    if (elem.className.match(/c-formcookie--redirect/) !== null) {
                        $.CookieManager.setCookie('api_indiv_id', data);
                        window.location.href = elem.action; 
                    }else if (elem.className.match(/c-formcookie--modal/) !== null){
                        var target = elem.dataset.target;
                        $(target + ' .modal-content').load(elem.action, function(response, status, xhr){
                            if(status == "success") {
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