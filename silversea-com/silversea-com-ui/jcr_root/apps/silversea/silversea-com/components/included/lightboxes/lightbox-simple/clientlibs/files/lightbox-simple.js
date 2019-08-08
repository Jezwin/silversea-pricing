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
            var number = ($lightboxSimpleContent.offset().top - $lightboxSimple.offset().top - header.height());
            number = number < 0 ? 0 : number;
            header.css("top", number + "px");
            header.css("left", ($lightboxSimpleContent.offset().left) + "px");
        };

        const toggleScroll = function () {
            $(document.body).toggleClass("bodyLightboxSimple");
            $(document.documentElement).toggleClass("bodyLightboxSimple");
        };

        const findAllLinks = function ($element) {
            while ($element.find("[data-simple-lightbox]").length < 2) {
                var $parent = $element.parent();
                if ($parent.length === 0) {
                    return [$element];
                }
                $element = $parent;
            }
            var $customParent = $parent.closest("[data-slb-parent]");
            if ($customParent.length > 0) {
                $parent = $customParent;
            }


            var list = [];
            var $links = $parent.find("[data-simple-lightbox]");
            $links.each(function (i, e) {
                if ($(e).closest(".slick-cloned").length === 0) {//removing fake slick elements
                    list.push($(e));
                }
            });
            return list;
        };

        const prevNext = function ($link) {
                var loadedPrev = $lightboxSimple.find(".used-by-lightbox-simple-prev");
                var loadedNext = $lightboxSimple.find(".used-by-lightbox-simple-next");
                if (loadedPrev.length > 0 && loadedNext.length > 0) {
                    $lightboxSimple.find('.lightbox-simple-prev-label').html(loadedPrev.html());
                    var prevHref = loadedPrev.attr("href");
                    var nextHref = loadedNext.attr("href");
                    if (prevHref && nextHref) {
                        return {
                            prev: loadedPrev,
                            next: loadedNext
                        };
                    }
                }
                const list = findAllLinks($link);
                var i;
                var length = list.length;
                for (i = 0; i < length; i++) {
                    if (list[i].attr("href") === $link.attr("href")) {
                        var prev = list[(i - 1 + length) % length];
                        var next = list[(i + 1) % length];
                        return {
                            prev: prev,
                            next: next
                        };
                    }
                }

            }
        ;

        const setNavigation = function ($link) {
            /*
            There are two ways to set prev/next:
            1) (Old way)
                In the ajax response add two <a> with class .used-by-lightbox-simple-prev, .used-by-lightbox-simple-next.
                They will be hidden by default. The href will be used for prev and next accordingly.
                This can be tricky because you need a prev and next link in each ajax call (so you will a backend assist)
            2) (New way. Easier. Just do nothing.)
                For any link it will create a list starting from any ancestor that has more than 1 data-simple-lightbox
                as descendant. (It's what you expect to be).
                For example:
                <div>
                    <div>
                        <span><a href="x" data-simple-lightbox>x</a><i/></span>
                    </div>
                    <div>
                        <span><a href="y" data-simple-lightbox>y</a><i/></span>
                    </div>
                    <div>
                        <span><a href="z" data-simple-lightbox>z</a><i/></span>
                    </div>
                </div>
                Will create x,y,z modals links.

                LABELS
                You can (in order of priority on how they are applied):
                    1) (TODO, not yet implemented) Set global label in the parent div with data-slb-prev-label, data-slb-next-label
                    2) Set specific label with data-slb-label in each link
                    3) Do nothing. The content of the <a> will be used
                    NB If you don't want label you can use 1 or 2 with empty content
             2bis) If you want to force a certain parent like in this example:
                <div data-slb-parent>
                    <div>
                        <div>
                            <span><a href="x" data-simple-lightbox>x</a><i/></span>
                        </div>
                        <div>
                            <span><a href="y" data-simple-lightbox>y</a><i/></span>
                        </div>
                        <div>
                            <span><a href="z" data-simple-lightbox>z</a><i/></span>
                        </div>
                    </div>
                    <div>
                        <div>
                            <span><a href="w" data-simple-lightbox>w</a><i/></span>
                        </div>
                    </div>
                </div>
                Use the tag data-slb-parent, thus obtaining: x,y,z,w

             */
            try {
                var links = prevNext($link);
                var loadedPrev = links.prev;
                var loadedNext = links.next;
                var prevHref = loadedPrev.attr("href");
                var nextHref = loadedNext.attr("href");
                var prevLabel = loadedPrev.data("slb-label") || loadedPrev.html();
                var nextLabel = loadedNext.data("slb-label") || loadedNext.html();

                $lightboxSimple.find('.lightbox-simple-prev-label').html(prevLabel);
                $lightboxSimple.find('.lightbox-simple-next-label').html(nextLabel);

                var prevLink = $lightboxSimple.find(".lightbox-simple-prev-link");
                var nextLink = $lightboxSimple.find(".lightbox-simple-next-link");
                prevLink.attr("href", prevHref);
                nextLink.attr("href", nextHref);
                prevLink.off("click");//remove others click events
                nextLink.off("click");
                prevLink.on("click", function (event) {
                    event.preventDefault();
                    event.stopPropagation();
                    navigationAnimation(prevHref, "right", loadedPrev);
                });
                nextLink.on("click", function (event) {
                    event.preventDefault();
                    event.stopPropagation();
                    navigationAnimation(nextHref, "left", loadedNext);
                });
                nextLink.show();
                prevLink.show();
            } catch (e) {

            }
            return true;
        };

        const navigationAnimation = function (url, direction, $nextLink) {
            var opposite = direction === 'left' ? 'right' : 'left';
            (function c(direction, opposite) {
                var obj = {};
                obj[direction] = '-150vw';
                $('.lightbox-close').hide();
                $lightboxSimple.animate(obj, 400, function () {//this move it out of view from one side
                    $lightboxSimpleContent.load(url.replace("#", ""), function () {
                        setNavigation($nextLink);
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
                var $link = $(event.relatedTarget);
                callCallback($link.data("slb-callback"));
                setHeader();
                setNavigation($link);
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
    }
);


