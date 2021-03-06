package org.bimserver.charting;

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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.commons.lang.mutable.MutableInt;
import org.bimserver.charting.Charts.SmallMultiplesArea;
import org.bimserver.emf.IfcModelInterface;
import org.bimserver.plugins.PluginManagerInterface;
import org.bimserver.plugins.serializers.ProgressReporter;
import org.bimserver.plugins.serializers.ProjectInfo;
import org.bimserver.plugins.serializers.SerializerException;
import org.bimserver.utils.UTF8PrintWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaterialUsageChartSerializer extends ChartEmfSerializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(MaterialUsageChartSerializer.class);

	@Override
	public void init(IfcModelInterface model, ProjectInfo projectInfo, PluginManagerInterface pluginManager, boolean normalizeOids) throws SerializerException {
		super.init(model, projectInfo, pluginManager, normalizeOids);
		// Pick chart.
		chart = new SmallMultiplesArea();
		integrateSettings();
		// Prepare for data.
		rawData = new ArrayList<>();
	}

	@Override
	protected boolean write(OutputStream outputStream, ProgressReporter progressReporter) throws SerializerException {
		if (getMode() == Mode.BODY) {
			// Get data.
			MutableInt subChartCount = new MutableInt(0);
			rawData = SupportFunctions.getIfcMaterialsByClassWithTreeStructure("group", model, chart, subChartCount);
			// Adjust height if there wasn't something explicit.
			int count = subChartCount.intValue();
			if (!hasOption("Height") && count > 3) {
				int heightPerChart = 330;
				int totalHeight = heightPerChart * count;
				if (totalHeight > 1000)
					chart.FitToSize = false;
				chart.setOption("Height", totalHeight);
			}
			// Write chart.
			PrintWriter writer = new UTF8PrintWriter(outputStream);
			try {
				writer.print(chart.writeSVG(rawData));
				writer.flush();
			} catch (Exception e) {
				LOGGER.error("", e);
			}
			writer.close();
			setMode(Mode.FINISHED);
			return true;
		} else if (getMode() == Mode.FINISHED)
			return false;
		//
		return false;
	}
}
