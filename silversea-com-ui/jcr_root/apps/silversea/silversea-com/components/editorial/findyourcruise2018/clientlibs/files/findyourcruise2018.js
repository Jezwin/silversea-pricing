$(function () {
    "use strict";

    var portsList = null;
    var elementToShow = 0;
    var optionAutoComplete = {
        getValue: "label",
        list: {
            match: {
                enabled: true
            },
            maxNumberOfElements: 8000,
            onLoadEvent: function () {
                var i = 0;
                var item = $("#filter-autocomplete").getItemData(i);
                console.log(item, item.state);
                var $listItem = $(".fyc2018-filter-autocomplete-content .filter-value");
                var $itemToRender = null;
                while (item != -1) {
                    $itemToRender = $($listItem[i]);
                    var status = item.state == 'ENABLED' ? 'filter-no-selected' : item.state == 'DISABLED' ? 'filter-disabled' : item.state == 'CHOSEN' ? 'filter-selected' : '';
                    $itemToRender.removeAttr("class");
                    $itemToRender.addClass("col-sm-12 filter-value " + status);
                    $itemToRender.data("value", item.key);
                    $itemToRender.find("span").html(item.label);
                    $itemToRender.show();
                    item = $("#filter-autocomplete").getItemData(++i);
                }
                while ($itemToRender.length > 0) {
                    $itemToRender = $($listItem[i++]);
                    $itemToRender.hide();
                }
            }
        }
    };


    $(document).ready(function () {
        setNumberAllFilterSelected();
        separateYears();
        portsList = JSON.parse(window.portsList);
    });


    $(".findyourcruise2018").on("click touhcstart", ".fyc2018-filter .fyc2018-filter-content span", function (e) {
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
            updateFilters(url, classShowElement, $parent,idShowElement);
            closeAllFiltersDiv();
        }
    });

    $(".findyourcruise2018").on({
        click: function (e) {
            e.preventDefault();
            e.stopPropagation();
            var idFilter = $(this).parent().parent().data("filter");
            selectDisableFilter($(this));
            setNumberFilterSelected(idFilter);
            console.log(idFilter);
            var urlTemplate = $("#results-url-request").data("url");
            var url = createUrl(urlTemplate) + "&onlyResults=true";
            updateCruises(url);
        },
        touchstart: function () {
            selectDisableFilter($(this));
        },
        mouseover: function () {
            var $filter = $(this);
            if ($filter.hasClass("filter-selected")) {
                $filter.addClass("filter-selected-hover");
            } else if ($filter.hasClass("filter-no-selected")) {
                $filter.addClass("filter-no-selected-hover");
            }
        }
    }, ".fyc2018-filter .fyc2018-filter-value .filter-value");

    $(document).on("click touchstart", function (e) {
        if (!(e.target.className.indexOf("fyc2018-filter-autocomplete") > -1 || e.target.className.indexOf("filter-value") > -1)){
            var $filter = $(".findyourcruise2018 .fyc2018-filter .fyc2018-filter-value-clicked");
            var isOpenFilters = $filter.length > 0;
            if (isOpenFilters) {
                var urlTemplate = $("#filter-url-request").data("url");
                var url = createUrl(urlTemplate)  + "&onlyFilters=true";
                updateFilters(url);
                closeAllFiltersDiv();
            }
        }
    });

    function separateYears() {
        var year = null;
        $(".filter-departure .filter-value").each(function () {
            var currentYear = $(this).data("value").split("-")[0];
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
            var $selectedElements = $this.find(".filter-value.filter-selected");
            var i = 0;
            var urlFilter = "";
            $selectedElements.each(function () {
                urlFilter += $(this).data('value');
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

    function tmp() {
        $(".findyourcruise2018 .fyc2018-filter-autocomplete-content").on("scroll", function () {
            var scrollTop = $(".fyc2018-filter-autocomplete-content").scrollTop();
            var isBottom = $(this).scrollTop() + $(this).innerHeight() >= $(this)[0].scrollHeight;
            if (isBottom) {
                var elementVisible = 0;
                if (elementVisible < JSON.parse(window.portsList).length) {
                    elementVisible = elementVisible + 30;
                    $(".fyc2018-filter-autocomplete-content .filter-value").each(function (index) {
                        var item = JSON.parse(window.portsList)[index + elementVisible];
                        if (item != null) {
                            var $itemToRender = $(this);
                            var status = item.state == 'ENABLED' ? 'filter-no-selected' : item.state == 'DISABLED' ? 'filter-disabled' : item.state == 'CHOSEN' ? 'filter-selected' : '';
                            $itemToRender.removeAttr("class");
                            $itemToRender.addClass("col-sm-12 filter-value " + status);
                            $itemToRender.data("value", item.key);
                            $itemToRender.find("span").html(item.label);
                            $itemToRender.show();
                        }
                    });
                }
                $(".findyourcruise2018 .fyc2018-filter-autocomplete-content").scrollTop(0);
            }
        });
    }

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
        var numberSelected = $("." + idFilter + " .filter-value.filter-selected").length;
        var type = (numberSelected > 1) ? "plural" : 'singular';
        var label = $("#" + idFilter).data(type);
        numberSelected = numberSelected == 0 ? "" : numberSelected;
        $("#" + idFilter).text(numberSelected + " " + label);
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
    };//selectDisableFilter

    function closeAllFiltersDiv() {
        $(".fyc2018-filter-content-clicked, .fyc2018-filter-value-clicked").each(function () {
            $(this).removeClass("fyc2018-filter-value-clicked");
            $(this).removeClass("fyc2018-filter-content-clicked");
        });
    };//closeAllFiltersDiv

});