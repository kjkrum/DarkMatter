package com.chalcodes.weaponm.test;

import com.chalcodes.weaponm.RotatingIteratorSet;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
public class RotatingIteratorSetTest {

	@Test
	public void performance() {
		final Set<Integer> standard = new HashSet<>();
		final Set<Integer> rotating = new RotatingIteratorSet<>();
		for(int i = 0; i < 10; ++i) {
			standard.add(i);
			rotating.add(i);
		}

		final long start1 = System.currentTimeMillis();

		for(int i = 0; i < 10000000; ++i) {
			for(int e : standard) {}
		}

		final long start2 = System.currentTimeMillis();

		for(int i = 0; i < 10000000; ++i) {
			for(int e : rotating) {}
		}

		final long finish = System.currentTimeMillis();

		System.out.println("standard: " + (start2 - start1) + " ms");
		System.out.println("rotating: " + (finish - start2) + " ms");
		System.out.println("difference: " + (float) (finish - start2) / (start2 - start1));
	}

	@Test
	public void output() {
		final Set<Integer> set = new RotatingIteratorSet<>();
		for(int i = 0; i < 10; ++i) {
			set.add(i);
		}
		for(int i = 0; i < 11; ++i) {
			for(int e : set) {
				System.out.print(e);
			}
			System.out.println();
		}
	}

}