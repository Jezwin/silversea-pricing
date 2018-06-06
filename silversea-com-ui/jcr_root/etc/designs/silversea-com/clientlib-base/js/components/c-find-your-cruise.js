var v1 = function () {
    var $filterWrapper = $('.c-fyc-filter');
    var firstUpdateFilter = true;
    if ($filterWrapper.length > 0) {
        var $btnReset = $filterWrapper.find('.c-fyc-filter__reset a');
        var $form = $('#filter-form');
        var $paginationWrapper = $('.c-fyc-pagination');
        var $resultWrapper = $('.c-fyc__result-wrapper');
        var $page = $paginationWrapper.find('a.active').data('page');

        // Filter : open feature drop down
        $('.feature-filter').on('click', function (e) {
            e.stopPropagation();
            if ($(e.target).closest('li').hasClass('disabled')) {
                e.preventDefault();
            }
        });

        /***********************************************************************
         * Update Filter according to result : compare option list full and
         * option available
         **********************************************************************/
        var updateFilter = (function updateFilter() {
            $filter = $form.find('select');

            $filter.each(function () {
                var $select = $(this);
                var $optionList = $select.find('option');

                // Build obj with available option
                var jsonStr = $resultWrapper.find('#' + $select.attr('name') + '-filter').data('ssc-filter');

                if (jsonStr !== '') {
                    var filterAvailableObj = jsonStr;

                    // Disabled option not available
                    $optionList.each(function () {
                        try {
                            var $option = $(this);
                            $option.attr('disabled', filterAvailableObj[$option.val()] !== true);
                        } catch (error) {
                            console.log('init issue');
                        }
                    });
                }
            });

            if (window.history.pushState && !firstUpdateFilter) {
                var currentUrl = window.location.href;
                var currentUrlSplit = currentUrl.split('/');
                var queryString = window.location.search;
                var lastPart = currentUrlSplit[currentUrlSplit.length - 1];
                var firstUsedPart = currentUrlSplit.slice(0, -1).join('/');
                var slingSplit = lastPart.split('.');
                var pageName = slingSplit[0];
                var slingParameterNew = ["destination_" + $('#current-destination-filter').val(),
            	                         "date_" + $('#current-date-filter').val(),
            	                         "ship_" + $('#current-ship-filter').val(),
            	                         "duration_" + $('#current-duration-filter').val(),
            	                         "cruisetype_" + $('#current-cruisetype-filter').val(),
            	                         "port_" + $('#current-port-filter').val(),
            	                         "page_" + $('#current-page-filter').val()];
                window.history.pushState({}, null, firstUsedPart + '/' + pageName + '.' + slingParameterNew.join('.') + ".html" + queryString);
            }
            firstUpdateFilter = false;
            $form.find('.destination-filter, .date-filter, .ship-filter, .duration-filter, .cruisetype-filter, .port-filter').each(function () {
                var $select = $(this);
                var currentFilter = $('#current-' + $select.attr('name') + '-filter').val();
                //console.log("current filter for " + $select.attr('name') + " haa : " + $('#current-' + $select.attr('name') + '-filter').val());

                $select.find('option').each(function () {
                    var $option = $(this);
                    $option.attr('selected', $option.val() === currentFilter);
                });
            });

            // Update chosen
            $form.find('.chosen').trigger('chosen:updated');

            // Update features filter
            $items = $form.find('.feature-filter li');
            if ($('#feature-filter').data('ssc-filter') !== undefined) {
                var filterFeatureAvailableObj = $('#feature-filter').data('ssc-filter');
                $items.each(function () {
                    var $item = $(this);
                    $item.toggleClass('disabled', filterFeatureAvailableObj[$item.find('input[name=feature]').val()] !== true);
                });
            } else {
                $items.toggleClass('disabled', true);
            }

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
            $resultWrapper.find('.meta_feature_inner:not(.hidden-xs)').each(function () {
                var $currentFeature = $(this);
                featureList[$currentFeature.find('i').attr('class')] = $currentFeature.find('.tooltip').text();
            });

            for (var key in featureList) {
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
            $form.find('select').each(function (i, element) {
                filterOjb[element.name] = $(element).find(':selected').data('value') || element.value;
            });

            $form.find('input:checked').each(function (i, element) {
                filterOjb[element.name.replace('[]', '[' + i + ']')] = $(element).data('value');
            });

            dataLayer.search_filters = filterOjb;
            dataLayer.search_page_number = $page;
            dataLayer.search_results_number = $('#matching-value').text();

            // Data from first result
            $cruise = $resultWrapper.find('.c-fyc__result:first');
            //dataLayer.track_destination_id = $('.c-fyc__result-wrapper #current-destination-filter').data('value');
            //dataLayer.track_destination_name = $('.c-fyc__result-wrapper #current-destination-filter').val();
            //dataLayer.track_voyage_id = $cruise.find('.cruise-code').text().trim();
            //dataLayer.track_departure_date = $cruise.find('.c-fyc__result__content__summary__item:first dd').data('date');
            // dataLayer.track_voyage_duration = $cruise.find('.c-fyc__result__content__summary__item--duration dd strong').text().trim();
            //dataLayer.track_voyage_departure_harbor = $cruise.find('.c-fyc__result__content__itinerary dd:first').text().trim();
            // dataLayer.track_voyage_arrival_harbor = $cruise.find('.c-fyc__result__content__itinerary dd:last').text().trim();
            //dataLayer.track_voyage_type = $cruise.find('.cruise-type').text().trim();
            //dataLayer.track_shipname = $cruise.find('.cruise-ship').text().trim();
            //dataLayer.track_revenue = $cruise.find('.c-fyc__result__content__price strong').text().trim();

            return searchAnalytics;
        })();

        /***************************************************************************
         * Pagination
         **************************************************************************/
        var pagination = (function pagination() {
            $paginationWrapper = $('.c-fyc-pagination');
            $resultWrapper.on('click', $paginationWrapper.find('a'), function (e) {
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
                        scrollTop: $('.c-fyc-filter').first().offset().top - $('.c-header').height()
                    }, 800);
                }
            });
            return pagination;
        })();

        /***************************************************************************
         * Filter : reset form
         **************************************************************************/
        $btnReset.on('click', function (e) {
            e.preventDefault();
            var $btn = $(this);

            if ($btn.hasClass('active')) {
                // Reset form
                $form.trigger('reset');

                $form.find('option').attr('selected', false);

                // Update select chosen plugin
                $form.find('.chosen').trigger('chosen:updated');

                // Force change event on form
                $form.trigger('change', [false]);

                // Set disable style on reset button
                $btn.removeClass('active');
            }
        });

        /***************************************************************************
         * Filter : update result label according to the number of results
         **************************************************************************/
        var resultLabel = (function resultLabel() {
            var $matchingValue = $('#matching-value');

            $matchingValue.closest('.c-fyc-filter__text').toggleClass('results', parseInt($('#matching-value').text()) > 1);

            return resultLabel;
        })();

        /***************************************************************************
         * Filter : behavior on page load
         **************************************************************************/
        var updateFilterState = (function updateFilterState() {
            var resetState,
                $filterValue = $($form.serializeArray());

            $filterValue.each(function (i, field) {
                var $fieldwrapper = $('[name="' + field.name + '"]').closest('.single-filter');
                if (typeof field != "undefined" && field != null) {
                    if (typeof field.value != "undefined") {
                        if (field.value !== 'all') {
                            resetState = true;

                            // Highlight filter
                            $fieldwrapper.addClass('active');
                        } else {
                            // Remove highlight filter
                            $fieldwrapper.removeClass('active');
                        }
                    }
                }
            });

            // Update reset style state
            if (resetState) {
                $btnReset.addClass('active');
            } else {
                $btnReset.removeClass('active');
            }

            return updateFilterState;
        })();

        /***************************************************************************
         * Filter : behavior on form change
         **************************************************************************/
        $form.on('change', function (e, isFromPagination) {            
            // Ignore change from Port search input (chosen)
            if ($(e.target).closest('.chosen-search').length === 0) {
                var dataLayer = window.dataLayer[0];
                var needToRedirect = false;

                $form.find('.destination-tracking').find('select option:selected').each(function (i, element) {
                    if ($(element).val() == "gv") {
                        window.location.href = "https://www.silversea.com" + $(element).data('ssc-link');
                        needToRedirect = true;
                    }
                    if ($(element).val() == "wc") {
                        window.location.href = "https://www.silversea.com" + $(element).data('ssc-link');
                        needToRedirect = true;
                    }
                });

                if (!needToRedirect) {
                    // Data search
                    var filterOjb = {};
                    $form.find('select').each(function (i, element) {
                        filterOjb[element.name] = $(element).find(':selected').data('value') || element.value;
                    });

                    $form.find('input:checked').each(function (i, element) {
                        filterOjb[element.name.replace('[]', '[' + i + ']')] = $(element).data('value');
                    });

                    dataLayer.search_filters = filterOjb;

                    updateFilterState();

                    // Set active state on reset button
                    var resetState,
                        $currentForm = $(this),
                        featureNumber = 0,
                        $filterValue = $($currentForm.serializeArray());

                    $filterValue.each(function (i, field) {
                        if (field.name === 'feature') {
                            featureNumber++;
                        }
                    });

                    // Show number of feature selected
                    var $featureLabel = $currentForm.find('.feature-filter').closest('.single-filter').find('.text-selected');
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
                    $filterValue.each(function (i, field) {
                        // Add filter
                        if (field.name === 'feature') {
                            featuresSelectorValue.push(field.value.replace(/\//g, 'forwardSlash'));
                        } else {
                            requestUrl += '.' + field.name + '_' + field.value.replace(/\//g, 'forwardSlash');
                        }
                    });

                    // Add features
                    if (featuresSelectorValue.length > 0) {
                        requestUrl += '.features_' + featuresSelectorValue.join("|");
                    } else {
                        requestUrl += '.features_all';
                    }

                    // Add pagination
                    $page = (isFromPagination === true) ? $page : '1';
                    requestUrl += '.page_' + $page;

                    // Add extension
                    requestUrl += '.html';

                    // Update result according to the request URL
                    $.ajax({
                        type: 'GET',
                        url: requestUrl,
                        success: function (result) {
                            $resultWrapper.html(result);

                            // Update result count
                            $('#matching-value').text($('#count-filter').val());
                            resultLabel();

                            // Update filter
                            updateFilter();

                            // Build feature legend according to the current result
                            featureListBuild();

                            // Set data layer key according to the current result
                            searchAnalytics();
                        }
                    });
                }
            }
        });
    }
};

var v2 = function () {
    var $filterWrapper = $('.c-fyc-v2-filter');
    var firstUpdateFilter = true;
    if ($filterWrapper.length > 0) {
        var $btnReset = $filterWrapper.find('.c-fyc-v2-filter__reset a');
        var $form = $('#filter-form-v2');
        var $paginationWrapper = $('.c-fyc-v2-pagination');
        var $resultWrapper = $('.c-fyc-v2__result-wrapper');
        var $page = $paginationWrapper.find('a.active').data('page');
        // Filter : open feature drop down
        $('.v2-feature-filter').on('click', function (e) {
            e.stopPropagation();
            if ($(e.target).closest('li').hasClass('disabled')) {
                e.preventDefault();
            }
        });

        /***********************************************************************
         * Update Filter according to result : compare option list full and
         * option available
         **********************************************************************/
        var updateFilter = (function updateFilter() {
            $filter = $form.find('select');

            $filter.each(function () {
                var $select = $(this);
                var $optionList = $select.find('option');

                // Build obj with available option
                var jsonStr = $resultWrapper.find('#v2-' + $select.attr('name') + '-filter').data('ssc-filter');


                if (jsonStr !== '') {
                    var filterAvailableObj = jsonStr;

                    // Disabled option not available
                    $optionList.each(function () {
                        try {
                            var $option = $(this);
                            $option.attr('disabled', filterAvailableObj[$option.val()] !== true);
                        } catch (error) {
                            console.log('init issue');
                        }
                    });
                }
            });

            if (window.history.pushState && !firstUpdateFilter) {
                var currentUrl = window.location.href;
                var currentUrlSplit = currentUrl.split('/');
                var queryString = window.location.search;
                var lastPart = currentUrlSplit[currentUrlSplit.length - 1];
                var firstUsedPart = currentUrlSplit.slice(0, -1).join('/');
                var slingSplit = lastPart.split('.');
                var pageName = slingSplit[0];
                var slingParameterNew = ["destination_" + $('#v2-current-destination-filter').val(),
            	                         "date_" + $('#v2-current-date-filter').val(),
            	                         "ship_" + $('#v2-current-ship-filter').val(),
            	                         "duration_" + $('#v2-current-duration-filter').val(),
            	                         "cruisetype_" + $('#v2-current-cruisetype-filter').val(),
            	                         "port_" + $('#v2-current-port-filter').val(),
            	                         "page_" + $('#v2-current-page-filter').val()];
                window.history.pushState({}, null, firstUsedPart + '/' + pageName + '.' + slingParameterNew.join('.') + ".html" + queryString);
            }
            firstUpdateFilter = false;
            $form.find('.destination-filter, .date-filter, .ship-filter, .duration-filter, .cruisetype-filter, .port-filter').each(function () {
                var $select = $(this);
                var currentFilter = $('#v2-current-' + $select.attr('name') + '-filter').val();
                $select.find('option').each(function () {
                    var $option = $(this);
                    $option.attr('selected', $option.val() === currentFilter);
                });
            });

            // Update chosen
            $form.find('.chosen').trigger('chosen:updated');

            // Update features filter            
            $items = $form.find('.v2-feature-filter li');
            if ($('#v2-feature-filter').data('ssc-filter') !== undefined) {
                var filterFeatureAvailableObj = $('#v2-feature-filter').data('ssc-filter');
                $items.each(function () {
                    var $item = $(this);
                    $item.toggleClass('disabled', filterFeatureAvailableObj[$item.find('input[name=feature]').val()] !== true);
                });
            } else {
                $items.toggleClass('disabled', true);
            }

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
            $resultWrapper.find('.meta_feature_inner:not(.hidden-xs)').each(function () {
                var $currentFeature = $(this);
                featureList[$currentFeature.find('i').attr('class')] = $currentFeature.find('.tooltip').text();
            });

            for (var key in featureList) {
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
            $form.find('select').each(function (i, element) {
                filterOjb[element.name] = $(element).find(':selected').data('value') || element.value;
            });

            $form.find('input:checked').each(function (i, element) {
                filterOjb[element.name.replace('[]', '[' + i + ']')] = $(element).data('value');
            });

            dataLayer.search_filters = filterOjb;
            dataLayer.search_page_number = $page;
            dataLayer.search_results_number = $('#v2-matching-value').text();

            // Data from first result TODO
            $cruise = $resultWrapper.find('.c-fyc-v2__result:first');


            return searchAnalytics;
        })();

        /***************************************************************************
         * Pagination
         **************************************************************************/
        var pagination = (function pagination() {
            $paginationWrapper = $('.c-fyc-pagination');
            $resultWrapper.on('click', $paginationWrapper.find('a'), function (e) {
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
                        scrollTop: $('.c-fyc-v2-filter').first().offset().top - $('.c-header').height()
                    }, 800);
                }
            });
            return pagination;
        })();

        /***************************************************************************
         * Filter : reset form
         **************************************************************************/
        $btnReset.on('click', function (e) {
            e.preventDefault();
            var $btn = $(this);

            if ($btn.hasClass('active')) {
                // Reset form
                $form.trigger('reset');

                $form.find('option').attr('selected', false);

                // Update select chosen plugin
                $form.find('.chosen').trigger('chosen:updated');

                // Force change event on form
                $form.trigger('change', [false]);

                // Set disable style on reset button
                $btn.removeClass('active');
            }
        });

        /***************************************************************************
         * Filter : update result label according to the number of results
         **************************************************************************/
        var resultLabel = (function resultLabel() {
            var $matchingValue = $('#v2-matching-value');

            $matchingValue.closest('.c-fyc-v2-filter__text').toggleClass('results', parseInt($('#v2-matching-value').text()) > 1);

            return resultLabel;
        })();

        /***************************************************************************
         * Filter : behavior on page load
         **************************************************************************/
        var updateFilterState = (function updateFilterState() {
            var resetState,
                $filterValue = $($form.serializeArray());

            $filterValue.each(function (i, field) {
                var $fieldwrapper = $('.c-fyc-v2-filter form [name="' + field.name + '"]').closest('.single-filter');
                if (typeof field != "undefined" && field != null) {
                    if (typeof field.value != "undefined") {
                        if (field.value !== 'all') {
                            resetState = true;

                            // Highlight filter
                            $fieldwrapper.addClass('active');
                        } else {
                            // Remove highlight filter
                            $fieldwrapper.removeClass('active');
                        }
                    }
                }
            });

            // Update reset style state
            if (resetState) {
                $btnReset.addClass('active');
            } else {
                $btnReset.removeClass('active');
            }

            return updateFilterState;
        })();

        /***************************************************************************
         * Filter : behavior on form change
         **************************************************************************/
        $form.on('change', function (e, isFromPagination) {
            // Ignore change from Port search input (chosen)            
            if ($(e.target).closest('.chosen-search').length === 0) {
                var dataLayer = window.dataLayer[0];
                var needToRedirect = false;

                $('#filter-form-v2 .destination-tracking').find('select option:selected').each(function (i, element) {
                    if ($(element).val() == "gv") {
                        window.location.href = "https://www.silversea.com" + $(element).data('ssc-link');
                        needToRedirect = true;
                    }
                    if ($(element).val() == "wc") {
                        window.location.href = "https://www.silversea.com" + $(element).data('ssc-link');
                        needToRedirect = true;
                    }
                });

                if (!needToRedirect) {
                    // Data search
                    var filterOjb = {};
                    $form.find('select').each(function (i, element) {
                        filterOjb[element.name] = $(element).find(':selected').data('value') || element.value;
                    });

                    $form.find('input:checked').each(function (i, element) {
                        filterOjb[element.name.replace('[]', '[' + i + ']')] = $(element).data('value');
                    });

                    dataLayer.search_filters = filterOjb;

                    updateFilterState();

                    // Set active state on reset button
                    var resetState,
                        $currentForm = $(this),
                        featureNumber = 0,
                        $filterValue = $($currentForm.serializeArray());

                    $filterValue.each(function (i, field) {
                        if (field.name === 'feature') {
                            featureNumber++;
                        }
                    });
                    // Show number of feature selected
                    var $featureLabel = $currentForm.find('.v2-feature-filter').closest('.single-filter').find('.text-selected');
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
                    $filterValue.each(function (i, field) {
                        // Add filter
                        if (field.name === 'feature') {
                            featuresSelectorValue.push(field.value.replace(/\//g, 'forwardSlash'));
                        } else {
                            requestUrl += '.' + field.name + '_' + field.value.replace(/\//g, 'forwardSlash');
                        }
                    });


                    // Add features
                    if (featuresSelectorValue.length > 0) {
                        requestUrl += '.features_' + featuresSelectorValue.join("|");
                    } else {
                        requestUrl += '.features_all';
                    }

                    // Add pagination
                    $page = (isFromPagination === true) ? $page : '1';
                    requestUrl += '.page_' + $page;

                    // Add extension
                    requestUrl += '.html';

                    // Update result according to the request URL
                    $.ajax({
                        type: 'GET',
                        url: requestUrl,
                        success: function (result) {
                            $resultWrapper.html(result);

                            // Update result count
                            $('#v2-matching-value').text($('#v2-count-filter').val());
                            resultLabel();

                            // Update filter
                            updateFilter();

                            // Build feature legend according to the current result
                            featureListBuild();

                            // Set data layer key according to the current result
                            searchAnalytics();
                        }
                    });
                }
            }
        });
    }
};

$(function () {
    v1();
    v2();
});
