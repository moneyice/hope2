package com.qianyitian.hope2.analyzer.job.backtracking;

import com.qianyitian.hope2.analyzer.model.ETradeType;
import com.qianyitian.hope2.analyzer.model.KLineInfo;

import java.util.List;

public interface IBacktrackingPolicy {
    ETradeType check(int index, List<KLineInfo> kLineInfos);
}
