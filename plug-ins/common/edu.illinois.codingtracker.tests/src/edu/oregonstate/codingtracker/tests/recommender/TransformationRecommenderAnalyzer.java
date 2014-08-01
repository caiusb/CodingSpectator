package edu.oregonstate.codingtracker.tests.recommender;

import java.util.List;

import edu.illinois.codingtracker.operations.UserOperation;
import edu.illinois.codingtracker.tests.analyzers.CSVProducingAnalyzer;

public class TransformationRecommenderAnalyzer extends CSVProducingAnalyzer {

	@Override
	protected String getTableHeader() {
		return "";
	}

	/**
	 * By default, I postprocess everything.
	 */
	@Override
	protected boolean shouldPostprocessVersionFolder(String folderName) {
		return true;
	}

	@Override
	protected void checkPostprocessingPreconditions() {
	}

	/**
	 * I am a processor that tries to recommend changes based on the things
	 * coming in. For simplicity, I only look at the AST Changes. I already know
	 * the common transformation patterns, because they are given to me by the
	 * UnknownTransformatioAnalyzer. 
	 */
	@Override
	protected List<UserOperation> postprocess(List<UserOperation> userOperations) {
		return userOperations;
	}

	@Override
	protected String getResultFilePostfix() {
		return ".recommender";
	}

}
