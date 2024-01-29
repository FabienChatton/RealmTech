package ch.realmtech.server.ecs.component;

import com.artemis.Component;

public class FaceComponent extends Component {
    public final static byte NORTH = 1 << 0;
    public final static byte EAST = 1 << 1;
    public final static byte SOUTH = 1 << 2;
    public final static byte WEST = 1 << 3;
    public final static byte ALL_FACE = NORTH | EAST | SOUTH | WEST;
    public final static byte MULTI_FACE_FLAG = 1 << 4;

    private byte flags;
    private String baseTexture;

    public FaceMultiComponentBuilder builderMultiFace() {
        flags |= MULTI_FACE_FLAG;
        return new FaceMultiComponentBuilder(this);
    }

    public FaceSingleComponentBuilder builderSingleFace() {
        return new FaceSingleComponentBuilder(this);
    }

    public byte getFlags() {
        return flags;
    }

    public byte getFace() {
        return (byte) (getFlags() & 0b00001111);
    }

    public boolean isMultiFace() {
        return (flags & MULTI_FACE_FLAG) == MULTI_FACE_FLAG;
    }

    public byte getFaceInverted() {
        return invertFace(flags);
    }

    public void setFace(byte face) {
        this.flags = (byte) ((this.flags & 0b11110000) | (face & 0b00001111));
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    /** remove all face if multi face to single face */
    public void setMultiFace(boolean multiFace) {
        if (multiFace) {
            flags |= MULTI_FACE_FLAG;
        } else {
            flags &= ~MULTI_FACE_FLAG;
            setFace((byte) 0);
        }
    }

    public void set(byte face, boolean multiFace) {
        setMultiFace(multiFace);
        setFace(face);
    }

    public boolean isNorth() {
        return FaceComponent.isNorth(flags);
    }

    public boolean addNorth() {
        if (flags == 0 || isMultiFace()) {
            flags |= NORTH;
            return true;
        } else {
            return false;
        }
    }

    public void setNorth() {
        setFace(NORTH);
    }

    public boolean isEast() {
        return FaceComponent.isEast(flags);
    }

    public boolean addEast() {
        if (flags == 0 || isMultiFace()) {
            flags |= EAST;
            return true;
        } else {
            return false;
        }
    }

    public void setEast() {
        setFace(EAST);
    }

    public boolean isSouth() {
        return FaceComponent.isSouth(flags);
    }

    public boolean addSouth() {
        if (flags == 0 || isMultiFace()) {
            flags |= SOUTH;
            return true;
        } else {
            return false;
        }
    }

    public void setSouth() {
        setFace(SOUTH);
    }

    public boolean isWest() {
        return FaceComponent.isWest(flags);
    }

    public boolean addWest() {
        if (flags == 0 || isMultiFace()) {
            flags |= WEST;
            return true;
        } else {
            return false;
        }
    }

    public void setWest() {
        setFace(WEST);
    }

    public void setBaseTextures(String baseTexture) {
        this.baseTexture = baseTexture;
    }

    public String getFaceTexture() {
        String ret;
        String binaryString = Integer.toBinaryString(getFace());
        if (binaryString.length() < 4) {
            ret = String.format("%4s", binaryString).replace(' ', '0');
        } else {
            ret = binaryString.substring(binaryString.length() -4);
        }
        return baseTexture + "-" + ret;
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

    public static boolean isEaster(int posOrigine, int posTest) {
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
        if (isEaster(posOrigineX, posTestX)) {
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

    public static boolean isNorth(byte face) {
        return (face >> 0 & 1) == 1;
    }
    public static boolean isEast(byte face) {
        return (face >> 1 & 1) == 1;
    }

    public static boolean isSouth(byte face) {
        return (face >> 2 & 1) == 1;
    }

    public static boolean isWest(byte face) {
        return (face >> 3 & 1) == 1;
    }
}
