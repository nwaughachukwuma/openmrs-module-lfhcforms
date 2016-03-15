var handleViewMode = function (formUuid) { 

	$("htmlform").each(function (index, currentForm) {
		if ($(currentForm).attr("formuuid") == formUuid) {

			

			/* display only '.toggleContainers' fields with observations */
			/* See toggleContainers.js for more documentation */
			$(currentForm).find(".toggleContainer").each(function (index, toggleContainer) {
				if (!($(toggleContainer).find('.value').text() == "")) {
					$(toggleContainer).show();
				}
			});

			/* remove buttons */
			$(currentForm).find('.addEntry').remove();
			$(currentForm).find('.removeEntry').remove();


			/* Iterate through each section of the current HTML form */
			$(currentForm).find("section").each(function (indexS, currentSection) {

				var emptyFieldset = -1;
				var hideSection = false;

				$(currentSection).find("fieldset").each(function (indexF, currentFieldset) {

					var hideFieldset = false;

					if ($(currentFieldset).find("p").length > 1) {
						$(currentFieldset).find("*").addClass("multiple");
						$(currentFieldset).find("h3").addClass("indented");
					}

					/* Hide elements with class value and text null */
					$(currentFieldset).find(".value").each(function (indexV, element) {
						/* Hide "p" section when value is empty */
						if ($(element).text() == "") {
							$(element).closest("p").hide();
							$(element).closest("p").prev("h3").hide();
							$(element).addClass("emptyValue");
						}
					});

					/*  */
					$(currentFieldset).find("p").prev("h3").addClass("questionLabel");

					/* hide the parent "p" section when Obs have emptyValue */
					$(currentFieldset).find(".emptyValue").closest("p").hide();
					$(currentFieldset).find(".emptyValue").closest("p").prev("h3").hide();

					var emptyValue = -1;
					/* hide the fieldset when all "p" contain ".emptyValue" */
					$(currentFieldset).find("p").each(function (indexP, currentP) {
						if ($(currentP).find('.emptyValue').val() == "") {
							emptyValue = emptyValue + 1;	
						}

						if ((emptyValue == indexP)) {
							hideFieldset = true;
						} else {
							hideFieldset = false;
						}
					});

					if (hideFieldset) {
						$(currentFieldset).addClass("emptyFieldset");
					}

					/* if all fieldsets of the current section are hidden, define an empty fieldset */
					if ($(currentFieldset).find(".emptyFieldset").val() == ""){
						emptyFieldset = emptyFieldset + 1;
					}
					/* if all fieldsets are empty, define the hideSection */
					if(emptyFieldset == indexF) {
						hideSection = true;
					} else {
						hideSection = false;
					}

				});

				if (hideSection) {
					$(currentSection).addClass("emptySection");
				}

			});
		}
	})
}
