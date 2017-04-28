$(function(){
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
                // HTML layout
                var $modalContent = $('<div class="modal-content modal-content--transparent">'
                        + '<div class="modal-header"><button class="close c-btn--close" type="button" data-dismiss="modal" aria-label="Close"></button></div>'
                        + '<div class="modal-body automatic-modal-body"><img class="o-img" /></div>' + '</div>')
                        
                // Activate Modal
                $($(this).data('target')).modal('show');
                
                // Append image inside Modal
                var imagePath = $(this).attr('href');
                $('.modal').on('shown.bs.modal', function(e) {
                    $(this).find('.modal-content').replaceWith($modalContent);
                    $(this).find('img').attr('src', imagePath);
                });
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
});
