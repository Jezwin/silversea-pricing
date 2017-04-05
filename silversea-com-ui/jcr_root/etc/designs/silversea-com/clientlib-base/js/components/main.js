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
     * Bootstrap Validator
     **************************************************************************/
    // Activate bootstrap validator plugin
    /*$(document).ready(function() {
        $('.c-rabwidget').validator();
    });*/

    /***************************************************************************
     * RAB Widget
     **************************************************************************/
    // On button click cookie store email and redirect
    /*jQuery(document).ready(function() {
        $('.c-rabwidget').validator().on('submit', function (e) {
            console.log(this, 'yolo');
            if (!e.isDefaultPrevented()) {
                console.log(this);
            }
        });
    });*/


    $('.c-rabwidget').on('submit', function (e) {

        if (!e.isDefaultPrevented()) {
            console.log('yolo', this.email, this.email.value);
            //var today = new Date();
            //today.setDate(today.getDate() + 365);
            //document.cookie = "cookieMessageDisclaimer=true;expires=" + today.toUTCString();
            document.cookie = "email=" + this.email.value + ";expires=" + today.toUTCString();
        }
    });

    /***************************************************************************
     * Modal
     **************************************************************************/
    // Clean modal content on close event
    $(document).on('hide.bs.modal', function(e) {
        // Destroy gallery inside modal
        $('.c-slider--for, .c-slider--nav').slick('unslick');

        $(e.target).removeData('bs.modal');
        var $modalContent = $('body > .modal .modal-content');
        $modalContent.empty();

        // Force to default class
        $modalContent.attr('class', 'modal-content');
    });

    // Build modal for image
    $('.automatic-modal, .virtual-tour-modal').on('click', function(e) {
        e.preventDefault();
        $modalContent = $('<div class="modal-content modal-content--transparent">'
                + '<div class="modal-header"><button class="close c-btn--close" type="button" data-dismiss="modal" aria-label="Close"></button></div>'
                + '<div class="modal-body automatic-modal-body"></div>' + '</div>')
        var $img = $('<img class="o-img" />');
        $img.attr('src', $(this).attr('href'));
        $($(this).data('target')).modal('show');
        $('.modal-content:visible').replaceWith($modalContent);
        $('.modal-content:visible .modal-body').append($img);
    });

    // Build modal fragment for Gallery
    $('.automatic-gallery-modal').on('click', function(e) {
        e.preventDefault();
        var $link = $(this);

        $($link.data('target')).modal('show');
        var $modalContent = $('.modal-content:visible');
        var $modal = $('.modal:visible');

        // Append gallery inside modal
        $modalContent.replaceWith($link.siblings(':hidden').html());
    });

    $('.modal').on('shown.bs.modal', function() {
        // Build gallery
        setTimeout(function() {
            $('.c-slider--for').slick({
                slidesToShow : 1,
                slidesToScroll : 1,
                asNavFor : '.modal .c-slider--nav'
            });

            $('.c-slider--nav').slick({
                slidesToShow : 6,
                slidesToScroll : 1,
                asNavFor : '.modal .c-slider--for',
                focusOnSelect : true
            });
        }, 10);

    });

    /***************************************************************************
     * Brochure teaser
     **************************************************************************/
    // Redirect to page with brochure in the current language
    $('#selectBrochureListLangId').on('change', function() {
        window.location.href = this.value;
    });
});