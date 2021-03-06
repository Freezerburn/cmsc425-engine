package com.artemis.utils;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * Collection type a bit like ArrayList but does not preserve the order of its
 * entities, speedwise it is very good, especially suited for games.
 */

public class Bag<E> implements ImmutableBag<E> {
	private E[] data;
	private int size = 0;

	/**
	 * Constructs an empty Bag with an initial capacity of 64.
	 * 
	 */
	public Bag() {
		this(64);
	}

	/**
	 * Constructs an empty Bag with the specified initial capacity.
	 * 
	 * @param capacity
	 *            the initial capacity of Bag
	 */
	@SuppressWarnings("unchecked")
	public Bag(int capacity) {
		data = (E[])new Object[capacity];
	}

	/**
	 * Removes the element at the specified position in this Bag. does this by
	 * overwriting it was last element then removing last element
	 * 
	 * @param index
	 *            the index of element to be removed
	 * @return element that was removed from the Bag
	 */
	public E remove(int index) {
		E e = data[index]; // make copy of element to remove so it can be returned
		data[index] = data[--size]; // overwrite item to remove with last element
		data[size] = null; // null last element, so gc can do its work
		return e;
	}
	
	
	/**
	 * Remove and return the last object in the bag.
	 * 
	 * @return the last object in the bag, null if empty.
	 */
	public E removeLast() {
		if(size > 0) {
			E e = data[--size];
			data[size] = null;
			return e;
		}
		
		return null;
	}

	/**
	 * Removes the first occurrence of the specified element from this Bag, if
	 * it is present. If the Bag does not contain the element, it is unchanged.
	 * does this by overwriting it was last element then removing last element
	 * 
	 * @param e
	 *            element to be removed from this list, if present
	 * @return <tt>true</tt> if this list contained the specified element
	 */
	public boolean remove(E e) {
		for (int i = 0; i < size; i++) {
			E e2 = data[i];
            /*
            The equals method is avoided here due to the possibility of finding an item that
            an overridden equals method declares as equal, but in actuality is not the item
            the user wants to remove.
             */
			if (e == e2) {
				data[i] = data[--size]; // overwrite item to remove with last element
				data[size] = null;
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Check if bag contains this element.
	 * 
	 * @param e
	 * @return
	 */
	public boolean contains(E e) {
		for(int i = 0; size > i; i++) {
			if(e == data[i]) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes from this Bag all of its elements that are contained in the
	 * specified Bag.
	 * 
	 * @param bag
	 *            Bag containing elements to be removed from this Bag
	 * @return {@code true} if this Bag changed as a result of the call
	 */
	public boolean removeAll(ImmutableBag<E> bag) {
		boolean modified = false;
		for (E e1 : bag) {
            modified = remove(e1);
		}

		return modified;
	}

	/**
	 * Returns the element at the specified position in Bag.
	 * 
	 * @param index
	 *            index of the element to return
	 * @return the element at the specified position in bag
	 */
	public E get(int index) {
		return data[index];
	}

	/**
	 * Returns the number of elements in this bag.
	 * 
	 * @return the number of elements in this bag
	 */
	public int size() {
		return size;
	}
	
	/**
	 * Returns the number of elements the bag can hold without growing.
	 * 
	 * @return the number of elements the bag can hold without growing.
	 */
	public int getCapacity() {
		return data.length;
	}
	
	/**
	 * Checks if the internal storage supports this index.
	 * 
	 * @param index
	 * @return
	 */
	public boolean isIndexWithinBounds(int index) {
		return index < getCapacity();
	}

	/**
	 * Returns true if this list contains no elements.
	 * 
	 * @return true if this list contains no elements
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Adds the specified element to the end of this bag. if needed also
	 * increases the capacity of the bag.
	 * 
	 * @param e element to be added to this list
	 */
	public void add(E e) {
		// is size greater than capacity increase capacity
		if (size == data.length) {
			grow();
		}

		data[size++] = e;
	}

	/**
	 * Set element at specified index in the bag.
	 * 
	 * @param index position of element
	 * @param e the element
	 */
	public void set(int index, E e) {
		if(index >= data.length) {
			grow(index*2);
		}
        if(index + 1 > size) {
            size = index+1;
        }
		data[index] = e;
	}

	private void grow() {
		int newCapacity = (data.length * 3) / 2 + 1;
		grow(newCapacity);
	}
	
	@SuppressWarnings("unchecked")
	private void grow(int newCapacity) {
		E[] oldData = data;
		data = (E[])new Object[newCapacity];
		System.arraycopy(oldData, 0, data, 0, oldData.length);
	}
	
	public void ensureCapacity(int index) {
		if(index >= data.length) {
			grow(index*2);
		}
	}

	/**
	 * Removes all of the elements from this bag. The bag will be empty after
	 * this call returns.
	 */
	public void clear() {
		// null all elements so gc can clean up
        Arrays.fill(data, null);
		size = 0;
	}

	/**
	 * Add all items into this bag. 
	 * @param items
	 */
	public void addAll(ImmutableBag<E> items) {
		for(E item : items) {
			add(item);
		}
	}

    /**
     * Iterate over each (non-null) item in the ImmutableBag.
     * @return Iterator over each item in the ImmutableBag
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int itSize = size;
            int cur = 0;

            @Override
            public boolean hasNext() {
                return cur < itSize;
            }

            @Override
            public E next() {
                if(hasNext()) {
                    E ret = null;
                    while((ret = data[cur++]) == null && cur < itSize) {
                        /*
                        Concurrent modification checking done inside the loop as it's possible
                        for this Bag to be changed at any time, not just at the beginning of the
                        next method.
                         */
                        if(itSize != size) {
                            throw new ConcurrentModificationException("Bag size changed");
                        }
                    }
                    return ret;
                }
                return null;
            }
        };
    }
}
