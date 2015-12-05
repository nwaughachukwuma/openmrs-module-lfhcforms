<% 
ui.includeJavascript("lfhcforms", "visit/startVisitWithLocation.js")
%>

<div id="start-visit-with-location-dialog" class="dialog" style="display: none;">
  <div class="dialog-header">  
    <h3>
      ${ ui.message("coreapps.visit.createQuickVisit.title") }
    </h3>
  </div>
  <div class="dialog-content">
    <% if (activeVisitsList) { %>
      <script type="text/javascript">
        jq("#start-visit-with-location-confirm").addClass("disabled");
      </script>
      
        <p class="dialog-instructions">
          <i class="icon-sign-warning">&#xf071;</i> ${ui.message("lfhcforms.app.visit.locationstart.activevisits", ui.format(patient.patient))}
        </p>
        <ul class="list" style="margin-bottom:0px">
          <% activeVisitsList.each { activeVisit -> %> 
            <li>
              ${ui.format(activeVisit)}
            </li>
          <% } %>
        </ul>
        <p class="dialog-instructions">
          ${ ui.message("lfhcforms.app.visit.locationstart.warning.instructions") }
        </p> 
      
    <% } else { %>
      <script type="text/javascript">
        jq("#start-visit-with-location-confirm").removeClass("disabled");
      </script>
        <p class="dialog-instructions">
          ${ ui.message("lfhcforms.app.visit.locationstart.instructions") }
        </p>
        <select id="visit-location-drop-down"> 
          <% locationList.each { loc -> %>
            <% if (userVisitLocation == loc) { %>
              <script type="text/javascript">
                jq("#visit-location-drop-down").val(${loc.id});
              </script>
            <% } %>
            <option class="dialog-drop-down" value ="${loc.id}">${ ui.format(loc) }</option>
          <% } %> 
        </select>
        <p class="dialog-instructions">${ ui.message("coreapps.task.startVisit.message", ui.format(patient.patient)) }</p>
    <% } %>
    <button id="start-visit-with-location-confirm" class="confirm right">${ ui.message("coreapps.confirm") }<i class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i></button>
    <button class="cancel">${ ui.message("coreapps.cancel") }</button>
  </div>
</div>