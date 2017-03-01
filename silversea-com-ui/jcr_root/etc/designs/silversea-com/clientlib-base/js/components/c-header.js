+function($) {
    'use strict';

    $(function() {
        // Show/hide navigation on scroll
        var show = true;
        $(document).scroll(function() {
            var $navBottom = $('.c-main-nav__bottom');
            if ($(document).scrollTop() > 120 && show) {
                $navBottom.toggleClass('c-main-nav__bottom--hide');
                show = false;
            } else if ($(document).scrollTop() < 120 && !show) {
                $navBottom.toggleClass('c-main-nav__bottom--hide');
                show = true;
            }
        });

        // split sub navigation every 16 items
        var subnavLi = $('.c-main-nav__subnav--col2 li li');
        var maxItem = 16;
        for(var i = 0; i < subnavLi.length; i+=maxItem) {
            subnavLi.slice(i, i+maxItem).wrapAll("<ul class='c-main-nav__subnav--col generated'></li><li class='c-main-nav__subnav--list'>");
        }
        $('.c-main-nav__subnav--col.generated').unwrap().unwrap();
    });
}(jQuery);