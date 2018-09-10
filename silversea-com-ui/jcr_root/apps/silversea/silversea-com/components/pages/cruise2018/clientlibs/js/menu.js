$.fn.isUnderViewport = function (offset) {
    return $(this).offset().top - headerHeight() + $(this).height() - offset > $(window).scrollTop();
};

function isScrolledIntoView(elem, offset) {
    var docViewTop = $(window).scrollTop() + headerHeight() + $(".ssc-sticky-top").height() + offset;
    var elemTop = $(elem).offset().top;
    var elemBottom = elemTop + $(elem).outerHeight(true);
    return ((elemBottom >= docViewTop) && (docViewTop >= elemTop));
}

function headerHeight() {
    return $("#header").height() + $(".c-main-nav__bottom").height();
}


function stickMenu() {
    var $wrapper = $(".cruise-2018-menu-container-wrapper");
    var $stickyTop = $(".cruise-2018-menu-container.ssc-sticky-top");
    var $overviewMenu = $(".menu-overview-fixed");
    if ($wrapper.isUnderViewport(15 + $(".ssc-sticky-top").outerHeight())) {
        $stickyTop.fadeOut(0, function () {
            $wrapper.css("padding-bottom", "60px");//this is for smoothness
            $wrapper.css("margin-top", "0px");
            $stickyTop.removeClass("ssc-sticky-top").fadeIn(0);
            $(".cruise-2018-menu").removeClass("ssc-table-full-width-with-overview");
            $("#header").animate({
                top: '0px',
                duration: 200
            }, {
                progress: function (now, fx) {
                    $(".menu-overview-fixed").hide(0);
                }
            });
        });

    } else {
        var $stickyTop2 = $(".cruise-2018-menu-container:not(.ssc-sticky-top)");
        $stickyTop2.fadeOut(0, function () {
            $wrapper.css("padding-bottom", "0px");//this is for smoothness
            $wrapper.css("margin-top", "50px");
            $stickyTop2.addClass("ssc-sticky-top").fadeIn(0);
            $overviewMenu.show(200);
            $(".cruise-2018-menu").addClass("ssc-table-full-width-with-overview");
            $("#header").animate({
                top: '-110px',
                duration: 200,
                easing: 'linear'
            }, {
                progress: function (now, fx) {
                    $(".cruise-2018-menu-container.ssc-sticky-top").css('top', "-50px");
                }
            });
        });
        //$stickyTop.css('top', ($(".c-header__container").height() + $(".c-main-nav__bottom").height() - 50) + "px");
    }
}

function activeTab() {
    $(".cruise-2018-menu-tab").each(function () {
        target = $(this).attr("href");
        if ($(target).position() && isScrolledIntoView(target, 11)) {
            $(this).addClass("active");
        } else {
            $(this).removeClass("active");
        }
    });
}

function onScroll() {
    stickMenu();
    activeTab();
    var $tabs = $(".cruise-2018-menu-tab");
    $tabs.css("width", (100 / $tabs.length - 1) + "%");
}

$(".cruise-2018-menu a[href^='#']").on("click touchstart", function (e) {
    e.preventDefault();
    $target = $($(this).attr("href"));
    $([document.documentElement, document.body]).animate({
        scrollTop: $target.position().top - headerHeight() - 70,
        duration: 500,
        easing: 'linear'
    });
});

$(function () {
    onScroll();
    $(document).on('scroll resize touchmove gesturechange', onScroll);
});
