$(function() {
    var $filter = $('.c-fyc-filter');
    var $btnReset = $filter.find('.c-fyc-filter__reset a');
    var $form = $filter.find('form.c-find-your-cruise-filter');

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
                        value : '/' + option.id + '/a/', // add slash only for testing url encoding
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

    // Filter : behavior on form change
    $form.on('change', function() {
        // Set active state on reset button
        var resetState, $currentForm = $(this);

        $($currentForm.serializeArray()).each(function(i, field) {
            if (field.value !== 'all') {
                resetState = true;
                return false;
            }
        });

        if (resetState) {
            $btnReset.addClass('active');
        } else {
            $btnReset.removeClass('active');
        }

        // Show number of feature selected
        // ...

        // Do request
        // ...
    });

    // Filter : reset form
    $btnReset.on('click', function(e) {
        e.preventDefault();
        var $btn = $(this);

        if ($btn.hasClass('active')) {
            // Reset form
            $form.trigger('reset');

            // Update select chosen plugin
            $form.find('.chosen').trigger('chosen:updated');

            // Force change event on form
            $form.trigger('change');

            // Set disable style on reset button
            $btn.removeClass('active');
        }
    });
});