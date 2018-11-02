$(function () {
    "use strict";

    $(document).ready(function() {
        setNumberAllFilterSelected();
    });

    $(".findyourcruise2018").on("click touhcstart", ".fyc2018-filter .fyc2018-filter-content span", function (e) {
        e.preventDefault();
        e.stopPropagation();
        var $filter = $(this),
            classShowElement = "." + $filter.attr("id"),
            $parent = $filter.parent();

        if ($parent.hasClass("fyc2018-filter-content-clicked")) {
            $parent.removeClass("fyc2018-filter-content-clicked");
            $(classShowElement).removeClass("fyc2018-filter-value-clicked");
            var urlTemplate = $("#filter-url-request").data("url");
            var url = createUrl(urlTemplate);
            url += "&onlyFilters=true";
            updateFilters(url);
        } else {
            closeAllFiltersDiv();
            $(classShowElement).addClass("fyc2018-filter-value-clicked");
            $parent.addClass("fyc2018-filter-content-clicked");
        }
    });

    $(".findyourcruise2018").on({
        click: function (e) {
            e.preventDefault();
            e.stopPropagation();
            var idFilter = $(this).parent().data("filter");
            selectDisableFilter($(this));
            setNumberFilterSelected(idFilter);
            var urlTemplate = $("#results-url-request").data("url");
            var url = createUrl(urlTemplate);
            url += "&onlyResults=true";
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

    $(document).on("click touchstart", function () {
        var $filter = $(".findyourcruise2018 .fyc2018-filter .fyc2018-filter-value-clicked");
        var isOpenFilters = $filter.length > 0;
        if (isOpenFilters) {
            var urlTemplate = $("#filter-url-request").data("url");
            var url = createUrl(urlTemplate);
            url += "&onlyFilters=true";
            updateFilters(url);
            closeAllFiltersDiv();
        }
    });

    function createUrl(urlTemplate) {
        var url = "";
        var j = 0;
        $(".fyc2018-filter .fyc2018-filter-value").each(function () {
            j++;
            var $this = $(this);
            var $selectedElements = $this.find(".filter-value.filter-selected");
            var i = 0;
            var urlFilter = "";
            $selectedElements.each(function () {
                urlFilter += $(this).data('value');
                urlFilter = (++i < $selectedElements.length) ? urlFilter += "." : urlFilter;
            });
            if (urlFilter != "") {
                url += (j > 1) ? "&" : url;
                url += $this.data('name') + "=" + urlFilter;
            }
        });
        var current = window.location.href;
        //window.location.href = encodeURI(current.substr(0, current.indexOf("html") + 4) + "?" + url);
        console.log(encodeURI(current.substr(0, current.indexOf("html") + 4) + "?" + url));
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

    function updateFilters(url) {
        $.ajax({
            type: 'GET',
            url: url,
            success: function (result) {
                $(".findyourcruise2018-header").replaceWith(result);
                setNumberAllFilterSelected();
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