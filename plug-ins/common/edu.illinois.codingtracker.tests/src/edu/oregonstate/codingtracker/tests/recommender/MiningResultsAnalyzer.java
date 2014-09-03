package edu.oregonstate.codingtracker.tests.recommender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import edu.illinois.codingtracker.helpers.Configuration;
import edu.illinois.codingtracker.operations.ast.ASTOperation;
import edu.illinois.codingtracker.operations.ast.ASTOperationDescriptor.OperationKind;
import edu.illinois.codingtracker.operations.ast.CompositeNodeDescriptor;
import edu.illinois.codingtracker.operations.ast.InferredUnknownTransformationOperation;
import edu.illinois.codingtracker.operations.ast.UnknownTransformationDescriptor;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.Item;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.LongItem;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.helpers.OperationFilePair;
import edu.illinois.codingtracker.tests.postprocessors.ast.ASTPostprocessor;
import edu.illinois.codingtracker.tests.postprocessors.ast.transformation.UnknownTransformationDescriptorFactory;
import edu.oregonstate.codingtracker.helpers.Tuple;

public abstract class MiningResultsAnalyzer extends ASTPostprocessor {

	private final File transformationKindsFile = new File(Configuration.TRAINING_DATA_FOLDER,
				Configuration.TRANSFORMATION_KINDS_FILE);
	private final File atomicTransformationsFile = new File(Configuration.TRAINING_DATA_FOLDER,
				Configuration.ATOMIC_TRANSFORMATIONS_FILE);
	private final File itemSetsFolder = new File(Configuration.TRAINING_DATA_FOLDER, Configuration.ITEM_SETS_FOLDER);
	protected Map<Long, UnknownTransformationDescriptor> transformationKinds;
	protected Map<Long, OperationFilePair> atomicTransformations;
	protected Set<Tuple<Tuple<String,OperationKind>, Long>> triggerTimeStamps;
	protected List<ItemSet> itemSets;
	
	protected long cutoffTimestamp = 1407102349988l;
	protected Map<Long, UnknownTransformationDescriptor> astMappedTransformationKinds;


	public MiningResultsAnalyzer() {
		super();
		
		transformationKinds = parseTransformationKindsFile();
		atomicTransformations = parseAtomicTransformationsFile(transformationKinds);
		triggerTimeStamps = new HashSet<Tuple<Tuple<String,OperationKind>, Long>>();
		itemSets = parseItemSets(atomicTransformations, transformationKinds, triggerTimeStamps);
		
		astMappedTransformationKinds = new HashMap<Long, UnknownTransformationDescriptor>();
		for (UnknownTransformationDescriptor descriptor : transformationKinds.values()) {
			Long hash = hash(descriptor);
			astMappedTransformationKinds.put(hash, descriptor);
		}
	}

