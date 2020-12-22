package edu.iastate.cs228.hw4;

/**
 *  
 * @author JakeM
 *
 */

import java.util.HashMap;
import java.util.Scanner;

/**
 * 
 * This class represents an infix expression. It implements infix to postfix conversion using 
 * one stack, and evaluates the converted postfix expression.    
 *
 */

public class InfixExpression extends Expression 
{
	private String infixExpression;   	// the infix expression to convert		
	private boolean postfixReady = false;   // postfix already generated if true
	private int rankTotal = 0;		// Keeps track of the cumulative rank of the infix expression.
	
	private PureStack<Operator> operatorStack; 	  // stack of operators 
	
	
	/**
	 * Constructor stores the input infix string, and initializes the operand stack and 
	 * the hash map.
	 * 
	 * @param st  input infix string. 
	 * @param varTbl  hash map storing all variables in the infix expression and their values. 
	 */
	public InfixExpression (String st, HashMap<Character, Integer> varTbl) throws ExpressionFormatException
	{
		infixExpression = removeExtraSpaces(st);
		varTable = new HashMap<Character, Integer>();
		varTable.putAll(varTbl);
		operatorStack = new ArrayBasedStack<Operator>();
	}
	

	/**
	 * Constructor supplies a default hash map. 
	 * 
	 * @param s
	 */
	public InfixExpression (String s)
	{
		infixExpression = s;
		varTable = new HashMap<Character, Integer>();
		operatorStack = new ArrayBasedStack<Operator>();
	}
	

	/**
	 * Outputs the infix expression according to the format in the project description.
	 */
	@Override
	public String toString()
	{
		
		return removeExtraSpaces(infixExpression); 
	}
	
	
	/** 
	 * @return equivalent postfix expression, or  
	 * 
	 *         a null string if a call to postfix() inside the body (when postfixReady 
	 * 		   == false) throws an exception.
	 * @throws ExpressionFormatException 
	 */
	public String postfixString()
	{
		if(postfixReady == false) return null;
		return postfixExpression;
	}


	/**
	 * Resets the infix expression. 
	 * 
	 * @param st
	 */
	public void resetInfix (String st)
	{
		infixExpression = st; 
	}


