/**
 * Logic to handle toggling div containers
 * @version v0.1
 * @link romain@mekomsolutions.com
 */

//Handles the toggle of div containers with Add / Remove buttons
//HTML ids and classes must be of the form of:
/*
<repeat>
    <template>
        <p id="{n}-toggleContainer" templateBlockId="xrayObs" class="toggleContainer" style="display:none;" >
            <obs id="{n}" conceptId="LFHC:1008" answerConceptIds="CIEL:12, CIEL:101, CIEL:161350" style="autocomplete"/>
            <i class="addEntry icon-">&#xf055; Add</i>
            <i class="removeEntry icon-">   &#xf056; Remove</i>
        </p>
    </template>
    <render n="401" />
    <render n="402" />
    <render n="404" />
</repeat>

For future tests:
templateBlockId will identify the whole block template. It has to be unique. It is mandatory
class="toggleContainer" has to be provided to the "p" in order to apply any logic (if multiple "obs" are to be added in a single container, all "p" could be wrapped within a "div". Mandatory
the *toggleContainer id has to be in the form of {n}String* (ex: {n}thisIsAToggleContainer) because of the parsefloat function
the obs id has to be {n} 
style="display:none;" is mandatory
a field with class="addEntry" has to be provided somewhere. Mandatory
a field with class="removeEntry" has to be provided somewhere. Mandatory
the {n} value can be any integer, as soon as it is unique over the whole htmlform.

*/

var toggleContainers = function() {

	$(".toggleContainer").each(function (index, toggleContainer) {

		// checks if this toggleContainer is the first of its block
		if(!($(toggleContainer).prev().attr("templateBlockId") == $(toggleContainer).attr("templateBlockId") )) {
			$(toggleContainer).show();
			$(toggleContainer).find(".removeEntry").remove();
		};

		// checks if this toggleContainer is the last
		if(!($(toggleContainer).next().attr("templateBlockId") == $(toggleContainer).attr("templateBlockId") )) {
			$(toggleContainer).find(".addEntry").remove();
		};
		
		// if the obs is not empty, show the field 
		if (!(getValue(parseFloat(toggleContainer.id)+".value") == "") || (getValue(parseFloat(toggleContainer.id)+".value") == null) ) {
			$(toggleContainer).show();
			
			// if the next obs is not empty, hide buttons
			if (!(getValue(parseFloat($(toggleContainer).next().attr("id"))+".value") == "")) {
				$(toggleContainer).find(".addEntry").hide();
				$(toggleContainer).find(".removeEntry").hide();
			}
		}
	});

	$('i.addEntry').click(function(){
		// allow Add only if one field is NOT empty
		var currentAddEntry = $(this);
		var addContainer = 0;
		$(currentAddEntry).closest(".toggleContainer").find("input").each( function (index, currentInput) {
			if (!($(currentInput).val() == "")) {
				// handle checkbox fields
				if ($(currentInput).attr('type') == "checkbox") {
					if($(currentInput).prop("checked") == true) {
						addContainer = addContainer + 1;
					}
				} else {
					addContainer = addContainer + 1;
				}
			}
		});
		// handle drop down lists
		$(currentAddEntry).closest(".toggleContainer").find("select").each( function (index, currentSelect) {
			if (!($(currentSelect).find(":selected").text() == "")) {
				addContainer = addContainer + 1;
			}
		});
		if (addContainer > 0) {
			$(currentAddEntry).hide();
			$(currentAddEntry).closest(".toggleContainer").find(".removeEntry").hide();
			$(currentAddEntry).closest(".toggleContainer").next().show();		
		}
		return false;});

	$('i.removeEntry').click(function(){
		// do not allow Remove if fields have a value	
		var currentRemoveEntry = $(this);
		var removeContainer = 0;

		$(currentRemoveEntry).closest(".toggleContainer").find("input").each( function (index, currentInput) {
			if (!($(currentInput).val() == "")) {
				// handle checkbox fields
				if ($(currentInput).attr('type') == "checkbox") {
					if($(currentInput).prop("checked") == true) {
						removeContainer = removeContainer + 1;
					}
				} else {
					removeContainer = removeContainer + 1;
				}
			}
		})
		// handle drop down lists
		$(currentRemoveEntry).closest(".toggleContainer").find("select").each( function (index, currentSelect) {
			if (!($(currentSelect).find(":selected").text() == "")) {
				removeContainer = removeContainer + 1;
			}
		});
		if (removeContainer == 0) {
			$(currentRemoveEntry).closest(".toggleContainer").hide();
			$(currentRemoveEntry).closest(".toggleContainer").prev().find(".addEntry").show();
			$(currentRemoveEntry).closest(".toggleContainer").prev().find(".removeEntry").show();
		}
		return false;});
}
