//package com.telerik.android.data;
//
//import com.telerik.android.common.Function2;
//
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.LinkedList;
//import java.util.List;
//
///**
// * Instances of this class define how a collection of raw data items is sorted.
// */
//public class SortDescriptor<E> implements Comparator<DataItem<E>> {
//
//    private Function2<E, E, Integer> compareCallback;
//    private SortDescriptor<E> nextDescriptor;
//
//    public SortDescriptor(Function2<E, E, Integer> compareCallback) {
//        if(compareCallback == null) {
//            throw new IllegalArgumentException("callback cannot be null.");
//        }
//
//        this.compareCallback = compareCallback;
//    }
//
//    public void setNextDescriptor(SortDescriptor<E> nextDescriptor) {
//        this.nextDescriptor = nextDescriptor;
//    }
//
//    Iterable<DataItem<E>> processData(Iterable<DataItem<E>> data) {
//        return this.sort(data);
//    }
//
//    private Iterable<DataItem<E>> sort(Iterable<DataItem<E>> source) {
//        for (DataItem<E> item : source) {
//            if (item.itemsCount() > 0) {
//                item.setItems(this.sort(item.getItems()));
//            } else {
//                break;
//            }
//        }
//
//        List<DataItem<E>> typedSource = new LinkedList<DataItem<E>>();
//        for(DataItem<E> item : source) {
//            typedSource.add(item);
//        }
//
//        Collections.sort(typedSource, this);
//
//        return typedSource;
//    }
//
//    @Override
//    public int compare(DataItem<E> eDataItem, DataItem<E> eDataItem2) {
//        int result = this.compareCallback.apply(eDataItem.entity(), eDataItem2.entity());
//
//        while(result == 0 && nextDescriptor != null) {
//            result = nextDescriptor.compare(eDataItem, eDataItem2);
//        }
//
//        return result;
//    }
//}
