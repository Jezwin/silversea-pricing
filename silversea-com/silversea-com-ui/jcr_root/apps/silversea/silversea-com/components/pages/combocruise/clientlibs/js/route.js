$(function () {

    var activeRoute = (function ($route) {
        $(".route-content.route-active").removeClass("route-active");
        $route.find('.route-content').addClass("route-active");
    });

    var addSpinnerOnItineraryDetail = (function () {
        $(".cruise2018-itinerarydetail-block").addClass("cruise2018-itinerarydetail-block-loading");
        $(".cruise-2018-itineraries-spinner").addClass("cruise-2018-itineraries-spinner-loading");
    });

    var removeSpinnerOnItineraryDetail = (function () {
        $(".cruise2018-itinerarydetail-block").removeClass("cruise2018-itinerarydetail-block-loading");
        $(".cruise-2018-itineraries-spinner").removeClass("cruise-2018-itineraries-spinner-loading");
    });

    var onClickSlideArrowMobile = function () {
        var $route = $(".route-slider .slick-current.slick-active"),
            url = $route.attr("href");
        getSegment($route, url);
    };//onClickSlideArrowMobile

    function lowerCaseContentPort(thisEl){
        var currentPort = $(thisEl).html();
        currentPort = currentPort.toLowerCase();
        $(thisEl).html(currentPort);
    }

    function getSegment($route, url) {
        activeRoute($route);
        addSpinnerOnItineraryDetail();
        $.get(url, function (data, status) {
            $(".cruise2018-itinerarydetail-block").replaceWith(data);
            //can't find solution with delegate or on()
            $(".cruise-2018-itineraries-itinerary-row-thumbnail img").lazy("lazy");
            $('.cruise-2018-itineraries-itinerary-row-text-name').each(function () {
                lowerCaseContentPort(this);
            });
        }).fail(function () {
            removeSpinnerOnItineraryDetail();
        })
    };//getSegment

    var onClickChangeRoute = (function (e) {
        e && e.preventDefault();
        e && e.stopPropagation();
        var $route = $(this),
            url = $route.attr("href");
        getSegment($route, url);
    });

    var iAmInComboCruisePage = $("body").hasClass("combocruise"),
        iHaveRoutesDiv = $(".change-route-onclick").length > 0,
        isMobile = $(window).width() < 768;

    if (iAmInComboCruisePage && iHaveRoutesDiv) {
        $(".change-route-onclick").on("click", onClickChangeRoute);
        if (isMobile) {
            $(".route-slider").on('afterChange', onClickSlideArrowMobile);
        }
    }
});