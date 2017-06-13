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

    $('.countrycode').each(function() {
        $countryCodeWrapper = $(this);

        // Init intl Tel Input Plugin
        var countryGeolocalized = $('#InputTelephoneNumber').data('country-geolocalized'),
        $inputTelephoneNumber = $countryCodeWrapper.find("#InputTelephoneNumber");

        $inputTelephoneNumber.intlTelInput({
            allowDropdown : false,
            geoIpLookup: function(callback) {
                callback(countryGeolocalized);
            },
            initialCountry: 'auto',
            separateDialCode : true
        });

        // Update flag according to select (chosen)
        $countryCodeWrapper.find('#countryCode').on('change', function() {
            $inputTelephoneNumber.intlTelInput("setCountry", $(this).val());

            // If value exists on page load
            if ($inputTelephoneNumber.val() !== '') {
                $inputTelephoneNumber.blur();
            }
        });

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

        // Validator !
        $('.request-quote-form').validator({
            focus : false,
            feedback : {
                success : 'feedback-success',
                error : 'feedback-error'
            },
            custom : {
                countrycodeformat : function($el) {
                    console.log($el.intlTelInput("isValidNumber"));

//                    if ($el.val() !== "" && $el.val() !== matchValue) {
//                        return "error format";
//                    }
                    
                    if ($.trim($el.val())) {
                        if (!$el.intlTelInput("isValidNumber")) {
                            return "error format";
                        }
                    }
                }
            }
        });
    });
});