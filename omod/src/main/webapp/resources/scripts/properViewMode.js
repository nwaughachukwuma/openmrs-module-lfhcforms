// By Romain - romain@mekomsolutions.com
// Generic JavaScript to proper the VIEW mode (handles the toggleContainers, the "when" HFE markup and empty observations)



var properViewMode = function (formUuid) { 

	jQuery("htmlform").each(function (index, currentForm) {
		if (jQuery(currentForm).attr("formUuid") == formUuid) {

			/* Handle the VIEW mode for the toggleContainers */
			/* display only fields with observations */
			jQuery(currentForm).find(".toggleContainer").each(function (index, toggleContainer) {
				if (!(jQuery(toggleContainer).find('.value').text() == "")) {
					jQuery(toggleContainer).show();
				}
			});

			/* remove buttons */
			jQuery(currentForm).find('.addEntry').remove();
			jQuery(currentForm).find('.removeEntry').remove();

			var i = 1;
			

			jQuery(currentForm).find("section").each(function (indexS, currentSection) {

				var emptyFieldset = -1;
				var hideSection = false;

				jQuery(currentSection).find("fieldset").each(function (indexF, currentFieldset) {

					var hideFieldset = false;

					jQuery(currentFieldset).hide();

					/* Show all non-empty obs */	
					jQuery(currentFieldset).find(".value").closest("fieldset").show();

					/* Hide empty values */
					var emptyValue = -1;
					jQuery(currentFieldset).find(".value").each(function (indexV, element) {
						/* Hide "p" section when value is empty */
						if (jQuery(element).text() == "") {
							jQuery(element).closest("p").hide();
							emptyValue= emptyValue+1;
						}	
						if ((emptyValue == indexV)) {
							hideFieldset = true;
						} else {
							hideFieldset = false;
						}
					});

					/* hide the "p" section when Obs have emptyValue */
					jQuery(currentFieldset).find(".emptyValue").closest("p").hide();

					if (hideFieldset) {
						jQuery(currentFieldset).hide()
					}

					/* handle the "when" markup */
					jQuery(currentFieldset).find(".thenDisplay").find(".value").each(function (indexD, thenDisplay) {
						if (jQuery(thenDisplay).text() == "") {
							jQuery(thenDisplay).closest("p").hide();
						}
					});
					i = i+1;

					if ($(currentFieldset).css("display") == "none"){
						emptyFieldset = emptyFieldset+1;
					}

					if(emptyFieldset == indexF) {
						hideSection = true;
					} else {
						hideSection = false;
					}
				});
if (hideSection) {
	jQuery(currentSection).hide()
}


});
}
});
}

var properViewModeForTable = function  (formUuid) {

	jQuery("htmlform").each(function (index, currentForm) {
		if (jQuery(currentForm).attr("formUuid") == formUuid) {

			jQuery(currentForm).find("fieldset").find("td").each(function (index, td){
				if (jQuery(td).find(".emptyValue").text()) {
					jQuery(td).hide();
				}
			});

					// replacing the tables by a list of "p" included in a "span"
					$("table").each(function(index, table){
						var span = $("<span>")
						$("th", this).each(function() {
							var title = $("<span>");
							title.append(this.innerHTML);
							$(title).addClass("title");
							span.append(title);
						});
						$("td", this).each(function(){
							span.append(this.innerHTML.replace("[X]&nbsp;", ""));
						});
						$(table).replaceWith(span);
					})

				}
			});
}