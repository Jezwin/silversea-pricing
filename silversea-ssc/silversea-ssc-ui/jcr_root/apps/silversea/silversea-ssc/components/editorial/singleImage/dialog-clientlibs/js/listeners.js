(function (document, $) {
    'use strict';
    var CHECKBOX_LIST = 'touch.checkbox';
    var FIELD_D_HEIGHT = 'touch.d_height'
    var FIELD_T_HEIGHT = 'touch.t_height'
    var FIELD_M_HEIGHT = 'touch.m_height'

    var dialog,
        checkbox;

    $(document).on("foundation-contentloaded", function (event) {

        // If it is not a singleImages dialog return
        dialog = $(event.target).find("input[value='silversea/silversea-ssc/components/editorial/singleImage']").get(0);
        dialog = $(dialog).parent();

        if (dialog === undefined || dialog === null) {
            return;
        }

        checkbox = $(dialog).find("[data-listener='" + CHECKBOX_LIST + "']");

        function showhideCheckbox() {
            var isChecked = false;


            if (checkbox[0] !== undefined) {
                isChecked = checkbox[0].checked;
            }
            if (isChecked === true) {
                $(dialog).find("[data-listener='" + FIELD_D_HEIGHT + "']").parent().show();
                $(dialog).find("[data-listener='" + FIELD_T_HEIGHT + "']").parent().show();
                $(dialog).find("[data-listener='" + FIELD_M_HEIGHT + "']").parent().show();
            } else {
                $(dialog).find("[data-listener='" + FIELD_D_HEIGHT + "']").parent().hide();
                $(dialog).find("[data-listener='" + FIELD_T_HEIGHT + "']").parent().hide();
                $(dialog).find("[data-listener='" + FIELD_M_HEIGHT + "']").parent().hide();
            }
        }

        var checkBtn = $("[data-listener='" + CHECKBOX_LIST + "']").closest('.coral-Checkbox');
        if (checkBtn.length === 1) {
            showhideCheckbox();
            checkBtn.on('click', function () {
                showhideCheckbox();
            });
        }


    });

    /* On save, clear values if not cover image */
    var triggerDone = false;

    $(document).on("click", ".cq-dialog-submit", function (e) {

        if (triggerDone) {
            triggerDone = false; // reset flag
            return; // let the event bubble away
        }

        e.preventDefault();

        // clean values
        if (checkbox[0] && !checkbox[0].checked) {
            $(dialog).find("[data-listener='" + FIELD_D_HEIGHT + "'] input").val("");
            $(dialog).find("[data-listener='" + FIELD_T_HEIGHT + "'] input").val("");
            $(dialog).find("[data-listener='" + FIELD_M_HEIGHT + "'] input").val("");
        }

        // re-trigger event
        triggerDone = true; // set flag
        $(this).trigger('click');

    });


})(document, Granite.$);
