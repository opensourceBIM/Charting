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

public class StreamLayout extends LayerLayout {

	public String getName() {
		return "Original Streamgraph Layout";
	}

	public void layout(Layer[] layers) {
		int n = layers[0].size.length;
		int m = layers.length;
		float[] baseline = new float[n];
		float[] center = new float[n];
		float totalSize;
		float moveUp;
		float increase;
		float belowSize;
		// Set shape of baseline values.
		for (int i = 0; i < n; i++) {
			// The 'center' is a rolling point. It is initialized as the previous iteration's center value.
			center[i] = (i == 0) ? 0 : center[i - 1];
			// Find the total size of all layers at this point.
			totalSize = 0;
			for (int j = 0; j < m; j++)
				totalSize += layers[j].size[i];
			// Account for the change of every layer to offset the center point.
			for (int j = 0; j < m; j++) {
				if (i == 0) {
					increase = layers[j].size[i];
					moveUp = 0.5f;
				} else {
					belowSize = 0.5f * layers[j].size[i];
					for (int k = j + 1; k < m; k++)
						belowSize += layers[k].size[i];
					increase = layers[j].size[i] - layers[j].size[i - 1];
					moveUp = totalSize == 0 ? 0 : (belowSize / totalSize);
				}
				center[i] += (moveUp - 0.5) * increase;
			}
			// Set baseline to the bottom edge according to the center line.
			baseline[i] = center[i] + 0.5f * totalSize;
		}
		// Put layers on top of the baseline.
		stackOnBaseline(layers, baseline);
	}
}
