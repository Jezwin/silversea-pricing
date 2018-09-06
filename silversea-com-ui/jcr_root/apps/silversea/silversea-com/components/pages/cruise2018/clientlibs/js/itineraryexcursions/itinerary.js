$(function () {

    function toggle(e) {
        e && e.preventDefault();
        var  $this = $(this);
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
        var  $this = $(this);
        var $arrow = $this.find(".cruise-2018-itineraries-itinerary-row-date-arrow");
        if ($arrow.data('ssc-accordion') === 'show') {
            $this.parent().find('.cruise-2018-itineraries-excursions-accordion').slideDown(200, 'linear');
            $arrow.data('ssc-accordion', 'hide');
            $this.css("border-bottom-color","transparent");
        } else if ($arrow.data('ssc-accordion') === 'hide') {
            $this.parent().find('.cruise-2018-itineraries-excursions-accordion').slideUp();
            $arrow.data('ssc-accordion', 'show');
            $this.css("border-bottom-color","rgba(152, 152, 155, 0.5)");
        }
        $arrow.blur();
        $arrow.find('i').toggle();
    }

    $(".cruise-2018-itineraries-port-excursions-btn").on('click touchstart', toggle);

    $(".cruise-2018-itineraries-itinerary-row-container-with-excursion").on('click touchstart', toggleExcursions);
});
