$(function () {
    function viewMore(e) {
        $('.cruise-2018-accordion').slideDown(200, 'linear');
        displayLazy();
    }

    function viewLess(e) {
        $('.cruise-2018-accordion').slideUp();
    }

    function displayLazy() {
        $('.cruise-2018-ship-dining-area .lazy').lazy();
    }

    function toggle(e) {
        e && e.preventDefault();
        const $this = $(this);
        if ($this.data('ssc-accordion') === 'show') {
            viewMore(e);
            $this.data('ssc-accordion', 'hide');
        } else if ($this.data('ssc-accordion') === 'hide') {
            viewLess(e);
            $this.data('ssc-accordion', 'show');
            $('.cruise-2018-ship-dining-area').get(0).scrollIntoView({block: 'end', behavior: 'smooth'});
        }
        $this.blur();
        $('.cruise-2018-accordion-button').toggle();
    }

    var createLineProgressBar = (function () {
        return function () {
            var $galleries = $(".activate-progressbar .slick-list");
            $galleries.each(function () {
                var $this = $(this);
                var widthSlider = $this.parent().find(".slick-dots").width();
                if (widthSlider > 768) {
                    widthSlider = 630;
                }
                var $dots = $this.parent().find("ul.slick-dots li");
                var liItem = $dots.length;
                var liWidth = Math.floor(widthSlider / liItem) - 1;
                $dots.css("width", liWidth + "px");

            });
        }
    })();

    function loadLazyImage($slider) {
        var $sliderActive = $slider.closest('.cruise-2018-ship-dining-area-list-slider').find('.slick-active');
        $sliderActive.find('.lazy').lazy();
        $sliderActive.prev().find('.lazy').lazy();
        $sliderActive.prev().prev().find('.lazy').lazy();
        $sliderActive.next().find('.lazy').lazy();
        $sliderActive.next().next().find('.lazy').lazy();
        setTimeout(function () {
            $sliderActive.find('.lazy').lazy();
        }, 50);
    }

    function initSlider() {
        const $slider = $(".cruise-2018-ship-dining-area-list-slider:visible");
        if ($slider) {
            try {
                $slider.slick("unslick");
            } catch (e) {

            }
            $slider.slick({
                centerMode: true,
                dots: true,
                draggable: true,
                infinite: true,
                slidesPerRow: 1,
                slidesToShow: 1,
                centerPadding: '40px'

            });
        }
        createLineProgressBar();
        $slider.on('afterChange', function (event, slick, currentSlide) {
            loadLazyImage($(this));
        });
    }

    $(".cruise-2018-ship-dining-area-btns a").on("click touchstart", toggle);
    initSlider();
    displayLazy();
});

