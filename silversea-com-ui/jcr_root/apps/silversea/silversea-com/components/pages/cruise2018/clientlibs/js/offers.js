$(function () {
    $(".offers .offers-btns a").on("click touchstart", toggle);

    function toggle(e) {
        e && e.preventDefault();
        e && e.stopPropagation();
        var $this = $(this);
        if ($this.data('ssc-accordion') === 'show') {
            $(".offers .offers-hide").slideDown('slow');
            $this.data('ssc-accordion', 'hide');
            //$('.offers').get(0).scrollIntoView({block: 'start', behavior: 'smooth'});
        } else if ($this.data('ssc-accordion') === 'hide') {
            $(".offers .offers-hide").slideUp('slow');
            $this.data('ssc-accordion', 'show');
           // $('.offers').get(0).scrollIntoView({block: 'start', behavior: 'smooth'});
        }
        $this.blur();
        $('.offers .offers-accordion-button').toggle();
    }
    
    $(".offers .open-lightbox-offer").click(function () {
        $(document).on('shown.bs.modal', function (e) {
            var $modalBody = $(".modal.lightbox .modal-body");
            if ($modalBody.hasClass("cruise-2018-offers")) {
                $(".modal.lightbox .lightbox-close").addClass("lightbox-close-offer");
            }
        });
        $(document).on('hidden.bs.modal', function (e) {
            var $modalClose = $(".modal.lightbox .lightbox-close");
            if ($modalClose.hasClass("lightbox-close-offer")) {
                $(".modal.lightbox .lightbox-close").removeClass("lightbox-close-offer");
            }
        });
    });

    $(document).ready(function () {
        if ($("body").hasClass("cruise") && $(".cruise-2018 ").length > 0 && $(".offers").length > 0) {
            if ($(window).width() < 768) {
                initSliderOffer();
            }
        }
    });


    var createLineProgressBar = (function () {
        return function () {
            var $galleries = $(".offers .offer-slider .slick-list");
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

    function initSliderOffer() {
        var $slider = $(".offers .offer-slider");
        if ($slider) {
            $slider.slick({
                centerMode: true,
                dots: true,
                draggable: true,
                infinite: true,
                slidesPerRow: 1,
                slidesToShow: 1,
                centerPadding: '30px'
            });
        }
        createLineProgressBar();
        loadLazyImage($slider);
        $(".cruise-2018-overview-big-pic").hide();
        $slider.on('afterChange', function (event, slick, currentSlide) {
            loadLazyImage($(this));
        });
    }
});