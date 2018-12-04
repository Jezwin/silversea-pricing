$(function () {
    "use strict";

    if ($(".findyourcruise2018").length > 0) {

        var createPortSelectedList = function (port) {
            var $containerPortSelected = $(".filter-port .fyc2018-filter-selected-content");
            var isAlreadyPresent = false;
            $containerPortSelected.find(".filter-value").each(function () {
                if ($(this).data("key") == port.attr("data-key")) {
                    isAlreadyPresent = true;
                    //if (port.data("state") == "ENABLED") {
                        $(this).remove();
                    // /}
                }
            });
            if (!isAlreadyPresent) {
                var portClass = "col-sm-12 filter-value filter-selected filter-port-selected";
                $containerPortSelected.append($("<div class='" + portClass + "' data-key='" + port.data("key") + "' data-label='" + port.data("label") + "' data-state='" + port.data("state") + "'><span>" + port.data("label") + "</span></div>"));
            }
        };//createPortSelectedList
        var showFilteredElement = function () {
            elementFilteredToShow = [];
            var $listItem = $(filterPortValueContainerClass),
                $itemToRender = null,
                index = 0,
                item = $(autocompleteInputId).getItemData(index);
            if (item == -1) {
                $(autocompleteContainerClass + " .filter-no-ports").show();
                $(autocompleteContainerClass + " .filter-value").hide();
                $(autocompleteContainerClass + " .filter-view-all").hide();
                return;
            } else {
                $(autocompleteContainerClass + " .filter-no-ports").hide();
                $(autocompleteContainerClass + " .filter-value").show();
            }

            while (item != -1) {
                $itemToRender = $($listItem[index]);
                if ($itemToRender.length > 0) {
                    if (elementFilterSelected[item.key] != null) {
                        item.state = elementFilterSelected[item.key].state;
                    }
                    var status = item.state == 'ENABLED' ? 'filter-no-selected' : item.state == 'DISABLED' ? 'filter-disabled' : item.state == 'CHOSEN' ? 'filter-selected' : '';
                    $itemToRender.removeAttr("class");
                    $itemToRender.addClass("col-sm-12 filter-value " + status);
                    $itemToRender.attr("data-key", item.key);
                    $itemToRender.attr("data-label", item.label);
                    $itemToRender.attr("data-state", item.state);
                    $itemToRender.find("span").html(item.label);
                    $itemToRender.show();
                } else {
                    elementFilteredToShow.push(item);
                }
                item = $(autocompleteInputId).getItemData(++index);
            }
            if (index < 7) {
                $(viewAllFilteredContainerClass).hide();
                $(viewAllOriginalContainerClass).hide();
            } else {
                $(autocompleteContainerClass).css("overflow-y", "scroll");
            }
            if (elementFilteredToShow.length > 0) {
                $(viewAllOriginalContainerClass).hide();
                $(autocompleteContainerClass).append($(viewAllFilteredContainerClass));
                $(viewAllFilteredContainerClass).show();
            } else {
                while ($itemToRender && $itemToRender.length > 0) {
                    $itemToRender = $($listItem[index++]);
                    $itemToRender.hide();
                }
            }

        };//showFilteredElement
        var loadPortsElementNotSelected = function (indexToStart, filtered) {
            /* Event triggered when the user click on Load more inside the port list
            *  Create DOM element to show other ports not loaded on startup.
            * */
            var list = filtered ? elementFilteredToShow : portsList;
            var $portsFilterContainer = $(".filter-port .fyc2018-filter-autocomplete-content");
            for (var i = indexToStart; (i < indexToStart + 100 && i < list.length); i++) {
                var port = list[i];
                var portClass = "col-sm-12 filter-value ";
                if (port.state == 'ENABLED') {
                    portClass += "filter-no-selected";
                } else if (port.state == 'CHOSEN') {
                    portClass += "filter-selected";
                }
                var $containerPortSelected = $(".filter-port .fyc2018-filter-selected-content");
                $containerPortSelected.each(function () {
                    if($(this).data("key") == port.key) {
                        portClass = "filter-selected";
                        port.state = "CHOSEN";
                    }
                });
                $portsFilterContainer.append($("<div class='" + portClass + "' data-key='" + port.key + "' data-label='" + port.label + "' data-state='" + port.state + "'><span>" + port.label + "</span></div>"));
            }
            /* I will move the both Load More as last element.
             * Hide them and show only the Load More if I have other ports to load.
             */
            var $loadMorePorts = $(".filter-port .filter-view-all-original");
            var $loadMoreSelectedPorts = $(".filter-port .filter-view-all-filtered");
            $loadMorePorts.hide();
            $loadMoreSelectedPorts.hide();
            $portsFilterContainer.append($(".filter-port .filter-view-all-original"));
            $portsFilterContainer.append($(".filter-port .filter-view-all-filtered"));
            if ($(".filter-port .filter-value").length < list.length - 1) {
                if (filtered) {
                    $loadMoreSelectedPorts.show();
                } else {
                    $loadMorePorts.show();
                }
            }
        };//loadPortsElementNotSelected
        var close = function () {
            $("#" + lastOpenFilter).parent().click();
        };//close
        var checkNumberSelectedFilters = function () {
            var length = $(".findyourcruise2018 .fyc2018-filter .filter-value.filter-selected:not(.filter-port-selected)").length;
            if (length > 0) {
                $(".fyc2018-header-reset-all").show();
                $(".fyc2018-filters-container").addClass("fyc2018-filters-container-clear-open");
            } else {
                //$(".fyc2018-header-reset-all").hide();
                $(".fyc2018-filters-container").removeClass("fyc2018-filters-container-clear-open");
            }
        };//checkNumberSelectedFilters
        var separateYears = function () {
            var year = null;
            $(".filter-departure .filter-value").each(function () {
                var currentYear = $(this).data("key").split("-")[0];
                if (currentYear != year) {
                    $(this).before("<div class='col-xs-12 fyc2018-filter-year'>" + currentYear + "</div>");
                    year = currentYear;
                }
            });
        };//separateYears
        var createUrl = function (urlTemplate, nextPage, updateHistory) {
            var url = "";
            var j = 0;
            $(".fyc2018-filter .fyc2018-filter-value").each(function () {
                var $this = $(this);
                var $selectedElements = $this.find(".filter-value.filter-selected:not(.filter-port-selected)");
                if ($this.data('name') == "port") {
                    $selectedElements = $this.find(".fyc2018-filter-selected-content .filter-value.filter-selected");
                }
                var i = 0;
                var urlFilter = "";
                $selectedElements.each(function () {
                    urlFilter += $(this).data('key');
                    urlFilter = (++i < $selectedElements.length) ? urlFilter += "." : urlFilter;
                });
                if (urlFilter != "") {
                    url += url != "" ? "&" : url;
                    url += $this.data('name') + "=" + urlFilter;
                }
            });
            var page = nextPage;
            if (nextPage == null) {
                if ($(".active.fyc2018-pag-link").length > 0) {
                    page = $(".active.fyc2018-pag-link").data("page");
                } else {
                    page = 1;
                }
            }

            if (url == "") {
                url += "pag=" + page;
            } else {
                url += "&pag=" + page;
            }
            if (updateHistory) {
                var current = window.location.href;
                history.pushState(null, null, encodeURI(current.substr(0, current.indexOf("html") + 4) + "?" + url));
            }
            return urlTemplate + ".html?" + url;
        };//createUrl
        var updateCruises = function (url, removeScroll) {
            $.ajax({
                type: 'GET',
                url: url,
                success: function (result) {
                    var $divToReplace = $(".findyourcruise2018 .fyc2018-results-div");
                    $divToReplace.html(result);
                    var numbersToUpdate = $divToReplace.find(".c-fyc-v2__results__wrapper").data("totals");
                    $(".findyourcruise2018 .fyc2018-header-total-num").html(numbersToUpdate);
                    setNumberResultOnMobileBtn(numbersToUpdate);
                    if ($(window).width() < 768 && !removeScroll) {
                        $("html").addClass("no-scroll-html");
                        $("body").addClass("no-scroll-body");
                        $("body").addClass("no-height-body");
                        window.iNoBounce.enable();
                    }
                }
            });
        };//updateCruises
        var setNumberResultOnMobileBtn = function (numbersToUpdate) {
            if ($(window).width() < 768) {
                numbersToUpdate = numbersToUpdate != null ? numbersToUpdate : 0;
                var $buttonShowResults = $(".findyourcruise2018 .fyc2018-view-results");
                var label = numbersToUpdate + " " + $buttonShowResults.data("plural");
                if (numbersToUpdate == 1) {
                    label = numbersToUpdate + " " + $buttonShowResults.data("singular");
                }
                label = numbersToUpdate > 0 ? $buttonShowResults.data("show") + " " + label : label;
                $buttonShowResults.find("span").html(label);
            }
        };//setNumberResultOnMobileBtn
        var updateFilters = function (url, classElement, parent, idShowElement) {
            if (urlToCompare != window.location.search) {

                $(".fyc2018-spinner").show();
                $(".fyc2018-filter-value-container").addClass("filter-content-disabled");
                $(".fyc2018-filter-autocomplete").addClass("filter-content-disabled");
                $(".filter-combo-cruises").addClass("filter-content-disabled");
                $.ajax({
                    type: 'GET',
                    url: url,
                    success: function (result) {
                        var showReset = $(".fyc2018-header-reset-all").is(":visible");
                        var showFilterMobile = $(".fyc2018-header-mobile-filter").hasClass("fyc2018-filter-mobile-open");
                        $(".fyc2018-tmp-div").html(result);
                        var idLastDivOpen = $(".fyc2018-filter-content-clicked span").attr("id");

                        $(".fyc2018-tmp-div #" + idLastDivOpen).parent().addClass("fyc2018-filter-content-clicked");
                        $(".fyc2018-tmp-div ." + idLastDivOpen).addClass("fyc2018-filter-value-clicked");

                        $(".findyourcruise2018-header").replaceWith($(".fyc2018-tmp-div").html());
                        $(".fyc2018-tmp-div").html("");
                        try {
                            portsList = JSON.parse(window.portsList);
                        } catch (e) {
                            console.info("Error Port list");
                        }

                        if (showReset) {
                            $(".fyc2018-header-reset-all").show();
                            $(".fyc2018-filters-container").addClass("fyc2018-filters-container-clear-open");
                        }
                        if (showFilterMobile && $(window).width() < 768) {
                            $(".fyc2018-header-mobile-filter").addClass("fyc2018-filter-mobile-open");
                            $(".fyc2018-filters-container").show();
                        }
                        if ($(".filter-port").length > 0) {
                            var $containerPortSelected = $(".filter-port .fyc2018-filter-selected-content");
                            for (var i = 0; i < portsList.length; i++) {
                                var port = portsList[i];
                                if (port.state == "CHOSEN") {
                                    var portClass = "col-sm-12 filter-value filter-selected filter-port-selected";
                                    $containerPortSelected.append($("<div class='" + portClass + "' data-key='" + port.key + "' data-label='" + port.label + "' data-state='" + port.state + "'><span>" + port.label + "</span></div>"));
                                }
                            }
                            $(".findyourcruise2018 #filter-autocomplete").easyAutocomplete(optionAutoComplete);
                            $(".findyourcruise2018 .easy-autocomplete-container ul").remove();
                            $(".findyourcruise2018 #filter-autocomplete").focus().select();
                            $(".findyourcruise2018 #filter-autocomplete").keyup(function () {
                                if ($(this).val().trim().length == 0) {
                                    $(".filter-port .filter-value").remove();
                                    loadPortsElementNotSelected(0);
                                    $(".fyc2018-filter-autocomplete-content .filter-no-ports").hide();
                                }
                            });
                        }
                        setNumberAllFilterSelected();
                        separateYears();
                        searchAnalytics();
                        urlToCompare = window.location.search;
                    }
                });
            }
        };//updateFilters
        var setNumberAllFilterSelected = function () {
            $(".findyourcruise2018 .fyc2018-filter-value").each(function () {
                var idFilter = $(this).data("filter");
                setNumberFilterSelected(idFilter);
            });
        };//setNumberAllFilterSelected
        var setNumberFilterSelected = function (idFilter) {
            var numberSelected = $("." + idFilter + " .filter-value.filter-selected").length;

            if (idFilter == "filter-port") {
                numberSelected = $("." + idFilter + " .fyc2018-filter-selected-content .filter-value").length;
                var $selectedPortLink = $(".filter-port .filter-show-selected");
                if (numberSelected > 0) {
                    $selectedPortLink.show();
                    $(".filter-port .fyc2018-filter-autocomplete-content").addClass("fyc2018-filter-autocomplete-content-open");
                    var lab = (numberSelected > 1) ? "labelplural" : 'labelsingular';
                    $selectedPortLink.find("span").text(numberSelected + " " + $selectedPortLink.data(lab));
                } else {
                    $selectedPortLink.hide();
                    $(".filter-port .fyc2018-filter-autocomplete-content").removeClass("fyc2018-filter-autocomplete-content-open");
                    $(".filter-port .fyc2018-filter-selected-content .filter-value").hide();
                }
            }
            var type = (numberSelected > 1) ? "plural" : 'singular';
            var label = $("#" + idFilter).data(type);
            numberSelected = numberSelected == 0 ? "" : numberSelected;
            $("#" + idFilter).text(numberSelected + " " + label);
            if (numberSelected > 0) {
                $("#" + idFilter).parent().addClass("with-filters");
            } else {
                $("#" + idFilter).parent().removeClass("with-filters");
            }
            if ($(window).width() < 768) {
                $("." + idFilter + " .fyc2018-filter-label-mobile").text(numberSelected + " " + label);
            }

        };//setNumberFilterSelected

        var onClickShowSelectedPort = function (e) {
            e.preventDefault();
            e.stopPropagation();
            var isOpenSelectedDiv = $(this).hasClass("filter-show-selected-open");
            var $containerPortSelected = $(".filter-port .fyc2018-filter-selected-content");
            var $containerPortList = $(".filter-port .fyc2018-filter-autocomplete-content");
            if (isOpenSelectedDiv) {
                $containerPortSelected.hide();
                $(this).removeClass("filter-show-selected-open");
                $containerPortList.show();
            } else {
                $containerPortList.hide();
                $(this).addClass("filter-show-selected-open");
                $containerPortSelected.show();
            }
        };//onClickShowSelectedPort

        var selectDisableFilter = function ($filter) {

            if ($filter.hasClass("filter-selected")) {
                $filter.removeClass("filter-selected");
                $filter.removeClass("filter-selected-hover");
                $filter.addClass("filter-no-selected");
            } else if ($filter.hasClass("filter-no-selected")) {
                $filter.removeClass("filter-no-selected");
                $filter.removeClass("filter-no-selected-hover");
                $filter.addClass("filter-selected");
                $(".fyc2018-header-reset-all").show();
                $(".fyc2018-filters-container").addClass("fyc2018-filters-container-clear-open");
            }
            if ($filter.parent().parent().hasClass("filter-port")) {
                var key = $filter.data("key");
                var value = {
                    key: key,
                    label: $filter.data("label")
                };
                if ($filter.hasClass("filter-selected")) {
                    value.state = 'CHOSEN';
                } else if ($filter.hasClass("filter-no-selected")) {
                    value.state = 'ENABLED';
                }
                elementFilterSelected[key] = value;
            }
        };//selectDisableFilter
        var closeAllFiltersDiv = function () {
            $(".fyc2018-filter-content-clicked, .fyc2018-filter-value-clicked").each(function () {
                $(this).removeClass("fyc2018-filter-value-clicked");
                $(this).removeClass("fyc2018-filter-content-clicked");
            });
        };//closeAllFiltersDiv
        var searchAnalytics = function () {
            var dataLayer = window.dataLayer[0];

            // Data search
            var filterOjb = {};
            $(".fyc2018-filter .fyc2018-filter-value").each(function () {
                var name = $(this).data("name");
                var value = "";
                $(this).find(".filter-value.filter-selected").each(function () {
                    value += $(this).data("key") + ",";
                });
                value = value.substring(0, value.length - 1);
                filterOjb[name] = value
            });

            dataLayer.search_filters = filterOjb;
            dataLayer.search_page_number = $(".findyourcruise2018 .fyc2018-pagination .active").data("page");
            dataLayer.search_results_number = $('.findyourcruise2018 .fyc2018-header-total-num').text();

        };//searchAnalytics
        var backMobile = function () {
            window.addEventListener('popstate', function (event) {
                try {
                    // The popstate event is fired each time when the current history entry changes.
                    var $filter = $(".findyourcruise2018 .fyc2018-filter .fyc2018-filter-value-clicked");
                    var isOpenFilters = $filter.length > 0;
                    if (isOpenFilters) {
                        var urlTemplate = $("#filter-url-request").data("url");
                        var url = createUrl(urlTemplate) + "&onlyFilters=true";
                        updateFilters(url);
                        closeAllFiltersDiv();
                        $("html").removeClass("no-scroll-html");
                        $("body").removeClass("no-scroll-body");
                        $("body").removeClass("no-height-body");
                        window.iNoBounce.disable();
                    }
                } catch (e) {
                    $("html").removeClass("no-scroll-html");
                    $("body").removeClass("no-scroll-body");
                    $("body").removeClass("no-height-body");
                    window.iNoBounce.disable();
                }

            }, false);
        };//backMobile
        var fycContainerClass = ".findyourcruise2018",
            autocompleteContainerClass = ".findyourcruise2018 .fyc2018-filter .fyc2018-filter-autocomplete-content",
            viewAllFilteredContainerClass = ".findyourcruise2018 .fyc2018-filter .filter-view-all-filtered",
            viewAllOriginalContainerClass = ".findyourcruise2018 .fyc2018-filter .filter-view-all-original",
            filterPortValueContainerClass = ".findyourcruise2018 .fyc2018-filter .filter-port .fyc2018-filter-autocomplete-content .filter-value",
            autocompleteInputId = ".findyourcruise2018 .fyc2018-filter #filter-autocomplete",
            elementFilteredToShow = [],
            elementFilterSelected = [],
            portsList = null,
            page = 1,
            onLoadFilterUrl = null,
            urlToCompare = window.location.search,
            lastOpenFilter = null;

        var optionAutoComplete = {
            getValue: "label",
            list: {
                match: {
                    enabled: true
                },
                maxNumberOfElements: 800,
                onLoadEvent: showFilteredElement
            }
        };

        $(".filter-port .fyc2018-filter-autocomplete-content .filter-value").each(function () {
            if ($(this).data("state") == "CHOSEN") {
                createPortSelectedList($(this));
            }
        });

        setNumberAllFilterSelected();
        separateYears();
        try {
            portsList = JSON.parse(window.portsList);
        } catch (e) {
            console.error("Error Port list");
        }
        onLoadFilterUrl = window.location.search;
        searchAnalytics();


        if ($(window).width() < 768) {
            backMobile();
        }
        if (window.location.search != "" && window.location.search.indexOf("?pag=") != 0) {
            $(".fyc2018-header-reset-all").show();
            $(".fyc2018-filters-container").addClass("fyc2018-filters-container-clear-open");
            searchAnalytics();
        }
        $(".findyourcruise2018").on("click", ".fyc2018-pag-link", function (e) {
            e.preventDefault();
            e.stopPropagation();
            try {
                var page = $(this).data("page");
                if ($(this).hasClass("next-page")) {
                    page = page + 1;
                } else if ($(this).hasClass("previous-page")) {
                    page = page - 1;
                }

                var urlTemplate = $("#results-url-request").data("url");
                var url = createUrl(urlTemplate, page, true) + "&onlyResults=true";
                updateCruises(url, true);
                var currentSearch = window.location.search;
                var current = window.location.href.split("?")[0];
                var currentS = currentSearch.split("page");
                currentSearch = currentS[0];
                //history.pushState(null, null, encodeURI(current.substr(0, current.indexOf("html") + 4) + currentSearch));
                $('html, body').animate({
                    scrollTop: $('.findyourcruise2018-header').first().offset().top - $('.c-header').height() - 50
                }, 800);
            }
            catch (e) {
            }
        });

        $(fycContainerClass).on('click', ".filter-view-all-original", function (e) {
            e.preventDefault();
            e.stopPropagation();
            var numberPortsAlreadyShowed = $(".filter-port .filter-value").length;
            loadPortsElementNotSelected(numberPortsAlreadyShowed);
        });

        $(fycContainerClass).on('click', ".filter-view-all-filtered", function (e) {
            e.preventDefault();
            e.stopPropagation();
            var numberPortsAlreadyShowed = $(".filter-port .filter-value").length;
            loadPortsElementNotSelected(numberPortsAlreadyShowed, true);
        });

        $(fycContainerClass).on("click", ".fyc2018-header-reset-all", function (e) {
            e.preventDefault();
            e.stopPropagation();
            closeAllFiltersDiv();
            var urlTemplateFilter = $("#filter-url-request").data("url");
            var urlTemplateCruises = $("#results-url-request").data("url");
            var current = window.location.href;
            $(".fyc2018-header-reset-all").hide();
            elementFilterSelected = [];
            $(".fyc2018-filters-container").removeClass("fyc2018-filters-container-clear-open");
            history.pushState(null, null, encodeURI(current.substr(0, current.indexOf("html") + 4)));
            urlToCompare = "force";
            updateFilters(urlTemplateFilter + ".html?onlyFilters=true");
            updateCruises(urlTemplateCruises + ".html?onlyResults=true", true);

        });

        $(fycContainerClass).on('click', ".fyc2018-filter-selected-content .filter-value", function (e) {
            var key = $(this).data("key");
            $(this).remove();
            elementFilterSelected[key] = null;
            $(".filter-port .fyc2018-filter-autocomplete-content .filter-value").each(function () {
                if (key == $(this).data("key")) {
                    $(this).removeClass();
                    $(this).attr("data", "ENABLED");
                    $(this).addClass("col-sm-12 filter-value filter-no-selected");
                }
            });
            if ($(".filter-port .fyc2018-filter-selected-content .filter-value").length == 0) {
                $(".fyc2018-filter-autocomplete-content").show();
            }

            setNumberFilterSelected("filter-port");
        });


        $(fycContainerClass).on("click ", ".fyc2018-filter .filter-show-selected", onClickShowSelectedPort);

        $(fycContainerClass).on("click ", ".fyc2018-filter .fyc2018-filter-content span", function (e) {
            e.preventDefault();
            e.stopPropagation();

            var $filter = $(this),
                classShowElement = "." + $filter.attr("id"),
                idShowElement = "#" + $filter.attr("id"),
                $parent = $filter.parent();
            var urlTemplate = $("#filter-url-request").data("url");
            var url = createUrl(urlTemplate) + "&onlyFilters=true";

            if ($parent.hasClass("fyc2018-filter-content-clicked")) {
                $parent.removeClass("fyc2018-filter-content-clicked");
                $(classShowElement).removeClass("fyc2018-filter-value-clicked");
                lastOpenFilter = null;
                elementFilterSelected[$(this).data("key")] = null;
                updateFilters(url);
            } else {
                lastOpenFilter = $(".fyc2018-filter-content-clicked span").attr("id");
                if ($(window).width() < 768) {
                    $("html").addClass("no-scroll-html");
                    $("body").addClass("no-scroll-body");
                    $("body").addClass("no-height-body");
                    window.iNoBounce.enable();
                    var numberResults = $(".findyourcruise2018 .fyc2018-header-total-num").text();
                    setNumberResultOnMobileBtn(numberResults);
                }
                close();
                if (classShowElement != null && $parent != null && idShowElement != null) {
                    $(classShowElement).addClass("fyc2018-filter-value-clicked");
                    $(idShowElement).parent().addClass("fyc2018-filter-content-clicked");
                    $parent.addClass("fyc2018-filter-content-clicked");
                    if (classShowElement == ".filter-port") {
                        $(".fyc2018-filter .filter-port .filter-value.filter-selected").each(function () {
                            var key = $(this).data("key");
                            var label = $(this).data("label");
                            var value = {
                                key: key,
                                label: label,
                                state: 'CHOSEN'
                            };

                            elementFilterSelected[key] = value;
                        });
                        var $containerPortSelected = $(".filter-port .fyc2018-filter-selected-content");
                        for (var i = 0; i < portsList.length; i++) {
                            var port = portsList[i];
                            if (port.state == "CHOSEN") {
                                var isAlreadyPresent = false;
                                $containerPortSelected.find(".filter-value").each(function () {
                                    if ($(this).data("key") == port.key) {
                                        isAlreadyPresent = true;
                                    }
                                });
                                if (!isAlreadyPresent) {
                                    $containerPortSelected.append($("<div class=\"col-sm-12 filter-value filter-selected filter-port-selected\" data-key='" + port.key + "' data-label='" + port.label + "' data-state='" + port.state + "'><span>" + port.label + "</span></div>"));
                                }
                            }
                        }
                        optionAutoComplete.data = portsList;
                        $(".findyourcruise2018 #filter-autocomplete").easyAutocomplete(optionAutoComplete);
                        $(".findyourcruise2018 .easy-autocomplete-container ul").remove();
                        $(".findyourcruise2018 #filter-autocomplete").focus().select();
                        $(".findyourcruise2018 #filter-autocomplete").keyup(function () {
                            if ($(this).val().trim().length == 0) {
                                $(".filter-port .fyc2018-filter-autocomplete-content .filter-value").remove();
                                loadPortsElementNotSelected(0);
                                $(".fyc2018-filter-autocomplete-content .filter-no-ports").hide();
                            }
                        });
                    }
                }
            }
        });

        $(fycContainerClass).on({
            click: function (e) {
                e.preventDefault();
                e.stopPropagation();
                var idFilter = $(this).parent().parent().data("filter");
                selectDisableFilter($(this));
                if (idFilter == "filter-port") {
                    createPortSelectedList($(this));
                }
                setNumberFilterSelected(idFilter);
                var urlTemplate = $("#results-url-request").data("url");
                var url = createUrl(urlTemplate, 1, true) + "&onlyResults=true";
                updateCruises(url);
                checkNumberSelectedFilters();
            },
            mouseover: function (e) {
                var $filter = $(this);
                if ($filter.hasClass("filter-selected")) {
                    $filter.addClass("filter-selected-hover");
                } else if ($filter.hasClass("filter-no-selected")) {
                    $filter.addClass("filter-no-selected-hover");
                }
            }
        }, ".fyc2018-filter .fyc2018-filter-value .filter-value");

        $(document).on("click ", function (e) {
            if ($(".findyourcruise2018").length > 0) {
                try {
                    var $filter = $(".findyourcruise2018 .fyc2018-filter .fyc2018-filter-value-clicked");
                    var isOpenFilters = $filter.length > 0;
                    if (!(e.target.className.indexOf("fyc2018-filter-autocomplete") > -1 || e.target.className.indexOf("filter-value") > -1 || e.target.className.indexOf("fyc2018-pagination") > -1)) {
                        if (isOpenFilters) {
                            var urlTemplate = $("#filter-url-request").data("url");
                            var url = createUrl(urlTemplate) + "&onlyFilters=true";
                            updateFilters(url);
                            closeAllFiltersDiv();
                        }
                    }
                    if ($(window).width() < 768) {
                        $("html").removeClass("no-scroll-html");
                        $("body").removeClass("no-scroll-body");
                        $("body").removeClass("no-height-body");
                        window.iNoBounce.disable();
                        var target = $(".findyourcruise2018").offset().top;
                        if (isOpenFilters) {
                            $('html, body').animate({
                                scrollTop: target
                            }, 0);
                        }
                    }
                } catch (e) {
                    if ($(window).width() < 768) {
                        $("html").removeClass("no-scroll-html");
                        $("body").removeClass("no-scroll-body");
                        $("body").removeClass("no-height-body");
                        window.iNoBounce.disable();
                    }
                }
            }
        });

        $(".findyourcruise2018").on("click", ".fyc2018-header-mobile-filter", function (e) {
            e.preventDefault();
            e.stopPropagation();
            var isOpen = $(this).hasClass("fyc2018-filter-mobile-open");
            if (isOpen) {
                $(this).removeClass("fyc2018-filter-mobile-open");
                $(".findyourcruise2018 .fyc2018-filters-container").slideUp('slow');
            } else {
                $(this).addClass("fyc2018-filter-mobile-open");
                $(".findyourcruise2018 .fyc2018-filters-container").slideDown('slow');
            }
        });
    }
});