$(function() {
    'use strict';
    $('.modal').on('shown.bs.modal', function(event) {
        event.preventDefault();

        $(this).find('.c-signupforoffer').each(function() {
            var $signUpForm = $(this);

            $signUpForm.find('.custom-checkbox').iCheck({
                checkboxClass : 'icheckbox_minimal',
            });

            var userInfo = JSON.parse($.CookieManager.getCookie('userInfo'));
            $signUpForm.find('[name="title"]').val(userInfo.title).trigger('chosen:updated');
            $signUpForm.find('[name="firstname"]').val(userInfo.firstname);
            $signUpForm.find('[name="lastname"]').val(userInfo.lastname);
            $signUpForm.find('[name="email"]').val(userInfo.email);

            // if( userInfo.isbooked == 1 ){
            // $signUpForm.find('[name="isbooked"]').iCheck('check'); }

            var opt = {
                focus : false,
                feedback : {
                    success : 'feedback-success',
                    error : 'feedback-error'
                }
            };

            $('.c-formcookie').validator(opt).on('submit', function(e) {
                if (!e.isDefaultPrevented()) {
                    $.signUp.signUpOffers(this, e);
                }
            });

            // Launch plugin after modal content loaded.
            $('.chosen').chosen();
        });
    });
});