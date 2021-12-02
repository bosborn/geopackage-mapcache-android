package mil.nga.mapcache.view.map.grid.mgrs;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import mil.nga.geopackage.BoundingBox;
import mil.nga.mapcache.view.map.grid.Grid;

/**
 * A GridZoneDesignator.
 */
public class GridZoneDesignator {
    /**
     * The zone letter.
     */
    private String zoneLetter;

    /**
     * The hemisphere.
     */
    private String hemisphere;

    /**
     * The zone number.
     */
    private int zoneNumber;

    /**
     * The bounding box.
     */
    private BoundingBox zoneBounds;

    /**
     * The utm bounds.
     */
    private double[] zoneUtmBounds;

    /**
     * The zones grid.
     */
    private Grid zonePolygon;

    /**
     * Constructs a new grid zone designator.
     *
     * @param zoneLetter The zone's letter.
     * @param zoneNumber The zone's number.
     * @param zoneBounds The zones bounds.
     */
    public GridZoneDesignator(String zoneLetter, int zoneNumber, BoundingBox zoneBounds) {
        this.zoneLetter = zoneLetter;
        this.hemisphere = zoneLetter.compareTo("N") < 0 ? UTM.HEMISPHERE_SOUTH : UTM.HEMISPHERE_NORTH;
        this.zoneNumber = zoneNumber;
        this.zoneBounds = zoneBounds;

        UTM ll = UTM.from(new LatLng(zoneBounds.getMinLatitude(), zoneBounds.getMinLongitude()), zoneNumber, this.hemisphere);
        UTM lr = UTM.from(new LatLng(zoneBounds.getMinLatitude(), zoneBounds.getMaxLongitude()), zoneNumber, this.hemisphere);
        UTM ul = UTM.from(new LatLng(zoneBounds.getMaxLatitude(), zoneBounds.getMinLongitude()), zoneNumber, this.hemisphere);
        UTM ur = UTM.from(new LatLng(zoneBounds.getMaxLatitude(), zoneBounds.getMaxLongitude()), zoneNumber, this.hemisphere);
        this.zoneUtmBounds = new double[]{
                Math.min(ll.getEasting(), ul.getEasting()),
                Math.min(ll.getNorthing(), lr.getNorthing()),
                Math.max(lr.getEasting(), ur.getEasting()),
                Math.max(ul.getNorthing(), ur.getNorthing())
        };
        this.zonePolygon = this.generatePolygon(zoneBounds);
    }

    /**
     * Gets the zones label text.
     *
     * @return The zones label.
     */
    private String getLabelText() {
        return String.valueOf(zoneNumber) + this.zoneLetter;
    }

    /**
     * The center point of the zone.
     *
     * @return The center point.
     */
    private LatLng getCenter() {
        double centerLon = (this.zoneBounds.getMaxLongitude() + this.zoneBounds.getMinLongitude()) / 2.0;
        double centerLat = (this.zoneBounds.getMaxLatitude() + this.zoneBounds.getMinLatitude()) / 2.0;
        return new LatLng(centerLat, centerLon);
    }

    /**
     * The zone's letter.
     *
     * @return The zone's letter.
     */
    private String getZoneLetter() {
        return this.zoneLetter;
    }

    /**
     * The zone's number.
     *
     * @return The zone's number.
     */
    private int getZoneNumber() {
        return this.zoneNumber;
    }

    /**
     * The zone's bounds.
     *
     * @return The zone's bounds.
     */
    private BoundingBox getZoneBounds() {
        return this.zoneBounds;
    }

    /**
     * Indicates if the specified bounding box is within this zone's boundary.
     *
     * @param bbox The bounding box to check for containment.
     * @return True if the bbox is within, false otherwise.
     */
    private boolean within(BoundingBox bbox) {
        return zoneBounds.contains(bbox);
    }

    /**
     * Generates a grid for the specified bounds.
     *
     * @param zoneBounds The bounds to create a grid for.
     * @return The new grid.
     */
    private Grid generatePolygon(BoundingBox zoneBounds) {
        Grid grid = new Grid();
        grid.setBounds(zoneBounds);

        return grid;
    }

