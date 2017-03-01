$(function() {
    // Activate chosen plugin
    jQuery(document).ready(function(){
        jQuery(".chosen").chosen();
    });

    // Clean modal content on close event
    $(document).on('hide.bs.modal', function (e) {
        $(e.target).removeData('bs.modal');
    });
});