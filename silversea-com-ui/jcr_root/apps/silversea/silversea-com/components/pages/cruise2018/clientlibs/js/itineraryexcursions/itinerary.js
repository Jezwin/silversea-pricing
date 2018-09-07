$(function () {

    function toggle(e) {
        e && e.preventDefault();
        var $this = $(this);
        if ($this.data('ssc-accordion') === 'show') {
            $this.parent().find('.cruise-2018-excursions-accordion').slideDown(200, 'linear');
            $this.data('ssc-accordion', 'hide');
        } else if ($this.data('ssc-accordion') === 'hide') {
            $this.parent().find('.cruise-2018-excursions-accordion').slideUp();
            $this.data('ssc-accordion', 'show');
        }
        $this.blur();
        $this.find('.cruise-2018-excursions-accordion-button').toggle();
    }

    function toggleExcursions(e) {
        e && e.preventDefault();
        var $this = $(this);
        var $arrow = $this.find(".cruise-2018-itineraries-itinerary-row-date-arrow");
        if ($arrow.data('ssc-accordion') === 'show') {
            $this.parent().find('.cruise-2018-itineraries-excursions-accordion').slideDown(200, 'linear');
            $arrow.data('ssc-accordion', 'hide');
            $this.css("border-bottom-color", "transparent");
        } else if ($arrow.data('ssc-accordion') === 'hide') {
            $this.parent().find('.cruise-2018-itineraries-excursions-accordion').slideUp();
            $arrow.data('ssc-accordion', 'show');
            $this.css("border-bottom-color", "rgba(152, 152, 155, 0.5)");
        }
        $arrow.blur();
        $arrow.find('i').toggle();
    }

    $(".cruise-2018-itineraries-port-excursions-btn").on('click touchstart', toggle);

    $(".cruise-2018-itineraries-itinerary-row-container-with-excursion").on('click touchstart', toggleExcursions);

    $(document).ready(function () {
        if ($("body").hasClass("cruise") && $(".cruise-2018 ").length > 0 && $(".cruise-2018-itineraries-container").length > 0) {
            window.slicePortImages = 0;
            $(document).on('scroll touchmove gesturechange', loadPortsImage);
            loadPortsImage();
        }
    });

    function loadPortsImage() {
        var $itinerayContainer = $(".cruise-2018-itineraries-title");
        if ($itinerayContainer) {
            var isInsideTheView =  isElementInView($itinerayContainer, true);
            var isLastElementInTheView = isElementInView(window.$lastPortImageShowed, true);
            if ((isInsideTheView || isLastElementInTheView) && window.slicePortImages >= 0) {
                var $elementsToShow = $(".cruise-2018-itineraries-itinerary-row-thumbnail img").slice(window.slicePortImages, window.slicePortImages + 5).lazy("lazy");
                if ($elementsToShow.length > 0) {
                    window.slicePortImages = window.slicePortImages + 5;
                    window.$lastPortImageShowed = $(".cruise-2018-itineraries-itinerary-row-thumbnail img").slice(window.slicePortImages - 1, window.slicePortImages);
                } else {
                    window.slicePortImages = -1;
                    window.$lastPortImageShowed = null;
                }
            }
        }
    };//loadPortsImage

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
    };//isElementInView
});
