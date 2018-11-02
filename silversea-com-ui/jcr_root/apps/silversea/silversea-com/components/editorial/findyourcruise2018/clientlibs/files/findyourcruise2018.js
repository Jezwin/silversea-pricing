$(function () {
    "use strict";

    $(".fyc2018-filter .fyc2018-filter-content span").on("click touhcstart", function (e) {
        e.preventDefault();
        e.stopPropagation();
        var $filter = $(this),
            classShowElement = "." + $filter.attr("id"),
            $parent = $filter.parent();

        if ($parent.hasClass("fyc2018-filter-content-clicked")) {
            $parent.removeClass("fyc2018-filter-content-clicked");
            $(classShowElement).removeClass("fyc2018-filter-value-clicked");
        } else {
            closeAllFiltersDiv();
            $(classShowElement).addClass("fyc2018-filter-value-clicked");
            $parent.addClass("fyc2018-filter-content-clicked");
        }
    });


    $(".fyc2018-filter .fyc2018-filter-value .filter-value").on({
        click: function (e) {
            e.preventDefault();
            e.stopPropagation();
            var idFilter = $(this).parent().data("filter");
            selectDisableFilter($(this));
            setNumberFilterSelected(idFilter);
            searchCruises();
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
    });

    $(document).on("click touchstart", function () {
        var $filter = $(".findyourcruise2018 .fyc2018-filter .fyc2018-filter-value-clicked");
        var isOpenFilters = $filter.length > 0;
        if (isOpenFilters) {
            closeAllFiltersDiv();
        }
    });

    function searchCruises() {
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
                url +=  (j > 1) ? "&" : url;
                url += $this.data('name') + "=" + urlFilter;
            }
        });
        var current = window.location.href;
        //window.location.href = encodeURI(current.substr(0, current.indexOf("html") + 4) + "?" + url);
        console.log(encodeURI(current.substr(0, current.indexOf("html") + 4) + "?" + url));
        url += "&onlyResults=true";
        url   = $("#filter-url-request").data("url") + ".html?" + url;

        $.ajax({
            type: 'GET',
            url: url,
            success: function (result) {
                console.log(result);
                $resultWrapper.html(result);
            }
        });


    };//searchCruises

    function setNumberFilterSelected(idFilter) {
        console.log("a");
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