	/**
	 * Converts infix expression to an equivalent postfix string stored at postfixExpression.
	 * If postfixReady == false, the method scans the infixExpression, and does the following
	 * (for algorithm details refer to the relevant PowerPoint slides): 
	 * 
	 *     1. Skips a whitespace character.
	 *     2. Writes a scanned operand to postfixExpression. 
	 *     3. When an operator is scanned, generates an operator object.  In case the operator is 
	 *        determined to be a unary minus, store the char '~' in the generated operator object.
	 *     4. If the scanned operator has a higher input precedence than the stack precedence of 
	 *        the top operator on the operatorStack, push it onto the stack.   
	 *     5. Otherwise, first calls outputHigherOrEqual() before pushing the scanned operator 
	 *        onto the stack. No push if the scanned operator is ). 
     *     6. Keeps track of the cumulative rank of the infix expression. 
     *     
     *  During the conversion, catches errors in the infixExpression by throwing 
     *  ExpressionFormatException with one of the following messages:
     *  
     *      -- "Operator expected" if the cumulative rank goes above 1;
     *      -- "Operand expected" if the rank goes below 0; 
     *      -- "Missing '('" if scanning a �)� results in popping the stack empty with no '(';
     *      -- "Missing ')'" if a '(' is left unmatched on the stack at the end of the scan; 
     *      -- "Invalid character" if a scanned char is neither a digit nor an operator; 
     *   
     *  If an error is not one of the above types, throw the exception with a message you define.
     *      
     *  Sets postfixReady to true.  
	 */
	public void postfix() throws ExpressionFormatException
	{
		 int first = 0;//to check if first operator is unary
		 boolean prevOp = false;//check if previous was operator
		 boolean prevLeftp = false; // check if previous was left parenthesis
		 boolean leftP = false; //check if there has been a left parenthesis
		 boolean rightP = false; //check if there has been a right parenthesis
		 removeExtraSpaces(infixExpression);
		 if(postfixReady == true) throw new ExpressionFormatException("can't call postfix() if it's already been called on this expression");
		 	 postfixExpression = "";
			 Scanner scan = new Scanner(infixExpression);
			 while(scan.hasNext())
			 {
				 if(rankTotal>1) throw new ExpressionFormatException("Operator expected");
				 if(rankTotal<0) throw new ExpressionFormatException("Operand expected");
				 String next = scan.next();
				 first++;
				 if(isInt(next))
				 {
					 postfixExpression+=next+' ';//allow to use scanner and call next() to seperate by spaces
					 rankTotal++;
					 prevOp = false;
					 prevLeftp = false;
				 }
				 else if(isVariable(next.charAt(0)))
				 {
					 postfixExpression+=next+' ';//allow to use scanner and call next() to seperate by spaces
					 rankTotal++;
					 prevOp = false;
					 prevLeftp = false;
				 }
				 else if(isOperator(next.charAt(0)))
				 {
					 prevOp = true;
					 Operator op;
					 if(next.charAt(0)=='-') // check if unary
					 {
						 if(first==1)//beginning of expression
						 {
							 op = new Operator('~');
							 prevLeftp = false;
						 }
						 else if(prevOp)//after another operator
						 {
							 op = new Operator('~');
							 prevLeftp = false; 
						 }
						 else if(prevLeftp)//after a left parenthesis
						 {
							 op = new Operator('~');
							 prevLeftp = false; 
						 }
						 else//not unary
						 {
							 op = new Operator('-');
							 prevLeftp = false; 
						 }
					 }
					 else//not minus sign
						 {
							 if(next.charAt(0)!='('&&next.charAt(0)!=')')
							 {
								 rankTotal--;
								 prevLeftp = false;
							 }
							 if(next.charAt(0)=='(')
							 {
								 prevLeftp = true;
								 leftP = true;
							 }
							 if(next.charAt(0)==')')
							 {
								 if(!leftP) throw new ExpressionFormatException("Missing '('");
								 rightP = true;
							 }
							 op = new Operator(next.charAt(0));
						 }
						 if(operatorStack.isEmpty()) 
						 {
							 operatorStack.push(op);
						 }
						 else 
						 {
							 if(operatorStack.peek().compareTo(op)==-1)//operator has higher input precedence than the stack precedence of top of stack, push onto it
							 {
								 operatorStack.push(op);
							 }
							 else//Otherwise, first calls outputHigherOrEqual() before pushing the scanned operator onto the stack. No push if the scanned operator is ).
							 {
								 outputHigherOrEqual(op);
								 if(op.operator!=')')
								 {
									 operatorStack.push(op);
								 }
							 }
						 }
					 }
				 else//not valid char
				 {
					 throw new ExpressionFormatException("Invalid character");
				 }
				}
			
		 if(leftP&&!rightP) throw new ExpressionFormatException("Missing '('");
		 postfixReady = true;
		 while(!operatorStack.isEmpty())
		 {
			 postfixExpression += operatorStack.pop().operator + " "; 
		 }
	}
	
	
	/**
	 * This function first calls postfix() to convert infixExpression into postfixExpression. Then 
	 * it creates a PostfixExpression object and calls its evaluate() method (which may throw  
	 * an exception).  It also passes any exception thrown by the evaluate() method of the 
	 * PostfixExpression object upward the chain. 
	 * 
	 * @return value of the infix expression 
	 * @throws ExpressionFormatException, UnassignedVariableException
	 * @throws UnassignedVariableException 
	 */
	public int evaluate() throws ExpressionFormatException, UnassignedVariableException  
    {
    	postfix();
    	PostfixExpression pe = new PostfixExpression(postfixExpression,varTable);
    	return pe.evaluate();
    }


	/**
	 * Pops the operator stack and output as long as the operator on the top of the stack has a 
	 * stack precedence greater than or equal to the input precedence of the current operator op.  
	 * Writes the popped operators to the string postfixExpression.  
	 * 
	 * If op is a ')', and the top of the stack is a '(', also pops '(' from the stack but does 
	 * not write it to postfixExpression. 
	 * 
	 * @param op  current operator
	 */
	private void outputHigherOrEqual(Operator op)
	{
		while(!operatorStack.isEmpty()&&operatorStack.peek().compareTo(op)==1||operatorStack.peek().compareTo(op)==0)
		{
			if(op.operator==')'&&operatorStack.peek().operator=='(')
			{
				operatorStack.pop();
			}
			else
				{
				postfixExpression += operatorStack.pop().operator + " ";
				if(operatorStack.isEmpty()) break;
				}
		}
		if(op.operator==')'&&operatorStack.peek().operator=='(')
		{
			operatorStack.pop();
		}
	}
	
	// other helper methods if needed
}
