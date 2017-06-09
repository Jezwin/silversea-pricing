$(function() {

    $('.modal').on('shown.bs.modal', function (){
		
            $(".chosen").chosen();

            $('.c-signupforoffer .c-accept').iCheck({
                checkboxClass: 'icheckbox_minimal',
                _checked: 'uncheck',
            });
        
            $('.c-signupforoffer [type="submit"]').prop("disabled", true);
        
            $('.c-signupforoffer .c-accept').on('ifChanged', function(event){
                if($(".c-accept").is(":checked")) {
                    $('.c-signupforoffer [type="submit"]').prop("disabled", false);
                 }else if($(".checkbox").is(":not(:checked)")) {
                    $('.c-signupforoffer [type="submit"]').prop("disabled", true);
                 }
           
            });
        
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