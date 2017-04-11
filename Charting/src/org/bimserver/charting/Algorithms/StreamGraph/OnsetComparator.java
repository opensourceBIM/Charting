package org.bimserver.charting.Algorithms.StreamGraph;

/******************************************************************************
 * Copyright (C) 2009-2017  BIMserver.org
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see {@literal<http://www.gnu.org/licenses/>}.
 *****************************************************************************/

import java.util.Comparator;

/**
 * OnsetSort
 * Compares two Layers based on their onset
 *
 * @author Lee Byron
 * @author Martin Wattenberg
 */
public class OnsetComparator implements Comparator {

	public boolean ascending;

	public OnsetComparator(boolean ascending) {
		this.ascending = ascending;
	}

	public int compare(Object p, Object q) {
		Layer pL = (Layer) p;
		Layer qL = (Layer) q;
		return (ascending ? 1 : -1) * (pL.onset - qL.onset);
	}

	public boolean equals(Object p, Object q) {
		Layer pL = (Layer) p;
		Layer qL = (Layer) q;
		return pL.onset == qL.onset;
	}
}
