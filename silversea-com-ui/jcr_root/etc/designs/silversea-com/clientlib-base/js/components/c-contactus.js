$( document ).ready(function() {
	if($(".c-contactus").length){
  var $form = $(this);
  var userInfo = null;
  try {
	  userInfo = JSON.parse($.CookieManager.getCookie('userInfo'))
  } catch(error){
	  $.CookieManager.setCookie("userInfo", JSON.stringify(""));
  }
  
  var sub_id, myJson;
$.each(JSON.parse(myJson).subjects, function (index, value) {

        $("#subject").append('<option rel="' + index + '" value="'+JSON.parse(value).subject+'">'+JSON.parse(value).subject+'</option>');
    	if (userInfo) {
                $("#subject").val(userInfo.subject);
            }
     	$("#subject").trigger("chosen:updated");


         $("#subject").change(function () {
     		$("#inquiry").find("option:gt(0)").remove();
            sub_id = $(this).find('option:selected').attr('rel');

            $.each(JSON.parse(JSON.parse(myJson).subjects[sub_id]).inquiries, function (index1, value1) {
                $("#inquiry").append('<option rel="' + index1 + '" value="'+value1.inquiry+'">'+value1.inquiry+'</option>');
                if (userInfo) {
                	$("#inquiry").val(userInfo.inquiry);
            	}
                $("#inquiry").trigger("chosen:updated");
            });

             $("#inquiry").change(function () {
			 	var inq_id = $(this).find('option:selected').attr('rel');
                 $("#from_email").val((JSON.parse(JSON.parse(myJson).subjects[sub_id]).inquiries[inq_id]).email);
			});
        });



});
    $("#inquiry").change(function () {
			 	var inq_id = $(this).find('option:selected').attr('rel');
                 $("#from_email").val((JSON.parse(JSON.parse(myJson).subjects[sub_id]).inquiries[inq_id]).email);
			});

 if (userInfo) {
    		$("#inquiry").find("option:gt(0)").remove();
            sub_id = $(this).find('option:selected').attr('rel');

            $.each(JSON.parse(JSON.parse(myJson).subjects[sub_id]).inquiries, function (index1, value1) {
                $("#inquiry").append('<option rel="' + index1 + '" value="'+value1.inquiry+'">'+value1.inquiry+'</option>');
                if (userInfo) {
                	$("#inquiry").val(userInfo.inquiry);
            	}
                $("#inquiry").trigger("chosen:updated");
            });
     		$("#from_email").val(userInfo.from_email);
 }

	}
});


