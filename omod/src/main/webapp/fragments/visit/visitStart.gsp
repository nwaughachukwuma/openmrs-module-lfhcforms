<% 
ui.includeJavascript("lfhcforms", "visit/startVisit.js")
%>

<div id="start-visit-with-visittype-dialog" class="dialog" style="display: none;">
	<div class="dialog-header">  
		<h3>
			${ ui.message("coreapps.visit.createQuickVisit.title") }
		</h3>
	</div>
	<div class="dialog-content">
		<% if (activeVisits) { %>
			<script type="text/javascript">
				jq("#start-visit-with-visittype-confirm").addClass("disabled");
			</script>

			<p class="dialog-instructions">
				<i class="icon-sign-warning">&#xf071;</i> ${ui.message("lfhcforms.app.visit.visittype.start.warning", ui.format(patient.patient))}
			</p>
			<ul class="list" style="margin-bottom:0px">
				<% activeVisits.each { activeVisit -> %> 
				<li>
					${ui.format(activeVisit)}
				</li>
				<% } %>
			</ul>
			<p class="dialog-instructions">
				${ ui.message("lfhcforms.app.visit.visittype.start.warning.instructions") }
			</p> 

		<% } else { %>
			<script type="text/javascript">
				jq("#start-visit-with-visittype-confirm").removeClass("disabled");
			</script>
			<p class="dialog-instructions">
				${ ui.message("lfhcforms.app.visit.visittype.start.instructions") }
			</p>
			<select id="visit-visittype-drop-down"> 
				<% visitTypes.each { type -> %>
					<% if (currentVisitType == type) { %>
						<script type="text/javascript">
							jq("#visit-visittype-drop-down").val(${type.id});
						</script>
					<% } %>
					<option class="dialog-drop-down" value ="${type.id}">${ ui.format(type) }</option>
				<% } %> 
			</select>
			<p class="dialog-instructions">${ ui.message("coreapps.task.startVisit.message", ui.format(patient.patient)) }</p>
		<% } %>
		<button id="start-visit-with-visittype-confirm" class="confirm right">${ ui.message("coreapps.confirm") }<i class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i></button>
		<button class="cancel">${ ui.message("coreapps.cancel") }</button>
	</div>
</div>