$(function() {
    $('.c-fyc-light__form').each(function() {
        var $form = $(this);
        var requestUrl = $form.attr('action').replace('.html','');
        var requestUrlFeedback;
        var $resultWrapper = $form.next('.feedback__wrapper');

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
                var jsonStr = $resultWrapper.find('#' + $select.attr('name') + '-filter').text();

                if (jsonStr !== '') {
                    var filterAvailableObj = JSON.parse(jsonStr);

                    // Disabled option not available
                    $optionList.each(function() {
                        var $option = $(this);
                        $option.attr('disabled', filterAvailableObj[$option.val()] !== true);
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
            document.location.href = requestUrl + '.html';
        });

    })
});