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

import java.util.ArrayList;

import org.bimserver.geometry.Vector2d;

/**
 * Layer
 * Represents a layer in a layered graph, maintaining properties which
 * define it's position, size, color and mathematical characteristics
 *
 * @author Lee Byron
 * @author Martin Wattenberg
 */
public class Layer {

	public String name;
	public float[] size;
	public float[] yBottom;
	public float[] yTop;
	public int rgb;
	public int onset;
	public int end;
	public float sum;
	public float volatility;
	//
	public Integer pointOfMax = null;
	public Double maxValue = null;
	//
	public ArrayList<Vector2d> points = new ArrayList<>();

	public Layer(String name, float[] size) {

		// check for reasonable data
		for (int i = 0; i < size.length; i++) {
			if (size[i] < 0) {
				throw new IllegalArgumentException("No negative sizes allowed.");
			}
		}

		this.name = name;
		this.size = size;
		yBottom = new float[size.length];
		yTop = new float[size.length];
		sum = 0;
		volatility = 0;
		onset = -1;

		for (int i = 0; i < size.length; i++) {
			float thisValue = size[i];
			Float previousValue = (i > 0) ? size[i - 1] : null;
			// sum is the summation of all points
			sum += thisValue;
			// onset is the first non-zero point
			// end is the last non-zero point
			if (thisValue > 0) {
				if (onset == -1)
					onset = i;
				else
					end = i;
			}
			// Pick up first zero after non-zero data set; treat it as significant.
			else if (i > 0 && previousValue > 0)
				end = i;
			// Do max pass.
			if (pointOfMax == null || maxValue < thisValue) {
				pointOfMax = i;
				maxValue = (double)thisValue;
			}
			// volatility is the maximum change between any two consecutive points
			if (i > 0)
				volatility = Math.max(volatility, Math.abs(thisValue - previousValue));
		}
	}
}
