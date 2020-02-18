
$(function () {
    $(document).ready(function () {
        adjustHeight("cruise-2018-suites-fares-description-title");
        var isCorrectPage = ($("body").hasClass("cruise") || $("body").hasClass("combocruise")) && $(".cruise-2018-suites-fares").length > 0;

        if (isCorrectPage) {
            $(document).on('scroll touchmove gesturechange', loadSuitesImage);
            $(".onclick-show-more-less-benefits").on("click", onClickShowMoreLessBenefits);
            loadSuitesImage();
        }
    });

    function onClickShowMoreLessBenefits(e) {
        e.preventDefault();
        e.stopPropagation();
        var $span = $(this);
        var isShowMore = $span.hasClass("benefits-show-more");
        if (isShowMore) {
            showLessBenefits($span);
        } else {
            showMoreBenefits($span);
        }
    };//onClickShowMoreLessBenefits

    function hideBenefitsGreaterThan6() {
        $(".cruise-2018-suites-fares-included-ul li.to-hide").addClass("hidden-xs");
    };//hideBenefitsGreaterThan6

    function showBenefitsGreaterThan6() {
        $(".cruise-2018-suites-fares-included-ul li.hidden-xs").addClass("to-hide");
        $(".cruise-2018-suites-fares-included-ul li.hidden-xs").removeClass("hidden-xs");
    };//showBenefitsGreaterThan6

    function showLessBenefits($spanToHide) {
        $spanToHide.hide();
        $(".cruise-2018-suites-fares-included-ul .benefits-show-less").show();
        showBenefitsGreaterThan6();
    };//showLessBenefits

    function showMoreBenefits($spanToHide) {
        $spanToHide.hide();
        $(".cruise-2018-suites-fares-included-ul .benefits-show-more").show();
        hideBenefitsGreaterThan6();
    };//showMoreBenefits

    function loadSuitesImage() {
        var $itinerayContainer = $(".cruise-2018-suites-fares-image img");
        if ($itinerayContainer) {
            var isInsideTheView = isElementInView($itinerayContainer, true);
            if (isInsideTheView) {
                $(".cruise-2018-suites-fares-image img").lazy("lazy");
                $(document).off('scroll touchmove gesturechange', loadSuitesImage);
            }
        }
    }//loadPortsImage

    function isElementInView(element, fullyInView) {
        if (element != null && element.length > 0) {
            var isVisible = false;
            for (var i = 0; i < element.length; i++) {
                var item = element[i];
                var pageTop = $(window).scrollTop();
                var pageBottom = pageTop + $(window).height();
                if ($(item).offset() != null) {
                    var elementTop = $(item).offset().top;
                    var elementBottom = elementTop + $(item).height();
                    if (fullyInView === true) {
                        isVisible = ((pageTop < elementTop) && (pageBottom > elementBottom));
                    } else {
                        isVisible = ((elementTop <= pageBottom) && (elementBottom >= pageTop));
                    }
                    if (isVisible && fullyInView) {
                        return isVisible;
                    }
                }
            }
        }
        return isVisible;
    }//isElementInView
});

function adjustHeight(div){
	var max = -1;
    $("."+div).each(function() {
        var h = $(this).height(); 
        max = h > max ? h : max;
    });
    $("."+div).css('height',max);
}