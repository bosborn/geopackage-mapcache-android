package mil.nga.giat.geopackage.data.c1;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import mil.nga.giat.geopackage.data.c2.Contents;
import mil.nga.giat.geopackage.data.c2.ContentsDao;
import mil.nga.giat.geopackage.data.c3.GeometryColumns;
import mil.nga.giat.geopackage.data.c3.GeometryColumnsDao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

/**
 * Spatial Reference System Data Access Object
 * 
 * @author osbornb
 */
public class SpatialReferenceSystemDao extends
		BaseDaoImpl<SpatialReferenceSystem, Integer> {

	/**
	 * Contents DAO
	 */
	private ContentsDao contentsDao;

	/**
	 * Geometry Columns DAO
	 */
	private GeometryColumnsDao geometryColumnsDao;

	/**
	 * Constructor, required by ORMLite
	 * 
	 * @param connectionSource
	 * @param dataClass
	 * @throws SQLException
	 */
	public SpatialReferenceSystemDao(ConnectionSource connectionSource,
			Class<SpatialReferenceSystem> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

	/**
	 * Delete the Spatial Reference System, cascading to Contents
	 * 
	 * @param srs
	 * @return
	 * @throws SQLException
	 */
	public int deleteCascade(SpatialReferenceSystem srs) throws SQLException {
		int count = 0;

		if (srs != null) {

			// Delete Contents
			ForeignCollection<Contents> contentsCollection = srs.getContents();
			if (!contentsCollection.isEmpty()) {
				ContentsDao dao = getContentsDao();
				dao.deleteCascade(contentsCollection);
			}

			// Delete Geometry Columns
			GeometryColumnsDao dao = getGeometryColumnsDao();
			if(dao.isTableExists()){
				ForeignCollection<GeometryColumns> geometryColumnsCollection = srs
						.getGeometryColumns();
				if (!geometryColumnsCollection.isEmpty()) {
					dao.delete(geometryColumnsCollection);
				}
			}

			// Delete
			count = super.delete(srs);
		}
		return count;
	}

	/**
	 * Delete the collection of Spatial Reference Systems, cascading to Contents
	 * 
	 * @param srsCollection
	 * @return
	 * @throws SQLException
	 */
	public int deleteCascade(Collection<SpatialReferenceSystem> srsCollection)
			throws SQLException {
		int count = 0;
		if (srsCollection != null) {
			for (SpatialReferenceSystem srs : srsCollection) {
				count += deleteCascade(srs);
			}
		}
		return count;
	}

	/**
	 * Delete the Spatial Reference Systems matching the prepared query,
	 * cascading to Contents
	 * 
	 * @param preparedDelete
	 * @return
	 * @throws SQLException
	 */
	public int deleteCascade(
			PreparedQuery<SpatialReferenceSystem> preparedDelete)
			throws SQLException {
		int count = 0;
		if (preparedDelete != null) {
			List<SpatialReferenceSystem> srsList = super.query(preparedDelete);
			count = deleteCascade(srsList);
		}
		return count;
	}

	/**
	 * Delete a Spatial Reference System by id, cascading to Contents
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public int deleteByIdCascade(Integer id) throws SQLException {
		int count = 0;
		if (id != null) {
			SpatialReferenceSystem srs = super.queryForId(id);
			if (srs != null) {
				count = deleteCascade(srs);
			}
		}
		return count;
	}

	/**
	 * Delete the Spatial Reference Systems with the provided ids, cascading to
	 * Contents
	 * 
	 * @param idCollection
	 * @return
	 * @throws SQLException
	 */
	public int deleteIdsCascade(Collection<Integer> idCollection)
			throws SQLException {
		int count = 0;
		if (idCollection != null) {
			for (Integer id : idCollection) {
				count += deleteByIdCascade(id);
			}
		}
		return count;
	}

	/**
	 * Get or create a Contents DAO
	 * 
	 * @return
	 * @throws SQLException
	 */
	private ContentsDao getContentsDao() throws SQLException {
		if (contentsDao == null) {
			contentsDao = DaoManager
					.createDao(connectionSource, Contents.class);
		}
		return contentsDao;
	}

	/**
	 * Get or create a Geometry Columns DAO
	 * 
	 * @return
	 * @throws SQLException
	 */
	private GeometryColumnsDao getGeometryColumnsDao() throws SQLException {
		if (geometryColumnsDao == null) {
			geometryColumnsDao = DaoManager.createDao(connectionSource,
					GeometryColumns.class);
		}
		return geometryColumnsDao;
	}

}
