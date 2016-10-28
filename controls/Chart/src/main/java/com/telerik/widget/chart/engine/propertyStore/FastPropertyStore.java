package com.telerik.widget.chart.engine.propertyStore;

/**
 * This class contains property value entries so that properties can be shared between classes.
 */
public class FastPropertyStore {

    private Entry[] entries;

    /**
     * Creates a new instance of the property store.
     */
    public FastPropertyStore() {
    }

    /**
     * Clear the property entries.
     */
    public void clear() {
        this.entries = null;
    }

    /**
     * Checks if the store contains an entry for the given property key.
     *
     * @param key The property key.
     * @return Returns <code>true</code> if the store contains an entry for this key and <code>false</code> otherwise.
     */
    public boolean containsEntry(int key) {
        return this.getEntry(key) != null;
    }

    /**
     * Gets a property value entry for the given key.
     *
     * @param key The key for which to get a value.
     * @return Returns a property value for the given key if it exists. Otherwise <code>null</code> is returned.
     */
    public Object getEntry(int key) {

        PropertyKeyValue splitKeyValue = this.splitKey(key);
        short element = splitKeyValue.Element;
        short entryKey = splitKeyValue.EntryKey;

        ValueExtractor<Integer> extractor = new ValueExtractor<Integer>();
        boolean found = this.locateObject(entryKey, extractor);
        int objectIndex = extractor.value;
        if (!found) {
            return null;
        }

        if (((1 << element) & this.entries[objectIndex].Mask) == 0) {
            return null;
        }

        switch (element) {
            case 0:
                return this.entries[objectIndex].Val1;

            case 1:
                return this.entries[objectIndex].Val2;

            case 2:
                return this.entries[objectIndex].Val3;

            case 3:
                return this.entries[objectIndex].Val4;
        }

        return null;
    }

    /**
     * Sets a property value for the given property key.
     *
     * @param key   The property key.
     * @param value The property value.
     */
    public void setEntry(int key, Object value) {

        PropertyKeyValue splitKeyValue = this.splitKey(key);
        short element = splitKeyValue.Element;
        short entryKey = splitKeyValue.EntryKey;

        ValueExtractor<Integer> extractor = new ValueExtractor<Integer>();
        boolean found = this.locateObject(entryKey, extractor);

        int objectIndex = extractor.value;
        if (!found) {
            if (this.entries == null) {
                this.entries = new Entry[1];
                this.entries[0] = new Entry();
            } else {
                Entry[] destinationArray = new Entry[this.entries.length + 1];
                for (int i = 0; i < destinationArray.length; ++i) {
                    destinationArray[i] = new Entry();
                }

                if (objectIndex > 0) {
                    System.arraycopy(this.entries, 0, destinationArray, 0, objectIndex);
                }
                if ((this.entries.length - objectIndex) > 0) {
                    System.arraycopy(this.entries, objectIndex, destinationArray, objectIndex + 1, this.entries.length - objectIndex);
                }
                this.entries = destinationArray;
            }

            this.entries[objectIndex].Key = entryKey;
        }

        switch (element) {
            case 0:
                this.entries[objectIndex].Val1 = value;
                break;

            case 1:
                this.entries[objectIndex].Val2 = value;
                break;

            case 2:
                this.entries[objectIndex].Val3 = value;
                break;

            case 3:
                this.entries[objectIndex].Val4 = value;
                break;
        }

        this.entries[objectIndex].Mask = (short) (((int) this.entries[objectIndex].Mask) | (1 << element));
    }

