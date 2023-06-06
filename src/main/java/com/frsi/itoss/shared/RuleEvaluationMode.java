package com.frsi.itoss.shared;

public enum RuleEvaluationMode {
    /*
     * Each rule is evaluated independently
     */
    SingleRule,
    /*
     * A unit rule group is a composite rule that acts as a unit: Either all rules are applied or nothing is applied.
     */
    UnitRuleGroup,
    /*
     * An activation rule group is a composite rule that fires the first applicable rule and
     * ignores other rules in the group (XOR logic). Rules are first sorted by their
     * natural order (priority by default) within the group.
     */
    ActivationRuleGroup,
    /*
     * A conditional rule group is a composite rule where the rule with the highest priority acts as a condition:
     * if the rule with the highest priority evaluates to true, then the rest of the rules are fired.
     */
    ConditionalRuleGroup
}
