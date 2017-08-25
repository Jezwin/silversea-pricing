$(function() {
    $('.c-fyc-light__form').each(function() {
        var $form = $(this);
        var requestUrl;
        $form.on('change', function(e) {
            e.preventDefault();
            var $filterValue = $($form.serializeArray());
            requestUrl = $form.attr('action');

            $filterValue.each(function(i, field) {
                // Add filter
                requestUrl += '.' + field.name + '_' + field.value.replace(/\//g, 'forwardSlash');
            });

            requestUrl += '.html'
            $form.attr('action', requestUrl);
        });

        $form.on('submit', function(e) {
            e.preventDefault();
            document.location.href = $form.attr('action');
        });

    })
});