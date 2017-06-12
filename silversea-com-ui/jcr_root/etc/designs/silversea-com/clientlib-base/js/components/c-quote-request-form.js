$(function() {
    // Autogrow Textarea
    $('.textarea_autogrow').each(function() {
        var $autogrow = $(this),
        $autogrowSize = $autogrow.find('.textarea_autogrow__size'),
        $input = $autogrow.find('.textarea_autogrow__field');

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
        $countryCodeWrapper = $(this);

        $countryCodeWrapper.find('#countryCode').on('change', function() {
            $inputTelephoneNumber = $countryCodeWrapper.find("#InputTelephoneNumber");
            $inputTelephoneNumber.intlTelInput("setCountry", $(this).val());

            // If value exists on page load
            if ($inputTelephoneNumber.val() !== '') {
                $inputTelephoneNumber.blur();
            }
        });

        // Active intlTelInput plugin (combo box with flag)
        $countryCodeWrapper.find('#InputTelephoneNumber').intlTelInput('setCountry', $('#countryCode').val());

        // Trigger open chosen event on flag click
        var openChosen = (function openChosen() {
            $countryCodeWrapper.find('.flag-container').on('click', function(event) {
                event.stopPropagation();
                $countryCodeWrapper.find('#countryCode').trigger('chosen:open');

                // Unbind click on flag
                $(this).off('click');
            });

            return openChosen;
        }());

        // Bind click on flag
        $countryCodeWrapper.find('#countryCode').on('chosen:hiding_dropdown', function() {
            openChosen();
        })
    });
});