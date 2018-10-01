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

    $(".cruise-2018-itineraries-port-excursions-btn").on('click', toggle);

    $(".cruise-2018-itineraries-itinerary-row-container-with-excursion").on('click', toggleExcursions);

    $(".cruise-2018-itineraries-itinerary-row-container.cruise-2018-itineraries-itinerary-row-container-with-excursion").mouseover(function (e) {
        $(this).closest('.cruise-2018-itineraries-itinerary').prev().prev()
            .find('.cruise-2018-itineraries-itinerary-row-container').css('border-bottom-color', 'rgb(214, 5, 36)');
    }).mouseout(function(e){
        $(this).closest('.cruise-2018-itineraries-itinerary').prev().prev()
            .find('.cruise-2018-itineraries-itinerary-row-container').css('border-bottom-color', 'rgba(152, 152, 155, 0.5)');
    });

    $(document).ready(function () {
        if ($("body").hasClass("cruise") && $(".cruise-2018 ").length > 0 && $(".cruise-2018-itineraries-container").length > 0) {
            $(document).on('scroll touchmove gesturechange', loadPortsImage);
            loadPortsImage();
        }
    });

    function loadPortsImage() {
        var $itinerayContainer = $(".cruise-2018-itineraries-itinerary-row-thumbnail img");
        if ($itinerayContainer) {
            var isInsideTheView = isElementInView($itinerayContainer, true);
            if (isInsideTheView) {
                $(".cruise-2018-itineraries-itinerary-row-thumbnail img").lazy("lazy");
                $(document).off('scroll touchmove gesturechange', loadPortsImage);
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
