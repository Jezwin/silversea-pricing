$(function() {
    $('.c-fyc-light__form').each(function() {
        var $form = $(this);
        var requestUrl = $form.attr('action').replace('.html','');
        var requestUrlFeedback;
        var $resultWrapper = $form.next('.feedback__wrapper');

        $form.find('[name=date]').val("all");
        $form.find('[name=destination]').val("all");
        $form.find('[name=ship]').val("all");
        $form.find('[name=date]').trigger("chosen:updated");
        $form.find('[name=destination]').trigger("chosen:updated");
        $form.find('[name=ship]').trigger("chosen:updated");

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
                var jsonStr = $resultWrapper.find('#' + $select.attr('name') + '-filter').data('ssc-filter');

                if (jsonStr !== '') {
                    var filterAvailableObj = jsonStr;

                    // Disabled option not available
                    $optionList.each(function() {
                       try {
                            var $option = $(this);
                            $option.attr('disabled', filterAvailableObj[$option.val()] !== true);
                        } catch (error) {
                            console.log('init issue');
                        }
                    });
                }
            });

            $form.find('.destination-filter, .date-filter, .ship-filter').each(function() {
                var $select = $(this);
                var currentFilter = $('#current-' + $select.attr('name') + '-filter').val();

                $select.find('option').each(function() {
                    var $option = $(this);
                    $option.attr('selected', $option.val() === currentFilter);
                });
            });

            // Update chosen
            $form.find('.chosen').trigger('chosen:updated');

            return updateFilter;
        })();

        /***************************************************************************
         * Filter : behavior on form change
         **************************************************************************/
        $form.on('change', function(e) {
            e.preventDefault();
            // Reset request url
            requestUrl = $form.attr('action').replace('.html','');
            requestUrlFeedback = $form.find('.fragment-feedback').attr('href').replace('.html','');

            var $filterValue = $($form.find(':not([name=":cq_csrf_token"])').serializeArray());

            $filterValue.each(function(i, field) {
                // Add filter
                requestUrl += '.' + field.name + '_' + field.value.replace(/\//g, 'forwardSlash');
                requestUrlFeedback += '.' + field.name + '_' + field.value.replace(/\//g, 'forwardSlash');
            });

            // Update result according to the request URL
            $.ajax({
                type : 'GET',
                url : requestUrlFeedback + '.html',
                success : function(result) {
                    $form.next('.feedback__wrapper').html(result);

                    // Update filter
                    updateFilter();
                }
            });
        });

        $form.on('submit', function(e) {
            var mapShip= [];
            mapShip["silver-cloud-expedition"] = 9;
            mapShip["silver-discoverer"] = 8;
            mapShip["silver-explorer"] = 2;
            mapShip["silver-galapagos"] = 3;
            mapShip["silver-moon"] = 12;
            mapShip["silver-muse"] = 10;
            mapShip["silver-shadow"] = 4;
            mapShip["silver-spirit"] = 5;
            mapShip["silver-whisper"] = 6;
            mapShip["silver-wind"] = 7;
            mapShip["silver-wind"] = 998;
            mapShip["silver-origin"] = 9999;

            var mapDest= [];
            mapDest["africa-indian-ocean-cruise"] = 3;
            mapDest["alaska-cruise"] = 9;
            mapDest["antarctica-cruise"] = 10;
            mapDest["arctic-greenland-cruise"] = 11;
            mapDest["asia-cruise"] = 13;
            mapDest["australia-new-zealand-cruise"] = 25;
            mapDest["canada-new-england-cruise"] = 5;
            mapDest["american-west-coast-cruise"] = 28;
            mapDest["central-america-caribbean-cruise"] = 6;
            mapDest["galapagos-islands-cruise"] = 23;
            mapDest["mediterranean-cruise"] = 1;
            mapDest["northern-europe-british-isles-cruise"] = 2;
            mapDest["russian-far-east-cruise"] = 21;
            mapDest["south-america-cruise"] = 7;
            mapDest["south-pacific-islands-cruise"] = 7;
            mapDest["transoceanic-cruise"] = 8;

            e.preventDefault();
            var $resultWrapper = $form.next('.feedback__wrapper');
            var paramterFYC2018= "";
            if ($form.find('[name=destination]').val() != null && $form.find('[name=destination]').val() != "all") {
                paramterFYC2018 = "?destination="+ mapDest[$form.find('[name=destination]').val()];
            }
            if ($form.find('[name=date]').val() != null &&  $form.find('[name=date]').val() != "all") {
                paramterFYC2018 += paramterFYC2018 != "" ? "&" : "?";
                paramterFYC2018 += "departure="+ $form.find('[name=date]').val();
            }
            if ($form.find('[name=ship]').val() != null && $form.find('[name=ship]').val() != "all") {
                paramterFYC2018 += paramterFYC2018 != "" ? "&" : "?";
                paramterFYC2018 += "ship="+ mapShip[$form.find('[name=ship]').val()];
            }

            var destFilter = $resultWrapper.find("#current-destination-filter").val();
            if (typeof destFilter != "undefined" && destFilter != null && (destFilter == "wc" || destFilter == "gv" || destFilter == "cc")) {
            		var jsonStr = JSON.parse(JSON.stringify($resultWrapper.find('#world-grand-voyage-path').data('ssc-path')));

            		var path;

            		if( destFilter == "cc") {
            		    path = jsonStr["comboCruisePath"];
                    } else {
                        path = (destFilter == "wc" ) ? jsonStr["worldCruisePath"] : jsonStr["grandVoyageCruisePath"];
                    }

            		window.location.href = path + '.html';
            } else {
            	document.location.href = requestUrl + '.html' + paramterFYC2018;
            }
        });

    })


});