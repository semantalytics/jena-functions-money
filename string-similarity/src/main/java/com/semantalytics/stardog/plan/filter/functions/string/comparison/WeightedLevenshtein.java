package com.semantalytics.stardog.plan.filter.functions.string.comparison;

import com.complexible.stardog.plan.filter.ExpressionEvaluationException;
import com.complexible.stardog.plan.filter.ExpressionVisitor;
import com.complexible.stardog.plan.filter.functions.AbstractFunction;
import com.complexible.stardog.plan.filter.functions.Function;
import com.complexible.stardog.plan.filter.functions.string.StringFunction;
import com.google.common.base.Objects;
import com.google.common.collect.Range;
import org.openrdf.model.Value;

import java.util.HashMap;
import java.util.Map;

import static com.complexible.common.rdf.model.Values.literal;

public final class WeightedLevenshtein extends AbstractFunction implements StringFunction {

    private info.debatty.java.stringsimilarity.WeightedLevenshtein weightedLevenshtein;

    protected WeightedLevenshtein() {
        super(Range.atLeast(2), StringSimilarityVocab.WEIGHTED_LEVENSHTEIN.iri().stringValue());
    }

    private WeightedLevenshtein(final WeightedLevenshtein weightedLevenshtein) {
        super(weightedLevenshtein);
    }

    public Function copy() {
        return new WeightedLevenshtein(this);
    }

    private info.debatty.java.stringsimilarity.WeightedLevenshtein getWeightedLevenshteinFunction(Value... values) {
        if(weightedLevenshtein == null) {
            Map<SubstitutionPair, Double> characterSubstitutionMap = new HashMap<>();

            for (int i = 2; i < values.length; i += 3) {

                final Character character1 = values[i].stringValue().charAt(0);
                final Character character2 = values[i + 1].stringValue().charAt(0);
                final SubstitutionPair substitutionPair = new SubstitutionPair(character1, character2);
                final Double weight = Double.parseDouble(values[i + 2].stringValue());

                characterSubstitutionMap.put(substitutionPair, weight);
            }

            weightedLevenshtein = new info.debatty.java.stringsimilarity.WeightedLevenshtein(
                    (char c1, char c2) -> {
                        SubstitutionPair substitutionPair = new SubstitutionPair(c1, c2);
                        if (characterSubstitutionMap.containsKey(substitutionPair)) {
                            return characterSubstitutionMap.get(substitutionPair);
                        } else {
                            return 1.0;
                        }
                    }
            );

        }
        return weightedLevenshtein;
    }

    @Override
    public void initialize() {
        weightedLevenshtein = null;
    }

    @Override
    protected Value internalEvaluate(final Value... values) throws ExpressionEvaluationException {

        assertStringLiteral(values[0]);
        assertStringLiteral(values[1]);

        if ((values.length - 2) % 3 != 0) {
            throw new ExpressionEvaluationException("Incorrect parameter count");
        }

        for (int i = 2; i < values.length; i += 3) {
            assertStringLiteral(values[i]);
            assertStringLiteral(values[i + 1]);
            assertNumericLiteral(values[i + 2]);
        }

        return literal(getWeightedLevenshteinFunction(values).distance(values[0].stringValue(), values[1].stringValue()));
    }

    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    @Override
    public String toString() {
        return "WeightedLevenshtein";
    }

    private class SubstitutionPair {

        private final char c1;
        private final char c2;

        SubstitutionPair(final char c1, final char c2) {
            this.c1 = c1;
            this.c2 = c2;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(c1, c2);
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }

            if (object instanceof SubstitutionPair) {
                SubstitutionPair other = (SubstitutionPair) object;

                return Objects.equal(c1, other.c1) && Objects.equal(c2, other.c2);
            }
            return false;
        }
    }
}
