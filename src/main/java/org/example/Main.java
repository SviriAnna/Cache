package org.example;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {

        TwoLevelCache lruTwoCache = new TwoLevelCache.Builder(StrategyType.LRU)
                .setDiskCapacity(1).setMemoryCapacity(2).build();
        lruTwoCache.put("ключ1", "значение1");
        lruTwoCache.getRemovalStrategy().print();
        lruTwoCache.put("ключ2", "значение2");
        lruTwoCache.getRemovalStrategy().print();
        lruTwoCache.put("ключ3", "значение3");
        lruTwoCache.getRemovalStrategy().print();
        lruTwoCache.get("ключ2");
        lruTwoCache.getRemovalStrategy().print();
        lruTwoCache.put("ключ4", "значение4");
        lruTwoCache.getRemovalStrategy().print();
        lruTwoCache.remove("ключ4");
        lruTwoCache.getRemovalStrategy().print();


        TwoLevelCache mruTwoCache = new TwoLevelCache.Builder(StrategyType.MRU)
                .setDiskCapacity(1).setMemoryCapacity(2).build();
        mruTwoCache.put("ключ1", "значение1");
        mruTwoCache.getRemovalStrategy().print();
        mruTwoCache.put("ключ2", "значение2");
        mruTwoCache.getRemovalStrategy().print();
        mruTwoCache.put("ключ3", "значение3");
        mruTwoCache.getRemovalStrategy().print();
        mruTwoCache.get("ключ2");
        mruTwoCache.getRemovalStrategy().print();
        mruTwoCache.put("ключ4", "значение4");
        mruTwoCache.getRemovalStrategy().print();
        mruTwoCache.put("ключ5", "значение5");
        mruTwoCache.getRemovalStrategy().print();
        mruTwoCache.remove("ключ5");
        mruTwoCache.getRemovalStrategy().print();


        TwoLevelCache lfuTwoCache = new TwoLevelCache.Builder(StrategyType.LFU)
                .setDiskCapacity(1).setMemoryCapacity(2).build();
        lfuTwoCache.put("ключ1", "значение1");
        lfuTwoCache.getRemovalStrategy().print();
        lfuTwoCache.put("ключ2", "значение2");
        lfuTwoCache.getRemovalStrategy().print();
        lfuTwoCache.put("ключ2", "значение2");
        lfuTwoCache.getRemovalStrategy().print();
        lfuTwoCache.put("ключ3", "значение3");
        lfuTwoCache.getRemovalStrategy().print();
        lfuTwoCache.get("ключ3");
        lfuTwoCache.getRemovalStrategy().print();
        lfuTwoCache.put("ключ3", "значение3");
        lfuTwoCache.getRemovalStrategy().print();
        lfuTwoCache.put("ключ1", "значение1");
        lfuTwoCache.getRemovalStrategy().print();
        lfuTwoCache.put("ключ4", "значение4");
        lfuTwoCache.getRemovalStrategy().print();
        lfuTwoCache.put("ключ4", "значение4");
        lfuTwoCache.getRemovalStrategy().print();
        lfuTwoCache.put("ключ4", "значение4");
        lfuTwoCache.getRemovalStrategy().print();
        lfuTwoCache.put("ключ4", "значение4");
        lfuTwoCache.getRemovalStrategy().print();

    }
}
