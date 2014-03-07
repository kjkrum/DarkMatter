package com.chalcodes.weaponm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A list with iterators that behave as though the elements of the list were
 * left-shifted by one index each time a new iterator is created.  The idea is
 * that some collections of event listeners should give each of their elements
 * a fair chance to be the first to handle an event, and shifting them is
 * probably more reliable and less computationally expensive than randomizing
 * them.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class ShiftingIteratorList<T> extends ArrayList<T> {
	private static final long serialVersionUID = 1L;
	// shift value for the next iterator
	private int nextShift = 0;

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			/** effective shift for this iterator */
			final int shift;
			/** how many elements have been returned */
			int count = 0;
			boolean removable = false;

			{
				shift = nextShift;
				if(size() > 0) {
					++nextShift;
					nextShift %= size();
				}
			}

			@Override
			public boolean hasNext() {
				return count < size();
			}

			@Override
			public T next() {
				if(!hasNext()) {
					throw new NoSuchElementException();
				}
				removable = true;
				return get((shift + count++) % size());
			}

			@Override
			public void remove() {
				if(!removable) {
					throw new IllegalStateException();
				}
				removable = false;
				--count;
				ShiftingIteratorList.this.remove((shift + count) % size());				
			}
		};
	}
	
//	public static void main(String[] args) {
//		ShiftingIteratorList<String> list = new ShiftingIteratorList<String>();
//		list.add("one");
//		list.add("two");
//		list.add("three");
//		list.add("four");
//		
////		Iterator<String> iter = list.iterator();
////		while(iter.hasNext()) {
////			if(iter.next().equals("two")) {			
////				iter.remove();
////			}
////		}
//		
//		for(int i = 0; i < list.size(); ++i) {
//			for(String item : list) {
//				System.out.print(item);
//				System.out.print(' ');
//			}
//			System.out.println();
//		}
//	}
}
