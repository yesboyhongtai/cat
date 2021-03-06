package com.dianping.cat.report.page.model.dependency;

import java.util.List;

import com.dianping.cat.consumer.dependency.model.entity.DependencyReport;
import com.dianping.cat.report.model.ModelRequest;
import com.dianping.cat.report.model.ModelResponse;
import com.dianping.cat.report.page.model.spi.internal.BaseCompositeModelService;
import com.dianping.cat.report.page.model.spi.internal.BaseRemoteModelService;

public class CompositeDependencyService extends BaseCompositeModelService<DependencyReport> {
	public CompositeDependencyService() {
		super("dependency");
	}

	@Override
	protected BaseRemoteModelService<DependencyReport> createRemoteService() {
		return new RemoteDependencyService();
	}

	@Override
	protected DependencyReport merge(ModelRequest request, List<ModelResponse<DependencyReport>> responses) {
		if (responses.size() == 0) {
			return null;
		}
		DependencyReportMerger merger = new DependencyReportMerger(new DependencyReport(request.getDomain()));
		for (ModelResponse<DependencyReport> response : responses) {
			DependencyReport model = response.getModel();

			if (model != null) {
				model.accept(merger);
			}
		}

		return merger.getDependencyReport();
	}
}
