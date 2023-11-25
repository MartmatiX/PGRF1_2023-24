package cz.uhk.fim.render;

import cz.uhk.fim.raster_data.Raster;
import cz.uhk.fim.raster_op.Liner;
import cz.uhk.fim.solids.Object3D;
import cz.uhk.fim.transforms.Mat4;
import cz.uhk.fim.transforms.Mat4Identity;
import cz.uhk.fim.transforms.Point3D;
import cz.uhk.fim.transforms.Vec3D;

import java.util.List;

public class WireRenderer {

    private final Liner liner;
    private final Raster img;
    private Mat4 view;
    private Mat4 projection;

    public WireRenderer(Liner liner, Raster img, Mat4 projection) {
        this.liner = liner;
        this.img = img;
        this.projection = projection;
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProjection(Mat4 projection) {
        this.projection = projection;
    }

    public void renderSolid(Object3D object3D, Mat4 model) {
        if (object3D.isTransferable()) {
            model = object3D.getTransMat().mul(model);
        } else {
            model = new Mat4Identity();
        }

        final Mat4 finalTransform = model.mul(view).mul(projection);

        for (int i = 0; i < object3D.getIb().size(); i += 2) {
            int index1 = object3D.getIb().get(i);
            int index2 = object3D.getIb().get(i + 1);

            Point3D point1 = object3D.getVb().get(index1);
            Point3D point2 = object3D.getVb().get(index2);

            point1 = point1.mul(finalTransform);
            point2 = point2.mul(finalTransform);

            if (point1.getW() < 0) break;
            if (point2.getW() < 0) break;

            Vec3D vectorA = null;
            Vec3D vectorB = null;

            if (point1.dehomog().isPresent()) {
                vectorA = point1.dehomog().get();
            }
            if (point2.dehomog().isPresent()) {
                vectorB = point2.dehomog().get();
            }

            assert vectorA != null;
            int x1 = (int) ((1 + vectorA.getX()) * (img.getWidth() - 1) / 2);
            int y1 = (int) ((1 - vectorA.getY()) * (img.getHeight() - 1) / 2);
            assert vectorB != null;
            int x2 = (int) ((1 + vectorB.getX()) * (img.getWidth() - 1) / 2);
            int y2 = (int) ((1 - vectorB.getY()) * (img.getHeight() - 1) / 2);

            if (x1 >= 0 && x1 < img.getWidth() && y1 >= 0 && y1 < img.getHeight() && x2 >= 0 && x2 < img.getWidth() && y2 >= 0 && y2 < img.getHeight()) {
                liner.drawLine(img, x1, y1, x2, y2, object3D.getColor(i % object3D.getColorSize()));
            }
        }
    }

    public void renderSpace(List<Object3D> object3DS, Mat4 model) {
        for (Object3D object3D : object3DS) {
            renderSolid(object3D, model);
        }
    }

}
