$(function() {
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
        $('.modal-content').empty();
        // Force to default class
        $('.modal-content').attr('class', 'modal-content');
    });

    // Build modal fragment for image
    $('.automatic-modal, .gallery-modal, .virtual-tour-modal').on(
            'click',
            function(e) {
                e.preventDefault();
                $modalContent = $('<div class="modal-content modal-content-transparent">'
                        + '<div class="modal-header"><button class="close c-btn--close" type="button" data-dismiss="modal" aria-label="Close"></button></div>'
                        + '<div class="modal-body automatic-modal-body"></div>' + '</div>')
                var $img = $('<img class="o-img" />');
                $img.attr('src', $(this).attr('href'));
                $($(this).data('target')).modal('show');
                $('.modal-content:visible').replaceWith($modalContent);
                $('.modal-content:visible .modal-body').append($img);
            });

    /***************************************************************************
     * Brochure teaser
     **************************************************************************/
    // Redirect to page with brochure in the current language
    $('#selectBrochureListLangId').on('change', function() {
        window.location.href = this.value;
    })

    /***************************************************************************
     * Gallery
     **************************************************************************/
    $('.slider-for').slick({
        slidesToShow : 1,
        slidesToScroll : 1,
        asNavFor : '.slider-nav'
    });
    $('.slider-nav').slick({
        slidesToShow : 6,
        slidesToScroll : 1,
        asNavFor : '.slider-for',
        dots : true,
        focusOnSelect : true
    });
});