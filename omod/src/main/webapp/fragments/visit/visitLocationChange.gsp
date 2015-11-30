<% 
ui.includeJavascript("lfhcforms", "visit/changeVisitLocation.js")
%>

<div id="change-visit-location-dialog" class="dialog" style="display: none;">
  <div class="dialog-header">  
    <h3>
      no-message: change visit location
    </h3>
  </div>
  
  <script type="text/javascript">
   jq("#start-visit-with-location-confirm").removeClass("disabled");
 </script>

 <div class="dialog-content">
   <p class="dialog-instructions">
   no-message: Select a visit location:    	
  </p>
  <select id="new-location-drop-down"> 
    <% locationList.each { loc -> %>
    <% if (userVisitLocation == loc) { %>
    <script type="text/javascript">
     jq("#new-location-drop-down").val(${loc.id});
   </script>
   <% } %>
   <option class="dialog-drop-down" value ="${loc.id}">${ ui.format(loc) }</option>
   <% } %> 
 </select>

 <p class="dialog-instructions">no-message: do you want to set visit location</p>

 <button id="change-visit-location-confirm" class="confirm right">${ ui.message("coreapps.confirm") }<i class="icon-spinner icon-spin icon-2x" style="display: none; margin-left: 10px;"></i></button>
 <button class="cancel">${ ui.message("coreapps.cancel") }</button>
</div>

</div>