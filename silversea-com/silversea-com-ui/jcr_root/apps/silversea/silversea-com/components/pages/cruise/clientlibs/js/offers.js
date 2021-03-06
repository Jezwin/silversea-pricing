$(function () {
    $(".offers .offers-btns a").on("click touchstart", toggle);

    function toggle(e) {
        e && e.preventDefault();
        e && e.stopPropagation();
        var $this = $(this);
        var offset = 130;
        if ($this.data('ssc-accordion') === 'show') {
            $(".offers .offers-hide").slideDown('slow');
            $this.data('ssc-accordion', 'hide');
            offset = 130;
        } else if ($this.data('ssc-accordion') === 'hide') {
            $(".offers .offers-hide").slideUp('slow');
            $this.data('ssc-accordion', 'show');
            offset = 100;
        }
        $this.blur();
        $('.offers .offers-accordion-button').toggle();
        $([document.documentElement, document.body]).animate({
            scrollTop: $(".offers-title").position().top - $(".row-menu-overview").height() - offset,
            duration: 500,
            easing: 'linear'
        });
    }

    $(document).ready(function () {
        if ($("body").hasClass("cruise") && $(".cruise-2018 ").length > 0 && $(".offers").length > 0) {
           // if ($(window).width() < 768) {
                initSliderOffer();
           //}
        }
    });


    var createLineProgressBar = (function () {
        return function () {
            var $galleries = $(".offers .offer-slider .slick-list");
            $galleries.each(function () {
                var $this = $(this);
                var widthSlider = $this.parent().find(".slick-dots").width();

                var $dots = $this.parent().find("ul.slick-dots li");
                var liItem = $dots.length;
                var liWidth = Math.floor(widthSlider / liItem) - 1;
                $dots.css("width", liWidth + "px");

            });
        }
    })();

    function loadLazyImage($slider) {
        var $sliderActive = $slider.closest('.offers .offer-slider').find('.slick-active');
        $sliderActive.find('.lazy').lazy();
        $sliderActive.prev().find('.lazy').lazy();
        $sliderActive.prev().prev().find('.lazy').lazy();
        $sliderActive.next().find('.lazy').lazy();
        $sliderActive.next().next().find('.lazy').lazy();
        setTimeout(function () {
            $sliderActive.find('.lazy').lazy();
        }, 50);
    }
    wofferC= $(window).width();

    function initSliderOffer() {
        var $slider = $(".offers .offer-slider");
        if ($slider) {
            $slider.slick({
                dots: true,
                draggable: true,
                infinite: true,
                slidesPerRow: 2,
                slidesToShow: 2,
                responsive : [  {
                    breakpoint : 768,
                    settings : {
                        slidesToShow : 1,
                        slidesToScroll : 1
                    }
                } ]
               // centerPadding: '30px'
            });
        }
        createLineProgressBar();
        loadLazyImage($slider);
        $(".offers .offer-slider").css("visibility", "visible");
        $slider.on('afterChange', function (event, slick, currentSlide) {
            loadLazyImage($(this));
        });
        $(window).resize(sscThrottled(function () {
            if ($(window).width() == wofferC) return;
            wofferC = $(window).width();
            createLineProgressBar();
        }));
    }
});