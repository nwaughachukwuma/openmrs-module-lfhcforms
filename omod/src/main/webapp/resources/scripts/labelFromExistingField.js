// This function will look up the text of the 'options' of a 'select' element, providing a given 'value'
// This is used when user wants to retrieve the mapping of conceptId and its Label (and obs field is present in the form)
// element: jquery element where the value/text mapping is present
// conceptId: conceptId of the concept to lookup
// only supports select fields with options value and labels
function mapBloodType (element , conceptId) {
	//console.log(conceptId);
	if (!(conceptId == "")) {
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
						if (value == conceptId) {
				var result = element.options[i].text;
				found = true;
			}
		}
		if(found) {
			return result;}
	}
}	