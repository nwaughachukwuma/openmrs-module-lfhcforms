// only supports select fields with option values and labels
function mapBloodType (element , bloodType) {
	//console.log(bloodType);
	if (!(bloodType == "")) {
		var info = propertyAccessorInfo[element];
		if (info) {
			var widgetId = info.id;
			//console.log(widgetId);
		}
		var element = dwr.util.byId(widgetId);
		var found = false;
		var i;
		for (i = 0; i < element.options.length; i++) {
			//console.log(element.options[i].value);
			var value = element.options[i].value;
						if (value == bloodType) {
				var result = element.options[i].text;
				found = true;
			}
		}
		if(found) {
			return result;}
	}
}	