package com.chalcodes.weaponm;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * A set with semi-predictable iteration order, in which elements are rotated
 * by one index each time the set is iterated.  Note that it is not specified
 * what order the elements are iterated in, only that the order remains stable
 * (aside from rotation) between modifications to the set.  A new, arbitrary
 * order may be established after each modification.
 *
 * @author Kevin Krumwiede
 */
public class RotatingIteratorSet<E> implements Set<E> {
	private final Set<E> mSet;
	private final List<E> mList;

	public RotatingIteratorSet() {
		mSet = new HashSet<>();
		mList = new ArrayList<>();
	}

	public RotatingIteratorSet(@Nonnull final Collection<E> elements) {
		mSet = new HashSet<>(elements);
		mList = new ArrayList<>(mSet);
	}

	/* The magic. */
	private int mNextIteratorStart = 0;

	private int nextIteratorStart() {
		mNextIteratorStart %= size();
		return mNextIteratorStart++;
	}

	/* We don't need to worry about concurrent modification because of what
	 * this class is used for.  Receiver sets in SimpleEventBus are copy on
	 * write, and their iterators do not need to support remove(). */

	@Override
	@Nonnull
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private final int mStart = nextIteratorStart();
			private int mReturned = 0;

			@Override
			public boolean hasNext() {
				return mReturned < size();
			}

			@Override
			public E next() {
				if(hasNext()) {
					return mList.get((mStart + mReturned++) % size());
				}
				else {
					throw new NoSuchElementException();
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/* Interesting delegate methods. */

	@Override
	public void clear() {
		mSet.clear();
		mList.clear();
		mNextIteratorStart = 0;
	}

	@Override
	public boolean add(final E e) {
		final boolean modified = mSet.add(e);
		if(modified) {
			mList.add(e);
			mNextIteratorStart = 0;
		}
		return modified;
	}

	@Override
	public boolean addAll(@Nonnull final Collection<? extends E> c) {
		final boolean modified = mSet.addAll(c);
		if(modified) {
			updateList();
		}
		return modified;
	}

	@Override
	public boolean retainAll(@Nonnull final Collection<?> c) {
		final boolean modified = mSet.retainAll(c);
		if(modified) {
			updateList();
		}
		return modified;
	}

	@Override
	public boolean remove(final Object o) {
		final boolean modified = mSet.remove(o);
		if(modified) {
			updateList();
		}
		return modified;
	}

	@Override
	public boolean removeAll(@Nonnull final Collection<?> c) {
		final boolean modified = mSet.removeAll(c);
		if(modified) {
			updateList();
		}
		return modified;
	}

	private void updateList() {
		mList.clear();
		mList.addAll(mSet);
		mNextIteratorStart = 0;
	}

	/* Boring delegate methods. */

	@Override
	public int size() {
		return mSet.size();
	}

	@Override
	public boolean isEmpty() {
		return mSet.isEmpty();
	}

	@Override
	public boolean contains(final Object o) {
		return mSet.contains(o);
	}

	@Override
	public boolean containsAll(@Nonnull final Collection<?> c) {
		return mSet.containsAll(c);
	}

	@Override
	@Nonnull
	public Object[] toArray() {
		return mSet.toArray();
	}

	@Override
	@Nonnull
	public <T> T[] toArray(@Nonnull final T[] a) {
		//noinspection SuspiciousToArrayCall
		return mSet.toArray(a);
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(final Object o) {
		return mSet.equals(o);
	}

	@Override
	public int hashCode() {
		return mSet.hashCode();
	}
}