	/**
	 * I parse the transformationKinds.csv file and return a new, populated map.
	 */
	protected Map<Long, UnknownTransformationDescriptor> parseTransformationKindsFile() {
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
						(String) transformations.get(4), (String) transformations.get(3)));
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

	protected Map<Long, OperationFilePair> parseAtomicTransformationsFile(Map<Long, UnknownTransformationDescriptor> transformationKinds) {
		TreeMap<Long, OperationFilePair> atomicTransformations = new TreeMap<Long, OperationFilePair>();
		CsvListReader reader = null;
		try {
			reader = new CsvListReader(new FileReader(atomicTransformationsFile), CsvPreference.STANDARD_PREFERENCE);
			reader.getHeader(true);
			List<Object> atomicTransformation;
			while ((atomicTransformation = reader.read(getAtomicTransformationsCSVProcessors())) != null) {
				Long transformationKindID = (Long) atomicTransformation.get(1);
				Long transformationID = (Long) atomicTransformation.get(0);
				String operationPath = (String) atomicTransformation.get(3);
				Long timestamp = (Long) atomicTransformation.get(2);
				atomicTransformations.put(transformationID, new OperationFilePair(
						new InferredUnknownTransformationOperation(transformationKindID, transformationID,
								transformationKinds.get(transformationKindID), timestamp), operationPath));
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

	protected List<ItemSet> parseItemSets(Map<Long, OperationFilePair> atomicTransformations, Map<Long, UnknownTransformationDescriptor> transformationKinds, Set<Tuple<Tuple<String, OperationKind>,Long>> triggerTimeStamps) {
		List<ItemSet> discoveredItemSets = new ArrayList<ItemSet>();
	
		File[] itemSetFiles = itemSetsFolder.listFiles();
		
		for (File itemSetFile : itemSetFiles) {
			ItemSet currentItemSet = new ItemSet();
			int itemSetNo = Integer.parseInt(itemSetFile.getName().substring(7));
			discoveredItemSets.add(currentItemSet);
			try {
				BufferedReader reader = new BufferedReader(new FileReader(itemSetFile));
				String thingAfterColon = getThingAfterColon(reader.readLine());
				String itemSet = thingAfterColon.substring(1, thingAfterColon.length() - 1);
				String[] items = itemSet.split(", ");
				for (String item : items) {
					currentItemSet.addItem(new LongItem(Long.parseLong(item)));
				}
	
				currentItemSet.setSize(Integer.parseInt(getThingAfterColon(reader.readLine())));
				currentItemSet.setFrequency(Integer.parseInt(getThingAfterColon(reader.readLine())));
				
				ArrayList<ExistingTransformation> itemSetOccurances = new ArrayList<ExistingTransformation>();
				
				String line;
				while ((line = reader.readLine()) != null) {
					long beginTimeStamp = Long.MAX_VALUE;
					long endTimeStamp = 0;
					String[] itemOccurances = line.split(":");
					if (itemOccurances.length == 0)
						continue;
					String middleItem = itemOccurances[itemOccurances.length / 2];
					Iterator<Item> itemSetIterator = currentItemSet.iterator();
					List<Long> itemOccurancesInAnInstance = new ArrayList<Long>();
					Tuple<Tuple<String,OperationKind>, Long> usableMiddleItem = null;
					for (String itemOccurance : itemOccurances) {
						Item item = itemSetIterator.next();
						String[] transformationKindIDs = itemOccurance.split(",");
						if (transformationKindIDs.length == 0)
							continue;
						if (middleItem == itemOccurance) {
							OperationFilePair operationFilePair = atomicTransformations.get(Long
									.parseLong(transformationKindIDs[0]));
							long operationTimestamp = operationFilePair.operation.getTime();
							if (operationTimestamp >= cutoffTimestamp) {
								UnknownTransformationDescriptor descriptor = transformationKinds.get(((LongItem)item).getValue());
								String nodeType = descriptor.getAffectedNodeType();
								OperationKind operationKind = descriptor.getOperationKind();
								usableMiddleItem = new Tuple<Tuple<String,OperationKind>,Long>(new Tuple<String,OperationKind>(nodeType,operationKind),operationTimestamp);
								triggerTimeStamps.add(usableMiddleItem);
							}
						}
						
						List<Long> transformationsList = Collections.emptyList();
						for (String transformationKindID : transformationKindIDs) {
							transformationsList = new ArrayList<Long>();
							if (transformationKindID.equals(""))
								continue;
							long longTransformationKindID = Long.parseLong(transformationKindID);
							transformationsList.add(longTransformationKindID);
							
							OperationFilePair operationFilePair = atomicTransformations.get(Long.parseLong(transformationKindID));
							long timestamp = operationFilePair.operation.getTime();
							if (beginTimeStamp > timestamp)
								beginTimeStamp = timestamp;
							if (endTimeStamp < timestamp)
								endTimeStamp = timestamp;
						}
						itemOccurancesInAnInstance.addAll(transformationsList);
					}
					
					ExistingTransformation tuple = new ExistingTransformation(beginTimeStamp, endTimeStamp, currentItemSet, itemOccurancesInAnInstance, usableMiddleItem, itemSetNo);
					if (!itemSetOccurances.contains(tuple))
						itemSetOccurances.add(tuple);
					currentItemSet.setOccurances(itemSetOccurances);
				}
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
	
		return discoveredItemSets;
	}
	
	/**
	 * I return the {@link CellProcessor} to be used while parsing the
	 * transformationKinds file.
	 * 
	 * @return an array with the processors
	 */
	CellProcessor[] getTransformationKindsCSVProcessors() {
		return new CellProcessor[] { new ParseLong(), null, null, null, null };
	}

	CellProcessor[] getAtomicTransformationsCSVProcessors() {
		return new CellProcessor[] { new ParseLong(), new ParseLong(), new ParseLong(), null };
	}

	String getThingAfterColon(String line) {
		String thingAfterColon = line.split(":")[1];
		return thingAfterColon.substring(1, thingAfterColon.length());
	}

	@SuppressWarnings("static-access")
	protected ASTNode getNodeForOperation(ASTOperation operation) {
		IEditorPart currentEditor = operation.getCurrentEditor();
		IEditorInput editorInput = currentEditor.getEditorInput();
		IFile file = null;
		if (editorInput instanceof IFileEditorInput) {
			file = ((IFileEditorInput) editorInput).getFile();
		} else {
			return null;
		}
		ICompilationUnit compilationUnit = JavaCore.createCompilationUnitFrom(file);
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(compilationUnit);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		ASTNode rootAST = parser.createAST(new NullProgressMonitor());
		CompositeNodeDescriptor affectedNodeDescriptor = operation.getAffectedNodeDescriptor();
		int nodeOffset = affectedNodeDescriptor.getNodeOffset();
		int nodeLength = affectedNodeDescriptor.getNodeLength();
		ASTNode affectedNode = NodeFinder.perform(rootAST, nodeOffset, nodeLength);
		return affectedNode;
	}

	private long hash(OperationKind operationKind, String affectedNodeType, String abstractedNodeContent) {
		return (long) (operationKind.hashCode() * 31) ^ affectedNodeType.hashCode() ^ abstractedNodeContent.hashCode();
	}
	
	protected Long hash(UnknownTransformationDescriptor descriptor) {
		OperationKind operationKind = descriptor.getOperationKind();
		String affectedNodeType = descriptor.getAffectedNodeType();
		String abstractedNodeContent = descriptor.getAbstractedNodeContent();
		return hash(operationKind, affectedNodeType, abstractedNodeContent);
	}
	
	protected UnknownTransformationDescriptor getExistingDescriptor(ASTOperation operation, ASTNode affectedNode) {
		UnknownTransformationDescriptor currentDescriptor = UnknownTransformationDescriptorFactory
				.createDescriptor(operation.getOperationKind(), affectedNode);
		UnknownTransformationDescriptor existingDescriptor = astMappedTransformationKinds
				.get(hash(currentDescriptor));
		return existingDescriptor;
	}
}