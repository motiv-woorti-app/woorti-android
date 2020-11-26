package inesc_id.pt.motivandroid.utils;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import inesc_id.pt.motivandroid.R;

/**
 *
 * GraphHelper
 *
 *  Utility functions for plotting x,y graphs (using com.jjoe64.graphview)
 *
 * (C) 2017-2020 - The Woorti app is a research (non-commercial) application that was
 * developed in the context of the European research project MoTiV (motivproject.eu). The
 * code was developed by partner INESC-ID with contributions in graphics design by partner
 * TIS. The Woorti app development was one of the outcomes of a Work Package of the MoTiV
 * project.
 * The Woorti app was originally intended as a tool to support data collection regarding
 * mobility patterns from city and country-wide campaigns and provide the data and user
 * management to campaign managers.
 *
 * The Woorti app development followed an agile approach taking into account ongoing
 * feedback of partners and testing users while continuing under development. This has
 * been carried out as an iterative process deploying new app versions. Along the
 * timeline, various previously unforeseen requirements were identified, some requirements
 * Were revised, there were requests for modifications, extensions, or new aspects in
 * functionality or interaction as found useful or interesting to campaign managers and
 * other project partners. Most stemmed naturally from the very usage and ongoing testing
 * of the Woorti app. Hence, code and data structures were successively revised in a
 * way not only to accommodate this but, also importantly, to maintain compatibility with
 * the functionality, data and data structures of previous versions of the app, as new
 * version roll-out was never done from scratch.
 * The code developed for the Woorti app is made available as open source, namely to
 * contribute to further research in the area of the MoTiV project, and the app also makes
 * use of open source components as detailed in the Woorti app license.
 * This project has received funding from the European Union’s Horizon 2020 research and
 * innovation programme under grant agreement No. 770145.
 * This file is part of the Woorti app referred to as SOFTWARE.
 */

public class GraphHelper {

    public static void drawGraph(GraphView graph, DataPoint[] dataPoints, boolean isSettingMobilityGoal, int target, final Resources resources) {

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //configure graoh
        graph.getGridLabelRenderer().setNumHorizontalLabels(11);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setMinX(0);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxX(10);
        graph.getViewport().setMaxY(100);

        graph.getViewport().setScrollable(false);

        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setGridColor(resources.getColor(R.color.graphPointFilling));
        graph.getGridLabelRenderer().setHorizontalLabelsColor(resources.getColor(R.color.graphPointFilling));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //build point series
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(dataPoints);

        series.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                drawPointOnGraph(x, y, canvas, paint, dataPoint, resources);
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //build line series (point linking line)
        LineGraphSeries<DataPoint> seriesLine = new LineGraphSeries<>(dataPoints);
        seriesLine.setDrawBackground(!isSettingMobilityGoal);
        seriesLine.setBackgroundColor(resources.getColor(R.color.graphUnderBackgroundColor));
        seriesLine.setColor(resources.getColor(R.color.graphPointFilling));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //plot series on graph
        graph.addSeries(seriesLine);
        graph.addSeries(series);

        // draw target line
        if(!isSettingMobilityGoal){
            drawTargetLine(graph, target);
        }

    }

    public static void drawTargetLine(GraphView graph, int target) {

        double minX = graph.getViewport().getMinX(false);
        double maxX = graph.getViewport().getMaxX(false);

        LineGraphSeries<DataPoint> targetLineSeries = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(minX, target),
                new DataPoint(maxX, target)
        });

        targetLineSeries.setDrawAsPath(true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.WHITE);
        paint.setPathEffect(new DashPathEffect(new float[] {8,5}, 0));
        targetLineSeries.setCustomPaint(paint);

        graph.addSeries(targetLineSeries);

    }

    private static void drawPointOnGraph(float x, float y, Canvas canvas, Paint paint, DataPointInterface dataPoint, Resources resources) {

        //draw filled circle
        paint.setColor(resources.getColor(R.color.graphPointFilling));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, 20, paint);

        //draw border
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setColor(Color.WHITE);
        paint.setAlpha(255);
        // paint.setXfermode(xfermode);
        paint.setAntiAlias(true);
        canvas.drawCircle(x, y, 20, paint);

    }
}
