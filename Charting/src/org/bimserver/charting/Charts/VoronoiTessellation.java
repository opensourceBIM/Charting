package org.bimserver.charting.Charts;

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
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;

import org.bimserver.charting.ColorScales.HSLColorScale;
import org.bimserver.charting.Containers.ChartExtent;
import org.bimserver.charting.Containers.ChartOption;
import org.bimserver.charting.Containers.ChartRow;
import org.bimserver.charting.Containers.ChartRows;
import org.bimserver.charting.Containers.ElementLike;
import org.bimserver.charting.Containers.GroupedChartExtents;
import org.bimserver.charting.Dimensions.ModelDimension;
import org.bimserver.charting.Models.PointsModel;
import org.bimserver.geometry.Vector2d;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.triangulate.VoronoiDiagramBuilder;

public class VoronoiTessellation extends Chart {

	public VoronoiTessellation() {
		this("Voronoi Tessellation");
	}

	@SuppressWarnings("serial")
	public VoronoiTessellation(String title) {
		this(
			title,
			"It creates the minimum area around each point defined by two variables. When applied to a scatterplot, it is useful to show the distance between points. <br/>Based on <a href='http://bl.ocks.org/mbostock/4060366'>http://bl.ocks.org/mbostock/4060366</a>",
			"Distributions",
			new ArrayList<ChartOption> () {{
				add(new ChartOption("Width", "Horizontal dimension.", 1000));
				add(new ChartOption("Height", "Vertical dimension.", 500));
				// Any valid GroupedChartExtents<String>, including HSLColorScale and LinearColorScale.
				add(new ChartOption("Color Scale", "Scale of the color.", new HSLColorScale()));
				add(new ChartOption("Show Points", "Show points explicitly.", true));
				add(new ChartOption("Voronoi Tolerance", "A tolerance towards the inclusion of other points; zero means all points will be placed in completely separate polygons.", 0));
			}},
			new PointsModel(Arrays.asList(new String[] {"x", "y", "color"})),
			true
		);
	}

	/**
	 * @param title
	 * @param description
	 * @param category
	 * @param options
	 * @param model
	 * @param fitToWidth
	 */
	public VoronoiTessellation(String title, String description, String category, ArrayList<ChartOption> options, org.bimserver.charting.Models.Model model, boolean fitToWidth) {
		super(title, description, category, options, model, fitToWidth);
	}

