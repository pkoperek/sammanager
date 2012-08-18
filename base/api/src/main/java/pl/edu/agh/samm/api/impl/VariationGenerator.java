/**
 * This file is part of SAMM.
 *
 * SAMM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SAMM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SAMM.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.agh.samm.api.impl;

import java.util.Arrays;

public class VariationGenerator {
	private int[] sizes = null;
	private int[] current = null;

	public VariationGenerator(int[] sizes) {
		this.sizes = sizes;
		this.current = new int[sizes.length];

		// clean
		for (int i = 0; i < current.length; i++) {
			current[i] = 0;
		}
	}

	public boolean hasNext() {
		return current[0] < sizes[0];
	}

	public int[] getNext() {
		int[] toReturn = Arrays.copyOf(current, current.length);

		// increment last column
		current[current.length - 1]++;

		// move numbers if necessary
		for (int i = current.length - 1; i > 0; i--) {
			if (current[i] >= sizes[i]) {
				current[i] = 0;
				current[i - 1]++;
			}
		}

		return toReturn;
	}

	public static void main(String[] args) {
		VariationGenerator generator = new VariationGenerator(new int[] { 3, 3 });

		while (generator.hasNext()) {
			System.out.println(Arrays.toString(generator.getNext()));
		}
	}
}
