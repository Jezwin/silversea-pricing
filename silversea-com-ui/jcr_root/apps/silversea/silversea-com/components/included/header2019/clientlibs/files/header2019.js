$(function () {
    const $menu = $(".header-2019-lang-menu");
    const openLangMenuClass = "clicked";

    function toggleLangMenu(e) {
        if ($menu.toggleClass(openLangMenuClass).attr('class').indexOf(openLangMenuClass) > 0) {
            $(document).one("click", function () {
                $menu.removeClass(openLangMenuClass);
            });
        }
        e.stopPropagation();
    }
    $menu.on('click', toggleLangMenu);
});