<!-- <% ui.includeCss("allergyui", "allergies.css") %> -->

<div class="info-section">
    <div class="info-header">
        <i class="icon-medical"></i>
        <h3>${ ui.message("lfhcforms.fluidbalance.title").toUpperCase() }</h3>
        
        <!-- <i class="icon-pencil edit-action right" title="${ ui.message("coreapps.edit") }" onclick="location.href='${ui.pageLink("allergyui", "allergies", [patientId: patient.patient.id])}';"></i> -->
    </div>
    <div class="info-body">
        <details>
            <summary></summary>
            <% if (true) { %>
            <p>Latest weight = ${ ui.format(myObs) }</p>
            <p>${ ui.message("lfhcforms.fluidbalance.total") } = ${ ui.format(fluidBalance) }</p>
            <p>${ ui.message("lfhcforms.fluidbalance.lasttotal") } = ${ ui.format(lastFluidBalance) }</p>
            <p>${ ui.message("lfhcforms.fluidbalance.urineout") } = ${ ui.format(avgUrineOutput) }</p>
            <p>${ ui.message("lfhcforms.fluidbalance.urineoutkg") } = ${ ui.format(avgUrineOutputPerKg) }</p>
            <% } %>
        </details>
    </div>
</div>