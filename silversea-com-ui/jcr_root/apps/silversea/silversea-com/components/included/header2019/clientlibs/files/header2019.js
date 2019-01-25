$(function () {

    function langMenu() {
        const $langMenu = $(".header-2019-lang-menu");
        const openLangMenuClass = "clicked";

        function toggleLangMenu(e) {
            if ($langMenu.toggleClass(openLangMenuClass).attr('class').indexOf(openLangMenuClass) > 0) {
                $(document).one("click", function () {
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

        function showSubMenu(e) {
            cancelHide();
            var $menu = $("#header-2019-secondrow-" + $(this).data("menu-index") + "-submenu");
            if ($menu.length === 0) {
                hideMenu("now");
            } else {
                hideSubMenu();
                $(e.target).addClass(openedLinkClass);
                $menu.addClass(openedMenuClass);
                $subMenu.show();
                $menu.css("display", "flex");
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

    langMenu();
    subMenu();
});