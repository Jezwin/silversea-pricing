$(function() {
    // Sort alphabetically
    function sortAlphabetically(a, b) {
        var x = a.title.toLowerCase();
        var y = b.title.toLowerCase();
        return x < y ? -1 : x > y ? 1 : 0;
    }

    $.fn.populateSelectFYC = function() {
        this.each(function() {
            var selectsFilter = [ 'destinations', 'cities', 'ships', 'types'];

            $.ajax({
                type : "GET",
                url : "/bin/cruises/search",
                contentType : "application/json",
                dataType : "json",
                success : function(json) {
                    selectsFilter.forEach(function(filterName) {
                        // Append option in select filter
                        buildOptions(json, filterName);

                        // update Chosen with the new content
                        $('.c-find-your-cruise-filter .chosen').trigger("chosen:updated");
                    });
                },
                failure : function(errMsg) {
                }
            });

            // functions utils
            function buildOptions(json, filterName) {
                if (filterName === 'features') {
                    json[filterName].slice(0).sort(sortAlphabetically).forEach(function(option) {
                        $('.' + filterName + '-filter').append($('<li>', {
                            value : option.id,
                            text : option.title,
                            'data-sscclicktype' : 'filters'
                        }));
                    });
                } else {
                    json[filterName].slice(0).sort(sortAlphabetically).forEach(function(option) {
                        $('.' + filterName + '-filter').append($('<option>', {
                            value : option.id,
                            text : option.title,
                            'data-sscclicktype' : 'filters'
                        }));
                    });
                }
            }
        });
    };

    $('.c-find-your-cruise-filter').populateSelectFYC();
});