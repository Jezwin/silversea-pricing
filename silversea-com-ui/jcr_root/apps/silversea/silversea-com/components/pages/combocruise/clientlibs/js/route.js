$(function () {

    var activeRoute = (function ($route) {
        $(".route-content.route-active").removeClass("route-active");
        $route.parent().addClass("route-active");
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
        $(".slick-current.slick-active .change-route-onclick").click();
    };//onClickSlideArrowMobile

    /*
    * Make POST ajax call to RouteUse and get the cruise model and itineray
     */
    var onClickChangeRoute = (function (e) {
        e && e.preventDefault();
        e && e.stopPropagation();
        var $route = $(this),
            url = $route.attr("href");
        activeRoute($route);
        addSpinnerOnItineraryDetail();
        $.get(url, function (data, status) {
            $(".cruise2018-itinerarydetail-block").replaceWith(data);
            //can't find solution with delegate or on()
            $(".cruise-2018-itineraries-itinerary-row-thumbnail img").lazy("lazy");
        }).fail(function () {
            removeSpinnerOnItineraryDetail();
        })
    });


    var iAmInComboCruisePage = $("body").hasClass("combocruise"),
        iHaveRoutesDiv = $(".route-content .change-route-onclick").length > 0,
        isMobile = $(window).width() < 768;

    if (iAmInComboCruisePage && iHaveRoutesDiv) {
        $(".route-content .change-route-onclick").on("click", onClickChangeRoute);
        if (isMobile) {
            $(".route-slider").on("click",".slick-arrow", onClickSlideArrowMobile);
        }
    }
});