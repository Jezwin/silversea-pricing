$(function () {
    "use strict";

    var fycContainerClass = ".findyourcruise2018",
        filterContainerClass = ".findyourcruise2018 .fyc2018-filter",
        autocompleteContainerClass = ".findyourcruise2018 .fyc2018-filter .fyc2018-filter-autocomplete-content",
        viewAllFilteredContainerClass = ".findyourcruise2018 .fyc2018-filter .filter-view-all-filtered",
        viewAllOriginalContainerClass = ".findyourcruise2018 .fyc2018-filter .filter-view-all-original",
        filterValueContainerClass = ".findyourcruise2018 .fyc2018-filter .filter-value",
        filterPortValueContainerClass = ".findyourcruise2018 .fyc2018-filter .filter-port .fyc2018-filter-autocomplete-content .filter-value",
        autocompleteInputId = ".findyourcruise2018 .fyc2018-filter #filter-autocomplete",
        showSelectedPortsClass = ".findyourcruise2018 .fyc2018-filter .filter-show-selected",
        selectedPortsContainerClass = ".findyourcruise2018 .fyc2018-filter .fyc2018-filter-selected-content",
        numElementToCreate = 100,
        indexElementFilteredToShow = 0,
        indexElementNotFilteredToShow = 100,
        elementFilteredToShow = [],
        elementFilterSelected = [],
        portsList = null,
        page = 1,
        onLoadFilterUrl = null;

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

    $(document).ready(function () {
        if ($(".findyourcruise2018").length > 0) {
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
        }
    });

    $(".findyourcruise2018 .fyc2018-pag-link").on("click", function (e) {
        e.preventDefault();
        e.stopPropagation();
        var page = $(this).data("page");
        page = $(this).hasClass("next") ? page + 1 : page;
        var urlTemplate = $("#results-url-request").data("url");
        var url = createUrl(urlTemplate) + "&onlyResults=true&pag=" + page;
        updateCruises(url);
        $('html, body').animate({
            scrollTop: $('.findyourcruise2018-header').first().offset().top - $('.c-header').height()
        }, 800);
    });

    $(fycContainerClass).on('click', ".filter-view-all-original", function (e) {
        e.preventDefault();
        e.stopPropagation();
        loadMoreElementNotFiltered();
    });

    $(fycContainerClass).on('click', ".filter-view-all-filtered", function (e) {
        e.preventDefault();
        e.stopPropagation();
        loadMoreElementFiltered();
    });

    $(fycContainerClass).on("click", ".fyc2018-header-reset-all", function (e) {
        e.preventDefault();
        e.stopPropagation();
        closeAllFiltersDiv();
        var urlTemplateFilter = $("#filter-url-request").data("url");
        var urlTemplateCruises = $("#results-url-request").data("url");
        var current = window.location.href;
        $(".fyc2018-header-reset-all").hide();
        $(".fyc2018-filters-container").removeClass("fyc2018-filters-container-clear-open");
        history.pushState(null, null, encodeURI(current.substr(0, current.indexOf("html") + 4)));

        updateFilters(urlTemplateFilter + ".html?onlyFilters=true");
        updateCruises(urlTemplateCruises + ".html?onlyResults=true", true);

    });

    $(fycContainerClass).on("click ", ".findyourcruise2018 .fyc2018-filter .filter-show-selected", function (e) {
        e.preventDefault();
        e.stopPropagation();
        if ($(this).hasClass("filter-show-selected-open")) {
            $(selectedPortsContainerClass).hide();
            $(selectedPortsContainerClass).html("");
            $(this).removeClass("filter-show-selected-open");
            $(autocompleteContainerClass).show();
        } else {
            $(autocompleteContainerClass).hide();
            for (var key in elementFilterSelected) {
                var item = elementFilterSelected[key];
                if (item != null) {
                    var status = item.state == "ENABLED" ? 'filter-no-selected' : item.state == "DISABLED" ? 'filter-disabled' : item.state == "CHOSEN" ? 'filter-selected' : '';
                    var $itemToRender = $("<div><span></span></div>");
                    $itemToRender.addClass("col-sm-12 filter-value filter-port-selected " + status);
                    $itemToRender.attr("data-key", item.key);
                    $itemToRender.attr("data-label", item.label);
                    $itemToRender.attr("data-state", item.state);
                    $itemToRender.find("span").html(item.label);
                    $(selectedPortsContainerClass).prepend($itemToRender);
                    $itemToRender.on("click ", function (e) {
                        e.stopPropagation();
                        e.preventDefault();
                        $(this).hide();
                        $(this).removeAttr("class");
                        var key = $(this).data("key");
                        $(filterPortValueContainerClass + '.filter-selected[data-key="' + key + '"]').each(function () {
                            $(this).attr("data-state", "ENABLED");
                            $(this).removeAttr("class");
                            $(this).addClass("col-sm-12 filter-value filter-value filter-no-selected");
                        });
                        elementFilterSelected[key] = null;
                        setNumberFilterSelectedPorts();
                        var urlTemplate = $("#results-url-request").data("url");
                        var url = createUrl(urlTemplate) + "&onlyResults=true";
                        updateCruises(url);
                    });
                }
            }
            $(selectedPortsContainerClass).show();
            $(this).addClass("filter-show-selected-open");
        }
    });

    //REFACTOR DONE
    function showFilteredElement() {
        elementFilteredToShow = [];
        indexElementFilteredToShow = 0;
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

    //REFACTOR DONE
    function loadMoreElementNotFiltered() {
        var i = 0, item = null;
        while (i++ <= numElementToCreate && indexElementNotFilteredToShow < portsList.length) {
            item = portsList[indexElementNotFilteredToShow++];
            if (elementFilterSelected[item.key] != null) {
                item.state = elementFilterSelected[item.key].state;
            }
            var status = item.state == 'ENABLED' ? 'filter-no-selected' : item.state == 'DISABLED' ? 'filter-disabled' : item.state == 'CHOSEN' ? 'filter-selected' : '';
            var $itemToRender = $("<div><span></span></div>");
            $itemToRender.addClass("col-sm-12 filter-value " + status);
            $itemToRender.attr("data-key", item.key);
            $itemToRender.attr("data-label", item.label);
            $itemToRender.attr("data-state", item.state);
            $itemToRender.find("span").html(item.label);
            $(autocompleteContainerClass).append($itemToRender);
        }
        $(viewAllFilteredContainerClass).hide();
        if (indexElementNotFilteredToShow >= portsList.length) {
            $(viewAllOriginalContainerClass).hide();
        } else {
            $(autocompleteContainerClass).append($(viewAllOriginalContainerClass));
            $(viewAllOriginalContainerClass).show();
        }
    };//loadMoreElementNotFiltered

    //REFACTOR DONE
    function loadMoreElementFiltered() {
        var i = 0, item = null;
        while (i++ <= numElementToCreate && indexElementFilteredToShow < elementFilteredToShow.length) {
            item = elementFilteredToShow[indexElementFilteredToShow++];
            if (elementFilterSelected[item.key] != null) {
                item.state = elementFilterSelected[item.key].state;
            }
            var status = item.state == 'ENABLED' ? 'filter-no-selected' : item.state == 'DISABLED' ? 'filter-disabled' : item.state == 'CHOSEN' ? 'filter-selected' : '';
            var $itemToRender = $("<div><span></span></div>");
            $itemToRender.addClass("col-sm-12 filter-value " + status);
            $itemToRender.attr("data-key", item.key);
            $itemToRender.attr("data-label", item.label);
            $itemToRender.attr("data-state", item.state);
            $itemToRender.find("span").html(item.label);
            $(autocompleteContainerClass).append($itemToRender);
        }
        $(viewAllOriginalContainerClass).hide();
        if (numElementToCreate >= elementFilteredToShow.length) {
            $(viewAllFilteredContainerClass).hide();
        } else {
            $(autocompleteContainerClass).append($(viewAllFilteredContainerClass));
            $(viewAllFilteredContainerClass).show();
        }
    };//loadMoreElementFiltered

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
            updateFilters(url);
        } else {
            if ($(window).width() < 768) {
                $("html").addClass("no-scroll-html");
                $("body").addClass("no-scroll-body");
                $("body").addClass("no-height-body");
                $(".parbase:not(.findyourcruise2018)").addClass("no-visible-element");
                $(".findyourcruise2018 .fyc2018-results-div").addClass("no-visible-element");
                $(".c-footer").addClass("no-visible-element");
                window.iNoBounce.enable();
                var numberResults = $(".findyourcruise2018 .fyc2018-header-total-num").text();
                setNumberResultOnMobileBtn(numberResults);
            }
            closeAllFiltersDiv();
            if (classShowElement != null && $parent != null && idShowElement != null) {
                $(classShowElement).addClass("fyc2018-filter-value-clicked");
                $(idShowElement).parent().addClass("fyc2018-filter-content-clicked");
                $parent.addClass("fyc2018-filter-content-clicked");
                if (classShowElement == ".filter-port") {
                    optionAutoComplete.data = portsList;
                    $(".findyourcruise2018 #filter-autocomplete").easyAutocomplete(optionAutoComplete);
                    $(".findyourcruise2018 .easy-autocomplete-container ul").remove();
                    $(".findyourcruise2018 #filter-autocomplete").focus().select();
                    $(".findyourcruise2018 #filter-autocomplete").keyup(function () {
                        if ($(this).val().trim().length == 0) {
                            //$(autocompleteContainerClass + " .filter-value").html("");
                            $(autocompleteContainerClass + " .filter-value").hide();
                            indexElementNotFilteredToShow = 0;
                            loadMoreElementNotFiltered();
                            $(autocompleteContainerClass + " .filter-no-ports").hide();
                        }
                    });
                    $('.findyourcruise2018 .fyc2018-filter-autocomplete-content').animate({
                        scrollTop: $('.findyourcruise2018 .fyc2018-filter-autocomplete-content .filter-no-selected').first().position().top - $('.findyourcruise2018 .fyc2018-filter-autocomplete-content .filter-no-selected').first().height() - 43
                    }, 800);
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
            setNumberFilterSelected(idFilter);
            var urlTemplate = $("#results-url-request").data("url");
            var url = createUrl(urlTemplate) + "&onlyResults=true";
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

    function checkNumberSelectedFilters() {
        var length = $(".findyourcruise2018 .fyc2018-filter .filter-value.filter-selected:not(.filter-port-selected)").length;
        if (length > 0) {
            $(".fyc2018-header-reset-all").show();
            $(".fyc2018-filters-container").addClass("fyc2018-filters-container-clear-open");
        } else {
            $(".fyc2018-header-reset-all").hide();
            $(".fyc2018-filters-container").removeClass("fyc2018-filters-container-clear-open");
        }
    };//checkNumberSelectedFilters

    $(document).on("click ", function (e) {
        var $filter = $(".findyourcruise2018 .fyc2018-filter .fyc2018-filter-value-clicked");
        var isOpenFilters = $filter.length > 0;
        if (!(e.target.className.indexOf("fyc2018-filter-autocomplete") > -1 || e.target.className.indexOf("filter-value") > -1)) {
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
            $(".parbase:not(.findyourcruise2018)").removeClass("no-visible-element");
            $(".findyourcruise2018 .fyc2018-results-div").removeClass("no-visible-element");
            $(".c-footer").removeClass("no-visible-element");
            var target = $(".findyourcruise2018").offset().top;
            if (isOpenFilters) {
                $('html, body').animate({
                    scrollTop: target
                }, 0);
            }
        }
    });

    function separateYears() {
        var year = null;
        $(".filter-departure .filter-value").each(function () {
            var currentYear = $(this).data("key").split("-")[0];
            if (currentYear != year) {
                $(this).before("<div class='col-xs-12 fyc2018-filter-year'>" + currentYear + "</div>");
                year = currentYear;
            }
        });
    };//separateYears

    function createUrl(urlTemplate) {
        var url = "";
        var j = 0;
        $(".fyc2018-filter .fyc2018-filter-value").each(function () {
            var $this = $(this);
            var $selectedElements = $this.find(".filter-value.filter-selected:not(.filter-port-selected)");
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
        var current = window.location.href;
        history.pushState(null, null, encodeURI(current.substr(0, current.indexOf("html") + 4) + "?" + url));
        return urlTemplate + ".html?" + url;
    };//createUrl

    function updateCruises(url, removeScroll) {
        $.ajax({
            type: 'GET',
            url: url,
            success: function (result) {
                var $divToReplace = $(".findyourcruise2018 .fyc2018-results-div");
                $divToReplace.html(result);
                var numbersToUpdate = $divToReplace.find(".c-fyc-v2__results__wrapper").data("totals");
                $(".findyourcruise2018 .fyc2018-header-total-num").html(numbersToUpdate);
                setNumberResultOnMobileBtn(numbersToUpdate);
                console.log(removeScroll);
                if ($(window).width() < 768 && !removeScroll) {
                    $("html").addClass("no-scroll-html");
                    $("body").addClass("no-scroll-body");
                    $("body").addClass("no-height-body");
                    $(".parbase:not(.findyourcruise2018)").addClass("no-visible-element");
                    $(".findyourcruise2018 .fyc2018-results-div").addClass("no-visible-element");
                    $(".c-footer").addClass("no-visible-element");
                    window.iNoBounce.enable();
                }
            }
        });
    };//updateCruises

    function setNumberResultOnMobileBtn(numbersToUpdate) {
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

    function updateFilters(url, classElement, parent, idShowElement) {
        $.ajax({
            type: 'GET',
            url: url,
            success: function (result) {
                var showReset = $(".fyc2018-header-reset-all").is(":visible");
                var showFilterMobile = $(".fyc2018-header-mobile-filter").hasClass("fyc2018-filter-mobile-open");
                $(".findyourcruise2018-header").replaceWith(result);
                setNumberAllFilterSelected();
                separateYears();
                try {
                    portsList = JSON.parse(window.portsList);
                } catch (e) {
                    console.error("Error Port list");
                }

                if (showReset) {
                    $(".fyc2018-header-reset-all").show();
                    $(".fyc2018-filters-container").addClass("fyc2018-filters-container-clear-open");
                }
                if (showFilterMobile && $(window).width() < 768) {
                    $(".fyc2018-header-mobile-filter").addClass("fyc2018-filter-mobile-open");
                    $(".fyc2018-filters-container").show();
                }
                searchAnalytics();
            }
        });
    };//updateFilters

    $(".findyourcruise2018").on("click", "fyc2018-header-mobile-filter",function () {
        var isOpen = $(this).hasClass("fyc2018-filter-mobile-open");
        if (isOpen){
            $(this).removeClass("fyc2018-filter-mobile-open");
            $(".findyourcruise2018 .fyc2018-filters-container").slideUp('slow');
        } else {
            $(this).addClass("fyc2018-filter-mobile-open");
            $(".findyourcruise2018 .fyc2018-filters-container").slideDown('slow');
        }
    });

    function setNumberAllFilterSelected() {
        $(".findyourcruise2018 .fyc2018-filter-value").each(function () {
            var idFilter = $(this).data("filter");
            setNumberFilterSelected(idFilter);
        });
    };//setNumberAllFilterSelected

    function setNumberFilterSelected(idFilter) {
        var numberSelected = $("." + idFilter + " .filter-value.filter-selected:not(.filter-port-selected)").length;

        if (idFilter == "filter-port") {
            if (numberSelected > 0) {
                $(showSelectedPortsClass).show();
                $(".fyc2018-filter-autocomplete-content").addClass("fyc2018-filter-autocomplete-content-open");
                $(showSelectedPortsClass).find("span").text(numberSelected + " " + $(showSelectedPortsClass).data("label"));
            } else {
                $(showSelectedPortsClass).hide();
                $(".fyc2018-filter-autocomplete-content").removeClass("fyc2018-filter-autocomplete-content-open");
                $(selectedPortsContainerClass).hide();
            }
        }
        var type = (numberSelected > 1) ? "plural" : 'singular';
        var label = $("#" + idFilter).data(type);
        numberSelected = numberSelected == 0 ? "" : numberSelected;
        $("#" + idFilter).text(numberSelected + " " + label);
        if ($(window).width() < 768) {
            $("." + idFilter + " .fyc2018-filter-label-mobile").text(numberSelected + " " + label);
        }

    };//setNumberFilterSelected

    function setNumberFilterSelectedPorts() {
        var numberSelected = $(".filter-port .fyc2018-filter-selected-content .filter-value").length;

        if (numberSelected > 0) {
            $(showSelectedPortsClass).show();
            $(showSelectedPortsClass).find("span").text(numberSelected + " " + $(showSelectedPortsClass).data("label"));
        } else {
            $(showSelectedPortsClass).hide();
            $(selectedPortsContainerClass).hide();
            $(autocompleteContainerClass).show();
        }
        var type = (numberSelected > 1) ? "plural" : 'singular';
        var label = $("#filter-port").data(type);
        numberSelected = numberSelected == 0 ? "" : numberSelected;
        $("#filter-port").text(numberSelected + " " + label);
    };//setNumberFilterSelected


    function selectDisableFilter($filter) {

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

    function closeAllFiltersDiv() {
        $(".fyc2018-filter-content-clicked, .fyc2018-filter-value-clicked").each(function () {
            $(this).removeClass("fyc2018-filter-value-clicked");
            $(this).removeClass("fyc2018-filter-content-clicked");
        });
    };//closeAllFiltersDiv

    function searchAnalytics() {
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

    function backMobile() {
        window.addEventListener('popstate', function (event) {
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
                $(".parbase:not(.findyourcruise2018)").removeClass("no-visible-element");
                $(".findyourcruise2018 .fyc2018-results-div").removeClass("no-visible-element");
                $(".c-footer").removeClass("no-visible-element");
            }
        }, false);
    };//backMobile
});