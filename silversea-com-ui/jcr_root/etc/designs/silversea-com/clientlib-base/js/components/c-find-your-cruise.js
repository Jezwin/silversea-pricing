$(function() {
    var $filter = $('.c-fyc-filter'),
    $btnReset = $filter.find('.c-fyc-filter__reset a'),
    $form = $filter.find('form.c-find-your-cruise-filter'),
    $paginationWrapper = $('.c-fyc-pagination'),
    $resultWrapper = $('.c-fyc__result-wrapper'),
    $page = $paginationWrapper.find('a.active').data('page');

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
            var selectsFilter = [ 'destinations', 'cities', 'ships', 'types', 'durations', 'dates' ];

            $.ajax({
                type : 'GET',
                url : '/bin/cruises/search?language=' + $form.data('lang'),
                contentType : 'application/json',
                dataType : 'json',
                success : function(json) {
                    selectsFilter.forEach(function(filterName) {
                        // Append option in select filter
                        buildOptions(json, filterName);

                        // update Chosen with the new content
                        $('.c-find-your-cruise-filter .chosen').trigger('chosen:updated');
                    });
                }
            });

            // Functions utils
            function buildOptions(json, filterName) {
                json[filterName].slice(0).sort(sortAlphabetically).forEach(function(option) {
                    $('.' + filterName + '-filter').append($('<option>', {
                        value : option.id,
                        text : option.title,
                        'data-sscclicktype' : 'filters',
                        'data-value' : ''
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
     * Features : show features legend according to the current page
     **************************************************************************/
    var featureListBuild = (function featureListBuild() {
        var template = '<span><i></i></span>';
        var featureList = {};
        var $featureWrapper = $('.feature-wrapper');

        // Create feature list without doublon
        $resultWrapper.find('.meta_feature_inner').each(function() {
            var $currentFeature = $(this);
            featureList[$currentFeature.find('i').attr('class')] = $currentFeature.find('.tooltip').text();
        });

        for ( var key in featureList) {
            $featureWrapper.append(template);
            $featureWrapper.find('span:last i').addClass(key);
            $featureWrapper.find('span:last').append(featureList[key]);
        }

        return featureListBuild;
    })();

    /***************************************************************************
     * Filter : analytics, set datalayer object according to the filter
     **************************************************************************/
    var searchAnalytics = (function searchAnalytics() {
        var dataLayer = window.dataLayer[0];

        // Data search
        var filterOjb = {};
        $('.c-find-your-cruise-filter').find('select').each(function(i, element) {
            filterOjb[element.name] = $(element).find(':selected').data('value') || element.value;
        });

        $('.c-find-your-cruise-filter').find('input:checked').each(function(i, element) {
            filterOjb[element.name.replace('[]', '[' + i + ']')] = $(element).data('value');
        });

        dataLayer.search_filters = filterOjb;
        dataLayer.search_page_number = $page;
        dataLayer.search_results_number = $('#matching-value').text();

        // Data from first result
        $cruise = $resultWrapper.find('.c-fyc__result:first');
        dataLayer.track_destination_id ='';
        dataLayer.track_destination_name = '';
        dataLayer.track_voyage_id = $cruise.find('.cruise-code').text().trim();
        dataLayer.track_departure_date = $cruise.find('c-fyc__result__content__summary__item:first').text().trim();
        dataLayer.track_voyage_duration = $cruise.find('.c-fyc__result__content__summary__item--duration dd strong').text().trim(),
        dataLayer.track_voyage_departure_harbor = $cruise.find('.c-fyc__result__content__itinerary dd:first').text().trim();
        dataLayer.track_voyage_arrival_harbor = $cruise.find('.c-fyc__result__content__itinerary dd:last').text().trim();
        dataLayer.track_voyage_type = $cruise.find('.cruise-type').text().trim();
        dataLayer.track_shipname = $cruise.find('.cruise-ship').text().trim();
        dataLayer.track_revenue = $cruise.find('.c-fyc__result__content__price strong').text().trim();
        dataLayer.track_suite = '';

        return searchAnalytics;
    })();

    /***************************************************************************
     * Pagination
     **************************************************************************/
    var pagination = (function pagination() {
        $paginationWrapper = $('.c-fyc-pagination');
        $resultWrapper.on('click', $paginationWrapper.find('a'), function(e) {
            e.preventDefault();
            var $currentPage = $(e.target);

            if ($currentPage.is('.c-fyc-pagination a')) {
                $paginationWrapper.find('a').removeClass('active');
                $currentPage.addClass('active');

                $form.trigger('change');

                // Scroll to filter
                $('html, body').animate({
                    scrollTop : $('.c-fyc-filter').first().offset().top - $('.c-header').offset().top
                }, 800);
            }
        });
        return pagination;
    })();

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

    /***************************************************************************
     * Filter : behavior on form change
     **************************************************************************/
    $form.on('change', function() {
        // Set active state on reset button
        var resetState,
        $currentForm = $(this),
        featureNumber = 0,
        $filterValue = $($currentForm.serializeArray()),
        $paginationWrapper = $('.c-fyc-pagination');

        $filterValue.each(function(i, field) {
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

        // Build request URL with filter, pagination and number of result per
        // page.
        var requestUrl = $currentForm.data('url');

        $filterValue.each(function(i, field) {
            // Add filter
            requestUrl = requestUrl + '.' + field.name + '_' + field.value.replace(/\//g, 'forwardSlash');
            //requestUrl = requestUrl + '.' + field.name + '_' + encodeURIComponent(field.value);
        });

        // Add pagination
        requestUrl = requestUrl + '.page_' + $page;

        // Add limit
        requestUrl = requestUrl + '.limit_' + $form.data('limit');

        // Add extension
        requestUrl = requestUrl + '.html';

        // Update result according to the request URL
        $.ajax({
            type : 'GET',
            url : requestUrl,
            success : function(result) {
                $resultWrapper.html(result);

                // Build feature legend according to the current result
                featureListBuild();

                // Set data layer key according to the current result
                searchAnalytics();
            }
        });
    });
});