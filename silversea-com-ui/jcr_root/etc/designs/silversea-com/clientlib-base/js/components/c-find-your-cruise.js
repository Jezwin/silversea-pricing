$(function() {
    var $filter = $('.c-fyc-filter');

    if ($filter.length > 0) {
        var $btnReset = $filter.find('.c-fyc-filter__reset a'),
            $form = $filter.find('form.c-find-your-cruise-filter'),
            $paginationWrapper = $('.c-fyc-pagination'),
            $resultWrapper = $('.c-fyc__result-wrapper'),
            $page = $paginationWrapper.find('a.active').data('page');

        // Filter : open feature drop down
        $('.features-filter').on('click', function(e) {
            e.stopPropagation();
        });

        /***********************************************************************
         * Update Filter according to result : compare option list full and
         * option available
         **********************************************************************/
        var updateFilter = (function updateFilter() {
            $filter = $form.find('select');

            $filter.each(function() {
                var $select = $(this);
                var $optionList = $select.find('option');

                // Build obj with available option
                var filterAvailableObj = JSON.parse($('#' + $select.attr('name') +'-filter').text());

                // Disabled option not available
                $optionList.each(function() {
                    var $option = $(this);

                    $option.attr('disabled', filterAvailableObj[$option.val()] !== true);
                });

            });

            // Update chosen
            $form.find('.chosen').trigger('chosen:updated');

            // Update features filter
            var filterFeatureAvailableObj = JSON.parse($('#feature-filter').text());
            $form.find('.features-filter li').each(function() {
                var $item = $(this);
                if (filterFeatureAvailableObj[$item.find('input[name=feature]').val()] !== true) {
                    $item.addClass('disabled');
                }
            });

            return updateFilter;
        })();

        /***********************************************************************
         * Features : show features legend according to the current page
         **********************************************************************/
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
            dataLayer.track_destination_id = '';
            dataLayer.track_destination_name = '';
            dataLayer.track_voyage_id = $cruise.find('.cruise-code').text().trim();
            dataLayer.track_departure_date = $cruise.find('c-fyc__result__content__summary__item:first').text().trim();
            dataLayer.track_voyage_duration = $cruise.find('.c-fyc__result__content__summary__item--duration dd strong').text().trim();
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
                var $currentPage = $(e.target);

                if ($currentPage.closest('.c-fyc-pagination ul').length > 0) {

                    e.preventDefault();
                    e.stopPropagation();
                    $paginationWrapper.find('a').removeClass('active');
                    $currentPage.addClass('active');

                    // Update parameters just before build request
                    $page = $currentPage.data('page');

                    $form.trigger('change', [true]);

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
                $form.trigger('change', [false]);

                // Set disable style on reset button
                $btn.removeClass('active');
            }
        });

        /***************************************************************************
         * Filter : behavior on form change
         **************************************************************************/
        $form.on('change', function(e, isFromPagination) {
            // Set active state on reset button
            var resetState, $currentForm = $(this), featureNumber = 0, $filterValue = $($currentForm.serializeArray()), $paginationWrapper = $('.c-fyc-pagination');

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

                if (field.name === 'feature') {
                    featureNumber++;
                }
            });

            // Update reset style state
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

            // Build request URL with filter, pagination and number of result per page.
            var requestUrl = $currentForm.data('url');

            var featuresSelectorValue = [];
            $filterValue.each(function(i, field) {
                // Add filter
                if (field.name === 'feature') {
                    featuresSelectorValue.push(field.value.replace(/\//g, 'forwardSlash'));
                } else {
                    requestUrl = requestUrl + '.' + field.name + '_' + field.value.replace(/\//g, 'forwardSlash');
                }
            });

            // Add features
            if (featuresSelectorValue.length > 0) {
                requestUrl = requestUrl + '.features_' + featuresSelectorValue.join("|");
            } else {
                requestUrl = requestUrl + '.features_all';
            }

            // Add pagination
            $page = (isFromPagination === true) ? $page : '1';
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

                    // Update result count
                    $('#matching-value').text($('#count-filter').val());

                    // Update filter
                    updateFilter();

                    // Build feature legend according to the current result
                    featureListBuild();

                    // Set data layer key according to the current result
                    searchAnalytics();
                }
            });
        });
    }
});