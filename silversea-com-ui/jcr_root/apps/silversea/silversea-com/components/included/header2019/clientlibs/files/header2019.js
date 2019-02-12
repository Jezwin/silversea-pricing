$(function () {
    const $langMenu = $(".header-2019");
    const openLangMenuClass = "lang-menu-open";

    function openMenuMobile() {
        $(".header2019-open-menu, .header2019-close-menu").toggle();
        $(".header2019-container-list").addClass("header2019-container-list-opened");
        $("body").removeClass("body-mobile-no-scroll");
        $("html").removeClass("body-mobile-no-scroll");

    };//openMenuMobile

    function closeMenuMobile() {
        $("body").removeClass("body-mobile-no-scroll");
        $("html").removeClass("body-mobile-no-scroll");
        $(".header2019-open-menu, .header2019-close-menu").toggle();
        $(".header2019-container-list").removeClass("header2019-container-list-opened");
    };//closeMenuMobile


    function langMenu() {

        function toggleLangMenu(e) {
            if ($langMenu.toggleClass(openLangMenuClass).attr('class').indexOf(openLangMenuClass) > 0) {
                $(document).one("click", function () {
                    $langMenu.removeClass(openLangMenuClass);
                });
                $(window).one("scroll", function () {
                    $langMenu.removeClass(openLangMenuClass);
                });
            }
            e.stopPropagation();
        }

        $(".header-2019-lang-menu-wrapper").on('click', toggleLangMenu);
    }

    function subMenu() {
        const $headerAndSons = $(".header-2019, #header-2019-submenu-container");
        const $subMenu = $("#header-2019-submenu-positioner");
        const openedMenuClass = "header-2019-showthis";
        const openedLinkClass = "header-2019-bordered";

        function fixColumnWidth($menu, index) {//style workaround
            if (window["__ssc_menu_fixed" + index]) {
                return;
            }
            window["__ssc_menu_fixed" + index] = true;
            $menu.find(".header-2019-submenu-snd").each(function (i, subMenuCategory) {
                var uniqsLeftOffsets = {};//each new column has a different left offset
                var $entries = $(subMenuCategory).find(".header-2019-submenu-entry");
                $entries.each(function (si, subMenuEntry) {
                    uniqsLeftOffsets[subMenuEntry.offsetLeft] = 1 + (uniqsLeftOffsets[subMenuEntry.offsetLeft] || 0);
                });//count how many columns
                const cols = Object.keys(uniqsLeftOffsets).length;
                $entries.css("max-width", (100 / cols - 3) + "%");
                $(subMenuCategory).css("flex-grow", cols === 0 ? 1 : cols);
            });
        }

        function showSubMenu(e) {
            cancelHide();
            var menuIndex = $(this).data("menu-index");
            var $menu = $("#header-2019-secondrow-" + menuIndex + "-submenu");
            if ($menu.length === 0) {
                hideMenu("now");
            } else {
                hideSubMenu();
                $(e.target).addClass(openedLinkClass);
                $menu.addClass(openedMenuClass);
                $subMenu.find(".lazy").lazy();
                $subMenu.show();
                $menu.css("display", "flex");
                fixColumnWidth($menu, menuIndex);
            }
        }

        function hideSubMenu() {
            var $menu = $("." + openedMenuClass);
            $menu.css("display", "none");
            $menu.removeClass(openedMenuClass);
            $("." + openedLinkClass).removeClass(openedLinkClass);
        }

        function hideMenu(now) {
            window._ssc_hidemenu = window.setTimeout(function () {
                $subMenu.hide();
                hideSubMenu();
            }, now === "now" ? 0 : 300);
        }


        function cancelHide() {
            clearTimeout(window._ssc_hidemenu);
        }

        $(".header-2019-first-level-link").on('mouseenter', showSubMenu);
        $headerAndSons.on('mouseleave', hideMenu);
        $headerAndSons.on('mouseenter', cancelHide);

    }

    function searchForm() {
        function toggleSearch(e) {
            if ($(".header-2019-top-section").toggleClass("search-visible").attr('class').indexOf("search-visible") > 0) {
                $(".header-2019-search").one("click", function (e) {
                    e.stopPropagation();
                });

                $(document).one("click", function (e) {
                    $(".header-2019-top-section").removeClass("search-visible");
                });
                $(".search-visible .header-2019-search input").focus();
            }
            e.stopPropagation();
        }

        $(".header-2019 .fa-search").on("click", toggleSearch);
    }

    function sticking() {
        $(window).scroll(sscThrottled(function () {
            var scrollPosition = (document.documentElement && document.documentElement.scrollTop) || document.body.scrollTop;
            if (scrollPosition > 0) {
                $(".header2019, .header-2019-wrapper").addClass("sticked");
                $(".header-2019-top-section").removeClass("search-visible");
            } else {
                $(".header2019, .header-2019-wrapper").removeClass("sticked");
                $(".header-2019 input").blur();
            }
        }));

    }

    var isDesktop = $(window).width() > 991;
    if (isDesktop) {
        sticking();
        langMenu();
        subMenu();
        searchForm();
    } else {
        $(".header-2019-mobile").on("click", ".header2019-open-menu",openMenuMobile);
        $(".header-2019-mobile").on("click", ".header2019-close-menu",closeMenuMobile);
    }

});