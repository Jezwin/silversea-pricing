(function () {
    var isSlider5LessonVisible = $(".slider-5-lessons").length > 0;
    var isMobile = $(window).width() < 768;
    var $mobileSlider = $(".slider-5-lessons-mobile"), $desktopSlider = $(".slider-5-lessons-desktop");
    if (isSlider5LessonVisible) {
        isMobile && $desktopSlider.remove();
        !isMobile && $mobileSlider.remove();
    }
});