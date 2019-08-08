function StickyMenu(replaceHeader) {
    this.replaceHeader = replaceHeader || false;
}

StickyMenu.prototype = {
    constructor: StickyMenu,
    replaceHeader: false,
    headerSelector: ".header-style3,.header-style4,.c-header,.c-main-nav__bottom,.c-header__container",
    isUnderViewport: function ($el, offset) {
        return $el.offset().top - this.headerHeight() + $el.height() - offset > $(window).scrollTop();
    },
    isScrolledIntoView: function isScrolledIntoView(elem, offset) {
        var docViewTop = $(window).scrollTop() + this.headerHeight() + $(".ssc-sticky-top").height();
        var elemTop = $(elem).offset().top;
        var elemBottom = elemTop + $(elem).outerHeight(true);
        return ((elemBottom + offset >= docViewTop) && (docViewTop >= elemTop - offset));
    },
    headerHeight: function headerHeight() {
        return ($(this.headerSelector).height() || 60) + $(".c-main-nav__bottom").height();

    },

    manageShadowHeader: function removeShadowHeader() {
        var isHeader2019 = $(".header2019").length > 0 && $(".header2019").is(":visible") && $(".header2019 .header-2019-wrapper").length > 0;
        if (isHeader2019) {
            $(".header2019 .header-2019-wrapper").toggleClass("header-2019-wrapper-nosticked");
        }
    },

    shouldReplaceHeader: function () {
        return this.replaceHeader && $(".stickyMenu>*:visible").length > 0;
    },

    stickMenu: function stickMenu() {
        var menu = this;
        var $wrapper = $(".sm-menu-container-wrapper");
        var $stickyTop = $(".sm-menu-container.ssc-sticky-top");
        var height = this.shouldReplaceHeader() ? 0 : this.headerHeight();
        var headerSelector = this.shouldReplaceHeader() ? this.headerSelector : "";
        if (this.isUnderViewport($wrapper, 15 + $(".ssc-sticky-top").outerHeight())) {
            $stickyTop.fadeOut(0, function () {
                $wrapper.css("padding-bottom", "60px");//this is for smoothness
                $wrapper.css("margin-top", "0px");
                $stickyTop.removeClass("ssc-sticky-top").fadeIn(0);
                menu.manageShadowHeader();
                $(".sm-menu-tab").removeClass('active');
                $(headerSelector).slideDown(200);
            });
        } else {
            var $stickyTop2 = $(".sm-menu-container:not(.ssc-sticky-top)");
            $stickyTop2.fadeOut(0, function () {
                $wrapper.css("padding-bottom", "0px");//this is for smoothness
                $wrapper.css("margin-top", "0px");
                $stickyTop2.addClass("ssc-sticky-top").fadeIn(0, function () {
                    $stickyTop2.css("top", height + "px");
                });
                menu.manageShadowHeader();
                $(headerSelector).slideUp(200);
            });

        }
    },

    getOffset: function getOffset(element) {
        var isDesktop = $(window).width() > 991,
            isMobile = $(window).width() < 768;
        var device = isDesktop ? "desktop" : isMobile ? "mobile" : "tablet";
        return element.attr("data-offset-" + device);
    },

    activeTab: function activeTab() {
        var menu = this;
        $(".sm-menu-tab").each(function () {
            target = $(this).attr("href");
            var offset = parseInt(menu.getOffset($(this)));
            if ($(target).position() && menu.isScrolledIntoView(target, offset)) {
                $(".sm-menu-tab").removeClass('active');
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
    $(".sm-menu a[href^='#']").on("click touchstart", function (e) {
        e.preventDefault();
        var ref = $(this).attr("href");
        $target = $(ref);
        history.replaceState(null, null, ref);

        var number = window.innerWidth < 768 ? 65 : 100;
        var offset = getOffesetBasedOnDevices($(this)) || number;

        //good velocity is 500 pixel per second
        var distance = $target.offset().top - offset;
        $([document.documentElement, document.body]).animate({
            scrollTop: distance,
            duration: (0.5 * distance),
            easing: 'linear'
        }, (0.5 * distance));
    });
    if ($(".sm-menu-container").length > 0) {
        var menu = window.$$stickyMenu;
        menu.onScroll();
        $(document).on('scroll resize touchmove gesturechange', menu.onScroll.bind(menu));
    }

    function getOffesetBasedOnDevices(element) {
        var isDesktop = $(window).width() > 991,
            isMobile = $(window).width() < 768;
        device = isDesktop ? "desktop" : isMobile ? "mobile" : "tablet";
        return element.attr("data-offset-" + device);
    };//getOffesetBasedOnDevices
});
