<%
ui.includeJavascript("uicommons", "angular.min.js")
ui.includeJavascript("uicommons", "angular-resource.min.js")
ui.includeJavascript("uicommons", "angular-common.js")
ui.includeJavascript("uicommons", "angular-app.js")

ui.includeJavascript("uicommons", "services/obsService.js")
ui.includeJavascript("uicommons", "services/encounterService.js")
%>

<%
ui.includeCss("lfhcforms", "patientHeaderNote/xeditable.min.css")
ui.includeJavascript("lfhcforms", "patientHeaderNote/resources/xeditable.min.js")
ui.includeJavascript("lfhcforms", "patientHeaderNote/resources/moment.min.js")

ui.includeJavascript("lfhcforms", "patientHeaderNote/app.js")
ui.includeCss("lfhcforms", "patientHeaderNote/patientHeaderNote.css")
ui.includeJavascript("lfhcforms", "patientHeaderNote/controllers/patientHeaderNoteCtrl.js")

ui.includeJavascript("lfhcforms", "patientHeaderNote/directives/clickToEditObs.js")
%>

<script type="text/javascript">
	// check if 'lfhcforms' global variable already exists (possibly set somewhere else in the module)
	if ("lfhcforms" in window) {
		window.lfhcforms.patientHeaderNote = {
			config: ${jsonConfig}
		}
	} else {
		window.lfhcforms = {
			patientHeaderNote: {
				config: ${jsonConfig}
			}
		}
	}
</script>

<div class="patientHeaderNote">
	<div ng-app="lfhcforms.fragment.patientHeaderNote" ng-controller="patientHeaderNoteCtrl">
		<lfhcforms-click-to-edit-obs config="config" module="module"></lfhcforms-click-to-edit-obs>
	</div>
</div>