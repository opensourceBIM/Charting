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

public class MinimizedWiggleLayout extends LayerLayout {

	public String getName() {
		return "Minimized Wiggle Layout";
	}

	public void layout(Layer[] layers) {
		int n = layers[0].size.length;
		int m = layers.length;
		float[] baseline = new float[n];

		// Set shape of baseline values.
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				baseline[i] += (m - j - 0.5) * layers[j].size[i];
			}
			baseline[i] /= m;
		}

		// Put layers on top of the baseline.
		stackOnBaseline(layers, baseline);
	}
}
