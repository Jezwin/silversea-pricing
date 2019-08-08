function destinationRedirect(location){if(window.location.hash){var hash=window.location.hash.substring(1);if(hash=='silversea_expeditions'){location=location+'?voyage-type=silversea_expedition'}
if(hash=='silversea_cruises'){location=location+'?voyage-type=silversea_cruise'}}
window.location.href=location;return!1}
