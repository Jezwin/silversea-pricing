$(function() {
    $('#archives').each(function() {
        var $archive = $(this);

        // target only year link
        $archive.find('> li > a').each(function() {
            var $link = $(this);
            $link.on('click', function(e) {
                e.preventDefault();

                if ($('.collapsing').length === 0) {
                    // Toggle only if there is not collapsing at this time
                    var $trigger = $(this);
                    var $targetcollapse = $trigger.next('ul');

                    // Toggle current year (close or open)
                    $targetcollapse.collapse('toggle');

                    // Close other year
                    $archive.find('.collapse').not($targetcollapse).collapse('hide')
                } else {
                    return false;
                }
            });
        });
    });
});