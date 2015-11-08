package org.openmrs.module.lfhcforms.activator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.lfhcforms.LFHCFormsActivator;
import org.openmrs.module.lfhcforms.reporting.reports.BasicDataReport;
import org.openmrs.module.reporting.common.ObjectUtil;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.openmrs.module.reporting.report.util.ReportUtil;

/**
 * Sets up the Encounter Types required by the HFE forms.
 */
public class ReportsInitializer implements Initializer {

	protected static final Log log = LogFactory.getLog(ReportsInitializer.class);

	/**
	 * @see Initializer#started()
	 */
	@Override
	public synchronized void started() {
		log.info("Setting new reports for " + LFHCFormsActivator.ACTIVATOR_MODULE_NAME);

		removeOldReports();
		ReportManagerUtil.setupAllReports(BasicDataReport.class);
	}

	/**
	 * @see Initializer#stopped()
	 */
	@Override
	public void stopped() {
		removeOldReports();
	}
	
	protected void removeOldReports() {
//		String gpVal = Context.getAdministrationService().getGlobalProperty("pihmalawi.oldReportsRemoved");
//		if (ObjectUtil.isNull(gpVal)) {
			AdministrationService as = Context.getAdministrationService();
			log.warn("Removing all reports");
			as.executeSQL("delete from reporting_report_design_resource;", false);
			as.executeSQL("delete from reporting_report_design;", false);
			as.executeSQL("delete from reporting_report_request;", false);
			as.executeSQL("delete from serialized_object;", false);
//			ReportUtil.updateGlobalProperty("pihmalawi.oldReportsRemoved", "true");
//		}
	}
}
