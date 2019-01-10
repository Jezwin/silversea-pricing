$(function () {
    window.$lightboxSimple = $("#lightbox-simple");
    window.$lightboxSimpleContent = $("#lightbox-simple-content");

    const isSimpleLightbox = function (event) {
        return typeof ($(event.relatedTarget).data("simple-lightbox")) !== "undefined";
    };


    const addCustomScopeClass = function (scopeClass) {
        if (scopeClass !== "undefined") {
            //this add a scope class to apply custom style
            $lightboxSimple.addClass(scopeClass);
            $lightboxSimple.data("current-scope", scopeClass);
        }
    };

    const removeCustomScopeClass = function () {
        var scope = $lightboxSimple.data("current-scope");
        $lightboxSimple.removeClass(scope);
        $lightboxSimple.data("current-scope", "");
    };

    const setHeader = function () {
        var header = $("#lightbox-simple .lightbox-close");
        var number = ($lightboxSimpleContent.offset().top - header.height());
        number = number < 0 ? 0 : number;
        header.css("top", number + "px");
        header.css("left", ($lightboxSimpleContent.offset().left) + "px");
    };

    const toggleScroll = function () {
        $(document.body).toggleClass("bodyLightboxSimple");
        $(document.documentElement).toggleClass("bodyLightboxSimple");
    };

    const setNavigation = function () {
        try {
            var loadedPrev = $lightboxSimple.find(".used-by-lightbox-simple-prev");
            var loadedNext = $lightboxSimple.find(".used-by-lightbox-simple-next");
            $lightboxSimple.find('.lightbox-simple-prev-label').html(loadedPrev.html());
            var prevLink = $lightboxSimple.find(".lightbox-simple-prev-link");
            var prevHref = loadedPrev.attr("href");
            var nextHref = loadedNext.attr("href");
            if (!prevHref || !nextHref) {
                return;
            }
            prevLink.attr("href", prevHref);
            $lightboxSimple.find('.lightbox-simple-next-label').html(loadedNext.html());
            var nextLink = $lightboxSimple.find(".lightbox-simple-next-link");
            nextLink.attr("href", nextHref);
            prevLink.off("click");//remove others click events
            nextLink.off("click");
            prevLink.on("click", function (event) {
                event.preventDefault();
                event.stopPropagation();
                navigationAnimation(prevHref, "left");
            });
            nextLink.on("click", function (event) {
                event.preventDefault();
                event.stopPropagation();
                navigationAnimation(nextHref, "right");
            });
            nextLink.show();
            prevLink.show();
        } catch (e) {

        }
        return true;
    };

    const navigationAnimation = function (url, direction) {
        var opposite = direction === 'left' ? 'right' : 'left';
        (function c(direction, opposite) {
            var obj = {};
            obj[direction] = '-150vw';
            $('.lightbox-close').hide();
            $lightboxSimple.animate(obj, 400, function () {//this move it out of view from one side
                $lightboxSimpleContent.load(url.replace("#", ""), function () {
                    setNavigation();
                    callCallback();
                    $lightboxSimple.animate({left: '', right: ''}, 500, function () {//this bring back to view from the other side
                        $lightboxSimple.css('left', '');//this is just to rest left && right because previous line put them to 0
                        $lightboxSimple.css('right', '');
                        $lightboxSimple.find('.lightbox-close').fadeIn();
                    });
                });
                $lightboxSimple.css(opposite, '-150vw');//this move it on the other side of the screen
                $lightboxSimple.css(direction, '');//this remove the first set
            });
        })(direction, opposite);
    };

    function callCallback(callback) {
        if (typeof callback !== "undefined") {
            window[callback]();
            window.$$lastCallback = callback;
        } else if (typeof window.$$lastCallback !== "undefined") {
            window[window.$$lastCallback]();
        }
    }

    function setScrollEvent(callback) {
        $lightboxSimple.bind('scroll', callback);
        $(window).resize('resize', callback)
    }

    function removeScrollEvent(callback) {
        $lightboxSimple.unbind('scroll', callback)
        $(window).unbind('resize', callback)

    }

    //--

    const onShow = function (event) {
        if (isSimpleLightbox(event)) {
            var $link = $(event.relatedTarget);
            $lightboxSimpleContent.load($link.attr("href").replace("#", ""));
            addCustomScopeClass($link.data("slb-scope"));
            if ($link.data("slb-arrows")) {
                $(".lightbox-simple-next-link,.lightbox-simple-prev-link").removeClass("hidden");
            } else {
                $(".lightbox-simple-next-link,.lightbox-simple-prev-link").addClass("hidden");
            }
        }
    };

    const onShown = function (event) {
        if (isSimpleLightbox(event)) {
            toggleScroll();
            callCallback($(event.relatedTarget).data("slb-callback"));
            setHeader();
            setNavigation();
            setScrollEvent(setHeader)
        }
    };

    const onHidden = function (event) {
        toggleScroll();
        $lightboxSimpleContent.html("");
        delete window.$$lastCallback;
        removeCustomScopeClass();
        removeScrollEvent(setHeader)
    };


    $lightboxSimple.on("show.bs.modal", onShow);
    $lightboxSimple.on('shown.bs.modal', onShown);
    $lightboxSimple.on("hidden.bs.modal", onHidden);
});


