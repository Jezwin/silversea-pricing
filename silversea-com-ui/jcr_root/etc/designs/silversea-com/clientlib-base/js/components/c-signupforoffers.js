$(function() {

    $('.modal').on('shown.bs.modal', function (){
        
    setTimeout(function(){
        var userInfo = JSON.parse($.CookieManager.getCookie('userInfo'))
        $('.c-signupforoffer [name="email"]').val(userInfo.email);
    },300)

        var opt = {
            feedback: {
              success: 'success',
              error: 'error'
            },
            custom: {

            }
        };

        $('.c-formcookie').validator(opt);
    });

});