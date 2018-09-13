$(function () {
    $(document).ready(function () {
        if ($("body").hasClass("cruise") && $(".cruise-2018 ").length > 0 && $(".cruise-2018-suites-fares").length > 0) {
            $(document).on('scroll touchmove gesturechange', loadSuitesImage);
            loadSuitesImage();
        }
    });

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