function replaceInlineGallery(heroBannerId, inlineGalleryID) {

    $inlineGalleryId = $(inlineGalleryID);
    if($(inlineGalleryID).length > 0){
	    $( heroBannerId + ' .coverImage').after($inlineGalleryId);
	    $( heroBannerId + ' .coverImage').addClass("hidden-xs");
	    if( $( heroBannerId + ' .c-slider--full-width').length > 0){
	    	$( heroBannerId + ' .c-slider--full-width').addClass("hidden-xs");
	    }
	    $inlineGalleryId.addClass("inside-hero-banner");

	    $inlineGalleryId.addClass("hidden-md hiddem-sm hidden-lg");
	    $( heroBannerId + ' .coverImage').css("display", "inline-block");
	    $( heroBannerId + ' a.automatic-gallery-modal:not(.video-link)').removeClass("automatic-gallery-modal");
	    $( heroBannerId + ' a.automatic-gallery-modal.video-link').css("left", "50%");
	    if(inlineGalleryID.indexOf('c-inlinegallery-') > -1){
	    	setTimeout(function () {
		    	$inlineGalleryId.slick('unslick');
		    	$inlineGalleryId.slick();
	    	}, 10);
	    }
	    $( heroBannerId + '').delegate("a.automatic-gallery-modal.video-link", "click", function () {
	        var intervalGallery = setInterval(function () {
	            var gallery = $(".modal .modal-content--gallery .c-gallery");
	            var slickElement = $(".modal .modal-content--gallery .c-gallery .c-slider");
	            if (gallery != null && gallery.length > 0 && slickElement != null && slickElement.slick != null && slickElement.hasClass('slick-initialized')) {
	                $(".modal .modal-content--gallery .c-gallery .c-slider").slick("slickSetOption", "swipe", false, false);
	                setTimeout(function () {
	                    $(".modal .modal-content--gallery .c-gallery .c-slider").slick("slickSetOption", "swipe", false, false);
	                }, 500);
	                clearInterval(intervalGallery);
	            }
	        }, 1000);
	    });
    }
}

$(function () {
    $('.c-hero-banner-homepage .video-link-dam').on('click', function (e) {
        e.preventDefault();
        var $link = $(this),
            ajaxContentPath = $link.attr('href'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);

        // Activate Modal
        $modalContent.modal('show');

        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function (e) {
            var $modal = $(this);
            $modal.off('shown.bs.modal');

            // Append html response inside modal
            $modal.find('.modal-dialog').load(ajaxContentPath, function () {
                // init Video after modal load
                $(this).find('.c-video').initVideo();
            });
        });
    });
    $('.c-hero-banner-homepage .c-hero-banner-homepage__scrolldown').on('click', function (e) {
        e.stopPropagation();
        var target = $(this).closest(".heroBannerDestinationND").next().offset().top;
        var height = $(this).closest(".c-hero-banner-homepage").height() != null ? $(this).closest(".c-hero-banner-homepage").height() : $(this).closest(".heroBannerDestinationND").height();
        var speed = 750;
        $('html, body').animate({
            scrollTop: target - 70
        }, speed);
    });

    createLineProgressBar();
    $('.c-slider .coverImage').addClass('insideSlider');
});

$(function () {

    /*
    * Make all hero banner inside the page cliccable if and only if the hero banner has 1 link inside
    * the parbase. Compatible with all smartButton old and new
    * */
    if ($(window).width() > 991) {
        $(".heroBannerDestinationND.parbase").each(function() {
            var $heroBanner = $(this);
            var enableClickableFeature = $heroBanner.find(".hero-banner-clickable-true").length > 0;
            var $parbaseInsideHeroBanner = $heroBanner.find(".parbase");
            var isOneButton = $parbaseInsideHeroBanner.length == 1;
            if (isOneButton && enableClickableFeature) {
                var href = null, target = null;
                var $aList = $parbaseInsideHeroBanner.find("a");
                $aList.each(function(){
                    var $a = $(this);
                    //check if desktop or mobile
                    var isVisibleLink = $a.css('display') != 'none';
                    if (isVisibleLink) {
                        href = $a.attr("href");
                        target = $a.attr("target");
                    }
                });
                var canAppendElementInsideDOM = href != null;
                if (canAppendElementInsideDOM) {
                    var $aToAdd = $("<a class='herobannerCliccable' alt='heroBannerCliccable' href='" + href + "'></a>");
                    if (target != null) {
                        $aToAdd.attr("target", target);
                    }
                    $heroBanner.append($aToAdd);
                }
            }
        });
    }


    $('.c-hero-banner-destination .video-link-dam').on('click', function (e) {
        e.preventDefault();
        var $link = $(this),
            ajaxContentPath = $link.attr('href'),
            modalTarget = $link.data('target'),
            $modalContent = $(modalTarget);

        // Activate Modal
        $modalContent.modal('show');

        // Wait for modal opening
        $modalContent.on('shown.bs.modal', function (e) {
            var $modal = $(this);
            $modal.off('shown.bs.modal');

            // Append html response inside modal
            $modal.find('.modal-dialog').load(ajaxContentPath, function () {
                // init Video after modal load
                $(this).find('.c-video').initVideo();
            });
        });
    });
    $('.c-hero-banner-destination .c-hero-banner-destination__scrolldown').on('click', function (e) {
        e.stopPropagation();
        var target = $(this).closest(".heroBannerDestinationND").next().offset().top;
        var height = $(this).closest(".c-hero-banner-destination").height() != null ? $(this).closest(".c-hero-banner-destination").height() : $(this).closest(".heroBannerDestinationND").height();
        var speed = 750;
        $('html, body').animate({
            scrollTop: target - 70
        }, speed);
    });

    createLineProgressBar();
});
function createLineProgressBar() {

    var widthSlider = $(window).width();
    var $dots = $(".c-hero-banner-homepage ul.slick-dots li");
    var liItem = $dots.length;
    var liWidth = ((widthSlider / liItem) );
    $dots.css("width", liWidth + "px");
}
$(window).resize(sscThrottled(createLineProgressBar));
