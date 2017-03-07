$(function() {
    // Activate chosen plugin
    jQuery(document).ready(function() {
        jQuery(".chosen").chosen();
    });

    // Clean modal content on close event
    $(document).on('hide.bs.modal', function(e) {
        $(e.target).removeData('bs.modal');
        $('.modal-content').empty();
    });

    // Redirect to page with brochure in the current language
    $('#selectBrochureListLangId').on('change', function() {
        window.location.href = this.value;
    })
});