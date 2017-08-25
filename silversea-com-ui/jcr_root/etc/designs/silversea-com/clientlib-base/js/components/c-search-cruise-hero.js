$(function() {
    $('.c-fyc-light__form').each(function() {
        var $form = $(this);
        var requestUrl = $form.attr('action');
        $form.on('change', function(e) {
            e.preventDefault();
            // Reset request url
            requestUrl = $form.attr('action');

            var $filterValue = $($form.find(':not([name=":cq_csrf_token"])').serializeArray());

            $filterValue.each(function(i, field) {
                // Add filter
                requestUrl += '.' + field.name + '_' + field.value.replace(/\//g, 'forwardSlash');
            });
        });

        $form.on('submit', function(e) {
            e.preventDefault();
            document.location.href = requestUrl + '.html';
        });

    })
});