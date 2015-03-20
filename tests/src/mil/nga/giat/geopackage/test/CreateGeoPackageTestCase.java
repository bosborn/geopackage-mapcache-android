package mil.nga.giat.geopackage.test;

import java.io.IOException;
import java.sql.SQLException;

import mil.nga.giat.geopackage.GeoPackage;

/**
 * Abstract Test Case for Created GeoPackages
 * 
 * @author osbornb
 */
public abstract class CreateGeoPackageTestCase extends GeoPackageTestCase {

	/**
	 * Constructor
	 */
	public CreateGeoPackageTestCase() {

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	@Override
	protected GeoPackage getGeoPackage() throws Exception {
		return TestSetupTeardown.setUpCreate(activity, testContext, true, true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void tearDown() throws Exception {

		// Tear down the create database
		TestSetupTeardown.tearDownCreate(activity, geoPackage);

		super.tearDown();
	}

}
