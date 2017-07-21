$(function() {
    var $filter = $('.c-fyc-filter');
    var $btnReset = $filter.find('.c-fyc-filter__reset a');
    var $form = $filter.find('form.c-find-your-cruise-filter');

    /***************************************************************************
     * Sort alphabetically
     **************************************************************************/
    function sortAlphabetically(a, b) {
        var x = a.title.toLowerCase();
        var y = b.title.toLowerCase();
        return x < y ? -1 : x > y ? 1 : 0;
    }

    /***************************************************************************
     * Filter : get/update filter from json response
     **************************************************************************/
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

    /***************************************************************************
     * Filter : behavior on form change
     **************************************************************************/
    $form.on('change', function() {
        // Set active state on reset button
        var resetState, $currentForm = $(this), featureNumber = 0;

        $($currentForm.serializeArray()).each(function(i, field) {
            var $fieldwrapper = $('[name="' + field.name + '"]').closest('.single-filter');

            if (field.value !== 'all') {
                resetState = true;

                // Highlight filter
                $fieldwrapper.addClass('active');
            } else {
                // Remove highlight filter
                $fieldwrapper.removeClass('active');
            }

            if (field.name === 'features[]') {
                featureNumber++;
            }
        });

        if (resetState) {
            $btnReset.addClass('active');
        } else {
            $btnReset.removeClass('active');
        }

        // Show number of feature selected
        var $featureLabel = $currentForm.find('.features-filter').closest('.single-filter').find('.text-selected');
        var $featureFieldWrapper = $featureLabel.closest('.single-filter');

        // Highlight features filter
        if (featureNumber === 0) {
            $featureLabel.text($featureLabel.data('default-text'));
            $featureFieldWrapper.removeClass('active');
        } else if (featureNumber === 1) {
            $featureLabel.text(featureNumber + ' ' + $featureLabel.data('feature-text'));
            $featureFieldWrapper.addClass('active');
        } else {
            $featureLabel.text(featureNumber + ' ' + $featureLabel.data('features-text'));
            $featureFieldWrapper.addClass('active');
        }

        // Do request
        // ...
    });

    /***************************************************************************
     * Filter : reset form
     **************************************************************************/
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