    /**
     * Removes the value entry for the given key.
     *
     * @param key The key for which to remove a value entry.
     */
    public void removeEntry(int key) {
        PropertyKeyValue splitKeyValue = this.splitKey(key);
        short entryKey = splitKeyValue.EntryKey;
        short element = splitKeyValue.Element;

        int objectIndex = -1;
        ValueExtractor extractor = new ValueExtractor();
        boolean hasObject = this.locateObject(entryKey, extractor);

        if (hasObject) {
            objectIndex = (Integer) extractor.value;
            boolean hasMask = ((1 << element) & this.entries[objectIndex].Mask) != 0;
            hasObject = hasObject && hasMask;
        }

        if (!hasObject) {
            return;
        }

        short mask = this.entries[objectIndex].Mask;
        mask = (short) (mask & ~((short) (1 << element)));
        this.entries[objectIndex].Mask = mask;

        if (mask == 0) {
            int length = this.entries.length;
            if (length == 1) {
                this.entries = null;
            } else {
                Entry[] destArray = new Entry[length - 1];
                if (objectIndex > 0) {
                    System.arraycopy(this.entries, 0, destArray, 0, objectIndex);
                }
                if (objectIndex < length) {
                    System.arraycopy(this.entries, objectIndex + 1, destArray, objectIndex, (length - objectIndex) - 1);
                }
                this.entries = destArray;
            }
        } else {
            switch (element) {
                case 0:
                    this.entries[objectIndex].Val1 = null;
                    return;

                case 1:
                    this.entries[objectIndex].Val2 = null;
                    return;

                case 2:
                    this.entries[objectIndex].Val3 = null;
                    return;

                case 3:
                    this.entries[objectIndex].Val4 = null;
            }
        }
    }

    private PropertyKeyValue splitKey(int key) {
        short element = (short) (key & 3);
        short entryKey = (short) (key & 0xfffffffc);
        return new PropertyKeyValue(entryKey, element);
    }

    private boolean locateObject(short entryKey, ValueExtractor<Integer> resultIndex) {
        resultIndex.value = 0;

        if (this.entries == null) {
            return false;
        }

        int middle;
        int length = this.entries.length;

        if (length <= 16) {
            middle = length >> 1; // / 2
            if (this.entries[middle].Key <= entryKey) {
                resultIndex.value = middle;
                if (this.entries[resultIndex.value].Key == entryKey) {
                    return true;
                }
            }

            middle = (length + 1) >> 2; // / 4
            if (this.entries[resultIndex.value + middle].Key <= entryKey) {
                resultIndex.value += middle;
                if (this.entries[resultIndex.value].Key == entryKey) {
                    return true;
                }
            }

            middle = (length + 3) >> 3; // / 8
            if (this.entries[resultIndex.value + middle].Key <= entryKey) {
                resultIndex.value += middle;
                if (this.entries[resultIndex.value].Key == entryKey) {
                    return true;
                }
            }

            middle = (length + 7) >> 4; // / 16
            if (this.entries[resultIndex.value + middle].Key <= entryKey) {
                resultIndex.value += middle;
                if (this.entries[resultIndex.value].Key == entryKey) {
                    return true;
                }
            }

            if (entryKey > this.entries[resultIndex.value].Key) {
                resultIndex.value++;
            }

            return false;
        }

        int left = 0;
        int right = length - 1;
        middle = 0;
        short key;

        do {
            middle = (right + left) >> 1;
            key = this.entries[middle].Key;

            if (key == entryKey) {
                resultIndex.value = middle;
                return true;
            }

            if (entryKey < key) {
                right = middle - 1;
            } else {
                left = middle + 1;
            }
        }
        while (right >= left);

        resultIndex.value = middle;
        if (entryKey > this.entries[resultIndex.value].Key) {
            resultIndex.value++;
        }

        return false;
    }

    class Entry {

        short Key;
        short Mask;
        Object Val1;
        Object Val2;
        Object Val3;
        Object Val4;

        public Object getVal(int valIndex) {
            Object val = null;
            switch (valIndex) {
                case 1:
                    val = this.Val1;
                    break;
                case 2:
                    val = this.Val2;
                    break;
                case 3:
                    val = this.Val3;
                    break;
                case 4:
                    val = this.Val4;
                    break;
            }

            return val;
        }
    }
}

class PropertyKeyValue {
    public short EntryKey;
    public short Element;

    public PropertyKeyValue(short entryKey, short element) {
        EntryKey = entryKey;
        Element = element;
    }
}
