$(function(){
    /***************************************************************************
     * Slider
     **************************************************************************/
    var prevArrowCustom ="<button type='button' data-role='none' class='slick-prev' aria-label='Previous' tabindex='0' role='button'><i class='fa fa-angle-left'></i></button>";
    var nextArrowCustom ="<button type='button' data-role='none' class='slick-next' aria-label='Next' tabindex='0' role='button'><i class='fa fa-angle-right'></i></button>";

    // On page load
    if($.viewportDetect() === "xs") {
        $(".c-slider:not(.c-slider--first-slide-only)").slick({
            prevArrow: prevArrowCustom,
            nextArrow: nextArrowCustom
        });
    } else {
        $(".c-slider:not(.c-slider--for, .c-slider--nav)").slick({
            prevArrow: prevArrowCustom,
            nextArrow: nextArrowCustom
        });
    }

    // On page resize
    $("body").on("trigger.viewport.changed", function() {
        if($.viewportDetect() === "xs") {
            // Disabled slider if the viewport is XS after page resizes
            $(".c-slider.c-slider--first-slide-only").slick("unslick");
        } else {
            // Enable slider if the viewport is not XS after page resize
            $(".c-slider.c-slider--first-slide-only").slick({
                prevArrow: prevArrowCustom,
                nextArrow: nextArrowCustom
            });
        }
    });
});