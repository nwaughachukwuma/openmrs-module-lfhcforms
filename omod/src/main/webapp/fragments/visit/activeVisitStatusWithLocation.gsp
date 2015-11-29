<% if (config.activeVisit) { %>
    <% def visit = activeVisit.visit %>
    <div class="active-visit-started-at-message">
        ${ui.message("coreapps.patientHeader.activeVisit.at", activeVisitStartDatetime)}
    </div>
    <div id="location-tag-${visit.visitId}" class="tag shortname active-visit-message">
        <script type="text/javascript">
            jq(document).ready(function () {
                if ('${visitLocAttr.color}' != null) {
                    jq("#location-tag-${visit.visitId}.tag").css("background",'${visitLocAttr.color}');
					jq("#location-tag-${visit.visitId}.tag").css("border-color",'${visitLocAttr.color}');

                }
            })
        </script>
        ${visitLocAttr.shortName}
    </div>
<% } %>