	@Override
	public StringBuilder writeSVGChartSpecificPayload(StringBuilder builder, ChartRows filteredData) {
		// Get "x" and "y" dimensions.
		ModelDimension x = Model.getDimensionByKey("x");
		ModelDimension y = Model.getDimensionByKey("y");
		// Get optional dimension.
		ModelDimension color = Model.getDimensionByKey("color");
		// Get the width and height options.
		double width = (hasOption("Width")) ? (int)getOptionValue("Width") : 1000;
		double height = (hasOption("Height")) ? (int)getOptionValue("Height") : 500;
		// Show small points in addition to data visualizations.
		boolean showPoints = (hasOption("Show Points")) ? (boolean)getOptionValue("Show Points") : false;
		double voronoiTolerance = (hasOption("Voronoi Tolerance")) ? ((Number)getOptionValue("Voronoi Tolerance")).doubleValue() : 0.0;
		// Get objects that represent the lowest and the highest values in a dimension of the data as a whole, including the context they exist in (i.e. some portion of the SVG's size).
		ChartExtent xExtent = Model.getExtentFromDimensionGivenKey("x", false, filteredData, 0, width, 11);
		// densitydesign/raw reverses this, going from upper to lower rather than from lower to upper. WARN: Queries need to specify the values are reversed.
		ChartExtent yExtent = Model.getExtentFromDimensionGivenKey("y", false, filteredData, 0, height, 10);
		// Color extent. Transforms data to be in range of 0 to 1. Put these values through a color scale.
		ChartExtent colorExtent = Model.getExtentFromDimensionGivenKey("color", false, filteredData, 0, 1);
		// Get color scale.
		@SuppressWarnings("unchecked")
		GroupedChartExtents<String> colorScale = (GroupedChartExtents<String>)getOptionValue("Color Scale");
		//
		LinkedHashMap<Coordinate, String> coordinateColors = new LinkedHashMap<>();
		// Make list of points.
		ArrayList<Coordinate> list = new ArrayList<>();
		ElementLike circlesGroup = new ElementLike("g");
		for (ChartRow row : filteredData) {
			ArrayList<Object> xValues = row.get(x);
			ArrayList<Object> yValues = row.get(y);
			ArrayList<Object> colorValues = row.get(color);
			// Sizes.
			int xValuesSize = xValues.size();
			int yValuesSize = yValues.size();
			int colorValuesSize = colorValues.size();
			// Determine how many points can be made out of the data by picking the largest dimension. Other data in the row will be cycled through if enough data is not explicitly available.
			int pointCount = Math.min(xValuesSize, yValuesSize);
			// Effectively, zip the data together.
			for (int i = 0; i < pointCount; i++) {
				// Coerce X value.
				Object xValue = xValues.get(i % xValuesSize);
				double coercedXValue = 0;
				if (xValue instanceof Number)
					coercedXValue = ((Number)xValue).doubleValue();
				else if (xValue instanceof Date)
					coercedXValue = ((Date)xValue).getTime();
				// Coerce Y value.
				Object yValue = yValues.get(i % yValuesSize);
				double coercedYValue = 0;
				if (yValue instanceof Number)
					coercedYValue = ((Number)yValue).doubleValue();
				else if (yValue instanceof Date)
					coercedYValue = ((Date)yValue).getTime();
				// Get actual values.
				Vector2d thisPoint = new Vector2d(coercedXValue, coercedYValue);
				// Convert the actual values into world-space coordinates.
				Coordinate coordinate = new Coordinate(
					xExtent.getLinearWorldSpaceValueAtXGivenActualValue(thisPoint.getX()),
					yExtent.getLinearWorldSpaceValueAtXGivenActualValue(thisPoint.getY(), true)
				);
				list.add(coordinate);
				// OPTIONAL: Color.
				double thisRawColorValue = 0;
				double thisWorldSpaceColorValue = 0;
				String colorString = null;
				if (colorValuesSize > 0) {
					thisRawColorValue = ((Number)colorValues.get(i % colorValuesSize)).doubleValue();
					thisWorldSpaceColorValue = colorExtent.getLinearWorldSpaceValueAtXGivenActualValue(thisRawColorValue);
					colorString = colorScale.getModulatedLinearWorldSpaceValueAtXGivenActualValue(thisWorldSpaceColorValue, false);
				} else
					colorString = colorScale.getModulatedLinearWorldSpaceValueAtXGivenActualValue(0, false);
				coordinateColors.put(coordinate, colorString);
				// Create a circle to represent the point.
				if (showPoints) {
					ElementLike circle = new ElementLike("circle");
					circle.attribute("r", String.format("%s", new Double(1.5)));
					circle.attribute("transform", String.format("translate(%s, %s)", new Double(coordinate.x), new Double(coordinate.y)));
					circle.attribute("style", String.format("fill: %s; pointer-events: none;", "black"));
					// Reparent.
					circlesGroup.child(circle);
				}
			}
		}
		// Move points to data structure as JTS will expect them.
		CoordinateList coordinateList = new CoordinateList(list.toArray(new Coordinate[list.size()]), false);
		// Configure diagram builder.
		VoronoiDiagramBuilder diagramBuilder = new VoronoiDiagramBuilder();
		diagramBuilder.setSites(coordinateList);
		diagramBuilder.setTolerance(voronoiTolerance);
		diagramBuilder.setClipEnvelope(new Envelope(xExtent.WorldSpaceStart, xExtent.WorldSpaceEnd, yExtent.WorldSpaceStart, yExtent.WorldSpaceEnd));
		// Build the Voronoi diagram.
		GeometryFactory geometryFactory = new GeometryFactory();
		Geometry geometry = diagramBuilder.getDiagram(geometryFactory);
		// Prepare a place to put the paths, representing polygons.
		ElementLike group = new ElementLike("g");
		// Iterate the Voronoi diagram, retrieving polygons.
		for (int n = 0; n < geometry.getNumGeometries(); n++) {
			// Get a polygon representing a Voronoi section.
			Geometry thisPolygon = geometry.getGeometryN(n);
			// Get the color of the point closest to the centroid. NOTE: This is because Coordinate objects are not maintained through the diagraming process; this diverges from densitydesign/raw because a different triangulation library is used.
			String colorString = findClosestColor(coordinateColors, thisPolygon);
			// Add path.
			ElementLike path = new ElementLike("path");
			path.attribute("style", String.format("fill: %s; stroke: #fff;", colorString));
			path.d(thisPolygon, true, true);
			// Reparent.
			group.child(path);
		}
		// Write it out.
		builder.append(group.buildString(1));
		// Add points as circles.
		if (showPoints && circlesGroup.Children.size() > 0)
			builder.append(circlesGroup.buildString(1));
		// Send it back.
		return builder;
	}

	/**
	 * Get the color of the point closest to the centroid.
	 * @param coordinateColors
	 * @param thisPolygon
	 * @return
	 */
	private static String findClosestColor(LinkedHashMap<Coordinate, String> coordinateColors, Geometry thisPolygon) {
		String colorString = "#ddd";
		Coordinate centroid = thisPolygon.getCentroid().getCoordinate();
		Double shortestDistance = null; 
		Coordinate closestCoordinate = null;
		for (Coordinate coordinate : coordinateColors.keySet()) {
			double thisDistance = centroid.distance(coordinate);
			if (shortestDistance == null || thisDistance < shortestDistance) {
				shortestDistance = thisDistance;
				closestCoordinate = coordinate;
			}
		}
		if (closestCoordinate != null)
			colorString = coordinateColors.get(closestCoordinate);
		return colorString;
	}
}
