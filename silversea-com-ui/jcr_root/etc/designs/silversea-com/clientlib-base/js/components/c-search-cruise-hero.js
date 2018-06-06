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
            e.preventDefault();
            var $resultWrapper = $form.next('.feedback__wrapper');

            var destFilter = $resultWrapper.find("#current-destination-filter").val();
            if (typeof destFilter != "undefined" && destFilter != null && (destFilter == "wc" || destFilter == "gv")) {
            		var jsonStr = JSON.parse(JSON.stringify($resultWrapper.find('#world-grand-voyage-path').data('ssc-path')))
            		var path = (destFilter == "wc" ) ? jsonStr["worldCruisePath"] : jsonStr["grandVoyageCruisePath"];
            		window.location.href = path + '.html';
            } else {
            	document.location.href = requestUrl + '.html';
            }
        });

    })
});