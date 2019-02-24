package microbots.core;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableTable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/** An {@link ArenaMap} is a preconfigured arrangement of {@link Terrain}. */
enum ArenaMap {
  /**
   * The empty map has no terrain. Microbots may be able to "wrap around" on the sides of the arena.
   */
  EMPTY(75, 100, "Empty", null),

  /**
   * The enclosed map is empty everywhere except along the borders. This prevents microbots from
   * wrapping around.
   */
  ENCLOSED(75, 100, "Enclosed", "enclosed.txt"),

  /** The diamond map has a diamond-shaped obstruction in the center of the map. */
  DIAMOND(75, 100, "Diamond", "diamond.txt"),

  /** The quadrant map is divided into four regions with a connecting area in the middle. */
  QUADRANTS(75, 100, "Quadrants", "quadrants.txt");

  private final int rows;
  private final int columns;
  private final String description;
  private final String filename;
  private final ImmutableTable<Integer, Integer, Terrain> terrain;

  ArenaMap(int rows, int columns, String description, String filename) {
    this.rows = rows;
    this.columns = columns;
    this.description = description;
    this.filename = filename;
    this.terrain = loadTerrain();
  }

  /** Returns the number of rows in this map. */
  int rows() {
    return rows;
  }

  /** Returns the number of columns in this map. */
  int columns() {
    return columns;
  }

  /** Returns a description of this map, suitable for displaying in the UI. */
  String description() {
    return description;
  }

  /** Returns this map's terrain. */
  ImmutableTable<Integer, Integer, Terrain> terrain() {
    return terrain;
  }

  /**
   * Returns the number of spaces on this map that are {@link Terrain#isTraversable() traversable}.
   */
  int traversableSpaceCount() {
    return (int) terrain.values().stream().filter(Terrain::isTraversable).count();
  }

  /**
   * Loads and returns the terrain in this map as a table. If this map's filename is non-null, then
   * the terrain is read from the indicated file. Otherwise, a simple map is created, populated
   * exclusively with {@link Terrain#FIELD fields}.
   */
  private ImmutableTable<Integer, Integer, Terrain> loadTerrain() {
    return filename == null ? createSimpleMap() : createComplexMap();
  }

  /** Returns a simple map consisting only of {@link Terrain#FIELD fields}. */
  private ImmutableTable<Integer, Integer, Terrain> createSimpleMap() {
    ImmutableTable.Builder<Integer, Integer, Terrain> builder = ImmutableTable.builder();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < columns; c++) {
        builder.put(r, c, Terrain.FIELD);
      }
    }
    return builder.build();
  }

  /** Returns a "complex" map, loaded from this map's filename. */
  private ImmutableTable<Integer, Integer, Terrain> createComplexMap() {
    ImmutableTable.Builder<Integer, Integer, Terrain> builder = ImmutableTable.builder();
    Path path = Paths.get(System.getProperty("user.dir"), "res", "maps", filename);
    try {
      List<String> lines = Files.readAllLines(path);
      checkArgument(lines.size() == rows, "Map has %d rows, expected %d.", lines.size(), rows);

      for (int row = 0; row < rows; row++) {
        addLineToTerrain(lines.get(row), row, builder);
      }

      return builder.build();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /** Parses a single line from a map file and adds it to the specified terrain table builder. */
  private void addLineToTerrain(
      String line, int row, ImmutableTable.Builder<Integer, Integer, Terrain> builder) {
    checkArgument(
        line.length() == columns, "Map row has length %d, expected %d.", line.length(), columns);

    for (int column = 0; column < columns; column++) {
      char symbol = line.charAt(column);
      Terrain terrain =
          Terrain.forSymbol(symbol)
              .orElseThrow(
                  () ->
                      new RuntimeException(
                          String.format("No such terrain exists with symbol '%s'.", symbol)));
      builder.put(row, column, terrain);
    }
  }
}
