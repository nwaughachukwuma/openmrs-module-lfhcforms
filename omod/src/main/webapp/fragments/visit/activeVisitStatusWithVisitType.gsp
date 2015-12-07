<% if (config.activeVisit) { %>
    <% def visit = activeVisit.visit %>
    <div class="active-visit-started-at-message">
        ${ui.message("coreapps.patientHeader.activeVisit.at", activeVisitStartDatetime)}
    </div>
    <div id="visittype-tag-${visit.visitId}" class="tag shortname active-visit-message">
        <script type="text/javascript">
            jq(document).ready(function () {
                if ('${activeVisitAttr.color}' != null) {
                    jq("#visittype-tag-${visit.visitId}.tag").css("background",'${activeVisitAttr.color}');
					jq("#visittype-tag-${visit.visitId}.tag").css("border-color",'${activeVisitAttr.color}');

                }
            })
        </script>
        ${activeVisitAttr.shortName}
    </div>
<% } %>
