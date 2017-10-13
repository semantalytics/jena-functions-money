package com.semantalytics.stardog.plan.filter.functions.strings;

import com.complexible.common.rdf.model.Values;
import com.complexible.stardog.plan.filter.ExpressionEvaluationException;
import com.complexible.stardog.plan.filter.ExpressionVisitor;
import com.complexible.stardog.plan.filter.functions.AbstractFunction;
import com.complexible.stardog.plan.filter.functions.string.StringFunction;
import org.apache.commons.lang3.StringUtils;
import org.openrdf.model.Value;

public final class Contains extends AbstractFunction implements StringFunction {

    protected Contains() {
        super(1, StringsVocab.ontology().contains.toString());
    }

    private Contains(final Contains contains) {
        super(contains);
    }

    @Override
    protected Value internalEvaluate(final Value... values) throws ExpressionEvaluationException {

        final String sequence = assertStringLiteral(values[0]).stringValue();
        final String searchSequence = assertStringLiteral(values[1]).stringValue();

        return Values.literal(StringUtils.contains(sequence, searchSequence));
    }

    @Override
    public Contains copy() {
        return new Contains(this);
    }

    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    @Override
    public String toString() {
        return "Remove the last character from a String";
    }
}