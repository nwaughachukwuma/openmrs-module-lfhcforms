<!-- <% ui.includeCss("allergyui", "allergies.css") %> -->

<div class="info-section">
    <div class="info-header">
        <i class="icon-tint"></i>
        <h3>${ ui.message("lfhcforms.fluidbalance.title").toUpperCase() }</h3>
        
        <!-- <i class="icon-pencil edit-action right" title="${ ui.message("coreapps.edit") }" onclick="location.href='${ui.pageLink("allergyui", "allergies", [patientId: patient.patient.id])}';"></i> -->
    </div>
    <div class="info-body">
        <details>
            <summary></summary>
            <table style="">
  				<tr>
    				<td>${ ui.message("lfhcforms.fluidbalance.total") }</td>
    				<td>${ ui.format(fluidBalance) }</td> 
  				</tr>
  				<tr>
    				<td>${ ui.message("lfhcforms.fluidbalance.lasttotal") }</td>
    				<td>${ ui.format(lastFluidBalance) }</td> 
  				</tr>
  				<tr>
    				<td>${ ui.message("lfhcforms.fluidbalance.urineout") }</td>
    				<td>${ ui.format(avgUrineOutput) }</td> 
  				</tr>
  				<tr>
    				<td>${ ui.message("lfhcforms.fluidbalance.urineoutkg") }</td>
    				<td>${ ui.format(avgUrineOutputPerKg) }</td> 
  				</tr>
			</table>
        </details>
    </div>
</div>