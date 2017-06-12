$(function() {
    'use strict';
    $('.modal').on('shown.bs.modal', function (){
        
            $(".chosen").chosen();
        
            $('.c-signupforoffer [name="isbooked"]').iCheck({
                checkboxClass: 'icheckbox_minimal',
            });
    
            setTimeout(function(){
                var userInfo = JSON.parse($.CookieManager.getCookie('userInfo'))
                $('.c-signupforoffer [name="title"]').val( userInfo.title ).trigger('chosen:updated');
                $('.c-signupforoffer [name="firstname"]').val(userInfo.firstname);
                $('.c-signupforoffer [name="lastname"]').val(userInfo.lastname); 
                $('.c-signupforoffer [name="email"]').val(userInfo.email); 
                    
                if( userInfo.isbooked == 1 ){
                    $('.c-signupforoffer [name="isbooked"]').iCheck('check');
                }
                
                /*$('.c-signupforoffer [name="isbooked"]').on('ifChecked ifUnchecked', function(event){
                    var booked = (event.type == 'ifUnchecked') ? 0 : 1 ;
                    $('.c-signupforoffer [name="isbooked"]').val(booked);
                });*/
                
            },500);
            
            var opt = {
                feedback: {
                  success: 'success',
                  error: 'error'
                }
            };
            $('.c-formcookie').validator(opt)
                .on('submit', function(e) {

                    if (!e.isDefaultPrevented()) {
                        $.signUp.signUpOffers(this, e);
                    }

             });
    });
});