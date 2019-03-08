function Menu() {
}

Menu.prototype = {
    constructor: Menu,
    isUnderViewport: function ($el, offset) {
        return $el.offset().top - this.headerHeight() + $el.height() - offset > $(window).scrollTop();
    },
    isScrolledIntoView: function isScrolledIntoView(elem, offset) {
        var docViewTop = $(window).scrollTop() + this.headerHeight() + $(".ssc-sticky-top").height() + offset;
        var elemTop = $(elem).offset().top;
        var elemBottom = elemTop + $(elem).outerHeight(true);
        return ((elemBottom >= docViewTop) && (docViewTop >= elemTop));
    },
    headerHeight: function headerHeight() {
        return $("#header").height() + $(".c-main-nav__bottom").height();
    },

    manageShadowHeader: function removeShadowHeader() {
        var isHeader2019 = $(".header2019").length > 0 && $(".header2019").is(":visible") && $(".header2019 .header-2019-wrapper").length > 0;
        if (isHeader2019) {
            $(".header2019 .header-2019-wrapper").toggleClass("header-2019-wrapper-nosticked");
        }
    },

    stickMenu: function stickMenu() {
        var menu = this;
        var $wrapper = $(".cruise-2018-menu-container-wrapper");
        var $stickyTop = $(".cruise-2018-menu-container.ssc-sticky-top");
        var $overviewMenu = $(".menu-overview-fixed");
        if (this.isUnderViewport($wrapper, 15 + $(".ssc-sticky-top").outerHeight())) {
            $stickyTop.fadeOut(0, function () {
                $wrapper.css("padding-bottom", "60px");//this is for smoothness
                $wrapper.css("margin-top", "0px");
                $stickyTop.removeClass("ssc-sticky-top").fadeIn(0);
                menu.manageShadowHeader();
                $(".cruise-2018-menu").removeClass("ssc-table-full-width-with-overview");
                $("#header").animate({
                    top: '0px',
                    duration: 200
                }, {
                    progress: function (now, fx) {
                        $(".menu-overview-fixed").hide(0);
                    },
                    complete: function () {
                        var $footerfixed = $(".fixed-footer-cruise");
                        $footerfixed.slideUp();
                    }
                });
                $(".cruise-2018-menu-tab").removeClass('active');
            });

        } else {
            var $stickyTop2 = $(".cruise-2018-menu-container:not(.ssc-sticky-top)");
            $stickyTop2.fadeOut(0, function () {
                $wrapper.css("padding-bottom", "0px");//this is for smoothness
                $wrapper.css("margin-top", "50px");
                $stickyTop2.addClass("ssc-sticky-top").fadeIn(0);

                $overviewMenu.show(200);
                $(".cruise-2018-menu").addClass("ssc-table-full-width-with-overview");
                menu.manageShadowHeader();
                $("#header:not(.c-main-nav--nav-expanded)").animate({
                    top: '-110px',
                    duration: 200,
                    easing: 'linear'
                }, {
                    progress: function (now, fx) {
                        $(".cruise-2018-menu-container.ssc-sticky-top").css('top', "-50px");
                    }
                });

            });
        }
    },

    activeTab: function activeTab() {
        var menu = this;
        $(".cruise-2018-menu-tab").each(function () {
            target = $(this).attr("href");
            if ($(target).position() && menu.isScrolledIntoView(target, 60)) {
                $(".cruise-2018-menu-tab").removeClass('active');
                $(this).addClass("active");
                return false;//this stops the cycle!
            }
        });
    },

    onScroll: function onScroll() {
        this.stickMenu();
        this.activeTab();
    }
};
$(function () {
    var menu = new Menu();
    $(".cruise-2018-menu a[href^='#']").on("click touchstart", function (e) {
        e.preventDefault();
        var ref = $(this).attr("href");
        $target = $(ref);
        history.replaceState(null, null, ref);

        var number = window.innerWidth < 768 ? 65 : 100;
        $([document.documentElement, document.body]).animate({
            scrollTop: $target.position().top - number,
            duration: 1200,
            easing: 'linear'
        }, 1500);
    });
    var isCorrectPage = $(".cruise-2018-overview").length > 0 && ($("body").hasClass("cruise") || $("body").hasClass("combocruise"));
    if (isCorrectPage) {
        menu.onScroll();
        $(document).on('scroll resize touchmove gesturechange', menu.onScroll.bind(menu));
    }
});
