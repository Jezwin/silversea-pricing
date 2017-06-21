$(function() {
    // toggle style for active/inactive tab
    $('.select-segment dd a').on('click', function(e) {
        e.preventDefault();
        $(this).closest('dd').toggleClass('active');
        $(this).closest('dd').siblings('dd.active').toggleClass('active');
    })
});