$(function(){
    $(window).on('resize load', function() {
        var $col = $('.c-hero-banner__content');
        var $row = $('.c-hero-banner__row');
        var offsetLeft = $row.offset().left;

        if($.viewportDetect() === 'sm' || $.viewportDetect() === 'md') {
            if (offsetLeft < 60) {
                $col.css('left', 60 - offsetLeft);
            } else {
                $col.css('left', 'auto');
            }
        } else {
            $col.css('left', 'auto');
        }
    });
});