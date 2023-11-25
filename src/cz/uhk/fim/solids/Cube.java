package cz.uhk.fim.solids;

import cz.uhk.fim.transforms.Point3D;
import cz.uhk.fim.utilities.Globals;

public class Cube extends Object3D {

    public Cube() {

        vb.add(new Point3D(-2, -2, -2));
        vb.add(new Point3D(-2, 2, -2));
        vb.add(new Point3D(2, 2, -2));
        vb.add(new Point3D(2, -2, -2));
        vb.add(new Point3D(-2, -2, 2));
        vb.add(new Point3D(-2, 2, 2));
        vb.add(new Point3D(2, 2, 2));
        vb.add(new Point3D(2, -2, 2));

        addIndices(0, 1, 0, 4, 1, 2, 1, 5, 2, 3, 2, 6, 3, 0, 3, 7, 4, 5, 5, 6, 6, 7, 7, 4);
        addColors(Globals.BLUE);
    }
}
