$(function () {
    /***************************************************************************
     * Activate slider with responsive setting
     **************************************************************************/

    function createTeaserBigGallery() {
        var sliderSettings = {
            prevArrow: "<button type=\"button\" class=\"slick-prev teaser_big__slick-prev\">Previous</button>",
            nextArrow: "<button type=\"button\" class=\"slick-next teaser_big__slick-next\">Next</button>",
            arrows: true,
            dots: false,
            infinite: true,
            adaptiveHeight: false,
            slidesToShow: 1,
            slidesToScroll: 1,
            responsive: [{
                breakpoint: 600,
                settings: {
                    adaptiveHeight: false,
                    slidesToShow: 1,
                    slidesToScroll: 1,
                    arrows: false,
                    dots: true
                }
            }]
        };

        $('.c-teaserbig.activate-progressbar').slick('unslick').slick(sliderSettings).slick('slickFilter', ':not(.toNotDisplay)');
        var setBackgroundImage = (function setBackgroundImage() {
            $('.activate-progressbar .o-img').each(function () {
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

        var setBackgroundHeight = (function () {
            return function () {
                $bg = $(".c-teaserbig__bg");
                $bg.css("height", $bg.closest(".c-teaserbig__container").height() + 50 + "px")
            }
        })();

        var createLineProgressBarTeaserBig = (function () {
            return function () {
                var $galleries = $(".activate-progressbar .slick-list");
                $galleries.each(function () {
                    var $this = $(this);
                    var widthSlider = $this.parent().find(".slick-dots").width();
                    var $body = $("body");
                    if ($body.hasClass("viewport-md") || $body.hasClass("viewport-lg")) {
                        if (widthSlider > 930) {
                            widthSlider = 930;
                        }
                    } else if ($body.hasClass("viewport-sm")) {
                        if (widthSlider > 768) {
                            widthSlider = 630;
                        }
                    }
                    var $dots = $this.parent().find("ul.slick-dots li");
                    var liItem = $dots.length;
                    var liWidth = Math.floor(widthSlider / liItem) - 1;
                    $dots.css("width", liWidth + "px");

                });
            }
        })();
        setBackgroundHeight();
        createLineProgressBarTeaserBig();
        $(window).resize(sscThrottled(function () {
            setBackgroundHeight();
            createLineProgressBarTeaserBig();
        }));
        $(document).on('click', '.c-teaserbig__full-gallery', function (event) {
            event.preventDefault();
            var $c = $(".c-teaserbig__container .slick-current a");
            $c.click();
        });
    }

    createTeaserBigGallery();

});
