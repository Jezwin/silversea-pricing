$(function() {
    'use strict';
    $('.modal').on('shown.bs.modal', function(event) {
        event.preventDefault();

        $(this).find('.c-signupforoffer').each(function() {
            var $signUpForm = $(this);

            // Call plugin to custom check box look and feel
            $signUpForm.find('.custom-checkbox').iCheck({
                checkboxClass : 'icheckbox_minimal'
            });

            // Autocomplete form with data from cookie
            var userInfo = null;
            try {
          	  userInfo = JSON.parse($.CookieManager.getCookie('userInfo'))
            } catch(error){
					  $.CookieManager.setCookie("userInfo", JSON.stringify(""));
			}
            if (userInfo) {
                $signUpForm.find('[name="title"]').val(userInfo.title).trigger('chosen:updated');
                $signUpForm.find('[name="firstname"]').val(userInfo.firstname);
                $signUpForm.find('[name="lastname"]').val(userInfo.lastname);
                $signUpForm.find('[name="email"]').val(userInfo.email);
            }

            // if( userInfo.isbooked == 1 ){
            // $signUpForm.find('[name="isbooked"]').iCheck('check'); }

            // Call plugin validator
            $('.c-formcookie').validator({
                focus : false,
                feedback : {
                    success : 'feedback-success',
                    error : 'feedback-error'
                }
            }).on('submit', function(e) {
                if (!e.isDefaultPrevented()) {
                    $.signUp.signUpOffers(this, e);
                }
            });

            // Call plugin after validator plugin
            if (!$.fn.mobileDetect()) {
                $('.chosen').chosen();
            }
        });
    });
});