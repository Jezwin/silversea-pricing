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
    });

    // Build modal fragment for image
    $('.automatic-modal, .gallery-modal, .virtual-tour-modal').on('click', function(e) {
        e.preventDefault();
        var img = $('<img style="display:block;margin: 0 auto;" />');
        img.attr('src', $(this).attr('href'));
        $($(this).data('target')).modal('show');
        $('.modal-content:visible').append(img);
    });

    /***************************************************************************
     * Brochure teaser
     **************************************************************************/
    // Redirect to page with brochure in the current language
    $('#selectBrochureListLangId').on('change', function() {
        window.location.href = this.value;
    })
});