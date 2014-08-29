package edu.oregonstate.codingtracker.tests.recommender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import edu.illinois.codingtracker.operations.UserOperation;
import edu.illinois.codingtracker.operations.ast.ASTOperation;
import edu.illinois.codingtracker.operations.ast.ASTOperationDescriptor.OperationKind;
import edu.illinois.codingtracker.operations.ast.InferredUnknownTransformationOperation;
import edu.illinois.codingtracker.operations.ast.UnknownTransformationDescriptor;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.LongItem;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.UnknownTransformationsAnalyzer;
import edu.illinois.codingtracker.tests.analyzers.ast.transformation.helpers.OperationFilePair;
import edu.illinois.codingtracker.tests.postprocessors.ast.transformation.UnknownTransformationDescriptorFactory;
import edu.oregonstate.codingtracker.EditTransformationMapper;

@RunWith(Parameterized.class)
public class TransformationRecommenderAnalyzer extends MiningResultsAnalyzer {

	private StringBuffer stringBuffer = new StringBuffer();
	private static StringBuffer resultsBuffer;

	private final int maxForeignItems;
	static {
		resultsBuffer = new StringBuffer();
	}
	
	public TransformationRecommenderAnalyzer(int maxForeignItems, int somethingElse) {
		this.maxForeignItems = maxForeignItems;
	}
	
	@Parameters
	public static Collection<Integer[]> maxItems() {
		return Arrays.asList(new Integer[][] {
				{0, 0}//, 
//				{1, 0},
//				{2, 0},
//				{3, 0},
//				{4, 0},
//				{5, 0},
//				{6, 0},
//				{7, 0},
//				{8, 0}, 
//				{9, 0},
//				{10, 0}
		});
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
		int totalTriggers = triggerTimeStamps.size();
		int actualTriggered = 0;

		List<ExistingTransformation> allExistingTransformationOccurances = new ArrayList<ExistingTransformation>();
		for (ItemSet itemSet : itemSets) {
			allExistingTransformationOccurances.addAll(itemSet.getOccurances());
		}
		
		List<CandidateTransformation> candidateTransformations = new ArrayList<CandidateTransformation>();

		List<ASTOperation> operationCache = new ArrayList<ASTOperation>();

		int missedNodes = 0;
		
		for (UserOperation userOperation : userOperations) {
			if (userOperation.getTime() < cutoffTimestamp) {// I do not want to do anything
												// with the training data
				replay(userOperation);
				continue;
			}
			if (userOperation instanceof ASTOperation)
				operationCache.add((ASTOperation) userOperation);
			else {
				for (ASTOperation operation : operationCache) {
					long timestamp = operation.getTime();
					String nodeType = operation.getAffectedNodeDescriptor().getNodeType();
					OperationKind operationKind = operation.getOperationKind();
					Tuple<Tuple<String, OperationKind>, Long> currentOperation = new Tuple<Tuple<String,OperationKind>, Long>(new Tuple<String,OperationKind>(nodeType, operationKind), timestamp);
					if (triggerTimeStamps.contains(currentOperation)) {
						List<ExistingTransformation> existingTransformations = new ArrayList<ExistingTransformation>();
						for (CandidateTransformation candidateTransformation : candidateTransformations) {
							ItemSet set = candidateTransformation.getItemSet();
							List<ExistingTransformation> timestamps = set.getOccurances();
							for (ExistingTransformation existingTransformation : timestamps) {
								if (existingTransformation.containsMiddleItem(currentOperation)) {
									existingTransformations.add(existingTransformation);
									break; // I break once I find a match. Is this correct?? Or not? And why?
								}
							}
						}
						Collections.sort(existingTransformations);
						Collections.sort(candidateTransformations, Collections.reverseOrder());
						int combinedRanking = 0;
						for (ExistingTransformation existingTransformation : existingTransformations) {
							stringBuffer.append("E:" + existingTransformation);
							for(int i=0; i<candidateTransformations.size(); i++)
								if (candidateTransformations.get(i).getItemSet().equals(existingTransformation.getItemSet())) {
									stringBuffer.append(" P:" + (i + 1) + "/" + candidateTransformations.size());
									combinedRanking += (i+1);
								}
							stringBuffer.append("\n");
						}
						resultsBuffer.append(combinedRanking + "/" + candidateTransformations.size() + "\t");

						addCandidatesToStringBuffer(candidateTransformations, stringBuffer);
						triggerTimeStamps.remove(timestamp); // two operations at the
																// same time stamp will
																// trigger only once
						actualTriggered++;
					}
					ASTNode affectedNode = getNodeForOperation(operation);
					if (affectedNode == null) { // can't find the affected node.
												// Should be problematic, but
												// I'm ignoring it for now
						System.out.println("Oops something went wrong");
						missedNodes++;
						continue;
					}

					UnknownTransformationDescriptor existingDescriptor = getExistingDescriptor(operation, affectedNode);

					// if I can't find a descriptor, oh well, moving on
					if (existingDescriptor == null)
						continue;

					Long transformationID = existingDescriptor.getID();
					EditTransformationMapper.getInstance().matchEditsToAST(affectedNode, transformationID);

					for (ItemSet itemSet : itemSets) {
						candidateTransformations = tryAndContinueATransformation(candidateTransformations,
								transformationID, timestamp);
						tryAndCreateANewTransformation(candidateTransformations, transformationID, itemSet, timestamp);
					}

					float maxCompleteness = 0;
					CandidateTransformation mostCompletedTransformation = null;
					for (CandidateTransformation transformation : candidateTransformations) {
						float completeness = transformation.getCompleteness();
						if (completeness > maxCompleteness) {
							maxCompleteness = completeness;
							mostCompletedTransformation = transformation;
						}
					}
					
				}
				operationCache = new ArrayList<ASTOperation>();

			}
			replay(userOperation);
		}

		System.out.println("In total, I missed " + missedNodes + " nodes :(");
		System.out.println("Triggered " + actualTriggered + " out of " + totalTriggers + " possible");
		resultsBuffer.append("\n");
		return userOperations;
	}
	
