package cz.uhk.fim.solids;

import cz.uhk.fim.transforms.Point3D;
import cz.uhk.fim.utilities.Globals;

public class HexagonalPrism extends Object3D {

    public HexagonalPrism() {

        vb.add(new Point3D(0, 0, 3));
        vb.add(new Point3D(1.5, -2.6, 1.5));
        vb.add(new Point3D(4.6, -1.3, 0));
        vb.add(new Point3D(4.6, 1.3, 0));
        vb.add(new Point3D(1.5, 2.6, 1.5));
        vb.add(new Point3D(-1.5, 2.6, 1.5));
        vb.add(new Point3D(-4.6, 1.3, 0));
        vb.add(new Point3D(-4.6, -1.3, 0));
        vb.add(new Point3D(-1.5, -2.6, 1.5));

        addIndices(0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 1);
        addColors(Globals.PURPLE);
    }

}
