$(function() {
    $('.modal').on('shown.bs.modal', function(event) {
        requestForm();
        $(this).find('.custom-checkbox').iCheck({
            checkboxClass : 'icheckbox_minimal',
        });
    });
    requestForm();
});

//make code in function to call on modal shown
function requestForm(){
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

    // Autocomplete form with data from cookie
    $('.request-quote-form').each(function() {
        var $form = $(this),
        userInfo = JSON.parse($.CookieManager.getCookie('userInfo'));
        if (userInfo) {
            $form.find('[name="title"]').val(userInfo.title).trigger('chosen:updated');
            $form.find('[name="firstname"]').val(userInfo.firstname);
            $form.find('[name="lastname"]').val(userInfo.lastname);
            $form.find('[name="localphone"]').val(userInfo.phone);
            $form.find('[name="email"]').val(userInfo.email);
        }
    });

    $('.countrycode').each(function() {
        $countryCodeWrapper = $(this);

        // Init intl Tel Input Plugin
        var countryGeolocalized = $('#InputTelephoneNumber').data('country-geolocalized'),
        $inputTelephoneNumber = $countryCodeWrapper.find("#InputTelephoneNumber");

        $inputTelephoneNumber.intlTelInput({
            allowDropdown : false,
            initialCountry: countryGeolocalized,
            separateDialCode : true,
            customPlaceholder : function(selectedCountryPlaceholder, selectedCountryData) {
                return $inputTelephoneNumber.data('prepend-placeholder') + ": 0" + selectedCountryPlaceholder;
            }
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
    });

    // Validator !
    $('.request-quote-form').validator({
        focus : false,
        feedback : {
            success : 'feedback-success',
            error : 'feedback-error'
        },
        custom : {
            countrycodeformat : function($el) {
                if ($.trim($el.val())) {
                    if (!$el.intlTelInput("isValidNumber")) {
                        return "error format";
                    }
                }
            }
        }
    }).on('submit', function(e) {
        $(this).find('[name="phone"]').val($('#InputTelephoneNumber').intlTelInput("getNumber"));

        if (!e.isDefaultPrevented()) {
            $.signUp.signUpOffers(this, e);
        }
    });

    $('.chosen.chosen-with-search').chosen({
        'disable_search' : false
    });
    
}