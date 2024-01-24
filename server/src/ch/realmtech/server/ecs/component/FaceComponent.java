package ch.realmtech.server.ecs.component;

import com.artemis.Component;

public class FaceComponent extends Component {
    public final static byte NORTH = 1 << 0;
    public final static byte EAST = 1 << 1;
    public final static byte SOUTH = 1 << 2;
    public final static byte WEST = 1 << 3;

    private byte face;
    private boolean multiFace;

    public FaceMultiComponentBuilder builderMultiFace() {
        this.multiFace = true;
        return new FaceMultiComponentBuilder(this);
    }

    public FaceSingleComponentBuilder builderSingleFace() {
        this.multiFace = false;
        return new FaceSingleComponentBuilder(this);
    }

    public byte getFace() {
        return face;
    }

    public boolean isMultiFace() {
        return multiFace;
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

    public boolean addNorth() {
        if (face == 0 || multiFace) {
            face |= NORTH;
            return true;
        } else {
            return false;
        }
    }

    public void setNorth() {
        setFace(NORTH);
    }

    public boolean isEast() {
        return (face >> 1 & 1) == 1;
    }

    public boolean addEast() {
        if (face == 0 || multiFace) {
            face |= EAST;
            return true;
        } else {
            return false;
        }
    }

    public void setEast() {
        setFace(EAST);
    }

    public boolean isSouth() {
        return (face >> 2 & 1) == 1;
    }

    public boolean addSouth() {
        if (face == 0 || multiFace) {
            face |= SOUTH;
            return true;
        } else {
            return false;
        }
    }

    public void setSouth() {
        setFace(SOUTH);
    }

    public boolean isWest() {
        return (face >> 3 & 1) == 1;
    }

    public boolean addWest() {
        if (face == 0 || multiFace) {
            face |= WEST;
            return true;
        } else {
            return false;
        }
    }

    public void setWest() {
        setFace(WEST);
    }

    public static class FaceMultiComponentBuilder {
        private final FaceComponent faceComponent;

        public FaceMultiComponentBuilder(FaceComponent faceComponent) {
            this.faceComponent = faceComponent;
        }

        public FaceMultiComponentBuilder addNorth() {
            faceComponent.addNorth();
            return this;
        }

        public FaceMultiComponentBuilder addEast() {
            faceComponent.addEast();
            return this;
        }

        public FaceMultiComponentBuilder addSouth() {
            faceComponent.addSouth();
            return this;
        }

        public FaceMultiComponentBuilder addWest() {
            faceComponent.addWest();
            return this;
        }

        public FaceComponent build() {
            return faceComponent;
        }
    }

    public static class FaceSingleComponentBuilder {
        private final FaceComponent faceComponent;

        public FaceSingleComponentBuilder(FaceComponent faceComponent) {
            this.faceComponent = faceComponent;
        }

        public FaceComponent setNorth() {
            faceComponent.addNorth();
            return faceComponent;
        }

        public FaceComponent setEast() {
            faceComponent.addEast();
            return faceComponent;
        }

        public FaceComponent setSouth() {
            faceComponent.addSouth();
            return faceComponent;
        }

        public FaceComponent setWest() {
            faceComponent.addWest();
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
