(function() {

    // collect data from widgets in multifield and POST them to CRX as JSON
    var collectDataFromFields = function() {
        $(document).on('click', 'button#shell-propertiespage-saveactivator, button.cq-dialog-submit', function(e) {
            var record;

            // Clean
            $('input.hidden-multifiels').remove();

            // parse field and build json string
            $('.multifields-wrapper').each(function(i, section) {
                record = {};
                var $fields = $(section).find('input[name],select[name],textarea[name]'), arrayValues = $fields.serializeArray();

                $(arrayValues).each(function(i, item) {
                    record[item.name] = item.value;
                });

                // Record json string
                $('<input />').attr('type', 'hidden').attr('name', $(section).data('name')).attr('class', 'hidden-multifiels').attr('value', JSON.stringify(record)).appendTo($(section));
            });
        });
    };

    $(document).ready(function() {
        collectDataFromFields();
    });

})();