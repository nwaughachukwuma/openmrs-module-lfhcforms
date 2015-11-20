<% 
ui.includeJavascript("lfhcforms", "startVisitWithLocation.js")
%>

<div id="start-visit-with-location-dialog" class="dialog" style="display: none;">
  <div class="dialog-header">  
    <h3>
      ${ ui.message("coreapps.visit.createQuickVisit.title") }
    </h3>
  </div>
  <% if (activeVisitList) { %>
  <script type="text/javascript">
   jq("#start-visit-with-location-confirm").addClass("disabled");
 </script>
 <div class="dialog-content">
   <p class="dialog-instructions">
     <i class="icon-sign-warning">&#xf071;</i> There is already active visit(s) for  ${ui.format(patient.patient)}
   </p>
   <ul class="list" style="margin-bottom:0px">
    <% activeVisitList.each { activeVisit -> %> 
    <li>
      ${ui.format(activeVisit)}
    </li>
    <% } %>
  </ul>
  <p class="dialog-instructions">
    Please end all visits before starting a new visit      
  </p>  
  <% } else { %>

  <script type="text/javascript">
   jq("#start-visit-with-location-confirm").removeClass("disabled");
 </script>

 <div class="dialog-content">
   <p class="dialog-instructions">
    Select a visit location:    	
  </p>
  <select id="visit-location-drop-down"> 
    <% locationList.each { loc -> %>
    <option class="dialog-drop-down" value ="${loc.id}">${ ui.format(loc) }</option>
    <% } %> 
  </select>

  <p class="dialog-instructions">${ ui.message("coreapps.task.startVisit.message", ui.format(patient.patient)) }</p>
  <% } %>
  <button id="start-visit-with-location-confirm" class="confirm right">${ ui.message("coreapps.confirm") }<i class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i></button>
  <button class="cancel">${ ui.message("coreapps.cancel") }</button>
</div>

</div>