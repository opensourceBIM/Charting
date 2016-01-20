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

public class DefaultColorPicker implements ColorPicker {

	public DefaultColorPicker() {
	}

	public void colorize(Layer[] layers) {
		// find the largest layer to use as a normalizer
		float maxSum = 0;
		for (int i = 0; i < layers.length; i++) {
			maxSum = (float) Math.max(maxSum, layers[i].sum);
		}

		// find the color for each layer
		for (int i = 0; i < layers.length; i++) {
			float normalizedOnset = (float) layers[i].onset / layers[i].size.length;
			float normalizedSum = layers[i].sum / maxSum;
			float shapedSum = (float) (1.0 - Math.sqrt(normalizedSum));

			layers[i].rgb = get(normalizedOnset, shapedSum);
		}
	}

	protected int get(float g1, float g2) {
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}
