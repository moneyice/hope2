package com.qianyitian.hope2.stock.search;

import java.util.Set;

public interface PrefixMatcher {

    Set<SearchItem> obtainMatchedWords(String inputText);
}