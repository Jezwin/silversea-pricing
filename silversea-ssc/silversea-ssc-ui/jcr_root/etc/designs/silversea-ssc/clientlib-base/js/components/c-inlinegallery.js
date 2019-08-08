$(function() {
    /***************************************************************************
     * Activate slider with responsive setting
     **************************************************************************/
if($('.c-inlinegallery').length > 0) {

    $('.c-inlinegallery').each(function () {
        var element = $(this);
        createInlineGallerySlider(element);
    });


    window.createInlineGallerySlider = createInlineGallerySlider;


    function createInlineGallerySlider(element) {
        var numberItemsDesktop = parseInt(element.data("number-items-desktop"));
        var numberItemsTablet = parseInt(element.data("number-items-tablet"));
        var numberItemsMobile = parseInt(element.data("number-items-mobile"));
        numberOfItemDeskScroll = numberItemsDesktop;
        if (typeof forceBiggerWidth != "undefined") {
            if (forceBiggerWidth == "true") {
                numberOfItemDeskScroll = numberItemsDesktop - 2;
            }
        }
        //To keep already existing working
        numberOfItemTabScroll = numberItemsTablet - 2;
        numberItemsTablet = numberItemsTablet - 2;
        numberOfItemMobScroll = numberItemsMobile;

        if (numberOfItemDeskScroll <= 0) {
            numberOfItemDeskScroll = 1;
        }
        if (numberOfItemTabScroll <= 0) {
            numberOfItemTabScroll = 1;
        }

        if (numberItemsTablet <= 0) {
            numberItemsTablet = 1;
        }
        if (numberOfItemMobScroll <= 0) {
            numberOfItemMobScroll = 1;
        }

        var inlineGallerySettings = {
            arrows: true,
            dots: true,
            infinite: true,
            adaptiveHeight: true,
            slidesToShow: numberItemsDesktop,
            slidesToScroll: numberOfItemDeskScroll,
            responsive: [{
                breakpoint: 991,
                settings: {
                    slidesToShow: numberItemsTablet,
                    slidesToScroll: numberOfItemTabScroll,
                }
            }, {
                breakpoint: 768,
                settings: {
                    slidesToShow: 1,
                    slidesToScroll: 1,
                    arrows: false,
                    dots: false
                }
            }]
        };

        if ($(window).width() > 767 && typeof forceBiggerWidth != "undefined") {
            if (forceBiggerWidth == "true") {
                element.slick('unslick');
                var yo = element.find(' > div').last().html();
                element.find(' > div').last().toggleClass("toNotDisplay");
                element.prepend("<div>" + yo + "</div>");
            }
        }

        element.slick('unslick').slick(inlineGallerySettings).slick('slickFilter', ':not(.toNotDisplay)').on('afterChange', function(event, slick, currentSlide) {
            lazyLoadImage();
        });


        createLineProgressBar();
        lazyLoadImage();
        wig = $(window).width();
        $(window).resize(sscThrottled(createLineProgressBar));
        /***************************************************************************
         * Set Image in background (allow to use background-size : cover)
         **************************************************************************/
        var setBackgroundImage = (function setBackgroundImage() {
            $('.c-inline-gallery .o-img').each(function () {
                var $image = $(this);
                var src = $image.prop('currentSrc') || $image.prop('src');
                $image.closest('div').css('background-image', 'url(' + src + ')');
                $image.css('visibility', 'hidden');
            });

            return setBackgroundImage;
        })();

        $('body').on('trigger.viewport.changed', function () {
            setBackgroundImage();
        });

        function createLineProgressBar() {
            $(".c-inlinegallery").each(function () {
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
                var liWidth = ((widthSlider / liItem) - 1);

                if ($("body").hasClass("viewport-md") || $("body").hasClass("viewport-lg")) {
                    $(this).find("ul.slick-dots li").css("width", liWidth + "px");
                } else if ($("body").hasClass("viewport-sm")) {
                    $(this).find("ul.slick-dots li").css("width", liWidth + "px");
                }
            });

        };

        function lazyLoadImage() {
            $(".c-inlinegallery").each(function () {
                $(this).find(".slick-active").find(".lazy").lazy();
                $(this).find(".slick-slide.slick-active:first").prev().find(".lazy").lazy();
                $(this).find(".slick-slide.slick-active:last").next().find(".lazy").lazy();
                $(this).find(".slick-slide.slick-active:first").prev().prev().find(".lazy").lazy();
                $(this).find(".slick-slide.slick-active:last").next().next().find(".lazy").lazy();
                $(this).find(".slick-slide>slick-active:first").prev().prev().prev().find(".lazy").lazy();
                $(this).find(".slick-slide.slick-active:last").next().next().next().find(".lazy").lazy();
                setTimeout(function() {
                    $(this).find(".slick-active").find(".lazy").lazy();

                }, 500);

            });

        };
    };//createInlineGallerSlider
}
});