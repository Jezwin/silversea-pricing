var handler = function () {
    if ($('.mediaMixSliderCustom').length > 0) {
        $(".slider-container").each(function () {
            var slider = $(this).find('.mediaMixSliderCustom');
            var offset = $(this).outerWidth();
            slider.css("width", window.innerWidth + 'px');
            slider.css("left", -((window.innerWidth - offset) / 2) + 'px');
            createLineProgressBar();

            slider.on('afterChange', function (event, slick, currentSlide) {
                // Call lazy load for 2 previous and 2 next slides
                loadLazyImage($(this));
            });

            loadLazyImage(slider);

        });
    }
};

function loadLazyImage($slider) {

    var activeSlide = $slider.find("div.slick-active");
    // call lazy loading for 2 previous and next images active
    activeSlide.prev().find("img:eq(0)").lazy({attribute: "data-lazy"})
    activeSlide.prev().prev().find("img:eq(0)").lazy({attribute: "data-lazy"})
    activeSlide.prev().prev().prev().find("img:eq(0)").lazy({attribute: "data-lazy"})

    activeSlide.next().find("img:eq(0)").lazy({attribute: "data-lazy"})
    activeSlide.next().next().find("img:eq(0)").lazy({attribute: "data-lazy"})
    activeSlide.next().next().next().find("img:eq(0)").lazy({attribute: "data-lazy"})

};//loadLazyImage


var createLineProgressBar = function () {
    $(".mediaMixSliderCustom").each(function () {

        var widthSlider = $(this).find(".slick-list").width();
        if ($("body").hasClass("viewport-md") || $("body").hasClass("viewport-lg")) {
            if (widthSlider > 930) {
                widthSlider = 930;
            }
        } else if ($("body").hasClass("viewport-sm")) {
            if (widthSlider > 768) {
                widthSlider = 630;
            }
        }
        var liItem = $(this).find("ul.slick-dots li").length;
        var liWidth = ((widthSlider / liItem) - 1) * 0.7;

        $(this).find("ul.slick-dots li").css("width", liWidth + "px");

    });

};

var createMediaMixSlider = function () {


    var inlineGallerySettings = {
        arrows: true,
        infinite: true,
        centerMode: true,
        variableWidth: true,
        dots: true
    };

    $("[id^='mediaMixSliderCustom-']").slick('unslick').slick(inlineGallerySettings).slick('slickFilter', ':not(.toNotDisplay)');

};



$(function () {
    if ($('.mediaMixSliderCustom').length > 0) {

        window.onresize = handler;

        //creationOfGallery
        createMediaMixSlider();

        //initialization
        handler();
        createLineProgressBar();



    }

});
