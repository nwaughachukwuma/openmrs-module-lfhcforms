// By Romain - romain@mekomsolutions.com
// Generic JavaScript to proper the VIEW mode (handles the toggleContainers, the "when" HFE markup and empty observations)


var properViewMode = function (formuuid) { 

	jQuery("htmlform").each(function (index, currentForm) {
		if (jQuery(currentForm).attr("formUuid") == formuuid) {

			// Handle the VIEW mode for the toggleContainers
			// display only fields with observations
			if (!(jQuery(currentForm).find(".toggleContainer").find('.value').text() == "")) {
				jQuery(currentForm).find(".toggleContainer").show();
			}
			// remove buttons
			jQuery(currentForm).find('.addEntry').remove();
			jQuery(currentForm).find('.removeEntry').remove();

			var i = 1;
			jQuery(currentForm).find("fieldset").each(function (index) {

				jQuery(this).hide();

				// Show all non-empty obs
				jQuery(this).find(".value").closest("fieldset").show();

				// or hide fields with empty value
				jQuery(this).find(".value").each(function (index1, element) {
					if (jQuery(element).text() == "") {
						jQuery(element).closest("fieldset").hide();	
					}	
				});
				// hide the "p" section when Obs have emptyValue
				jQuery(this).find(".emptyValue").closest("p").hide();


				// handle the "when" markup
				jQuery(this).find(".thenDisplay").find(".value").each(function (index, thenDisplay) {
					if (jQuery(this).text() == "") {
						jQuery(this).closest("p").hide();
					}

				});
				i = i+1;
			});
		}
	});
}
