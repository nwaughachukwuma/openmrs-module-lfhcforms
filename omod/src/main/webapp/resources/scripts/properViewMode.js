// By Romain - romain@mekomsolutions.com
// Generic JavaScript to proper the VIEW mode (handles the toggleContainers, the "when" HFE markup and empty observations)

/* All forms should be in the form of :
<htmlform formUuid="44305e02-7e49-4f5a-8acf-49042acbe483" formName="OPD Nurse" formEncounterType="c7700650-63a6-4893-976e-7a7a0cc43b04" formVersion="0.4.5">

<ifMode mode="VIEW">
	<script type="text/javascript" id="properViewModeForTable">
		// JavaScript for VIEW mode
		jQuery(document).ready(function(){
			properViewMode("44305e02-7e49-4f5a-8acf-49042acbe483");
			properViewModeForTable("44305e02-7e49-4f5a-8acf-49042acbe483");
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



var properViewMode = function (formUuid) { 

	jQuery("htmlform").each(function (index, currentForm) {
		if (jQuery(currentForm).attr("formUuid") == formUuid) {

			/* Handle the VIEW mode for the toggleContainers */
			/* display only fields with observations */
			/* See toggleContainers.js for more documentation */
			jQuery(currentForm).find(".toggleContainer").each(function (index, toggleContainer) {
				if (!(jQuery(toggleContainer).find('.value').text() == "")) {
					jQuery(toggleContainer).show();
				}
			});

			/* remove buttons */
			jQuery(currentForm).find('.addEntry').remove();
			jQuery(currentForm).find('.removeEntry').remove();

			var i = 1;
			
			/* Iterate through each section of the current HTML form */
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

					/* hide the parent "p" section when Obs have emptyValue */
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
	jQuery(currentSection).hide()
}


});
}
});
}

var properViewModeForTable = function  (formUuid) {

	jQuery("htmlform").each(function (index, currentForm) {
		if (jQuery(currentForm).attr("formUuid") == formUuid) {

			/* iterate through each <td> of the forms */
			jQuery(currentForm).find("fieldset").find("td").each(function (index, td){
				/* hide if the <td> has emptyValue */
				if (jQuery(td).find(".emptyValue").text()) {
					jQuery(td).hide();
				}
			});

			/* replacing the tables by a list of "p" included in a "span" */
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