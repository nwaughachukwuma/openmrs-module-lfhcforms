<% if (!missingConcept) { %>
	<div class="info-section">
		<div class="info-header">
			<i class="icon-diagnosis"></i>
			<h3>${ ui.message("lfhcforms.pewsscore.title").toUpperCase() }</h3>
		</div>
		<div class="info-body">
			<% if (errorMsg.isEmpty()) { %>
			<div style="margin: 2% 0 2% 0;">
				<div>
					<span style="font-size: 115%;"><b>${ ui.format(pews) }</b></span>
					<span style="font-size: 85%;">${ ui.format("(" + lastUpdated + ")") }</span>
				</div>
				<span><b>${ ui.format(action) }</b></span>
			</div>
			<details style="font-size: 85%;">
				<summary>${ ui.message("lfhcforms.pewsscore.summary.title") }</summary>
				<table>
					<% pewsComponents.each { component -> %>
					<tr>
						<td>
							${component.getLabel()}
						</td>
						<td>
							${component.getValue()}
						</td>
					</tr>
					<%}%>
				</table>
			</details>
			<% } else { %>
			<div>${ ui.format(errorMsg) }</div>
			<% } %>
		</div>
	</div>
<% } %>