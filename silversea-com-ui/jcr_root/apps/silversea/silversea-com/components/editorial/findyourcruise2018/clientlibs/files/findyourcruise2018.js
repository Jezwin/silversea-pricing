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
        portsList = null;

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
        setNumberAllFilterSelected();
        separateYears();
        portsList = JSON.parse(window.portsList);
    });

    $(fycContainerClass).on('click', ".filter-view-all-original", function (e) {
        e.preventDefault();
        e.stopPropagation();
        loadMoreElementNotFiltered();
    });

    $(fycContainerClass).on("click touchstart", ".findyourcruise2018 .fyc2018-filter .filter-show-selected", function (e) {
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
                    $itemToRender.on("click touchstart", function (e) {
                        e.stopPropagation();
                        e.preventDefault();
                        $(this).hide();
                        $(this).removeAttr("class");
                        var key = $(this).data("key");
                        $(filterPortValueContainerClass+'.filter-selected[data-key="'+ key +'"]').each(function () {
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
            $(this).addClass("filter-show-selected-open")
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
        while (item != -1) {
            $itemToRender = $($listItem[index]);
            if ($itemToRender.length > 0) {
                if (elementFilterSelected[item.key] != null) {
                    item.state = elementFilterSelected[item.key].state;
                }
                var status = item.state == 'ENABLED' ? 'filter-no-selected' : item.state == 'DISABLED' ? 'filter-disabled' : item.state == 'CHOSEN' ? 'filter-selected' : '';
                $itemToRender.removeAttr("class");
                //$itemToRender.removeAttr("data-key");
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
            //$(autocompleteContainerClass).css("overflow-y", "hidden");
            $(viewAllFilteredContainerClass).hide();
            $(viewAllOriginalContainerClass).hide();
        } else {
            $(autocompleteContainerClass).css("overflow-y", "scroll");
        }
        if (elementFilteredToShow.length > 0) {
            var $viewAllFiltered = $('<div class="col-sm-12 filter-view-all filter-view-all-filtered"></div>');
            $viewAllFiltered.append($(viewAllOriginalContainerClass).html());
            $(autocompleteContainerClass).append($viewAllFiltered);
            $viewAllFiltered.on("click touchstart", function (e) {
                e.preventDefault();
                e.stopPropagation();
                loadMoreElementFiltered();
            });
        } else {
            while ($itemToRender.length > 0) {
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
        if (numElementToCreate >= elementFilteredToShow.length) {
            $(viewAllFilteredContainerClass).hide();
        } else {
            $(viewAllFilteredContainerClass).show();
        }
    };//loadMoreElementFiltered

    $(fycContainerClass).on("click touchstart", ".fyc2018-filter .fyc2018-filter-content span", function (e) {
        e.preventDefault();
        e.stopPropagation();

        var $filter = $(this),
            classShowElement = "." + $filter.attr("id"),
            idShowElement = "#" + $filter.attr("id"),
            $parent = $filter.parent();
        var urlTemplate = $("#filter-url-request").data("url");
        var url = createUrl(urlTemplate);
        url += "&onlyFilters=true";

        if ($parent.hasClass("fyc2018-filter-content-clicked")) {
            $parent.removeClass("fyc2018-filter-content-clicked");
            $(classShowElement).removeClass("fyc2018-filter-value-clicked");
            updateFilters(url);
        } else {
            updateFilters(url, classShowElement, $parent, idShowElement);
            closeAllFiltersDiv();
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
        },
        touchstart: function (e) {
            e.preventDefault();
            e.stopPropagation();
            var idFilter = $(this).parent().parent().data("filter");
            selectDisableFilter($(this));
            setNumberFilterSelected(idFilter);
            var urlTemplate = $("#results-url-request").data("url");
            var url = createUrl(urlTemplate) + "&onlyResults=true";
            updateCruises(url);
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

    $(document).on("click touchstart", function (e) {
        if (!(e.target.className.indexOf("fyc2018-filter-autocomplete") > -1 || e.target.className.indexOf("filter-value") > -1)) {
            var $filter = $(".findyourcruise2018 .fyc2018-filter .fyc2018-filter-value-clicked");
            var isOpenFilters = $filter.length > 0;
            if (isOpenFilters) {
                var urlTemplate = $("#filter-url-request").data("url");
                var url = createUrl(urlTemplate) + "&onlyFilters=true";
                updateFilters(url);
                closeAllFiltersDiv();
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

    function updateCruises(url) {
        $.ajax({
            type: 'GET',
            url: url,
            success: function (result) {
                var $divToReplace = $(".findyourcruise2018 .fyc2018-results-div");
                $divToReplace.html(result);
                var numbersToUpdate = $divToReplace.find(".c-fyc-v2__results__wrapper").data("totals");
                $(".findyourcruise2018 .fyc2018-header-total-num").html(numbersToUpdate);
            }
        });
    };//updateCruises

    function updateFilters(url, classElement, parent, idShowElement) {
        $.ajax({
            type: 'GET',
            url: url,
            success: function (result) {
                $(".findyourcruise2018-header").replaceWith(result);
                setNumberAllFilterSelected();
                separateYears();
                portsList = JSON.parse(window.portsList);
                if (classElement != null && parent != null && idShowElement != null) {
                    $(classElement).addClass("fyc2018-filter-value-clicked");
                    $(idShowElement).parent().addClass("fyc2018-filter-content-clicked");
                    parent.addClass("fyc2018-filter-content-clicked");
                    if (classElement == ".filter-port") {
                        optionAutoComplete.data = portsList;
                        $(".findyourcruise2018 #filter-autocomplete").easyAutocomplete(optionAutoComplete);
                        $(".findyourcruise2018 .easy-autocomplete-container ul").remove();
                        $(".findyourcruise2018 #filter-autocomplete").focus().select();
                        $(".findyourcruise2018 #filter-autocomplete").keyup(function () {
                            if ($(this).val().trim().length == 0) {
                                $(autocompleteContainerClass).html("");
                                indexElementNotFilteredToShow = 0;
                                loadMoreElementNotFiltered();
                            }
                        });
                    }
                }
            }
        });
    };//updateFilters

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
                $(showSelectedPortsClass).find("span").text(numberSelected + " " + $(showSelectedPortsClass).data("label"));
            } else {
                $(showSelectedPortsClass).hide();
                $(selectedPortsContainerClass).hide();
            }
        }
        var type = (numberSelected > 1) ? "plural" : 'singular';
        var label = $("#" + idFilter).data(type);
        numberSelected = numberSelected == 0 ? "" : numberSelected;
        $("#" + idFilter).text(numberSelected + " " + label);
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

});