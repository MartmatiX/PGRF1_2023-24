package cz.uhk.fim.first_second_task.object_ops;

import cz.uhk.fim.first_second_task.object_data.Line;
import cz.uhk.fim.first_second_task.object_data.Point;
import cz.uhk.fim.first_second_task.raster_data.Polygon;

public class PolygonCutter {

    private final Polygon cuttingPolygon;

    public PolygonCutter(Polygon cuttingPolygon) {
        this.cuttingPolygon = cuttingPolygon;
    }

    public Polygon cut(Polygon polygon) {
        Polygon croppedPolygon = new Polygon(polygon);
        for (int i = 0; i < cuttingPolygon.getPoints().size(); i++) {
            Line cutter = new Line(cuttingPolygon.getPoints().get(i), new Point(cuttingPolygon.getPoints().get((i + 1) % cuttingPolygon.getPoints().size()).getX(), cuttingPolygon.getPoints().get((i + 1) % cuttingPolygon.getPoints().size()).getY()));
            polygon = new Polygon(croppedPolygon);
            croppedPolygon.getPoints().clear();
            Point v1 = new Point(polygon.getPoints().get(polygon.getPoints().size() - 1).getX(), polygon.getPoints().get(polygon.getPoints().size() - 1).getY());
            for (int j = 0; j < polygon.getPoints().size(); j++) {
                Point v2 = new Point(polygon.getPoints().get(j).getX(), polygon.getPoints().get(j).getY());
                if (cutter.isInside(v2)) {
                    if (!cutter.isInside(v1)) croppedPolygon.addPoint(cutter.intersection(v1, v2));
                    croppedPolygon.addPoint(v2);
                } else {
                    if (cutter.isInside(v1)) croppedPolygon.addPoint(cutter.intersection(v1, v2));
                }
                v1 = v2;
            }

        }
        return croppedPolygon;
    }

}
