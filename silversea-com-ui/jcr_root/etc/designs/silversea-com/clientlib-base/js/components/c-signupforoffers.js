$(function() {

    $('.modal').on('shown.bs.modal', function (){
        
            setTimeout(function(){
                var userInfo = JSON.parse($.CookieManager.getCookie('userInfo'))
                $('.c-signupforoffer [name="email"]').val(userInfo.email);
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