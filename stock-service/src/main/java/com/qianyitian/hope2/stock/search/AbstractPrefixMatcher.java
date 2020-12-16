package com.qianyitian.hope2.stock.search;

import java.util.*;

public abstract class AbstractPrefixMatcher implements PrefixMatcher {

    protected final String[] javaKeywords = new String[]{
            "abstract", "assert",
            "boolean", "break", "byte",
            "case", "catch", "char", "class", "const", "continue",
            "default", "do", "double",
            "else", "enum", "extends",
            "final", "finally", "float", "for",
            "goto",
            "if", "implements", "import", "instanceof", "int", "interface",
            "long",
            "native", "new",
            "package", "private", "protected", "public",
            "return",
            "strictfp", "short", "static", "super", "switch", "synchronized",
            "this", "throw", "throws", "transient", "try",
            "void", "volatile",
            "while"
    };

    protected Map<String, Set<SearchItem>> prefixMatchers = new HashMap<String, Set<SearchItem>>();

    abstract void dynamicAddNew(String inputText);

    @Override
    public Set<SearchItem> obtainMatchedWords(String inputText) {
        Set<SearchItem> matchers = prefixMatchers.get(inputText);
//        if (matchers == null) {
//            Set<String> input = new HashSet<String>();
//            input.add(inputText);
//            dynamicAddNew(inputText);
//            return input;
//        }
        return matchers;
    }

    protected Map<String, Set<SearchItem>> obtainPrefixMatchers() {
        return Collections.unmodifiableMap(prefixMatchers);
    }


}