package cz.uhk.fim.solids;

import cz.uhk.fim.transforms.Point3D;
import cz.uhk.fim.utilities.Globals;

public class Sphere extends Object3D {

    private static final int STACKS = 20;
    private static final int SLICES = 20;

    public Sphere() {
        this.setName("Sphere");

        generateVertices();
        generateIndices();
        addColors(Globals.YELLOW);
    }

    private void generateVertices() {
        for (int i = 0; i <= STACKS; ++i) {
            double phi = Math.PI * i / STACKS;
            double sinPhi = Math.sin(phi);
            double cosPhi = Math.cos(phi);

            for (int j = 0; j <= SLICES; ++j) {
                double theta = 2 * Math.PI * j / SLICES;
                double sinTheta = Math.sin(theta);
                double cosTheta = Math.cos(theta);

                double x = cosTheta * sinPhi;
                double z = sinTheta * sinPhi;

                vb.add(new Point3D(x, cosPhi, z));
            }
        }
    }

    private void generateIndices() {
        for (int i = 0; i < STACKS; ++i) {
            for (int j = 0; j < SLICES; ++j) {
                int first = (i * (SLICES + 1)) + j;
                int second = first + SLICES + 1;

                ib.add(first);
                ib.add(second);
                ib.add(first + 1);

                ib.add(second);
                ib.add(second + 1);
                ib.add(first + 1);
            }
        }
    }

}
