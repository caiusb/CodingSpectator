package edu.oregonstate.codingtracker.tests.recommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import edu.illinois.codingtracker.helpers.Configuration;
import edu.illinois.codingtracker.operations.UserOperation;
import edu.illinois.codingtracker.operations.ast.ASTOperation;
import edu.illinois.codingtracker.operations.ast.ASTOperationDescriptor.OperationKind;
import edu.illinois.codingtracker.operations.ast.InferredUnknownTransformationOperation;
import edu.illinois.codingtracker.operations.ast.UnknownTransformationDescriptor;
import edu.illinois.codingtracker.tests.analyzers.CSVProducingAnalyzer;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.Item;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.LongItem;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.UnknownTransformationsAnalyzer;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.helpers.OperationFilePair;

public class TransformationRecommenderAnalyzer extends CSVProducingAnalyzer {

	private final File transformationKindsFile = new File(Configuration.postprocessorRootFolderName,
			Configuration.TRANSFORMATION_KINDS_FILE);

	private final File atomicTransformationsFile = new File(Configuration.postprocessorRootFolderName,
			Configuration.ATOMIC_TRANSFORMATIONS_FILE);
	
	private final File itemSetsFolder = new File(new File(Configuration.postprocessorRootFolderName,Configuration.MINING_RESULTS_FOLDER),
			Configuration.ITEM_SETS_FOLDER);
	
	/**
	 * I parse the transformationKinds.csv file and return a new, populated map.
	 */
	private Map<Long, UnknownTransformationDescriptor> parseTransformationKindsFile() {
		CsvListReader csvReader = null;
		Map<Long, UnknownTransformationDescriptor> transformationKinds = new TreeMap<Long, UnknownTransformationDescriptor>();
		try {
			csvReader = new CsvListReader(new FileReader(transformationKindsFile), CsvPreference.STANDARD_PREFERENCE);
			List<Object> transformations;
			csvReader.getHeader(true);
			while ((transformations = csvReader.read(getTransformationKindsCSVProcessors())) != null) {
				Long transformationID = (Long) transformations.get(0);
				transformationKinds.put(transformationID, new UnknownTransformationDescriptor(transformationID,
						OperationKind.valueOf((String) transformations.get(1)), (String) transformations.get(2),
						(String) transformations.get(3), (String) transformations.get(4)));	
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
	private CellProcessor[] getTransformationKindsCSVProcessors() {
		return new CellProcessor[] { new ParseLong(), null, null, null, null };

	}

	private Map<Long, OperationFilePair> parseAtomicTransformationsFile(
			Map<Long, UnknownTransformationDescriptor> transformationKinds) {
		TreeMap<Long, OperationFilePair> atomicTransformations = new TreeMap<Long, OperationFilePair>();
		CsvListReader reader = null;
		try {
			reader = new CsvListReader(new FileReader(atomicTransformationsFile), CsvPreference.STANDARD_PREFERENCE);
			reader.getHeader(true);
			List<Object> atomicTransformation;
			while ((atomicTransformation = reader.read(getAtomicTransformationsCSVProcessors())) != null) {
				Long transformationKindID = (Long) atomicTransformation.get(1);
				Long transformationID = (Long) atomicTransformation.get(0);
				atomicTransformations.put(transformationID, new OperationFilePair(
						new InferredUnknownTransformationOperation(transformationKindID, transformationID,
								transformationKinds.get(transformationKindID), (Long) atomicTransformation.get(2)),
						(String) atomicTransformation.get(3)));
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
		}
		return atomicTransformations;
	}

	private CellProcessor[] getAtomicTransformationsCSVProcessors() {
		return new CellProcessor[] { new ParseLong(), new ParseLong(), new ParseLong(), null };
	}
	
	private List<TreeSet<Item>> parseItemSets() {
		List<TreeSet<Item>> discoveredItemSets = new ArrayList<TreeSet<Item>>();
		
		File[] itemSetFiles = itemSetsFolder.listFiles();
		for (File itemSetFile : itemSetFiles) {
			TreeSet<Item> currentItemSet = new TreeSet<Item>();
			discoveredItemSets.add(currentItemSet);
			try {
				BufferedReader reader = new BufferedReader(new FileReader(itemSetFile));
				String itemSetLine = reader.readLine();
				String[] bits = itemSetLine.split(":");
				String itemSet = bits[1];
				itemSet = itemSet.substring(2, itemSet.length() - 2);
				String[] items = itemSet.split(", ");
				for (String item : items) {
					currentItemSet.add(new LongItem(Long.parseLong(item)));
				}
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}

		return discoveredItemSets;
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
		Map<Long, OperationFilePair> atomicTransformations = parseAtomicTransformationsFile(transformationKinds);
		List<TreeSet<Item>> discoveredItemSets = parseItemSets();

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
