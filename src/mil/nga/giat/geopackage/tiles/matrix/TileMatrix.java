package mil.nga.giat.geopackage.tiles.matrix;

import mil.nga.giat.geopackage.GeoPackageException;
import mil.nga.giat.geopackage.core.contents.Contents;
import mil.nga.giat.geopackage.core.contents.ContentsDataType;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Tile Matrix object. Documents the structure of the tile matrix at each zoom
 * level in each tiles table. It allows GeoPackages to contain rectangular as
 * well as square tiles (e.g. for better representation of polar regions). It
 * allows tile pyramids with zoom levels that differ in resolution by factors of
 * 2, irregular intervals, or regular intervals other than factors of 2.
 * 
 * @author osbornb
 */
@DatabaseTable(tableName = "gpkg_tile_matrix", daoClass = TileMatrixDao.class)
public class TileMatrix {

	/**
	 * Table name
	 */
	public static final String TABLE_NAME = "gpkg_tile_matrix";

	/**
	 * tableName field name
	 */
	public static final String COLUMN_TABLE_NAME = Contents.COLUMN_TABLE_NAME;

	/**
	 * zoomLevel field name
	 */
	public static final String COLUMN_ZOOM_LEVEL = "zoom_level";

	/**
	 * matrixWidth field name
	 */
	public static final String COLUMN_MATRIX_WIDTH = "matrix_width";

	/**
	 * matrixHeight field name
	 */
	public static final String COLUMN_MATRIX_HEIGHT = "matrix_height";

	/**
	 * tileWidth field name
	 */
	public static final String COLUMN_TILE_WIDTH = "tile_width";

	/**
	 * tileHeight field name
	 */
	public static final String COLUMN_TILE_HEIGHT = "tile_height";

	/**
	 * pixelXSize field name
	 */
	public static final String COLUMN_PIXEL_X_SIZE = "pixel_x_size";
	/**
	 * pixelYSize field name
	 */
	public static final String COLUMN_PIXEL_Y_SIZE = "pixel_y_size";

	/**
	 * Foreign key to Contents by table name
	 */
	@DatabaseField(columnName = COLUMN_TABLE_NAME, canBeNull = false, unique = true, foreign = true, foreignAutoRefresh = true)
	private Contents contents;

	/**
	 * Tile Pyramid User Data Table Name
	 */
	@DatabaseField(columnName = COLUMN_TABLE_NAME, id = true, canBeNull = false, uniqueCombo = true)
	private String tableName;

	/**
	 * 0 ⇐ zoom_level ⇐ max_level for table_name
	 */
	@DatabaseField(columnName = COLUMN_ZOOM_LEVEL, canBeNull = false, uniqueCombo = true)
	private int zoomLevel;

	/**
	 * Number of columns (>= 1) in tile matrix at this zoom level
	 */
	@DatabaseField(columnName = COLUMN_MATRIX_WIDTH, canBeNull = false)
	private int matrixWidth;

	/**
	 * Number of rows (>= 1) in tile matrix at this zoom level
	 */
	@DatabaseField(columnName = COLUMN_MATRIX_HEIGHT, canBeNull = false)
	private int matrixHeight;

	/**
	 * Tile width in pixels (>= 1)for this zoom level
	 */
	@DatabaseField(columnName = COLUMN_TILE_WIDTH, canBeNull = false)
	private int tileWidth;

	/**
	 * Tile height in pixels (>= 1)for this zoom level
	 */
	@DatabaseField(columnName = COLUMN_TILE_HEIGHT, canBeNull = false)
	private int tileHeight;

	/**
	 * In t_table_name srid units or default meters for srid 0 (>0)
	 */
	@DatabaseField(columnName = COLUMN_PIXEL_X_SIZE, canBeNull = false)
	private double pixelXSize;

	/**
	 * In t_table_name srid units or default meters for srid 0 (>0)
	 */
	@DatabaseField(columnName = COLUMN_PIXEL_Y_SIZE, canBeNull = false)
	private double pixelYSize;

	/**
	 * Default Constructor
	 */
	public TileMatrix() {

	}

	/**
	 * Get the tile matrix id
	 * 
	 * @return
	 */
	public TileMatrixKey getId() {
		return new TileMatrixKey(tableName, zoomLevel);
	}

	/**
	 * Set the tile matrix id
	 * 
	 * @param id
	 */
	public void setId(TileMatrixKey id) {
		tableName = id.getTableName();
		zoomLevel = id.getZoomLevel();
	}

	public Contents getContents() {
		return contents;
	}

	public void setContents(Contents contents) {
		this.contents = contents;
		if (contents != null) {
			// Verify the Contents have a tiles data type (Spec Requirement 42)
			ContentsDataType dataType = contents.getDataType();
			if (dataType == null || dataType != ContentsDataType.TILES) {
				throw new GeoPackageException("The "
						+ Contents.class.getSimpleName() + " of a "
						+ TileMatrix.class.getSimpleName()
						+ " must have a data type of "
						+ ContentsDataType.TILES.getName());
			}
			tableName = contents.getId();
		}
	}

	public String getTableName() {
		return tableName;
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	public void setZoomLevel(int zoomLevel) {
		validateIntegerValues(COLUMN_ZOOM_LEVEL, zoomLevel, true);
		this.zoomLevel = zoomLevel;
	}

	public int getMatrixWidth() {
		return matrixWidth;
	}

	public void setMatrixWidth(int matrixWidth) {
		validateIntegerValues(COLUMN_MATRIX_WIDTH, matrixWidth, false);
		this.matrixWidth = matrixWidth;
	}

	public int getMatrixHeight() {
		return matrixHeight;
	}

	public void setMatrixHeight(int matrixHeight) {
		validateIntegerValues(COLUMN_MATRIX_HEIGHT, matrixHeight, false);
		this.matrixHeight = matrixHeight;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public void setTileWidth(int tileWidth) {
		validateIntegerValues(COLUMN_TILE_WIDTH, tileWidth, false);
		this.tileWidth = tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public void setTileHeight(int tileHeight) {
		validateIntegerValues(COLUMN_TILE_HEIGHT, tileHeight, false);
		this.tileHeight = tileHeight;
	}

	public double getPixelXSize() {
		return pixelXSize;
	}

	public void setPixelXSize(double pixelXSize) {
		validateDoubleValues(COLUMN_PIXEL_X_SIZE, pixelXSize);
		this.pixelXSize = pixelXSize;
	}

	public double getPixelYSize() {
		return pixelYSize;
	}

	public void setPixelYSize(double pixelYSize) {
		validateDoubleValues(COLUMN_PIXEL_Y_SIZE, pixelYSize);
		this.pixelYSize = pixelYSize;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Validate the integer values are greater than 0, or greater than or equal
	 * to 0 based upon the allowZero flag
	 * 
	 * @param column
	 * @param value
	 * @param allowZero
	 */
	private void validateIntegerValues(String column, int value,
			boolean allowZero) {
		if (value < 0 || (value == 0 && !allowZero)) {
			throw new GeoPackageException(column
					+ " value must be greater than "
					+ (allowZero ? "or equal to " : "") + "0: " + value);
		}
	}

	/**
	 * Validate the double values are greater than 0
	 * 
	 * @param column
	 * @param value
	 */
	private void validateDoubleValues(String column, double value) {
		if (value <= 0.0) {
			throw new GeoPackageException(column
					+ " value must be greater than 0: " + value);
		}
	}

}
