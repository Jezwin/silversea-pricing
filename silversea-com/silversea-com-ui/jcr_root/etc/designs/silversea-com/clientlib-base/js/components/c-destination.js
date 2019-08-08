$(function() {
    var $html_block_wrapper = $('#destination-html-switchable-block'),
    $html_block_trigger = $('#destination-html-switcher'),
    $fyc_block_wrapper = $('#destination-fyc-switchable-block'),
    $fyc_block_trigger = $('#destination-fyc-switcher');

    $html_block_trigger.on('click', function() {
        $(this).addClass('hidden');
        $html_block_wrapper.removeClass('hidden');
        $fyc_block_wrapper.addClass('hidden');
        $fyc_block_trigger.removeClass('hidden');
    });

    $fyc_block_trigger.on('click', function() {
        $(this).addClass('hidden');
        $html_block_wrapper.addClass('hidden');
        $html_block_trigger.removeClass('hidden');
        $fyc_block_wrapper.removeClass('hidden');
    });
});