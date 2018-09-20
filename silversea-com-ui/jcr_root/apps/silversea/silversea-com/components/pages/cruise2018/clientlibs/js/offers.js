$(function () {
    $(".offers .offers-btns a").on("click touchstart", toggle);

    function toggle(e) {
        e && e.preventDefault();
        e && e.stopPropagation();
        var $this = $(this);
        if ($this.data('ssc-accordion') === 'show') {
            $(".offers .offers-hide").slideDown('slow');
            $this.data('ssc-accordion', 'hide');
            $('.offers').get(0).scrollIntoView({block: 'start', behavior: 'smooth'});
        } else if ($this.data('ssc-accordion') === 'hide') {
            $(".offers .offers-hide").slideUp('slow');
            $this.data('ssc-accordion', 'show');
            $('.offers').get(0).scrollIntoView({block: 'end', behavior: 'smooth'});
        }
        $this.blur();
        $('.offers .offers-accordion-button').toggle();
    }
});