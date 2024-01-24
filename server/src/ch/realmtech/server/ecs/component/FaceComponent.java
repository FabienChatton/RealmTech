package ch.realmtech.server.ecs.component;

import com.artemis.Component;

public class FaceComponent extends Component {
    public final static byte NORTH = 1 << 0;
    public final static byte EAST = 1 << 1;
    public final static byte SOUTH = 1 << 2;
    public final static byte WEST = 1 << 3;

    private byte face;

    public FaceComponentBuilder builder() {
        return new FaceComponentBuilder(this);
    }

    public byte getFace() {
        return face;
    }

    public byte getFaceInverted() {
        return invertFace(face);
    }

    public void setFace(byte face) {
        this.face = face;
    }

    public boolean isNorth() {
        return (face >> 0 & 1) == 1;
    }

    public void addNorth() {
        face |= NORTH;
    }

    public boolean isEast() {
        return (face >> 1 & 1) == 1;
    }

    public void addEast() {
        face |= EAST;
    }

    public boolean isSouth() {
        return (face >> 2 & 1) == 1;
    }

    public void addSouth() {
        face |= SOUTH;
    }

    public boolean isWest() {
        return (face >> 3 & 1) == 1;
    }

    public void addWest() {
        face |= WEST;
    }

    public static class FaceComponentBuilder {
        private FaceComponent faceComponent;

        public FaceComponentBuilder(FaceComponent faceComponent) {
            this.faceComponent = faceComponent;
        }

        public FaceComponentBuilder addNorth() {
            faceComponent.addNorth();
            return this;
        }

        public FaceComponentBuilder addEast() {
            faceComponent.addEast();
            return this;
        }

        public FaceComponentBuilder addSouth() {
            faceComponent.addSouth();
            return this;
        }

        public FaceComponentBuilder addWest() {
            faceComponent.addWest();
            return this;
        }

        public FaceComponent build() {
            return faceComponent;
        }
    }

    public static boolean isNorther(int posOrigine, int posTest) {
        return posOrigine < posTest;
    }

    public static boolean isEster(int posOrigine, int posTest) {
        return posOrigine < posTest;
    }

    public static boolean isSouther(int posOrigine, int posTest) {
        return posOrigine > posTest;
    }

    public static boolean isWester(int posOrigine, int posTest) {
        return posOrigine > posTest;
    }

    public static byte getFace(int posOrigineX, int posOrigineY, int posTestX, int posTestY) {
        byte ret = 0;
        if (isNorther(posOrigineY, posTestY)) {
            ret |= NORTH;
        }
        if (isEster(posOrigineX, posTestX)) {
            ret |= EAST;
        }
        if (isSouther(posOrigineY, posTestY)) {
            ret |= SOUTH;
        }
        if (isWester(posOrigineX, posTestX)) {
            ret |= WEST;
        }
        return ret;
    }

    public static byte invertFace(byte face) {
        return (byte) ((face & 0b11110000) | (~(face & 0b00001111) & 0b00001111));
    }
}
