$(function(){
    $.fn.initGallery = function() {
        this.each(function() {
            var $component = $(this);

            // Activate sliders in sync
            var $slideFor = $component.find('.c-slider--for').slick({
                slidesToShow : 1,
                slidesToScroll : 1,
                asNavFor : $component.find('.c-slider--nav')
            });

            var $slideNav = $component.find('.c-slider--nav').slick({
                slidesToShow : 6,
                slidesToScroll : 1,
                asNavFor : $component.find('.c-slider--for'),
                focusOnSelect : true
            });
        });
    };

    $('.c-gallery--cc').initGallery();
});