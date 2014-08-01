package edu.oregonstate.codingtracker.tests.recommender;

import java.util.List;

import edu.illinois.codingtracker.operations.UserOperation;
import edu.illinois.codingtracker.tests.analyzers.CSVProducingAnalyzer;

public class TransformationRecommenderAnalyzer extends CSVProducingAnalyzer {

	@Override
	protected String getTableHeader() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	protected boolean shouldPostprocessVersionFolder(String folderName) {
		return true;
	}

	@Override
	protected void checkPostprocessingPreconditions() {
		// TODO Auto-generated method stub

	}

	@Override
	protected List<UserOperation> postprocess(List<UserOperation> userOperations) {
		// TODO Auto-generated method stub
		return userOperations;
	}

	@Override
	protected String getResultFilePostfix() {
		return ".recommender";
	}

}
