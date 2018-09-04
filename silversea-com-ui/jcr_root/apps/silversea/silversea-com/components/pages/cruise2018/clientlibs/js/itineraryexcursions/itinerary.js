$(function () {

    function toggle(e) {
        e && e.preventDefault();
        const $this = $(this);
        if ($this.data('ssc-accordion') === 'show') {
            $this.parent().find('.cruise-2018-excursions-accordion').slideDown(200, 'linear');
            $this.data('ssc-accordion', 'hide');
        } else if ($this.data('ssc-accordion') === 'hide') {
            $this.parent().find('.cruise-2018-excursions-accordion').slideUp();
            $this.data('ssc-accordion', 'show');
        }
        $this.blur();
        $this.find('.cruise-2018-excursions-accordion-button').toggle();
    }

    function toggleExcursions(e) {
        e && e.preventDefault();
        const $this = $(this);
        if ($this.data('ssc-accordion') === 'show') {
            $this.parent().parent().find('.cruise-2018-itineraries-excursions-accordion').slideDown(200, 'linear');
            $this.data('ssc-accordion', 'hide');
        } else if ($this.data('ssc-accordion') === 'hide') {
            $this.parent().parent().find('.cruise-2018-itineraries-excursions-accordion').slideUp();
            $this.data('ssc-accordion', 'show');
        }
        $this.blur();
        $this.find('i').toggle();

    }

    $(".cruise-2018-itineraries-port-excursions-btn").on('click touchstart', toggle);

    $(".cruise-2018-itineraries-itinerary-row-date-arrow").on('click touchstart', toggleExcursions);
});
