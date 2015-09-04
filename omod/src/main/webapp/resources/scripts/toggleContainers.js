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
        <div id="{n}-toggleContainer" templateBlockId="xrayObs" class="toggleContainer" style="display:none;" >
            <p>
                <obs id="{n}" conceptId="LFHC:1008" answerConceptIds="CIEL:12, CIEL:101, CIEL:161350" style="autocomplete"/>
                <i class="addEntry icon-">&#xf055; Add</i>
                <i class="removeEntry icon-">   &#xf056; Remove</i>
            </p>
        </div>
    </template>
    <render n="401" />
    <render n="402" />
    <render n="404" />
</repeat>

For future tests:
templateBlockId will identify the whole block template. It has to be unique. It is mandatory
class="toggleContainer" has to be provided to the div in order to apply any logic. Mandatory
the *toggleContainer id has to be in the form of {n}String* (ex: {n}thisIsAToggleContainer) because of the parsefloat function 
style="display:none;" is mandatory
a field with class="addEntry" has to be provided somewhere. Mandatory
a field with class="removeEntry" has to be provided somewhere. Mandatory
the n value can be any integer, as soon as it is unique over the whole htmlform.

 */

var toggleContainers = function() {

	jQuery(".toggleContainer").each(function (index, toggleContainer) {

		// checks if this toggleContainer is the first of its block
		if(!(jQuery(toggleContainer).prev().attr("templateBlockId") == jQuery(toggleContainer).attr("templateBlockId") )) {
			jQuery(toggleContainer).show();
			jQuery(toggleContainer).find(".removeEntry").remove();
		};

		// checks if this toggleContainer is the last
		if(!(jQuery(toggleContainer).next().attr("templateBlockId") == jQuery(toggleContainer).attr("templateBlockId") )) {
			jQuery(toggleContainer).find(".addEntry").remove();
		};
		
		// if the obs is not empty, show the field 
		if (!(getValue(parseFloat(toggleContainer.id)+".value") == "") || (getValue(parseFloat(toggleContainer.id)+".value") == null) ) {
			jQuery(toggleContainer).show();
			
			// if the next obs is not empty, hide buttons
			if (!(getValue(parseFloat(jQuery(toggleContainer).next().attr("id"))+".value") == "")) {
				jQuery(toggleContainer).find(".addEntry").hide();
				jQuery(toggleContainer).find(".removeEntry").hide();
			}
		}
	});

	jQuery('i.addEntry').click(function(){
		// allow Add only if field is not empty
		if (!(getValue(parseFloat(jQuery(this).closest(".toggleContainer").attr("id"))+".value") == "") ) {
			jQuery(this).hide();
			jQuery(this).closest(".toggleContainer").find(".removeEntry").hide();
			jQuery(this).closest(".toggleContainer").next().show();
		}
		return false;});

	jQuery('i.removeEntry').click(function(){
		// do not allow Remove if some text is in the field		
		if ((getValue(parseFloat(jQuery(this).closest(".toggleContainer").attr("id"))+".value") == "")) {
			jQuery(this).closest(".toggleContainer").hide();
			jQuery(this).closest(".toggleContainer").prev().find(".addEntry").show();
			jQuery(this).closest(".toggleContainer").prev().find(".removeEntry").show();
		}
		return false;});
}
