package edu.oregonstate.codingtracker.tests.recommender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import edu.illinois.codingtracker.helpers.Configuration;
import edu.illinois.codingtracker.operations.UserOperation;
import edu.illinois.codingtracker.operations.ast.ASTOperation;
import edu.illinois.codingtracker.operations.ast.ASTOperationDescriptor.OperationKind;
import edu.illinois.codingtracker.operations.ast.UnknownTransformationDescriptor;
import edu.illinois.codingtracker.tests.analyzers.CSVProducingAnalyzer;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.UnknownTransformationsAnalyzer;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.helpers.OperationFilePair;

public class TransformationRecommenderAnalyzer extends CSVProducingAnalyzer {

	private final Map<Long, OperationFilePair> atomicTransformations = new TreeMap<Long, OperationFilePair>();

	private final File transformationKindsFile = new File(
			Configuration.postprocessorRootFolderName,
			Configuration.TRANSFORMATION_KINDS_FILE);

	/**
	 * I parse the transformationKinds.csv file and return a new, populated map.
	 */
	private Map<Long, UnknownTransformationDescriptor> parseTransformationKindsFile() {
		CsvListReader csvReader = null;
		Map<Long, UnknownTransformationDescriptor> transformationKinds = new TreeMap<Long, UnknownTransformationDescriptor>();
		try {
			csvReader = new CsvListReader(new FileReader(
					transformationKindsFile), CsvPreference.STANDARD_PREFERENCE);
			List<Object> transformations;
			csvReader.getHeader(true);
			while ((transformations = csvReader.read(getCSVProcessors())) != null) {
				transformationKinds.put(
						(Long) transformations.get(0),
						new UnknownTransformationDescriptor(OperationKind
								.valueOf((String) transformations.get(1)),
								(String) transformations.get(2),
								(String) transformations.get(3),
								(String) transformations.get(4)));
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			try {
				csvReader.close();
			} catch (IOException e) {
			}
		}

		return transformationKinds;
	}

	/**
	 * I return the {@link CellProcessor} to be used while parsing the
	 * transformationKinds file.
	 * 
	 * @return an array with the processors
	 */
	private CellProcessor[] getCSVProcessors() {
		return new CellProcessor[] { new ParseLong(), null, null, null, null };

	}

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
	 * {@link UnknownTransformationsAnalyzer}.
	 */
	@Override
	protected List<UserOperation> postprocess(List<UserOperation> userOperations) {
		Map<Long, UnknownTransformationDescriptor> transformationKinds = parseTransformationKindsFile();

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

	@Override
	protected String getResult() {
		return "";
	}
}
