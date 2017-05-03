$(function(){
    /***************************************************************************
     * Modal
     **************************************************************************/
    // Clean modal content on close event
    $(document).on('hide.bs.modal', function(e) {
        // Destroy gallery inside modal
        //$('.c-slider--for, .c-slider--nav').slick('unslick');

        $(e.target).removeData('bs.modal');
        var $modalContent = $('body > .modal .modal-content');
        $modalContent.empty();

        // Force to default class
        $modalContent.attr('class', 'modal-content');
    });

    // Build modal for image
    $('.automatic-modal, .virtual-tour-modal').on('click', function(e) {
        e.preventDefault();

        // HTML layout
        var $modalContent = $('<div class="modal-content modal-content--transparent">'
                + '<div class="modal-header"><button class="close c-btn--close" type="button" data-dismiss="modal" aria-label="Close"></button></div>'
                + '<div class="modal-body automatic-modal-body"><img class="o-img" /></div>'
                + '</div>');

        // Activate Modal
        $($(this).data('target')).modal('show');

        // Append image inside Modal
        var imagePath = $(this).attr('href');
        $('.modal').on('shown.bs.modal', function(e) {
            $(this).find('.modal-dialog').empty().append($modalContent);
            $(this).find('img').attr('src', imagePath);
        });
    });

    // Build modal fragment for Gallery
    $('.automatic-gallery-modal').on('click', function(e) {
        e.preventDefault();
        var $link = $(this);

        // Activate Modal
        $($link.data('target')).modal('show');

        $($link.data('target')).on('shown.bs.modal', function(e) {
            var $modal = $(this);

            // Append gallery inside modal
            $modal.find('.modal-content').replaceWith($link.siblings(':hidden').html());

            // Activate gallery
            $modal.find('.c-slider--for').slick({
                slidesToShow : 1,
                slidesToScroll : 1,
                asNavFor : '.modal .c-slider--nav'
            });

            $modal.find('.c-slider--nav').slick({
                slidesToShow : 6,
                slidesToScroll : 1,
                asNavFor : '.modal .c-slider--for',
                focusOnSelect : true
            });
        });
    });
});