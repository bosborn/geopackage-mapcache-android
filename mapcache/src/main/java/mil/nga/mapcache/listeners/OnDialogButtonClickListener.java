package mil.nga.mapcache.listeners;

/**
 * Listener to be implemented for opening dialog windows
 */
public interface OnDialogButtonClickListener {

    /**
     * Delete GeoPackage
     * @param gpName GeoPackage name
     */
    void onDeleteGP(String gpName);

    /**
     * Show the details dialog of a GeoPackage
     * @param gpName - GeoPackage name
     */
    void onDetailGP(String gpName);

    /**
     * Rename a GeoPackage
     * @param originalName GeoPackage original name
     * @param newName New GeoPackage name
     */
    void onRenameGP(String originalName, String newName);

    /**
     * Share a GeoPackage
     * @param gpName GeoPackage name
     */
    void onShareGP(String gpName);

    /**
     * Copy a GeoPackage
     * @param gpName GeoPackage name
     * @param newName name of the new GeoPackage that you're making
     */
    void onCopyGP(String gpName, String newName);

    /**
     * Delete a layer from a GeoPackage
     * @param gpName GeoPackage name
     * @param layerName Layer name to delete
     */
    void onDeleteLayer(String gpName, String layerName);

    /**
     * Rename a layer in a GeoPackage
     * @param gpName GeoPackage name
     * @param layerName Current layer name
     * @param newLayerName New layer name
     */
    void onRenameLayer(String gpName, String layerName, String newLayerName);

    /**
     * Copy a layer in a GeoPackage
     * @param gpName GeoPackage name
     * @param layerName Layer name to copy
     */
    void onCopyLayer(String gpName, String oldLayer, String layerName);

    /**
     * Cancel button
     */
    void onCancelButtonClicked();
}
