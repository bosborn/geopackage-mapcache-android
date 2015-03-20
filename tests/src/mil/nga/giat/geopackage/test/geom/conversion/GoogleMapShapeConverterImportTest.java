package mil.nga.giat.geopackage.test.geom.conversion;

import java.sql.SQLException;

import mil.nga.giat.geopackage.test.ImportGeoPackageTestCase;

/**
 * Test Google Map Shape Converter from an imported database
 * 
 * @author osbornb
 */
public class GoogleMapShapeConverterImportTest extends ImportGeoPackageTestCase {

	/**
	 * Constructor
	 */
	public GoogleMapShapeConverterImportTest() {

	}

	/**
	 * Test shapes
	 * 
	 * @throws SQLException
	 */
	public void testShapes() throws SQLException {

		GoogleMapShapeConverterUtils.testShapes(geoPackage);

	}

}
