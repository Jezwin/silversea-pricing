var map,
itemSelected,
infoWindowList = [],
markerList = [],
dataFilter =  [],
defaultResults = null,
latLngFilter = null,
placeFilter = null,
agencyFilter = null,
boundsMap= null,
geocoder = new google.maps.Geocoder();

var cFindTravelAgent = function(data, phoneLabel) {
	
	map = new google.maps.Map(document.getElementById('map-travel-agent'), {
		center: {
			lat: -34.397,
			lng: 150.644
		},
		mapTypeId: google.maps.MapTypeId.ROADMAP,
		gestureHandling: "greedy",//"cooperative",
		zoom: 4
	});
	
	if (window.hasOwnProperty("dataLayer") && window.dataLayer.length > 0) {
		geocoder.geocode({
			'address': window.dataLayer[0].user_country	
		}, function (results, status) {
			if (status === 'OK') {
				defaultResults = results;
				createMapAndListTravelAgent();
			}
		});
	}
	
	var input = document.getElementById("location");
	var autocomplete = new google.maps.places.Autocomplete(input, null);
	autocomplete.bindTo('bounds', map);
	autocomplete.addListener("place_changed", function () {
		var place = autocomplete.getPlace();
		if (!place.geometry) {
			return;
		}
		placeFilter = place;
		map.changeBounds = true;
		var bounds =  new google.maps.LatLngBounds(placeFilter.geometry.location);
		boundsMap = new google.maps.LatLngBounds(placeFilter.geometry.location);
		map.fitBounds(bounds);
		map.setCenter(placeFilter.geometry.location);
		if (placeFilter.address_components.length > 1) {
			map.setZoom(10);
		} else {
			map.setZoom(6);
		}
		updateTravelAgentList('location');
	});
	
	var options = {
			data: data,
			getValue:  "title",
			list: {
				match: {
					enabled: true
				},
				onChooseEvent: function () {
					var item = $("#agency").getSelectedItemData();
					itemSelected = item;
					latLngFilter = new google.maps.LatLng(item.latitude, item.longitude);
					updateTravelAgentList('agency');
				}
			}
	}
	$("#agency").easyAutocomplete(options);
	$('div.easy-autocomplete').removeAttr('style');
	
	function updateTravelAgentList(type) {
		closeInfoWindows();
		$("#list").empty();
		var location = $("#location").val();
		var agency = $("#agency").val();
		dataFilter = [];
		dataFilter = data.filter(function(item){
			var latLng = new google.maps.LatLng(item.latitude, item.longitude);
			if (location.trim().length > 0 && agency.trim().length > 0) {
				return map.getBounds().contains(latLng) && item.agencyId === itemSelected.agencyId;
			} else if (location.trim().length > 0) {
				return map.getBounds().contains(latLng);
			} else if (agency.trim().length > 0) {
				return item.agencyId === itemSelected.agencyId;
			}
		});
		if (dataFilter.length > 0 ){
			if (type == 'agency') {
				map.setCenter(new google.maps.LatLng(itemSelected.latitude, itemSelected.longitude));
				map.setZoom(10);
			}
			sortByDistance(map.getCenter(), dataFilter);
			$(".scroll-box").show();
			createOrDestroyMarkers(map);
		} else {
			$(".scroll-box").hide();
			createOrDestroyMarkers(null);
		}
		$("#number_results").html(dataFilter.length);
		for (var i = 0; i < dataFilter.length; i++) {
			(function (item) {
				    var latLng = new google.maps.LatLng(item.latitude, item.longitude);
					var a = $('<a id="'+  item.agencyId  +'" class="list-group-item"/>');
					a.append('\
							<h4 class="list-group-item-heading">' + item.title + '</h4>\
							<p class="list-group-item-text">' + item.address + '<br>' + item.city + '<br> <b>' + phoneLabel + ': </b>'+ item.phone  +'</p>\
					');
					a.click(function () {
						itemSelected = item;
						createMarkerInfoWindow(item);
						map.setCenter(latLng);
						map.setZoom(10);
					});
					$("#list").append(a);
					createMarkerInfoWindow(item);
			})(dataFilter[i]);
		}
	};//updateTravelAgentList
	
	function createMapAndListTravelAgent(type) {
		var boundsToCompare = null;
		if (type == 'update') {
			boundsToCompare = defaultResults[0].geometry.bounds;
			closeInfoWindows();
		} else {
			boundsToCompare = map.getBounds();
		}
		$("#list").empty();
		var numEle = 0;
		for (var i = 0; i < data.length; i++) {
			(function (item) {
				var latLng = new google.maps.LatLng(item.latitude, item.longitude);
				if (boundsToCompare.contains(latLng)) {
					var a = $('<a id="'+  item.agencyId  +'" class="list-group-item"/>');
					a.append('\
							<h4 class="list-group-item-heading">' + item.title + '</h4>\
							<p class="list-group-item-text">' + item.address + '<br>' + item.city + '<br> <b>'+ phoneLabel +': </b>' + item.phone + '</p>\
					');
					a.click(function () {
						itemSelected = item;
						createMarkerInfoWindow(item);
						map.setCenter(latLng);
						map.setZoom(10);
					});
					$("#list").append(a);
					numEle++;
				}
				if (type != 'update') {
					createMarkerInfoWindow(item);
				}
			})(data[i]);
		}
		$("#number_results").html(numEle);
		if (numEle > 0 ){
			$(".scroll-box").show();
			map.changeBounds = true;
			map.fitBounds(boundsToCompare);
			map.setCenter(defaultResults[0].geometry.location);
			map.setZoom(4);
			createOrDestroyMarkers(map);
		} else {
			$(".scroll-box").hide();
			createOrDestroyMarkers(null);
		}
	};//createMapAndListTravelAgent
	
	
	function createMarkerInfoWindow(item) {
		var latLng = new google.maps.LatLng(item.latitude, item.longitude);
		var infoWindow = new google.maps.InfoWindow();
		infoWindow.setContent('\
				<div class="agent-info">\
				<div class="agent-title">' + item.title + '</div>\
				<div>' + item.address + '</div>\
				<div>' + item.city + '</div>\
				<div><label>'+ phoneLabel +'</label>: <a href="callto:"' + item.phone + '</a>' + item.phone + '</div></div>\
				<div><a href="#" class="action zoomhere" onclick="zoomHere(' + item.latitude + ',' + item.longitude + ',event)">Zoom here</a>\
				<a href="#" class="action streetview" onclick="streetView(' + item.latitude + ',' + item.longitude + ',event)">Street view</a>\
				</div>\
		');
		
		infoWindowList.push(infoWindow);
		var latLng = new google.maps.LatLng(item.latitude, item.longitude);

		var marker = new google.maps.Marker({
			position: latLng,
			map: map
		});
		
		marker.addListener('click', function () {
			closeInfoWindows();
			infoWindow.open(map, marker);
		});
		
		markerList.push(marker);
		
		if (itemSelected && (itemSelected.agencyId == item.agencyId)) {
			closeInfoWindows();
			infoWindow.open(map, marker);
		}
		return {
			marker: marker,
			infoWindow: infoWindow
		};
	}; //createMarkerInfoWindow
	
	function closeInfoWindows() {
		infoWindowList.forEach(function (item) {
			item.close();
		});
	}; //closeInfoWindow
	
	function createOrDestroyMarkers(mapToInsert) {
		markerList.forEach(function (item) {
			item.setMap(mapToInsert);
		});
	}; //createOrDestroyMarkers
	
	function distanceTo(point, location) {
		var R = 6371; // mean radius of earth
		var lat1 = toRad_(location.latitude);
		var lon1 = toRad_(location.longitude);
		var lat2 = toRad_(point.lat());
		var lon2 = toRad_(point.lng());
		var dLat = lat2 - lat1;
		var dLon = lon2 - lon1;
		
		var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
		Math.cos(lat1) * Math.cos(lat2) *
		Math.sin(dLon / 2) * Math.sin(dLon / 2);
		var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return R * c;
	}; //distanceTo
	
	function sortByDistance(latLng, data) {
		data.sort(function (a, b) {
			return distanceTo(latLng, a) - distanceTo(latLng, b);
		});
	}; //sortByDistance
	
	function toRad_(degrees) {
		return degrees * Math.PI / 180;
	}; //toRad_
	
	$("#agency").keyup(function(){
		if ($("#agency").val().trim().length == 0 && $("#location").val().trim().length == 0) {
			createMapAndListTravelAgent('update');
		} else if ($("#agency").val().trim().length == 0 && $("#location").val().trim().length > 0) {
			updateTravelAgentList('location');
		}
	});
	
	$("#location").keyup(function(){
		if ($("#location").val().trim().length == 0 && $("#agency").val().trim().length == 0) {
			createMapAndListTravelAgent('update');
		} else if ($("#location").val().trim().length == 0 && $("#agency").val().trim().length > 0) {
			updateTravelAgentList('agency');
		}
	});
	
};//cFindTravelAgent

function zoomHere(lat, lng, event) {
	event.preventDefault();
	map.setOptions({
		center: {
			lat: lat,
			lng: lng
		},
		zoom: 16
	});
}; //zoomHere

function streetView(lat, lng, event) {
	event.preventDefault();
	var streetView = map.getStreetView();
	streetView.setPosition({
		lat: lat,
		lng: lng
	});
	streetView.setVisible(true);
}; //streetView

