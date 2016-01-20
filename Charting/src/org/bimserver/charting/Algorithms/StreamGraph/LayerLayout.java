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

public abstract class LayerLayout {

	public abstract void layout(Layer[] layers);

	abstract String getName();

	/**
	 * We define our stacked graphs by layers atop a baseline.
	 * This method does the work of assigning the positions of each layer in an
	 * ordered array of layers based on an initial baseline.
	 */
	protected void stackOnBaseline(Layer[] layers, float[] baseline) {
		// Put layers on top of the baseline.
		for (int i = 0; i < layers.length; i++) {
			System.arraycopy(baseline, 0, layers[i].yBottom, 0, baseline.length);
			for (int j = 0; j < baseline.length; j++) {
				baseline[j] -= layers[i].size[j];
			}
			System.arraycopy(baseline, 0, layers[i].yTop, 0, baseline.length);
		}
	}
}
