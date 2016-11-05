<%
ui.includeJavascript("uicommons", "angular.min.js")
ui.includeJavascript("uicommons", "angular-resource.min.js")
ui.includeJavascript("uicommons", "angular-common.js")
ui.includeJavascript("uicommons", "angular-app.js")

ui.includeJavascript("uicommons", "services/obsService.js")
ui.includeJavascript("uicommons", "services/encounterService.js")
%>

<%
ui.includeJavascript("lfhcforms", "patientQuickNote/resources/xeditable.min.js")
ui.includeJavascript("lfhcforms", "patientQuickNote/resources/moment.min.js")
ui.includeCss("lfhcforms", "patientQuickNote/xeditable.min.css")
ui.includeCss("lfhcforms", "patientQuickNote/quickNote.css")
ui.includeJavascript("lfhcforms", "patientQuickNote/app.js")
ui.includeJavascript("lfhcforms", "patientQuickNote/directives/patientQuickNote.js")
ui.includeJavascript("lfhcforms", "patientQuickNote/controllers/patientQuickNote.js")
%>

<script type="text/javascript">
  window.config = ${jsonConfig}; // Getting the config from the Spring Java controller.
</script>


<div class="">
	<div ng-app="lfhcforms.fragment.patientQuickNote" ng-controller="patientQuickNoteCtrl">
		<lfhcforms-quick-note obs-query="obsQuery"></lfhcforms-quick-note>
	</div>
</div>