	private List<Tuple<Long, Long>> getTriggerTimeStamps(long cutoffTimestamp, Map<Long, OperationFilePair> atomicTransformations,
			List<ExistingTransformation> allExistingTransformationOccurances) {
		List<Tuple<Long,Long>> triggers = new ArrayList<Tuple<Long, Long>>();
		for (ExistingTransformation transformation : allExistingTransformationOccurances) {
			List<Long> transformationIDs = transformation.getTransformationIDs();
			Long middleTransformationID = transformationIDs.get(transformationIDs.size() / 2);
			InferredUnknownTransformationOperation middleOperation = atomicTransformations.get(middleTransformationID).operation;
			long middleOperationTimestamp = middleOperation.getTime();
			if (middleOperationTimestamp < cutoffTimestamp)
				continue;
			long middleTransformationKindID = middleOperation.getTransformationKindID();
			triggers.add(new Tuple<Long, Long>(middleOperationTimestamp, middleTransformationKindID));
		}
		
		return triggers;
		
	}

	private void addCandidatesToStringBuffer(List<CandidateTransformation> candidateTransformations,
			StringBuffer stringBuffer) {
		Collections.sort(candidateTransformations, Collections.reverseOrder());
		stringBuffer.append("************\n");
		for (CandidateTransformation candidateTransformation : candidateTransformations) {
			stringBuffer.append("C:");
			stringBuffer.append(candidateTransformation + "\n");
		}
		stringBuffer.append("----\n");
	}

	private void tryAndCreateANewTransformation(List<CandidateTransformation> candidateTransformations,
			Long transformationID, ItemSet itemSet, long timestamp) {
		LongItem item = new LongItem(transformationID);
		if (itemSet.contains(item)) {
			//CandidateTransformation candidateTransformation = new ForeignItemCandidateTransformation(itemSet, item, maxForeignItems);
			CandidateTransformation candidateTransformation = new AgeCandidateTransformation(itemSet, item, timestamp, maxForeignItems);
			if (candidateTransformations.contains(candidateTransformation))
				return;
			candidateTransformations.add(candidateTransformation);
		}
	}

	private List<CandidateTransformation> tryAndContinueATransformation(
			List<CandidateTransformation> candidateTransformations, Long transformationID, long time) {
		ArrayList<CandidateTransformation> remainingTransformations = new ArrayList<CandidateTransformation>();
		for (CandidateTransformation transformation : candidateTransformations) {
			if (transformation.continuesCandidate(new ItemOccurance(new LongItem(transformationID), time))) {
				transformation.addItem(new LongItem(transformationID));
				remainingTransformations.add(transformation);
			}
		}
		return remainingTransformations;
	}

	@Override
	protected String getRecordFileName() {
		return "codechanges.txt.inferred_ast_operations";
	}

	@Override
	protected String getResultFilePostfix() {
		return ".recommender";
	}

	@Override
	protected String getResult() {
//		return resultsBuffer.toString();
		return stringBuffer.toString();
	}
}
