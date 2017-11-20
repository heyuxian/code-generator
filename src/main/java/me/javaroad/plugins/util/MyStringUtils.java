package me.javaroad.plugins.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

/**
 * @author heyx
 */
public class MyStringUtils {

    private static final Pattern UNDERSCORE_PATTERN_1 = Pattern.compile("([A-Z]+)([A-Z][a-z])");
    private static final Pattern UNDERSCORE_PATTERN_2 = Pattern.compile("([a-z\\d])([A-Z])");

    private static List<RuleAndReplacement> plurals = new ArrayList<>();
    private static List<RuleAndReplacement> singulars = new ArrayList<>();
    private static List<String> unCountAbles = new ArrayList<>();

    private MyStringUtils() {
    }

    public static String pluralize(String word) {
        if (unCountAbles.contains(word.toLowerCase())) {
            return word;
        }
        return replaceWithFirstRule(word, plurals);
    }

    public static String singularize(String word) {
        if (unCountAbles.contains(word.toLowerCase())) {
            return word;
        }
        return replaceWithFirstRule(word, singulars);
    }

    public static String decapitalize(String src) {
        return StringUtils.uncapitalize(src);
    }

    public static String capitalize(String src) {
        return StringUtils.capitalize(src);
    }

    public static String dcp(String src) {
        return pluralize(decapitalize(src));
    }

    private static void plural(String rule, String replacement) {
        plurals.add(0, new RuleAndReplacement(rule, replacement));
    }

    private static void singular(String rule, String replacement) {
        singulars.add(0, new RuleAndReplacement(rule, replacement));
    }

    private static void irregular(String singular, String plural) {
        plural(singular, plural);
        singular(plural, singular);
    }

    private static void uncountable(String... words) {
        Collections.addAll(unCountAbles, words);
    }

    private static String underscore(String camelCasedWord) {

        String underscoredWord = UNDERSCORE_PATTERN_1.matcher(camelCasedWord).replaceAll("$1_$2");
        underscoredWord = UNDERSCORE_PATTERN_2.matcher(underscoredWord).replaceAll("$1_$2");
        underscoredWord = underscoredWord.replace('-', '_').toLowerCase();

        return underscoredWord;
    }

    private static String replaceWithFirstRule(String word, List<RuleAndReplacement> ruleAndReplacements) {

        for (RuleAndReplacement rar : ruleAndReplacements) {
            String rule = rar.getRule();
            String replacement = rar.getReplacement();
            // Return if we find a match.
            Matcher matcher = Pattern.compile(rule, Pattern.CASE_INSENSITIVE).matcher(word);
            if (matcher.find()) {
                return matcher.replaceAll(replacement);
            }
        }
        return word;
    }

    static  {
        plural("$", "s");
        plural("s$", "s");
        plural("(ax|test)is$", "$1es");
        plural("(octop|vir)us$", "$1i");
        plural("(alias|status)$", "$1es");
        plural("(bu)s$", "$1es");
        plural("(buffal|tomat)o$", "$1oes");
        plural("([ti])um$", "$1a");
        plural("sis$", "ses");
        plural("(?:([^f])fe|([lr])f)$", "$1$2ves");
        plural("(hive)$", "$1s");
        plural("([^aeiouy]|qu)y$", "$1ies");
        plural("([^aeiouy]|qu)ies$", "$1y");
        plural("(x|ch|ss|sh)$", "$1es");
        plural("(matr|vert|ind)ix|ex$", "$1ices");
        plural("([m|l])ouse$", "$1ice");
        plural("(ox)$", "$1es");
        plural("(quiz)$", "$1zes");

        singular("s$", "");
        singular("(n)ews$", "$1ews");
        singular("([ti])a$", "$1um");
        singular("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "$1$2sis");
        singular("(^analy)ses$", "$1sis");
        singular("([^f])ves$", "$1fe");
        singular("(hive)s$", "$1");
        singular("(tive)s$", "$1");
        singular("([lr])ves$", "$1f");
        singular("([^aeiouy]|qu)ies$", "$1y");
        singular("(s)eries$", "$1eries");
        singular("(m)ovies$", "$1ovie");
        singular("(x|ch|ss|sh)es$", "$1");
        singular("([m|l])ice$", "$1ouse");
        singular("(bus)es$", "$1");
        singular("(o)es$", "$1");
        singular("(shoe)s$", "$1");
        singular("(cris|ax|test)es$", "$1is");
        singular("([octop|vir])i$", "$1us");
        singular("(alias|status)es$", "$1");
        singular("^(ox)es", "$1");
        singular("(vert|ind)ices$", "$1ex");
        singular("(matr)ices$", "$1ix");
        singular("(quiz)zes$", "$1");

        irregular("person", "people");
        irregular("man", "men");
        irregular("child", "children");
        irregular("sex", "sexes");
        irregular("move", "moves");

        uncountable("equipment", "information", "rice", "money", "species", "series", "fish", "sheep");
    }

}

@Getter
@Setter
@AllArgsConstructor
class RuleAndReplacement {
    private String rule;
    private String replacement;
}
