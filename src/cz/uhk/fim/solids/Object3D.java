package cz.uhk.fim.solids;

import cz.uhk.fim.transforms.Mat4;
import cz.uhk.fim.transforms.Mat4Identity;
import cz.uhk.fim.transforms.Point3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Object3D {

    protected List<Point3D> vb = new ArrayList<>();
    protected List<Integer> ib = new ArrayList<>();
    protected List<Integer> colors = new ArrayList<>();
    protected Mat4 transMat = new Mat4Identity();
    protected boolean transferable = true;

    private String name = "";

    public List<Point3D> getVb() {
        return vb;
    }

    public List<Integer> getIb() {
        return ib;
    }

    protected void addIndices(Integer... indices) {
        ib.addAll(Arrays.asList(indices));
    }

    public int getColor(int index) {
        if (this.colors != null) {
            return this.colors.get(index);
        }
        return 0x000000;
    }

    public int getColorSize() {
        return this.colors.size();
    }

    public void addColors(Integer... colors) {
        this.colors.addAll(Arrays.asList(colors));
    }

    public Mat4 getTransMat() {
        return transMat;
    }

    public void setTransMat(Mat4 transMat) {
        this.transMat = transMat;
    }

    public boolean isTransferable() {
        return transferable;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
