/* JS functions to calculate the Living Conditions Score, based on a map provied as JSON file */

// TODO: Retrieve this map from external resource
var json = {
	"LFHC:1320": {
		"0":">=4",
		"1":"2-3",
		"2":"0-1"
	},
	"LFHC:1321": {
		"0":"LFHC:1400",
		"1":"LFHC:1401",
		"2":"LFHC:1402"
	}
};


var calculateLCS = function (inputMap, jsonMap) {
	for (var concept in inputMap) {
		if (inputMap.hasOwnProperty(concept)) {
			alert(key + " -> " + p[key]);
		}
	}

}
