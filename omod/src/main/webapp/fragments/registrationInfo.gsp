<div>
	<% if (fatherName) { %>
	<h4>${ ui.message("lfhcforms.app.registerPatient.father.name") }</h4>
	<p>
		${ui.format (fatherName) }
	</p>
	<% } %>

	<% if (fatherOccupation) { %>
	<h4>${ ui.message("lfhcforms.app.registerPatient.father.occupation") }</h4>
	<p>
		${ui.format (fatherOccupation) }
	</p>
	<% } %>

	<% if (motherName) { %>
	<h4>${ ui.message("lfhcforms.app.registerPatient.mother.name") }</h4>
	<p>
		${ui.format (motherName) }
	</p>
	<% } %>

	<% if (insuranceDetails) { %>
	<h4>${ ui.message("lfhcforms.app.registerPatient.insurance.details") }</h4>
	<p>
		${ui.format (insuranceDetails) }
	</p>
	<% } %>

	<% if (village) { %>
	<h4>${ ui.message("Location.village") }</h4>
	<p>
		${ui.format (village) }
	</p>
	<% } %>
</div>