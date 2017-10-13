package com.semantalytics.stardog.kibble.geo;

import com.complexible.common.rdf.model.Values;
import com.complexible.stardog.plan.filter.ExpressionEvaluationException;
import com.complexible.stardog.plan.filter.ExpressionVisitor;
import com.complexible.stardog.plan.filter.functions.AbstractFunction;
import com.complexible.stardog.plan.filter.functions.Function;
import com.complexible.stardog.plan.filter.functions.string.StringFunction;
import com.github.davidmoten.geo.GeoHash;
import org.openrdf.model.Value;


public final class Latitude extends AbstractFunction implements StringFunction {

    protected Latitude() {
        super(1, GeoVocabulary.decode.iri.stringValue());
    }

    private Latitude(final Latitude caseFormat) {
        super(caseFormat);
    }

    @Override
    protected Value internalEvaluate(final Value... values) throws ExpressionEvaluationException {
        assertLiteral(values[0]);
        return Values.literal(GeoHash.decodeHash(values[0].stringValue()).getLat());
    }

    @Override
    public Latitude copy() {
        return new Latitude(this);
    }

    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    @Override
    public String toString() {
        return GeoVocabulary.latitude.name();
    }

}