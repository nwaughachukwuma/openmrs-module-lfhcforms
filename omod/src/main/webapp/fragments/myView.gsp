<!-- <% ui.includeCss("allergyui", "allergies.css") %> -->

<div class="info-section">
    <div class="info-header">
        <i class="icon-medical"></i>
        <h3>${ ui.message("lfhcforms.mytitle").toUpperCase() }</h3>
        
        <!-- <i class="icon-pencil edit-action right" title="${ ui.message("coreapps.edit") }" onclick="location.href='${ui.pageLink("allergyui", "allergies", [patientId: patient.patient.id])}';"></i> -->
    </div>
    <div class="info-body">
        <details>
            <summary></summary>
            <% if (myObs) { %>
            <p>Latest weight = ${ ui.format(myObs) }</p>
            <% } %>
        </details>
    </div>
</div>