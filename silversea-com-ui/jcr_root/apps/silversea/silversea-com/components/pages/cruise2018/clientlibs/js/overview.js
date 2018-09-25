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
    $(".cruise-2018-overview-big-pic").replaceWith($(".ow-slider"));

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


    $('.open-video-slider').on('click', function (e) {
        e && e.preventDefault();
        e && e.stopPropagation();
        $('body').addClass('modal-open');
        // HTML layout
        var $modalContent = '<div class="modal-content modal-content--transparent modal-content--single">'
            + '<div class="modal-header"><button class="close c-btn--close" type="button" data-dismiss="modal" aria-label="Close"></button></div>'
            + '<div class="modal-body automatic-modal-body">'
             + '<div class="cruise-video-lightbox">'
                +'<div class="ratio">'
                    +'<div class="video-itinerary" id="currentIdNode" data-video-asset="assetPath" data-video-autoplay="1"></div>'
                +'</div>'
            +'</div>'+
            '</div>' + '</div>';

        var $link = $(this);
        var currentIdNode = $link.data("current-id-node");
        var assetPath = $link.data("asset-path");

        $modalContent = $modalContent.replace("currentIdNode", currentIdNode);
        $modalContent = $modalContent.replace("assetPath", assetPath);

        // Activate Modal
        $($(this).data('target')).modal('show');

        // Append image inside Modal
        $('.modal').on('shown.bs.modal', function (e) {
            $(this).find('.modal-dialog').empty().append($modalContent);
            $(".video-itinerary").initVideo();
        });

        $(document).on('hide.bs.modal', function (e) {
            if ($("body").hasClass("cruise") && $(".cruise-2018").length > 0 && $(".modal.lightbox .cruise-video-lightbox").length > 0) {
                var $video = $(".modal.lightbox .cruise-video-lightbox").find('.s7container');
                $video.find('.s7playpausebutton[selected="false"]').trigger('click');
            }
        });
    });

});

window.voyagePdfPrintClicked = false;
