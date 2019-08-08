$(function() {
    // show / hide menu on click event
    $('.o-sidemenu .accordion-trigger').on('click', function(e) {
        e.preventDefault();
        $siblings = $(this).siblings();
        $siblings.toggleClass('show');

        // force hide menu when viexport it s not xs anymore
        $('body').on('trigger.viewport.changed', function() {
            if($.viewportDetect() !== 'xs') {
                $siblings.removeClass('show');
            }
        });
    });
});