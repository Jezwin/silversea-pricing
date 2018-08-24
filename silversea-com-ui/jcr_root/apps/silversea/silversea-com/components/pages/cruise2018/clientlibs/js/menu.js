$(".cruise-2018-menu a[href^=#]").on("click touchstart", function (e) {
    e.preventDefault();
    $target = $($(this).attr("href"));
    $([document.documentElement, document.body]).animate({
        scrollTop: $target.offset().top,
        duration: 500,
        easing: 'linear'
    });
});