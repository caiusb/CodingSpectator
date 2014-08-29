package edu.oregonstate.codingtracker.tests.recommender;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;

import edu.illinois.codingtracker.operations.UserOperation;
import edu.illinois.codingtracker.operations.ast.ASTOperation;
import edu.illinois.codingtracker.operations.ast.UnknownTransformationDescriptor;
import edu.illinois.codingtracker.operations.textchanges.TextChangeOperation;
import edu.illinois.codingtracker.recording.TextRecorder;
import edu.oregonstate.codingtracker.EditTransformationMapper;

public class TransformationMapperAnalysis extends MiningResultsAnalyzer {
	
	@Override
	protected List<UserOperation> postprocess(List<UserOperation> userOperations) {
		TextRecorder.turnOff();
		
		int noOfTextChangeOperations = 0;
		for (UserOperation operation : userOperations) {
			replay(operation);
			if (operation instanceof TextChangeOperation) {
				EditTransformationMapper.getInstance().processTextChange((TextChangeOperation) operation);
				noOfTextChangeOperations++;
			}
			if (operation instanceof ASTOperation) {
				ASTOperation astOperation = (ASTOperation) operation;
				ASTNode affectedNode = getNodeForOperation(astOperation);
				if (affectedNode == null)
					continue;
				UnknownTransformationDescriptor existingDescriptor = getExistingDescriptor(astOperation, affectedNode);
				if (existingDescriptor == null)
					continue;
				Long transformationID = existingDescriptor.getID();
				EditTransformationMapper.getInstance().matchEditsToAST(affectedNode, transformationID);
			}
		}
		
		TextRecorder.turnOn();

		for (UserOperation userOperation : userOperations) {
			record(userOperation);
			if (userOperation instanceof TextChangeOperation)
				System.out.println(((TextChangeOperation)userOperation).getTransformationID());
		}
		
		System.out.println("# of unmached transformations: " + EditTransformationMapper.getInstance().getNumberOFUnmachedTrasformations() + 
				" out of " + noOfTextChangeOperations);
		
		return userOperations;
	}
	
	@Override
	protected String getRecordFileName() {
		return "codechanges.txt.inferred_ast_operations";
	}

	@Override
	protected String getResultFilePostfix() {
		return ".with_mapped_transformations";
	}

}
