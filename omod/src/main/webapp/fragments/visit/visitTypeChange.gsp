<% 
ui.includeJavascript("lfhcforms", "visit/changeVisitType.js")
%>

<div id="change-visit-visittype-dialog" class="dialog" style="display: none;">
	<div class="dialog-header">  
    	<h3>
			${ui.message("lfhcforms.app.visit.visittype.change.header")}
		</h3>
	</div>
	<script type="text/javascript">
   		jq("#start-visit-with-visittype-confirm").removeClass("disabled");
	</script>
	<div class="dialog-content">
		<p class="dialog-instructions">
			${ui.message("lfhcforms.app.visit.visittype.change.title")}
		</p>
		<select id="new-visittype-drop-down"> 
			<% visitTypes.each { type -> %>
				<option class="dialog-drop-down" value ="${type.id}">${ ui.format(type) }</option>
			<% } %> 
		</select>
		<p class="dialog-instructions">${ui.message("lfhcforms.app.visit.visittype.change.instructions")}</p>
		<button id="change-visit-visittype-confirm" class="confirm right">${ ui.message("coreapps.confirm") }<i class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i></button>
		<button class="cancel">${ ui.message("coreapps.cancel") }</button>
	</div>
</div>