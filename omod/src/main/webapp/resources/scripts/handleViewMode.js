// By Romain - romain@mekomsolutions.com
// Generic JavaScript to clean the VIEW mode (handles the toggleContainers, the "when" HFE markup and empty observations...)

/* All forms should be in the form of :
<htmlform formUuid="44305e02-7e49-4f5a-8acf-49042acbe483" formName="OPD Nurse" formEncounterType="c7700650-63a6-4893-976e-7a7a0cc43b04" formVersion="0.4.5">

<ifMode mode="VIEW">
	<script type="text/javascript" id="handleViewModeForTable">
		// JavaScript for VIEW mode
		$(document).ready(function(){
			handleViewMode("44305e02-7e49-4f5a-8acf-49042acbe483");
			handleViewModeForTable("44305e02-7e49-4f5a-8acf-49042acbe483");
		});
	</script>
</ifMode>


<div class="hidden" id="who-when-where">
	...
</div>

<section id="general" sectionTag="section" headerStyle="title" headerCode="General">

	<fieldset>
		<legend>
			Other Infectious Concerns
		</legend>
		<h3>
			Other infectious concerns
		</h3>
		// observations should be nested in a <p>. One <obs> per <p>. Ideally, one <obs> per <fieldset>
		<p>
			<obs conceptId="CIEL:159395" rows="4" cols="50"/>
		</p>
	</fieldset>

</section>
<submit/>
<htmlform>
*/



var handleViewMode = function (formUuid) { 

	$("htmlform").each(function (index, currentForm) {
		if ($(currentForm).attr("formuuid") == formUuid) {
			
			
			/* Handle the VIEW mode for the toggleContainers */
			/* display only fields with observations */
			/* See toggleContainers.js for more documentation */
			$(currentForm).find(".toggleContainer").each(function (index, toggleContainer) {
				if (!($(toggleContainer).find('.value').text() == "")) {
					$(toggleContainer).show();
				}
			});

			/* remove buttons */
			$(currentForm).find('.addEntry').remove();
			$(currentForm).find('.removeEntry').remove();

			var i = 1;
			
			/* Iterate through each section of the current HTML form */
			$(currentForm).find("section").each(function (indexS, currentSection) {

				var emptyFieldset = -1;
				var hideSection = false;

				$(currentSection).find("fieldset").each(function (indexF, currentFieldset) {

					var hideFieldset = false;

					$(currentFieldset).hide();

					/* Show all non-empty obs */	
					$(currentFieldset).find(".value").closest("fieldset").show();

					/* Hide empty values */
					var emptyValue = -1;
					$(currentFieldset).find(".value").each(function (indexV, element) {
						/* Hide "p" section when value is empty */
						if ($(element).text() == "") {
							$(element).closest("p").hide();
							$(element).closest("p").prev("h3").hide();
							emptyValue= emptyValue+1;
						}

						
						if ((emptyValue == indexV)) {
							hideFieldset = true;
						} else {
							hideFieldset = false;
						}

						/* display the text of a "p" as an obs title when there is class "questionLabel" */
						/*<p>
						/*		<span class="questionLabel">Patient is contact of known or suspected infectious case</span>
						/*		<obs conceptId="162633"/>
						/*</p>
						*/

						$(element).closest("p").find(".questionLabel").filter(":visible").each(function (indexL, labelSpan) {

							/* get the free text of the obs to be used as h3 */
							var obsTitle = $(labelSpan)
							.clone()
							.children()
							.remove()
							.end()
							.text();

							/* get the "obsSpan" innerHTML */
							var obsSpan = $(element).closest("span");

							/* Create a new "p" */
							var newP = $("<p>");
							$(newP).append("<span class=\"obs-field\">"+$('<div>').append($(element).closest("span").clone()).html()+"</span>");
							/*var newSpan = $("<span>"); */
							/*$(newSpan).append("<h3>"+obsTitle+" </h3>"+$('<div>').append($(newP).clone()).html()); */

							/* replace the current "p" by its new version */
							$(element).closest("p").replaceWith("<h3>"+obsTitle+" </h3>"+$('<div>').append($(newP).clone()).html());
						});

					});

/* hide the parent "p" section when Obs have emptyValue */
$(currentFieldset).find(".emptyValue").closest("p").hide();

if (hideFieldset) {
	$(currentFieldset).hide()
}

/* handle the "when" markup */
$(currentFieldset).find(".thenDisplay").find(".value").each(function (indexD, thenDisplay) {
	if ($(thenDisplay).text() == "") {
		$(thenDisplay).closest("p").hide();
	}
});
i = i+1;

/* if all fieldsets of the current section are hidden, define an empty fieldset */
if ($(currentFieldset).css("display") == "none"){
	emptyFieldset = emptyFieldset+1;
}

/*1 if all fieldsets are empty, define the hideSection */
if(emptyFieldset == indexF) {
	hideSection = true;
} else {
	hideSection = false;
}
});

if (hideSection) {
	$(currentSection).hide()
}


});
}
});
}

var handleViewModeForTable = function  (formUuid) {

	$("htmlform").each(function (index, currentForm) {

		if ($(currentForm).attr("formUuid") == formUuid) {

			/* iterate through each <td> of the forms */
			$(currentForm).find("fieldset").find("td").each(function (index, td){
				/* remove if the <td> has emptyValue */
				if ($(td).find(".emptyValue").text()) {
					$(td).remove();
				}
			});

			/* replacing the tables by a list of "p" included in a "span" */
			$("table").each(function(index, table){
				var span = $("<span>");
				$(span).addClass("autogenerated");
				$("th", this).each(function() {
					var title = $("<span>");
					title.append(this.innerHTML);
					$(title).addClass("title");
					span.append(title);
				});
				$("td", this).each(function(){
					span.append(this.innerHTML);
				});
				$(table).replaceWith(span);
			})

		}

	});

}
