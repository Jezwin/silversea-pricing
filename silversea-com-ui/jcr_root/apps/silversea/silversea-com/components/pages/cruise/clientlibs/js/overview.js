function pdfInit(uri, countryCode, ccpt) {
    var printWin = {};
    $('#cruise-2018-pdf-print').on('click', function (e) {
        e.preventDefault();
        $.ajax({
            url: uri + '.rendition.print.' + countryCode + ccpt + '.pdf',
            beforeSend: function () {
                $('.cruise-2018-pdf-print-link').toggle();
                printWin = window.open('', '_blank');
            },
            complete: function () {
                $('.cruise-2018-pdf-print-link').toggle();
            },
            success: function () {
                printWin.location = uri + '.rendition.print.' + countryCode + ccpt + '.pdf';
                printWin.focus();
            }
        });
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

//change bottom vule voyage number depending of the height of pre price
function changeBottomValueVoyageNumber() {
    var $quotePriceBonusDiv = $(".cruise-2018-overview-quote-price-bonus"),
        isVisiblequotePriceBonus = $quotePriceBonusDiv.length > 0;
    if (isVisiblequotePriceBonus) {
        var heightDiv = $quotePriceBonusDiv.height(),
            $voyageCodeDiv = $(".cruise-2018-overview-quote-icons"),
            canChangeBottomValue = heightDiv > 36;
        if (canChangeBottomValue) {
            var pxBottom = (heightDiv / 36) * 10;
            $voyageCodeDiv.css("bottom", "-" + pxBottom + "px")
        }
    }
};//changeBottomValueVoyageNumber



$(function () {
    if ($(window).width() > 990) {
        changeBottomValueVoyageNumber();
    }

    if ($(window).width() < 768) {
        createOverviewGallerySlider();
    }
    $("a.coolanchorminussmall").click(function (e) {
        //check if it has a hash (i.e. if it's an anchor link)
        if (this.hash) {
            var hash = this.hash.substr(1);
            var $toElement = $("[id=" + hash + "]");
            var $body = $('body');
            var number = window.innerWidth < 768 ? 65 : 100;
            var toPosition = $toElement.position().top - number;

            //scroll to element
            $("body,html").animate({
                scrollTop: toPosition
            }, 1500);

            $body.focus();
            history.replaceState(null, null, $(this).attr("href"));
            return false;
        }
    });


    $('.open-video-slider').on('click', function (e) {
        e && e.preventDefault();
        e && e.stopPropagation();
        $('body').addClass('modal-open');
        // HTML layout
        var $modalContent =
            '<div class="modal-body automatic-modal-body">'
            + '<div class="cruise-video-lightbox">'
            + '<div class="ratio">'
            + '<div class="video-itinerary" id="currentIdNode" data-video-asset="assetPath" data-video-autoplay="1"></div>'
            + '</div>'
            + '</div>' +
            '</div>';

        var $link = $(this);
        var currentIdNode = $link.data("current-id-node");
        var assetPath = $link.data("asset-path");

        $modalContent = $modalContent.replace("currentIdNode", currentIdNode);
        $modalContent = $modalContent.replace("assetPath", assetPath);

        // Activate Modal
        $($(this).data('target')).modal('show');

        // Append image inside Modal
        $('.modal.lightbox').on('shown.bs.modal', createVideo);

        $(document).on('hide.bs.modal', destroyVideo);

        function createVideo(e) {
            $(this).find(".modal-dialog").addClass("custom-lightbox-width");
            $(this).find(".modal-dialog").addClass("lightbox-width-1200");
            $(this).find('.modal-dialog .modal-content').html($modalContent);
            $(this).find('.lightbox-close').addClass("lightbox-close-1200");
            $(".video-itinerary").initVideo();
        }

        function destroyVideo(e) {
            if ($("body").hasClass("cruise") && $(".cruise-2018").length > 0) {
                var $video = $(".modal.lightbox .cruise-video-lightbox").find('.s7container');
                $video.find('.s7playpausebutton[selected="false"]').trigger('click');
                $('.modal.lightbox').off('shown.bs.modal', createVideo);
                $(document).off('hide.bs.modal', destroyVideo);
                $(this).find('.lightbox-close').removeClass("lightbox-close-1200");
            }
        }
    });

});

window.voyagePdfPrintClicked = false;
