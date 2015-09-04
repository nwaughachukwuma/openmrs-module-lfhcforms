<div class="info-section">
    <div class="info-header">
    	<i class="icon-diagnosis"></i>
        <h3>${ ui.message("lfhcforms.pewsscore.title").toUpperCase() }</h3>
    </div>
    <div class="info-body">
		<div style="line-height: 200%;"><b>${ ui.format(pews) }</b></div>
		<details style="font-size: 85%;">
			<summary>${ ui.message("lfhcforms.pewsscore.summary.title") }</summary>
			<table>
				<% pewsComponents.each { component -> %>
				<tr>
					<td>
	         			${component.label}
		        	</td>
		        	<td>
	         			${component.value}
		        	</td>
		        </tr>
		        <%}%>
			</table>
		</details>
    </div>
</div>