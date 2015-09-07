// By Romain - romain@mekomsolutions.com
// Generic JavaScript to proper the VIEW mode (handles the toggleContainers, the "when" HFE markup and empty observations)


var properViewMode = function (formUuid) { 

	jQuery("htmlform").each(function (index, currentForm) {
		if (jQuery(currentForm).attr("formUuid") == formUuid) {

			// Handle the VIEW mode for the toggleContainers
			// display only fields with observations
			jQuery(currentForm).find(".toggleContainer").each(function (index, toggleContainer) {
				if (!(jQuery(toggleContainer).find('.value').text() == "")) {
					jQuery(toggleContainer).show();
				}
			});

			// remove buttons
			jQuery(currentForm).find('.addEntry').remove();
			jQuery(currentForm).find('.removeEntry').remove();

			var i = 1;

			jQuery(currentForm).find("fieldset").each(function (index) {

				var hideFieldset = false;

				jQuery(this).hide();

				// Show all non-empty obs
				jQuery(this).find(".value").closest("fieldset").show();

				// Hide empty values
				var emptyValue = -1;
				jQuery(this).find(".value").each(function (index, element) {
					// Hide "p" section when value is empty
					if (jQuery(element).text() == "") {
						jQuery(element).closest("p").hide();
						emptyValue= emptyValue+1;
					}	
					if ((emptyValue == index)) {
						hideFieldset = true;
					}
				});

				// hide the "p" section when Obs have emptyValue
				jQuery(this).find(".emptyValue").closest("p").hide();


				if (hideFieldset) {
					jQuery(this).hide()
				}

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