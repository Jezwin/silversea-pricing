$(function() {
    $('[data-toggle-search]').on('click', function(e) {
        e.preventDefault();
        var $trigger = $(this);
        var $targetExpand = $($trigger.data('toggle-search'));
        var isOpen = $targetExpand.hasClass('open');
        var maxWidth = $targetExpand.find('.search-expand__input').outerWidth() + $targetExpand.find('.search-expand__btn').outerWidth()

        $targetExpand.css('width', isOpen ? '' : maxWidth);

        $targetExpand.toggleClass('open');

        // Close search on page click
        $(document).on('click', function(e) {
            e.stopPropagation();
            if ($targetExpand.hasClass('open') && $(e.target).closest('.search-expand').length === 0) {
                $trigger.trigger('click');
            }
        })
    });
});