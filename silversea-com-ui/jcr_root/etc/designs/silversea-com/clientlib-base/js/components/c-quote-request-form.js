$(function() {
    var phoneActivated = false;

    // Autogrow Textarea
    var textContainer, textareaSize, input;
    var autoSize = function() {
        textareaSize.innerHTML = input.value + '\n';
    };
    textContainer = document.querySelector('.textarea_autogrow');
    textareaSize = textContainer.querySelector('.textarea_autogrow-size');
    input = textContainer.querySelector('textarea');
    autoSize();
    input.addEventListener('input', autoSize);

    // Desktop toggle Country Code
    (function countryCodeToggle() {
        var raqm = $('#raqm');
        var countryCodeSelector = $('.raqm__countrycode_selector');
        var countryCodeContainer = $('#countryCodeContainer');
        var countryCode = $('#countryCode');
        var flag = $('.flag_wrapper .flag');
        var countrycode_wrapper = $('.countrycode_wrapper > span');
        if (raqm.length) {
            countryCodeSelector.click(function(event) {
                countryCodeContainer.toggle();
                countryCodeSelector.toggleClass('active');
                $('body').trigger('click');
                $('#countryCode').trigger('chosen:open');
                event.stopPropagation();
            });

            $('#countryCode').on('chosen:hiding_dropdown', function() {
                setTimeout(function() {
                    if (!$('#countryCode_chosen').hasClass('chosen-container-active'))
                        $('#countryCodeContainer').hide()
                }, 50);
            });

            countryCode.on('change', function() {
                var val = this.options[this.selectedIndex].text;
                var code = val.substr(val.indexOf("("));
                flag.removeAttr('class');
                flag.addClass('flag flag-' + this.value.toLowerCase());
                countrycode_wrapper.html(code);
                countryCodeContainer.toggle();
                countryCodeSelector.toggleClass('active');
            });
        }
    })();

    (function() {
        var submitFunc = function() {
            $('#requestQuoteForm').find('input').each(function() {
                $(this).blur();
            });

            $('#requestQuoteForm #titleContainer select').trigger('change');

            var container = $('#formContainer');
            if ($('#raqm #requestQuoteForm .valid_success .valid_icon').length == 5) {
                updateForm(container.find('*').serialize());
            }
        };

        var form = $('#requestQuoteForm');
        form.find('button.fire-modal').on('click', function(event) {
            submitFunc(form);
        });

        var newRaqForm = $('#requestQuoteForm');

        newRaqForm.find('input').on('blur', function(event) {
            var inputTarget = $('#' + event.target.id);

            if (inputTarget.attr('type') == 'hidden') {
                return false;
            }

            var parent = inputTarget.parent();

            if (event.target.id == 'InputTelephoneNumber') {
                parent = parent.parent();
            }

            parent.addClass('validating');

            var realtimeValid = parent.find('.valid_icon');
            var realtimeinValid = parent.find('.invalid_icon');
            var realtimeError = parent.find('.form-helper-error-text.live');

            if (event.target.id == 'InputTelephoneNumber' || event.target.id == 'InputEmail') {
                var realtimeEmptyError = parent.find('.form-helper-error-text.live.empty');
                var realtimeInvalidError = parent.find('.form-helper-error-text.live.incorrect');
            }

            parent.find('.form-helper-error-text:not(.live)').hide();

            var value = inputTarget.val();
        });
    })();

    // init intl Tel Input Plugin
    $("#InputTelephoneNumber").intlTelInput();

    $('#countryCode').change(function() {
        $("#InputTelephoneNumber").intlTelInput("setCountry", $(this).val());
        if ($("#InputTelephoneNumber").val() != '' || $("#InputEmail").val() != '') {
            $("#InputTelephoneNumber").blur();
        }
    });

    $("#InputTelephoneNumber").intlTelInput("setCountry", $('#countryCode').val());

    if ($('#countryCode').val() != '') {
        $('#countryCode').trigger('change');
        $('.raqm__countrycode_selector').trigger('click');
        $("#InputTelephoneNumber").intlTelInput("setCountry", $('#countryCode').val());
        if ($("#InputTelephoneNumber").val() == '') {
            var inputTarget = $("#InputTelephoneNumber");
            var parent = inputTarget.parent().parent();
            var realtimeinValid = parent.find('.invalid_icon');
            var realtimeEmptyError = parent.find('.form-helper-error-text.live.empty');
            var realtimeInvalidError = parent.find('.form-helper-error-text.live.incorrect');
            realtimeEmptyError.hide();
            realtimeInvalidError.hide();
            realtimeinValid.hide();
            inputTarget.removeClass('form-helper-error-field');
        }
    }

    if ($('#requestQuoteForm #titleContainer select').val() != '') {
        $('#requestQuoteForm #titleContainer select').trigger('change');
    }

    $('#requestQuoteForm').find('input').each(function() {
        if ($(this).val() != '') {
            $(this).blur();
        }
    });
});