package org.bimserver.charting.Algorithms.StreamGraph;

/******************************************************************************
 * Copyright (C) 2009-2016  BIMserver.org
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

import java.util.Arrays;

/**
 * LateOnsetSort
 * Sorts by onset, and orders to the outsides of the graph.
 *
 * This is the sort technique preferred when using late-onset data, which the
 * Streamgraph technique is best suited to represent
 *
 * @author Lee Byron
 * @author Martin Wattenberg
 */
public class LateOnsetSort extends LayerSort {

	public String getName() {
		return "Late Onset Sorting, Evenly Weighted";
	}

	public Layer[] sort(Layer[] layers) {
		// first sort by onset
		Arrays.sort(layers, new OnsetComparator(true));

		return orderToOutside(layers);
	}
}
