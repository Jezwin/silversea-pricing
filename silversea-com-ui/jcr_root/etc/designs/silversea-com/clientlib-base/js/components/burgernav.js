+function ($) {
    'use strict';
    
    alert(1);

    $(".c-burger-menu").click(function () {
        alert(2);
        $(this).toggleClass("is-active");
    });
}(jQuery);