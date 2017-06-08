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
    jQuery(document).ready(function() {
        $('.chosen').chosen({
            'disable_search': true
        });
    });

    /***************************************************************************
     * Form cookie value
     **************************************************************************/
    // On submit store mandatory value
    
    
    $('.c-formcookie').validator()
        .off('input.bs.validator change.bs.validator focusout.bs.validator')
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
    
    
});



+function($) {
    'use strict';
    
    $.signUp = {
        signUpOffers: function (elem, event) {
            
            var cookieValues = [ 'title','firstname', 'lastname', 'email', 'phone', 'description' ];
            
            var pos = document.cookie.indexOf( "userInfo=" );
            if( pos <= 0){        
                $.CookieManager.setCookie('userInfo', JSON.stringify(cookieValues) );
            }
                                       
            var leadApiData = {},
                currentData = JSON.parse($.CookieManager.getCookie('userInfo'));
            
            for (var i in cookieValues) {

                if (elem[cookieValues[i]] && elem[cookieValues[i]].value !== undefined) {
                    leadApiData[cookieValues[i]] = elem[cookieValues[i]].value;
                }
            }
            
  
            event.preventDefault();
            $.ajax({
                type : "POST",
                url : "/content/silversea/aa.lead.json",
                data : JSON.stringify(leadApiData),
                contentType : "application/json",
                dataType : "json",
                success : function(data) {
                    var obj = {};
                    cookieValues.forEach(function(dat, index){
                         obj[cookieValues[index]] = '';
                    });
                    currentData = Object.assign(obj, leadApiData);
                    $.CookieManager.setCookie('userInfo', JSON.stringify(currentData));
                    $.CookieManager.setCookie('api_indiv_id', data);
                    
                },
                failure : function(errMsg) {
                    console.log('error LeadAPI', errMsg);
                }
            });
            
            if (elem.className.match(/c-formcookie--redirect/) !== null) {
                //event.preventDefault();
                setTimeout(function(){ window.location.href = elem.action; }, 1000);
            } else if (elem.className.match(/c-formcookie--modal/) !== null) {
                event.preventDefault();
                var target = elem.dataset.target;
                $(target + ' .modal-content').load(elem.action, function(response, status, xhr) {
                    if (status == "success") {
                        $(target).modal('show');
                    }
                });
            }
            
        }
    }
}(jQuery);