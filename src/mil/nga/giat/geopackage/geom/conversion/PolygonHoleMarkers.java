package mil.nga.giat.geopackage.geom.conversion;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.Marker;

/**
 * Polygon Hole with Markers object
 * 
 * @author osbornb
 */
public class PolygonHoleMarkers implements ShapeMarkers {

	final private PolygonMarkers parentPolygon;

	private List<Marker> markers = new ArrayList<Marker>();

	/**
	 * Constructor
	 * 
	 * @param polygonMarkers
	 */
	public PolygonHoleMarkers(PolygonMarkers polygonMarkers) {
		parentPolygon = polygonMarkers;
	}

	public void add(Marker marker) {
		markers.add(marker);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Marker> getMarkers() {
		return markers;
	}

	public void setMarkers(List<Marker> markers) {
		this.markers = markers;
	}

	/**
	 * Remove from the map
	 */
	public void remove() {
		for (Marker marker : markers) {
			marker.remove();
		}
	}

	/**
	 * Is it valid
	 * 
	 * @return
	 */
	public boolean isValid() {
		return markers.isEmpty() || markers.size() >= 3;
	}

	/**
	 * Is it deleted
	 * 
	 * @return
	 */
	public boolean isDeleted() {
		return markers.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Marker marker) {
		if (markers.remove(marker)) {
			marker.remove();
			parentPolygon.update();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addNew(Marker marker) {
		GoogleMapShapeMarkers.addMarkerAsPolygon(marker, markers);
	}

}
