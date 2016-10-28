package com.telerik.widget.chart.engine.dataPoints;

import com.telerik.widget.chart.engine.elementTree.ChartElement;
import com.telerik.widget.chart.engine.elementTree.ElementCollection;

import java.util.List;

/**
 * Represents a typed element collection which accepts {@link DataPoint} instances only.
 *
 * @param <T> the type of the instances.
 */
public class DataPointCollection<T extends DataPoint> extends ElementCollection<T> implements List<T> {

    /**
     * Initializes a new instance of the {@link DataPointCollection} class.
     *
     * @param owner the owner of the current data point collection instance.
     */
    public DataPointCollection(ChartElement owner) {
        super(owner);
    }

/* TODO c#
        DataPointCollection(ChartSeriesModel owner)
        :super(owner)
        {
        }

        int ICollection<DataPoint>.size()
        {
        get
        {
        return this.size();
}
        }

        boolean ICollection<DataPoint>.IsReadOnly
        {
        get
        {
        return false;
}
        }

        DataPoint List<DataPoint>.this[int index]
        {
        get
        {
        return this[index]as DataPoint;
}
        set
        {
        this[index]=value as T;
}
        }

        int List<DataPoint>.IndexOf(DataPoint item)
        {
        return this.IndexOf(item as T);
}

        void List<DataPoint>.Insert(int index,DataPoint item)
        {
        this.Insert(index,item as T);
}

        void List<DataPoint>.RemoveAt(int index)
        {
        this.RemoveAt(index);
}

        void ICollection<DataPoint>.ADD(DataPoint item)
        {
        this.ADD(item as T);
}

        void ICollection<DataPoint>.clear()
        {
        this.clear();
}

        boolean ICollection<DataPoint>.contains(DataPoint item)
        {
        return this.contains(item as T);
}

        void ICollection<DataPoint>.CopyTo(DataPoint[]array,int arrayIndex)
        {
        throw new UnsupportedOperationException();
}

        boolean ICollection<DataPoint>.REMOVE(DataPoint item)
        {
        return this.REMOVE(item as T);
}

        IEnumerator<DataPoint>Iterable<DataPoint>.GetEnumerator()
        {
        foreach(DataPoint point in this)
        {
        yield return point;
}
        }

        IEnumerator Iterable.GetEnumerator()
        {
        foreach(DataPoint point in this)
        {
        yield return point;
}
        }
        */
}

