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

import java.util.Arrays;

/**
 * StackLayout
 * Standard stacked graph layout, with a straight baseline
 *
 * @author Lee Byron
 * @author Martin Wattenberg
 */
public class StackLayout extends LayerLayout {

	public String getName() {
		return "Stacked Layout";
	}

	public void layout(Layer[] layers) {
		int n = 0;
		if (layers.length > 0)
			n = layers[0].size.length;
		// Lay out layers, top to bottom.
		float[] baseline = new float[n];
		Arrays.fill(baseline, 0);
		// Put layers on top of the baseline.
		stackOnBaseline(layers, baseline);
	}
}
