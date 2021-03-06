package org.bimserver.charting.Testing;

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
import java.util.LinkedHashMap;
import java.util.Random;

import org.bimserver.charting.Charts.Delaunay;

public class TestDelaunay extends BaseChartTest {

	public static void main(String[] args) {
		//
		getRandomData(rawData);
		//
		Delaunay chart = new Delaunay();
		chart.setDimensionLookupKey("x", "A");
		chart.setDimensionLookupKey("y", "B");
		chart.setOption("Width", 698);
		chart.setOption("Height", 500);
		// Save chart.
		chart.saveToSVGInUserDirectory(rawData, "test.svg");
		// Print data.
		System.out.println(chart.getRawTextDataSet(rawData));
	}

	public static void getRandomData(ArrayList<LinkedHashMap<String, Object>> rawData) {
		int n = 12;
		final Random r = new Random();
		while (n > 0) {
			rawData.add(new LinkedHashMap<String, Object>() {
				{
					put("A", r.nextInt(100));
					put("B", r.nextInt(100));
				}
			});
			n--;
		}
	}
}
