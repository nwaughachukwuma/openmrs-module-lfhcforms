package org.openmrs.module.lfhcforms.reporting.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.lfhcforms.utils.DefaultResouceLoaderImpl;
import org.openmrs.module.lfhcforms.utils.ResourceLoader;
import org.openmrs.module.reporting.common.MessageUtil;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.manager.BaseReportManager;
import org.openmrs.module.reporting.report.manager.ReportManagerUtil;
import org.springframework.stereotype.Component;

/**
 * This class was inspired by <a href=”https://github.com/PIH/openmrs-module-pihmalawi/tree/master/api/src/main/java/org/openmrs/module/pihmalawi/reporting/reports”>PIH/openmrs-module-pihmalawi</a> handling of their reports.
 * 
 * @author Dimitri Renault
 */
@Component
public class BasicDataReport extends BaseReportManager {

	protected static final Log log = LogFactory.getLog(BasicDataReport.class);
	
	protected static final String SQL_RESOURCE_PATH = "report-basic-data.sql";
	
	public BasicDataReport() {};
	
	@Override
	public String getVersion() {
		// Append with -SNAPSHOT to install modifications.
		// Versions withtout -SNAPSHOT are ignored unless the version number has changed.
		return "1.0-SNAPSHOT";
	}
	
	@Override
	public ReportDefinition constructReportDefinition() {
		
		String sqlQuery = getSqlFromResource();
		
		SqlDataSetDefinition dataSetDef = new SqlDataSetDefinition(getShortName() + " " + MessageUtil.translate("lfhcforms.reporting.labels.dataset"), "", sqlQuery);
		dataSetDef.setParameters(getParameters());

		ReportDefinition reportDef = new ReportDefinition();
		reportDef.setUuid(getUuid());
		reportDef.setName(getName());
		reportDef.setName(reportDef.getName());
		reportDef.setDescription(getDescription());
		reportDef.setParameters(getParameters());
	
		reportDef.addDataSetDefinition(getDataSetKey(), Mapped.mapStraightThrough(dataSetDef));
		return reportDef;
	}

	protected String getSqlFromResource() {
		final ResourceLoader loader = new DefaultResouceLoaderImpl();
		String sql = "";
		try {
			sql = loader.getResourceAsString(SQL_RESOURCE_PATH, "UTF-8");
		}
		catch (IOException e) {
			log.error("Could not load resource file '" + SQL_RESOURCE_PATH + ".", e);
		}
		return sql;
	}
	
	@Override
	public List<ReportDesign> constructReportDesigns(ReportDefinition reportDefinition) {
		ReportDesign design = ReportManagerUtil.createExcelDesign(getReportDesignUuid(), reportDefinition);
		design.setName(MessageUtil.translate("lfhcforms.reports.basicdata.reportdesign"));
		design.setName(design.getName());	//TODO Remove this eventually
		
		List<ReportDesign> l = new ArrayList<ReportDesign>();
        l.add(design);
        return l;
	}

	@Override
	public String getDescription() {
		return "";
	}
	
	protected String getDataSetKey() {
		return getShortName() + MessageUtil.translate("lfhcforms.reports.basicdata.key");
	}
	
	@Override
	public List<Parameter> getParameters() {
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(new Parameter("startDate", "From Date", Date.class));
		params.add(new Parameter("endDate", "To Date", Date.class));
		return params;
	}

	@Override
	public String getName() {
		return getShortName() + " " + MessageUtil.translate("lfhcforms.reporting.labels.report");
	}
	
	public static String getShortName() {
		return MessageUtil.translate("lfhcforms.reports.basicdata.name");
	}

	@Override
	public String getUuid() {
		return "201b4ba8-7e2a-11e5-8bcf-feff819cdc9f";
	}
	
	protected String getReportDesignUuid() {
		return "201b4f72-7e2a-11e5-8bcf-feff819cdc9f";
	}
}