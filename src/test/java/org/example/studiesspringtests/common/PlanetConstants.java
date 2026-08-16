package org.example.studiesspringtests.common;

import org.example.studiesspringtests.domain.Planet;

import java.util.ArrayList;
import java.util.List;

public class PlanetConstants {
    public static final Planet PLANET = new Planet("name", "climate", "terrain");
    public static final Planet INVALID_PLANET = new Planet("", "", "");

    public static final Planet TATOOINE = new  Planet("Tatooine", "arid", "desert");
    public static final Planet ALDERAAN = new  Planet("Alderaan", "temperate", "grasslands");
    public static final Planet YAVINIV = new  Planet("Yavin IV", "tropical", "jungle");

    public static final List<Planet> PLANETS = new ArrayList<Planet>() {
        {
            add(TATOOINE);
            add(ALDERAAN);
            add(YAVINIV);
        }
    };
}
