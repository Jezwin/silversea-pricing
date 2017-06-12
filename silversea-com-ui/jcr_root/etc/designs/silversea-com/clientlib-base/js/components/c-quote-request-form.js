$(function() {
    // Autogrow Textarea
    $('.textarea_autogrow').each(function() {
        var $autogrow = $(this),
        $autogrowSize = $autogrow.find('.textarea_autogrow-size'),
        $input = $autogrow.find('textarea');

        var autoSize = (function autoSize() {
            $autogrowSize.html($input.val() + '\n');
            return autoSize; 
        })();

        $input.bind('input', autoSize());
    });

    // Init intl Tel Input Plugin
    $('#InputTelephoneNumber').intlTelInput({
        allowDropdown : false,
        // geoIpLookup: function(callback) {
        // $.get("http://ipinfo.io", function() {},
        // "jsonp").always(function(resp) {
        // var countryCode = (resp && resp.country) ? resp.country : "";
        // callback(countryCode);
        // });
        // },
        // initialCountry: 'auto',
        separateDialCode : true
    });

    $('.countrycode').each(function() {
        $countryCode = $(this);

        $countryCode.find('#countryCode').on('change', function() {
            $("#InputTelephoneNumber").intlTelInput("setCountry", $(this).val());

            if ($('#InputTelephoneNumber').val() !== '') {
                $('#InputTelephoneNumber').blur();
            }
        });

        // Active intlTelInput plugin (combo box with flag)
        $countryCode.find('#InputTelephoneNumber').intlTelInput('setCountry', $('#countryCode').val());

        // Trigger open chosen event on flag click
        var openChosen = (function openChosen() {
            $countryCode.find('.flag-container').on('click', function(event) {
                event.stopPropagation();
                $countryCode.find('#countryCode').trigger('chosen:open');

                // Unbind click on flag
                $countryCode.find('.flag-container').off('click');
            });

            return openChosen;
        }());

        // Bind click on flag
        $countryCode.find('#countryCode').on('chosen:hiding_dropdown', function() {
            openChosen();
        })
    });
});