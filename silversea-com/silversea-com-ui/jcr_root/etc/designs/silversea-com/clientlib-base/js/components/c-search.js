$(function() {
    // Open in main navigation
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

    // Expand search in search page
    $('.c-search-result__search-trigger').on('click', function() {
        $('.c-search-result__expand').toggle();
        $(this).find('i').toggleClass('fa-angle-up fa-angle-down');
    });
    

    // highlight item on touch
    var $itemList = $('.c-search-result__results__item');
    $itemList.on('touchstart', function() {
        $(this).trigger('focus');
    });

    $itemList.on('touchend', function() {
        $(this).trigger('blur');
    });
});