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
     * Modal
     **************************************************************************/
    // Clean modal content on close event
    $(document).on('hide.bs.modal', function(e) {
        $(e.target).removeData('bs.modal');
        var $modalContent = $('body > .modal-content');
        $modalContent.empty();

        // Force to default class
        $modalContent.attr('class', 'modal-content');

        // Destroy gallery inside modal
        $modalContent.find('.c-slider--for, .c-slider--nav').slick('unslick');

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
        var $modal = $('.modal-content:visible');
        $modal.replaceWith($link.siblings(':hidden').html());

        // Build gallery
        $('.c-slider--for').slick({
            slidesToShow : 1,
            slidesToScroll : 1,
            asNavFor : '.c-slider--nav'
        });
        $('.c-slider--nav').slick({
            slidesToShow : 6,
            slidesToScroll : 1,
            asNavFor : '.c-slider--for',
            focusOnSelect : true
        });
    });
    /***************************************************************************
     * Brochure teaser
     **************************************************************************/
    // Redirect to page with brochure in the current language
    $('#selectBrochureListLangId').on('change', function() {
        window.location.href = this.value;
    })
});