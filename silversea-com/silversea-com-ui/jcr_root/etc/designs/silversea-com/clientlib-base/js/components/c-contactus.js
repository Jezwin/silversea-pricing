$( document ).ready(function() {
	if($(".c-contactus").length){
  var $form = $(this);
  var userInfo = null;
  try {
	  userInfo = JSON.parse($.CookieManager.getCookie('userInfo'))
  } catch(error){
	  $.CookieManager.setCookie("userInfo", JSON.stringify(""));
  }
  
  var sub_id;
$.each(JSON.parse(window.jsonContact).subjects, function (index, value) {

        $("#subject").append('<option rel="' + index + '" value="'+JSON.parse(value).subject+'">'+JSON.parse(value).subject+'</option>');
    	if (userInfo) {
                $("#subject").val(userInfo.subject);
            }
     	$("#subject").trigger("chosen:updated");


         $("#subject").change(function () {
     		$("#inquiry").find("option:gt(0)").remove();
            sub_id = $(this).find('option:selected').attr('rel');
             $("#inquiry").trigger("chosen:updated");
try {
    $("#requesttype").val((JSON.parse(JSON.parse(window.jsonContact).subjects[sub_id])).subjectapikey);
    $.each(JSON.parse(JSON.parse(window.jsonContact).subjects[sub_id]).inquiries, function (index1, value1) {
        $("#inquiry").append('<option rel="' + index1 + '" value="' + value1.inquiry + '">' + value1.inquiry + '</option>');
        if (userInfo) {
            $("#inquiry").val(userInfo.inquiry);
        }
        $("#inquiry").trigger("chosen:updated");
    });
}catch(e){

}

             $("#inquiry").change(function () {
                 try {
                     var inq_id = $(this).find('option:selected').attr('rel');
                     $("#requestsubtype").val((JSON.parse(JSON.parse(window.jsonContact).subjects[sub_id]).inquiries[inq_id]).inquiryapikey);
                 }catch(e){}
			});
        });



});
    $("#inquiry").change(function () {
        try {
            var inq_id = $(this).find('option:selected').attr('rel');
            $("#requestsubtype").val((JSON.parse(JSON.parse(window.jsonContact).subjects[sub_id]).inquiries[inq_id]).inquiryapikey);
        }catch(e){

        }
			});


	}
});


