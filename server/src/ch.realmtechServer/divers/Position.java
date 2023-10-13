package ch.realmtechServer.divers;

import java.util.List;

public record Position(int x, int y) {

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Position position) {
            return x == position.x() && y == position.y();
        } else {
            return false;
        }
    }

    public static boolean replace(List<Position> poss, Position oldPos, Position newPos) {
        if (!poss.contains(oldPos)) return false;
        int oldIndex = poss.indexOf(oldPos);
        if (oldIndex == -1) return false;
        poss.set(oldIndex, newPos);
        return true;
    }

    public static boolean replace(List<Position> poss, int oldX, int oldY, int newX, int newY) {
        return replace(poss, new Position(oldX, oldY), new Position(newX, newY));
    }
}
