function pdfInit(uri, countryCode, ccpt) {
    var printWin = {};
    $('#cruise-2018-pdf-print').on('click', function (e) {
        e.preventDefault();
        if (!window.voyagePdfPrintClicked) {
            window.voyagePdfPrintClicked = true;
            $.ajax({
                url: uri + '.rendition.print.' + countryCode + ccpt + '.pdf',
                beforeSend: function () {
                    $('.cruise-2018-pdf-print-link').toggle();
                    printWin = window.open('', '_blank');
                },
                success: function () {
                    $('.cruise-2018-pdf-print-link').toggle();
                    printWin.location = uri + '.rendition.print.' + countryCode + ccpt + '.pdf';
                    printWin.focus();
                    window.voyagePdfPrintClicked = false;
                }
            });
        }
    });
}

function socialInit(shareText) {
    $('#cruise-2018-facebook').jsSocials({
        showLabel: false,
        showCount: false,
        shareIn: "popup",
        text: shareText,
        shares: ["facebook"]
    });
}

function createOverviewGallerySlider() {
    var $mainSlider = $('.main-slider.ow-slider').slick({
        slidesToShow: 1,
        slidesToScroll: 1
    });
    // Init video on click
    $mainSlider.find('.video-link').on('click', function (e) {
        e.preventDefault();
        e.stopPropagation();
        $(this).next('.c-video').initVideo();
    });

    $mainSlider.on('beforeChange', function (event, slick, currentSlide, nextSlide) {
        var $slider = $(this);
        // Kill video if current slide contains video
        var $video = $slider.find('.slick-current .c-video');
        $video.find('.s7playpausebutton[selected="false"]').trigger('click');
        $video.attr('class', 'c-video').empty();
        // Call lazy load for 2 previous and 2 next slides
    }).on('afterChange', function (event, slick, currentSlide) {
        // Call lazy load for 2 previous and 2 next slides
        loadLazyImageOverview($(this));
    });

    loadLazyImageOverview($mainSlider);
}

function loadLazyImageOverview($slider) {
    var $sliderActive = $slider.find('.slick-active');
    var $main = $(".main-slider.ow-slider");
    $main.slick("slickSetOption", "draggable", true, false);
    $main.slick("slickSetOption", "swipe", true, false);
    // call lazy loading for active image
    $sliderActive.find('.lazy').lazy();

    // call lazy loading for 2 previous and next images active
    $sliderActive.prev().find('.lazy').lazy();
    $sliderActive.prev().prev().find('.lazy').lazy();
    $sliderActive.next().find('.lazy').lazy();
    $sliderActive.next().next().find('.lazy').lazy();
    setTimeout(function () {
        $sliderActive.find('.lazy').lazy();
    }, 50);

}

$(function () {
    if ($(window).width() < 768) {
        createOverviewGallerySlider();
    }
    $("a.coolanchorminussmall").click(function () {
        //check if it has a hash (i.e. if it's an anchor link)
        if (this.hash) {
            var hash = this.hash.substr(1);
            var $toElement = $("[id=" + hash + "]");
            var toPosition = $toElement.offset().top - 120;
            //scroll to element
            $("body,html").animate({
                scrollTop: toPosition
            }, 1000);
            return false;
        }
    });
});

window.voyagePdfPrintClicked = false;
