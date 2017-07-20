$(function() {
    var $form = $('form.c-find-your-cruise-filter');

    // Sort alphabetically
    function sortAlphabetically(a, b) {
        var x = a.title.toLowerCase();
        var y = b.title.toLowerCase();
        return x < y ? -1 : x > y ? 1 : 0;
    }

    // Filter : get/update filter from json response
    $.fn.populateSelectFYC = function() {
        this.each(function() {
            var selectsFilter = [ 'destinations', 'cities', 'ships', 'types', 'durations', 'dates'];

            $.ajax({
                type : "GET",
                url : "/bin/cruises/search?language=" + $('html').attr('lang'),
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

            // Functions utils
            function buildOptions(json, filterName) {
                json[filterName].slice(0).sort(sortAlphabetically).forEach(function(option) {
                    $('.' + filterName + '-filter').append($('<option>', {
                        value : option.id,
                        text : option.title,
                        'data-sscclicktype' : 'filters'
                    }));
                });
            }
        });
    };

    $form.populateSelectFYC();

    // Filter : open feature drop down
    $('.features-filter').on('click', function(e) {
        e.stopPropagation();
    });

    // Filter : set active state / show number of feature selected

    $form.on('change', function() {
        
    });

    // Filter :  reset form
});