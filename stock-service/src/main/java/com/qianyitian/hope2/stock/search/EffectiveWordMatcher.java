package com.qianyitian.hope2.stock.search;

import com.qianyitian.hope2.stock.config.ChineseCharToEn;
import com.qianyitian.hope2.stock.dao.IStockDAO;
import com.qianyitian.hope2.stock.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EffectiveWordMatcher extends AbstractPrefixMatcher {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource(name = "stockDAO4FileSystem")
    private IStockDAO stockDAO;

    public EffectiveWordMatcher() {
    }

    public void init() {
        List<Stock> allStocks = stockDAO.getAllSymbols();
        if (allStocks == null) {
            logger.error("all stock info has not loaded.");
            return;
        }
        List<SearchItem> list = allStocks.parallelStream().map(stock -> {
            SearchStockItem item = new SearchStockItem(stock);
            item.setAlias(ChineseCharToEn.getAllFirstLetter(stock.getName()));
            return item;
        }).collect(Collectors.toList());
        prefixMatchers = buildPrefixMatchers(list);
    }


    static class Pair {
        private String key;
        private SearchItem value;

        public Pair(String key, SearchItem value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public SearchItem getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "<" + key + "," + value + ">";
        }
    }

    private Map<String, Set<SearchItem>> buildPrefixMatchers(List<SearchItem> stocks) {
        List<Pair> pairs = strarr2pairs(stocks);
        return mergePairs(pairs);
    }

    /*
     * 将 字符串数组转化为前缀匹配对
     * eg. ["ab", "ac"] ===>
     *     [<"a","ab">, <"ab", "ab">, <"a", "ac">, <"ac", "ac">]
     */
    private List<Pair> strarr2pairs(List<SearchItem> list) {
        List<Pair> pairs = new LinkedList<>();
        for (SearchItem searchItem : list) {
            processOneItem(pairs, searchItem);
        }
        return pairs;
    }

    private void processOneItem(List<Pair> pairs, SearchItem searchItem) {
        String[] keywords = searchItem.getKeys();
        for (String keyword : keywords) {
            processOneWord(keyword, pairs, searchItem);
        }
    }

    protected void processOneWord(String keyword, List<Pair> pairs, SearchItem searchItem) {
        if (keyword == null) {
            return;
        }
        int keyWordLength = keyword.length();
        for (int i = 1; i <= keyWordLength; i++) {
            String prefix = keyword.substring(0, i);
            Pair pair = new Pair(prefix, searchItem);
            pairs.add(pair);
        }
    }

    /*
     * 将多个 <key,value> 合并为一个映射
     * eg. [<"a", "abstract">, <"b", "boolean">, <"a", "assert">, <"b", "break">, <"c", "continue">] ===>
     *     {"a"=>["abstract", "assert", "b"=>["boolean", "break"], "c"=>["continue"]}
     */
    private static Map<String, Set<SearchItem>> mergePairs(List<Pair> pairs) {
        Map<String, Set<SearchItem>> result = new TreeMap<String, Set<SearchItem>>();
        if (pairs != null && pairs.size() > 0) {
            for (Pair pair : pairs) {
                String key = pair.getKey();
                SearchItem value = pair.getValue();
                Set<SearchItem> matchers = result.get(key);
                if (matchers == null) {
                    matchers = new HashSet<SearchItem>();
                }
                matchers.add(value);
                result.put(key, matchers);
            }
        }
        return result;
    }

    @Override
    void dynamicAddNew(String inputText) {
//        if (checkValid(inputText)) {
//            List<Pair> newpairs = strarr2pairs(new String[]{inputText});
//            Map<String, Set<String>> newPreixMatchers = mergePairs(newpairs);
//            mergeMap(newPreixMatchers, prefixMatchers);
//        }
    }

    private boolean checkValid(String inputText) {
        return false;
    }

    private Map<String, Set<String>> mergeMap(Map<String, Set<String>> src, Map<String, Set<String>> dest) {
        Set<Map.Entry<String, Set<String>>> mapEntries = src.entrySet();
        Iterator<Map.Entry<String, Set<String>>> iter = mapEntries.iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Set<String>> entry = iter.next();
            String key = entry.getKey();
            Set<String> newMatchers = entry.getValue();
            if (dest.containsKey(key)) {
                dest.get(key).addAll(newMatchers);
            } else {
                dest.put(key, newMatchers);
            }
        }
        return dest;
    }
}