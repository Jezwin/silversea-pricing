$.fn.requestForm = function() {
    this.each(function() {
        var $currentForm = $(this);

        // Destroy chosen plugin, need to be initialized after validator()
        $currentForm.find('select').chosen('destroy');

        // Customize checkebox
        $currentForm.find('.custom-checkbox').iCheck({
            checkboxClass : 'icheckbox_minimal'
        });

        // Autogrow Textarea
        $currentForm.find('.textarea_autogrow').each(function() {
            var $autogrow = $(this), $autogrowSize = $autogrow.find('.textarea_autogrow__size'), $input = $autogrow.find('.textarea_autogrow__field');

            var autoSize = (function autoSize() {
                $autogrowSize.html($input.val() + '\n');
                return autoSize;
            })();

            $input.bind('input', autoSize());
        });

        // Autocomplete form with data from cookie
        $currentForm.each(function() {
            var $form = $(this);
            var userInfo = null;
            try {
          	  userInfo = JSON.parse($.CookieManager.getCookie('userInfo'))
            } catch(error){
				$.CookieManager.setCookie("userInfo", JSON.stringify(""));
			}
            if (userInfo) {
                $form.find('[name="title"]').val(userInfo.title).trigger('chosen:updated');
                $form.find('[name="firstname"]').val(userInfo.firstname);
                $form.find('[name="lastname"]').val(userInfo.lastname);
                $form.find('[name="localphone"]').val(userInfo.phone);
                $form.find('[name="email"]').val(userInfo.email);
                $form.find('[name="comments"]').val(userInfo.comments);
                $form.find('[name="postaladdress"]').val(userInfo.postaladdress);
                $form.find('[name="postalcode"]').val(userInfo.postalcode);
                $form.find('[name="city"]').val(userInfo.city);
                $form.find('[name="country"]').val(userInfo.country);
                $form.find('[name="bookingnumber"]').val(userInfo.bookingnumber);
                $form.find('[name="vsnumber"]').val(userInfo.vsnumber);
                $form.find('[name="state"]').val(userInfo.state);
            }
        });

        $currentForm.find('.countrycode').each(function() {
            $countryCodeWrapper = $(this);

            // Init intl Tel Input Plugin
            var countryGeolocalized = $('#InputTelephoneNumber').data('country-geolocalized'), $inputTelephoneNumber = $countryCodeWrapper.find("#InputTelephoneNumber");

            $inputTelephoneNumber.intlTelInput({
                allowDropdown : false,
                initialCountry : countryGeolocalized,
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
                    event.preventDefault();
                    event.stopPropagation();
                    $countryCodeWrapper.find('#countryCode').trigger('chosen:open');

                    // Unbind click on flag when dropdown is opened
                    $(this).off('click');
                });

                return openChosen;
            }());

            $countryCodeWrapper.find('#countryCode').on('chosen:hiding_dropdown', function(e) {
                // Trick : force close completely chosen
                $(document).trigger('click');

                // Bind click on flag again
                openChosen();
            })
        });

        // Display Postal code, city, and country fields when postal adress
        // focused.
        $currentForm.find('#postalAddress').focus(function() {
            $('.hideArea').show();
        });

        (($currentForm.find('#postalAddress').val() == undefined) || ($currentForm.find('#postalAddress').val() == '')) ? $('.hideArea').hide() : $('.hideArea').show();

        // Validate immediately select on chosen 'blur' (needed for form validation when chosen value is modified)
        $currentForm.find('.chosen').on('change', function(e) {
            $(this).trigger('blur');
        });

        // Validator !
        $currentForm.validator({
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
                },
                cityzip : function($el) {
                    var id = $el.data('cityzip');
                    return !$el.val() || !$('input[name="' + id + '"]').val();
                },
                email : function($el){
                	var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
                	 if(!re.test($el.val())){
                		 return "error";
                	 }
                	if(typeof prevemail !== 'undefined'){
                		if(prevemail == $el.val()){
                			return window.answerBrite;
                		}else{
                    		prevemail = $el.val();
                    		window.briteVerify($el.val());
                    	  return window.answerBrite;
                    	}
                		
                	}else{
                		prevemail = $el.val();
                		window.briteVerify($el.val());
                	  return window.answerBrite;
                	}
                }
            }
        }).off('input.bs.validator change.bs.validator').on('submit', function(e) {
            $(this).find('[name="phone"]').val($('#InputTelephoneNumber').intlTelInput("getNumber"));
            if (!e.isDefaultPrevented()) {
                $.signUp.signUpOffers(this, e);
            }
        });

        // Init chosen if form is inside modal
        if (!$.fn.mobileDetect()) {
            $currentForm.find('.chosen.chosen-with-search').chosen({
                'disable_search' : false
            });
        }
    });
}

$(function() {
    $('.modal').on('loaded.bs.modal', function(event) {
        $(this).find('.request-quote-form').requestForm();
    });

    // on page load for form inside current page
    $('.request-quote-form').requestForm();
});