    /**
     * Generates the polygon and label
     *
     * @param boundingBox The bounding box.
     * @param precision   The precision.
     * @param easting     The easting coordinate.
     * @param northing    The northing coordinate.
     * @param newEasting  The new easting coordinate.
     * @param newNorthing The new northing coordinate.
     * @param polygons    The list to add new grids to.
     */
    private void generatePolygonAndLabel(BoundingBox boundingBox, double precision, double easting, double northing, double newEasting, double newNorthing, List<Grid> polygons) {
        LatLng ll = new UTM(this.zoneNumber, this.hemisphere, easting, northing).toLatLng();
        LatLng ul = new UTM(this.zoneNumber, this.hemisphere, easting, newNorthing).toLatLng();
        LatLng ur = new UTM(this.zoneNumber, this.hemisphere, newEasting, newNorthing).toLatLng();
        LatLng lr = new UTM(this.zoneNumber, this.hemisphere, newEasting, northing).toLatLng();
  /*      boundingBox.
    const intersection = intersect(this.generatePolygon([[ll.asArray(), ul.asArray(), ur.asArray(), lr.asArray(), ll.asArray()]]), this.zonePolygon)
        if (intersection != null) {
            polygons.push(intersection)
            ll = new LatLng(intersection.geometry.coordinates[0][0][1], intersection.geometry.coordinates[0][0][0])
            ul = new LatLng(intersection.geometry.coordinates[0][1][1], intersection.geometry.coordinates[0][1][0])
            ur = new LatLng(intersection.geometry.coordinates[0][2][1], intersection.geometry.coordinates[0][2][0])
            lr = new LatLng(intersection.geometry.coordinates[0][3][1], intersection.geometry.coordinates[0][3][0])
            if (precision === 100000) {
                // determine center easting/northing given the bounds, then convert to lat/lng
        const utm1 = UTM.from(ll, this.zoneNumber, this.hemisphere)
        const utm2 = UTM.from(ul, this.zoneNumber, this.hemisphere)
        const utm3 = UTM.from(ur, this.zoneNumber, this.hemisphere)
        const utm4 = UTM.from(lr, this.zoneNumber, this.hemisphere)
        const minEasting = Math.min(utm1.getEasting(), utm2.getEasting())
        const maxEasting = Math.max(utm3.getEasting(), utm4.getEasting())
        const minNorthing = Math.min(utm1.getNorthing(), utm4.getNorthing())
        const maxNorthing = Math.max(utm2.getNorthing(), utm3.getNorthing())
        const labelCenter = LatLng.from(new UTM(this.zoneNumber, this.hemisphere, minEasting + ((maxEasting - minEasting) / 2), minNorthing + ((maxNorthing - minNorthing) / 2)))

        const intersectionBounds = [
                Math.min(ll.longitude, ul.longitude),
                        Math.min(ll.latitude, lr.latitude),
                        Math.max(lr.longitude, ur.longitude),
                        Math.max(ul.latitude, ur.latitude)
        ]
                labels.push(new Label(get100KId(easting, northing, this.zoneNumber), labelCenter, intersectionBounds, this.zoneLetter, this.zoneNumber))
            }
        }*/
    }

    /**
     * Return zone grids at provided precision
     *
     * @param boundingBox The bounding box.
     * @param precision   The precision.
     * @return The list of grids for the zone.
     */
    private List<Grid> polygonsAndLabelsInBounds(BoundingBox boundingBox, double precision) {
        List<Grid> grids = new ArrayList<>();
        if (precision == 0) {
            Grid grid = this.generatePolygon(zoneBounds);
            grid.setText(getLabelText());
            grids.add(grid);
        } else {
            double minLat = Math.max(boundingBox.getMinLatitude(), this.zoneBounds.getMinLatitude());
            double maxLat = Math.min(boundingBox.getMaxLatitude(), this.zoneBounds.getMaxLatitude());
            double minLon = Math.max(boundingBox.getMinLongitude(), this.zoneBounds.getMaxLongitude());
            double maxLon = Math.min(boundingBox.getMaxLatitude(), this.zoneBounds.getMaxLongitude());

            if (this.hemisphere.equals(UTM.HEMISPHERE_NORTH)) {
                UTM lowerLeftUTM = UTM.from(new LatLng(minLat, minLon), this.zoneNumber, this.hemisphere);
                double lowerLeftEasting = (Math.floor(lowerLeftUTM.getEasting() / precision) * precision);
                double lowerLeftNorthing = (Math.floor(lowerLeftUTM.getNorthing() / precision) * precision);

                UTM upperRightUTM = UTM.from(new LatLng(maxLat, maxLon), this.zoneNumber, this.hemisphere);
                double endEasting = (Math.ceil(upperRightUTM.getEasting() / precision) * precision);
                double endNorthing = (Math.ceil(upperRightUTM.getNorthing() / precision) * precision);

                double easting = lowerLeftEasting;
                while (easting <= endEasting) {
                    double newEasting = easting + precision;
                    double northing = lowerLeftNorthing;
                    while (northing <= endNorthing) {
                        double newNorthing = northing + precision;
                        this.generatePolygonAndLabel(boundingBox, precision, easting, northing, newEasting, newNorthing, grids);
                        northing = newNorthing;
                    }
                    easting = newEasting;
                }
            } else {
                UTM upperLeftUTM = UTM.from(new LatLng(maxLat, minLon), this.zoneNumber, this.hemisphere);
                double upperLeftEasting = (Math.floor(upperLeftUTM.getEasting() / precision) * precision);
                double upperLeftNorthing = (Math.ceil(upperLeftUTM.getNorthing() / precision + 1) * precision);
                if (this.zoneLetter.equals("M")) {
                    upperLeftNorthing = 10000000.0;
                    upperLeftUTM = new UTM(upperLeftUTM.getZoneNumber(), UTM.HEMISPHERE_SOUTH, upperLeftUTM.getEasting(), upperLeftUTM.getNorthing());
                }
                UTM lowerRightUTM = UTM.from(new LatLng(minLat, maxLon), this.zoneNumber, this.hemisphere);
                double lowerRightEasting = (Math.ceil(lowerRightUTM.getEasting() / precision) * precision);
                double lowerRightNorthing = (Math.floor(lowerRightUTM.getNorthing() / precision) * precision);
                for (double easting = upperLeftEasting; easting <= lowerRightEasting; easting += precision) {
                    double northing = upperLeftNorthing;
                    while (northing >= lowerRightNorthing) {
                        double newNorthing = northing - precision;
                        this.generatePolygonAndLabel(boundingBox, precision, easting, newNorthing, easting + precision, northing, grids);
                        northing = newNorthing;
                    }
                }
            }
        }

        return grids;
    }
}
