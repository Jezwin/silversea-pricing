$(function() {

    $('.modal').on('shown.bs.modal', function ()
    {

        console.log('--->', this, $(this).find('.chosen'));
//      $(".chosen").chosen();
        $(this).find('.chosen').chosen();

      var opt = {
        feedback: {
          success: 'success',
          error: 'error'
        }
      };

      $('.c-formcookie').validator(opt);
    });

});