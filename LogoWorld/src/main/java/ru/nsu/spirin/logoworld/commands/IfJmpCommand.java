package ru.nsu.spirin.logoworld.commands;

import ru.nsu.spirin.logoworld.exceptions.InvalidConditionException;
import ru.nsu.spirin.logoworld.logic.Program;
import ru.nsu.spirin.logoworld.logic.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class IfJmpCommand implements Command {
    private final Program program;
    private final World world;
    private boolean lastConditionState = false;
    private int nextCommand = 0;

    public IfJmpCommand(Program program, World world) {
        this.program = program;
        this.world = world;
    }

    /**
     * Validates arguments of command
     *
     * @param args arguments of command
     * @return true if arguments are valid
     */
    @Override
    public boolean validateArgs(String[] args) {
        if (args.length != 2) return false;

        try {
            lastConditionState = parseCondition(args[0]);
            nextCommand = Integer.parseInt(args[1]);
            return true;
        }
        catch (InvalidConditionException | NumberFormatException e) {
            lastConditionState = false;
            return false;
        }
    }

    /**
     * Executes command
     *
     * @param args arguments of command
     * @return true if command was executed successfully
     */
    @Override
    public boolean execute(String[] args) {
        if (lastConditionState) program.setNextCommand(nextCommand);
        else program.setNextCommand();
        return false;
    }

    private boolean compareValues(String first, String second, String op) throws InvalidConditionException {
        try {
            if (op.equals("==")) {
                return first.equals(second);
            }

            int firstValue = Integer.parseInt(first);
            int secondValue = Integer.parseInt(second);
            switch(op) {
                case ">" -> { return firstValue > secondValue; }
                case ">=" -> { return firstValue >= secondValue; }
                case "<" -> { return firstValue < secondValue; }
                case "<=" -> { return firstValue <= secondValue; }
                default -> { throw  new InvalidConditionException(""); }
            }

        }
        catch (NumberFormatException e) {
            throw new InvalidConditionException("");
        }
    }

    private boolean logicOperation(boolean first, boolean second, String op) throws InvalidConditionException {
        switch(op) {
            case "and" -> { return first && second; }
            case "or" -> { return first || second; }
            default -> { throw  new InvalidConditionException(""); }
        }
    }

    private String getConstant(String constant) throws InvalidConditionException {
        switch(constant) {
            case "POS_X" -> { return world.getTurtlePosition().getSecond() + ""; }
            case "POS_Y" -> { return world.getTurtlePosition().getFirst() + ""; }
            case "FIELD_WIDTH" -> { return world.getFieldSize().getFirst() + ""; }
            case "FIELD_HEIGHT" -> { return world.getFieldSize().getSecond() + ""; }
            case "IS_DRAWING" -> { return world.getIsTurtleDrawing() ? "TRUE" : "FALSE"; }
        }
        throw new InvalidConditionException("");
    }

    private boolean parseCondition(String condition) throws InvalidConditionException {
        List<String> tokens = tokenize(condition);
        List<String> notation = convertToReversePolishNotation(tokens);

        Stack<String> stack = new Stack<>();
        for (String elem : notation) {
            switch(elem) {
                case ">=", ">", "<=", "<", "==" -> {
                    String second = stack.pop();
                    String first = stack.pop();
                    stack.push(compareValues(first, second, elem) ? "TRUE" : "FALSE");
                }
                case "not" -> {
                    boolean value = stack.pop().equals("TRUE");
                    stack.push(!value ? "TRUE" : "FALSE");
                }
                case "and", "or" -> {
                    boolean value1 = stack.pop().equals("TRUE");
                    boolean value2 = stack.pop().equals("TRUE");
                    stack.push(logicOperation(value1, value2, elem) ? "TRUE" : "FALSE");
                }
                case "POS_X", "POS_Y", "FIELD_WIDTH", "FIELD_HEIGHT", "IS_DRAWING" -> {
                    stack.push(getConstant(elem));
                }
                case "TRUE", "FALSE" -> { stack.push(elem); }
                default -> {
                    try {
                        Integer.parseInt(elem);
                        stack.push(elem);
                    }
                    catch (NumberFormatException e) {
                        throw new InvalidConditionException("");
                    }
                }
            }
        }

        if (stack.empty()) throw new InvalidConditionException("");
        return stack.pop().equals("TRUE");
    }

    private List<String> tokenize(String condition) {
        List<String> tokens = new ArrayList<>();

        char[] characters = condition.toCharArray();
        StringBuilder curToken = new StringBuilder();

        for (char ch : characters) {
            if (ch == ' ') {
                if (curToken.length() != 0) {
                    tokens.add(curToken.toString());
                }
                curToken.setLength(0);
                continue;
            }
            if (ch == '(' || ch == ')') {
                if (curToken.length() != 0) {
                    tokens.add(curToken.toString());
                }
                curToken.setLength(0);
                tokens.add(ch + "");
                continue;
            }
            curToken.append(ch);
        }

        if (curToken.length() != 0) {
            tokens.add(curToken.toString());
        }
        curToken.setLength(0);

        return tokens;
    }

    private int getOperationPriority(String op) {
        switch(op) {
            case ">=", ">", "<=", "<", "==" -> {
                return 4;
            }
            case "not" -> {
                return 3;
            }
            case "and" -> {
                return 2;
            }
            case "or" -> {
                return 1;
            }
            case "(", ")" -> {
                return 0;
            }

        }
        return -1;
    }

    private List<String> convertToReversePolishNotation(List<String> tokens) throws InvalidConditionException {
        List<String> notation = new ArrayList<>();

        Stack<String> stack = new Stack<>();
        int bracketBalance = 0;

        for (String token : tokens) {
            switch (token) {
                case "(" -> {
                    stack.push(token);
                    bracketBalance++;
                }
                case ")" -> {
                    bracketBalance--;
                    if (stack.empty()) throw new InvalidConditionException("");
                    while (!stack.empty() && !stack.peek().equals("(")) {
                        notation.add(stack.pop());
                    }
                    stack.pop();
                }
                case "and", "or", "not", "<=", "<", ">=", ">", "==" -> {
                    while (!stack.empty() && getOperationPriority(token) <= getOperationPriority(stack.peek())) {
                        notation.add(stack.pop());
                    }
                    stack.push(token);
                }
                case "POS_X", "POS_Y", "FIELD_WIDTH", "FIELD_HEIGHT", "IS_DRAWING", "TRUE", "FALSE" -> {
                    notation.add(token);
                }
                default -> {
                    try {
                        Integer.parseInt(token);
                        notation.add(token);
                    }
                    catch (NumberFormatException e) {
                        throw new InvalidConditionException("");
                    }
                }
            }
        }

        while (!stack.empty()) {
            notation.add(stack.pop());
        }

        if (bracketBalance != 0) throw new InvalidConditionException("");

        return notation;
    }
}
