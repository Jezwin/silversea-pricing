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
    var cookieValues = [ 'firstname', 'lastname', 'email', 'phone', 'description' ];
    $('.c-formcookie').validator()
        .off('input.bs.validator change.bs.validator focusout.bs.validator')
        .on('submit', function(e) {
        if (!e.isDefaultPrevented()) {
            var leadApiData = {},
                currentData = JSON.parse($.CookieManager.getCookie('userInfo'));

            for (var i in cookieValues) {
                if (this[cookieValues[i]] && this[cookieValues[i]].value !== undefined) {
                    leadApiData[cookieValues[i]] = this[cookieValues[i]].value;
                }
            }

            $.ajax({
                type : "POST",
                url : "/content/silversea/data.lead.json",
                data : JSON.stringify(leadApiData),
                contentType : "application/json",
                dataType : "json",
                success : function(data) {
                    currentData = Object.assign(currentData, leadApiData);
                    $.CookieManager.setCookie('userInfo', JSON.stringify(currentData));
                    $.CookieManager.setCookie('api_indiv_id', data);
                },
                failure : function(errMsg) {
                    console.log('error LeadAPI', errMsg);
                }
            });

            if (this.className.match(/c-formcookie--redirect/) !== null) {
                e.preventDefault();
                window.location.href = this.action;
            } else if (this.className.match(/c-formcookie--modal/) !== null) {
                e.preventDefault();

                var target = this.dataset.target;
                $(target + ' .modal-content').load(this.action, function(response, status, xhr) {
                    if (status == "success") {
                        $(target).modal('show');
                    }
                });
            }
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