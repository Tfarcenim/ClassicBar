package tfar.classicbar.impl;

import org.joml.Vector2i;

import java.util.List;

public record IconData(List<Vector2i> uvs) {

    public static final IconData DEFAULT_DATA = new IconData(List.of(new Vector2i(0,0)));
}
