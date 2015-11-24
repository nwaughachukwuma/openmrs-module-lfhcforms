<%
def patient = config.patient
    ui.includeJavascript("lfhcforms", "visit/disableVisitsSectionWidget.js")
    %>

    <div id="visits-with-location" class="info-section override-previous">
    <!-- class 'override-previous' will enable jq code to hide the previous widget -->
        <div class="info-header">
            <i class="icon-calendar"></i>
            <h3>VISITS</h3>
            <% if (context.hasPrivilege("App: coreapps.patientVisits")) { %>
            <a href="${visitsUrl}" class="right">
                <i class="icon-share-alt edit-action" title="${ ui.message("coreapps.edit") }"></i>
            </a>
            <% } %>
        </div>
        <div class="info-body">
            <% if (recentVisitsWithLinks.isEmpty()) { %>
            ${ui.message("coreapps.none")}
            <% } %>
            <ul>
                <% recentVisitsWithLinks.each { it, url -> %>
                <li class="clear">
                    <a href="${url}" class="visit-link">
                        ${ ui.formatDatePretty(it.startDatetime) }
                        <% if(it.stopDatetime && !it.startDatetime.format("yyyy/MM/dd").equals(it.stopDatetime.format("yyyy/MM/dd"))){ %> - ${ ui.formatDatePretty(it.stopDatetime) }<% } %>
                    </a>

                    <% recentVisitsWithAttr.each { visitId, attr -> %>
                    
                    <% if (visitId == it.visit.id) { %>
                    <script type="text/javascript">
                        jq(document).ready(function () {
                            if ('${attr.color}' != null) {
                                jq("#location-tag-${visitId}.tag").css("background",'${attr.color}');
                            }
                        })
                    </script>
                    <span id="location-tag-${visitId}" class="tag shortname" >
                        ${attr.shortName}
                    </span>
                    <% } %>
                    <% } %>
                    <span id="active-tag-${it.visit.id}" class="tag active">
                        <% if (it.stopDatetime == null || new Date().before(it.stopDatetime)) { %> ${ ui.message("coreapps.clinicianfacing.active") } <% } else { %>
                        <script type="text/javascript">
                            jq(document).ready(function () {
                                jq("#active-tag-${it.visit.id}.tag").css("display","none");
                            })
                        </script>
                        <%  } %>

                    </span>
                </li>
                <% } %>
            </ul>
        </div>
    </div>