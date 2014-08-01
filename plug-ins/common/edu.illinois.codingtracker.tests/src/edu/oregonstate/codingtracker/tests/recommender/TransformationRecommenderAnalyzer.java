package edu.oregonstate.codingtracker.tests.recommender;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.illinois.codingtracker.helpers.Configuration;
import edu.illinois.codingtracker.operations.UserOperation;
import edu.illinois.codingtracker.operations.ast.ASTOperation;
import edu.illinois.codingtracker.operations.ast.UnknownTransformationDescriptor;
import edu.illinois.codingtracker.tests.analyzers.CSVProducingAnalyzer;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.helpers.OperationFilePair;

public class TransformationRecommenderAnalyzer extends CSVProducingAnalyzer {

	private final Map<Long, UnknownTransformationDescriptor> transformationKinds = new TreeMap<Long, UnknownTransformationDescriptor>();

	private final Map<Long, OperationFilePair> atomicTransformations = new TreeMap<Long, OperationFilePair>();

	private final File transformationKindsFile = new File(
			Configuration.postprocessorRootFolderName,
			Configuration.TRANSFORMATION_KINDS_FILE);

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
		for (UserOperation userOperation : userOperations) {
			if (!(userOperation instanceof ASTOperation))
				continue;

		}
		return userOperations;
	}

	@Override
	protected String getResultFilePostfix() {
		return ".recommender";
	}
}
