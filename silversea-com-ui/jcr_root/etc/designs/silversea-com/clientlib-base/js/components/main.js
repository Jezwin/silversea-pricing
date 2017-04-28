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
        jQuery(".chosen").chosen();
    });

    /***************************************************************************
     * Form cookie value
     **************************************************************************/
    // On submit store mandatory value
    var cookieValues = ['email', 'firstname', 'lastname'];
    $('.c-formcookie').validator().on('submit', function (e) {
        if (!e.isDefaultPrevented()) {
            for ( var value in cookieValues ) {
                if (this[cookieValues[value]] !== undefined) {
                    $.CookieManager.setCookie(cookieValues[value], this[cookieValues[value]].value);
                }
            }
            if (this.className.match(/c-formcookie--redirect/) !== null) {
                e.preventDefault();
                window.location.href = this.action;
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