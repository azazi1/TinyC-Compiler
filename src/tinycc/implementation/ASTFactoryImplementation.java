package tinycc.implementation;

import java.util.ArrayList;
import java.util.List;

import tinycc.diagnostic.Locatable;
import tinycc.implementation.expression.ConditionalExpression;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.FunctionCall;
import tinycc.implementation.expression.BinaryExpression.AddExpression;
import tinycc.implementation.expression.BinaryExpression.AndExpression;
import tinycc.implementation.expression.BinaryExpression.AssignExpression;
import tinycc.implementation.expression.BinaryExpression.CmpExpression;
import tinycc.implementation.expression.BinaryExpression.DivExpression;
import tinycc.implementation.expression.BinaryExpression.EqCmpExpression;
import tinycc.implementation.expression.BinaryExpression.MulExpression;
import tinycc.implementation.expression.BinaryExpression.OrExpression;
import tinycc.implementation.expression.BinaryExpression.SubtExpression;
import tinycc.implementation.expression.PrimaryExpression.ConstExpression;
import tinycc.implementation.expression.PrimaryExpression.VarExpression;
import tinycc.implementation.expression.UnaryExpression.AddrOfExpression;
import tinycc.implementation.expression.UnaryExpression.IndirExpression;
import tinycc.implementation.expression.UnaryExpression.NegExpression;
import tinycc.implementation.expression.UnaryExpression.SizeofExpression;
import tinycc.implementation.statement.AssertStatement;
import tinycc.implementation.statement.AssumeStatement;
import tinycc.implementation.statement.BlockStatement;
import tinycc.implementation.statement.BreakStatement;
import tinycc.implementation.statement.ContinueStatement;
import tinycc.implementation.statement.DeclarationStatement;
import tinycc.implementation.statement.ErrorStatement;
import tinycc.implementation.statement.ExpressionStatement;
import tinycc.implementation.statement.IfStatement;
import tinycc.implementation.statement.ReturnStatement;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.statement.WhileStatement;
import tinycc.implementation.topLevelConstruct.ExternalDeclaration;
import tinycc.implementation.topLevelConstruct.FunctionDeclaration;
import tinycc.implementation.topLevelConstruct.FunctionDefinition;
import tinycc.implementation.topLevelConstruct.GlobalVariable;
import tinycc.implementation.type.CharType;
import tinycc.implementation.type.FunctionType;
import tinycc.implementation.type.IntType;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.VoidType;
import tinycc.parser.ASTFactory;
import tinycc.parser.Token;
import tinycc.parser.TokenKind;

public class ASTFactoryImplementation implements ASTFactory {

    private List<ExternalDeclaration> translationUnit;

    public ASTFactoryImplementation(){
        translationUnit = new ArrayList<ExternalDeclaration> ();
    }

    public List<ExternalDeclaration> getTranslationUnit() {
        return translationUnit;
    }

    @Override
    public Statement createBlockStatement(Locatable loc, List<Statement> statements) {
        return new BlockStatement(loc, statements);
    }

    @Override
    public Statement createBreakStatement(Locatable loc) {
        return new BreakStatement(loc);
    }

    @Override
    public Statement createContinueStatement(Locatable loc) {
        return new ContinueStatement(loc);
    }

    @Override
    public Statement createDeclarationStatement(Type type, Token name, Expression init) {
        return new DeclarationStatement(type, name, init);
    }

    @Override
    public Statement createErrorStatement(Locatable loc) {
        return new ErrorStatement(loc);
    }

    @Override
    public Statement createExpressionStatement(Locatable loc, Expression expression) {
        return new ExpressionStatement(loc, expression);
    }

    @Override
    public Statement createIfStatement(Locatable loc, Expression condition, Statement consequence,
            Statement alternative) {
        return new IfStatement(loc, condition, consequence, alternative);
    }

    @Override
    public Statement createReturnStatement(Locatable loc, Expression expression) {
        return new ReturnStatement(loc, expression);
    }

    @Override
    public Statement createWhileStatement(Locatable loc, Expression condition, Statement body, Expression invariant,
            Expression term, Token loopBound) {
        return new WhileStatement(loc, condition, body, invariant, term, loopBound);
    }

    @Override
    public Statement createAssumeStatement(Locatable loc, Expression condition) {
        return new AssumeStatement(loc, condition);
    }

    @Override
    public Statement createAssertStatement(Locatable loc, Expression condition) {
        return new AssertStatement(loc, condition);
    }

    @Override
    public Type createFunctionType(Type returnType, List<Type> parameters) {
        return new FunctionType(returnType, parameters);
    }

    @Override
    public Type createPointerType(Type pointsTo) {
        return new PointerType(pointsTo);
    }

    @Override
    public Type createBaseType(TokenKind kind) {
        switch (kind) {
            case VOID:
                return new VoidType();
        
            case CHAR:
                return new CharType();

            case INT:
                return new IntType();
            
            default:
                return null;
        }
    }

    @Override
    public Expression createBinaryExpression(Token operator, Expression left, Expression right) {
        switch (operator.getKind()) {
            case PLUS:
                return new AddExpression(operator, left, right);
            case MINUS:
                return new SubtExpression(operator, left, right);
            case ASTERISK:
                return new MulExpression(operator, left, right);
            case SLASH:
                return new DivExpression(operator, left, right);
            case EQUAL_EQUAL, BANG_EQUAL:
                return new EqCmpExpression(operator, left, right);
            case LESS, LESS_EQUAL, GREATER, GREATER_EQUAL:
                return new CmpExpression(operator, left, right);
            case EQUAL:
                return new AssignExpression(operator, left, right);
            case AND_AND:
                return new AndExpression(operator, left, right);
            case PIPE_PIPE:
                return new OrExpression(operator, left, right);
        
            default:
                return null;
        }
    }

    @Override
    public Expression createCallExpression(Token token, Expression callee, List<Expression> arguments) {
        return new FunctionCall(token, callee, arguments);
    }

    @Override
    public Expression createConditionalExpression(Token token, Expression condition, Expression consequence,
            Expression alternative) {
        return new ConditionalExpression(token, condition, consequence, alternative);
    }

    @Override
    public Expression createUnaryExpression(Token operator, boolean postfix, Expression operand) {
        switch (operator.getKind()) {
            case ASTERISK:
                return new IndirExpression(operator, postfix, operand);   
            case AND:
                return new AddrOfExpression(operator, postfix, operand); 
            case SIZEOF:
                return new SizeofExpression(operator, postfix, operand); 
            case BANG:
                return new NegExpression(operator, postfix, operand); 
            default:
                return null;
        }
    }

    @Override
    public Expression createPrimaryExpression(Token token) {
        switch (token.getKind()) {
            case IDENTIFIER:
                return new VarExpression(token);  
            case NUMBER, STRING, CHARACTER:
                return new ConstExpression(token); 
            default:
                return null;
        }
    }

    @Override
    public void createExternalDeclaration(Type type, Token name) {
        if (type.isFunctionType()) {
            translationUnit.add(new FunctionDeclaration(type, name));
        } else {
            translationUnit.add(new GlobalVariable(type, name));
        }

    }

    @Override
    public void createFunctionDefinition(Type type, Token name, List<Token> parameterNames, Statement body) {
        translationUnit.add(new FunctionDefinition(type, name, parameterNames, body));
    }
    
}
