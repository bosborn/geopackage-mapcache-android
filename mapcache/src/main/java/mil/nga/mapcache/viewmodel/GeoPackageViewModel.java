package mil.nga.mapcache.viewmodel;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mil.nga.geopackage.BoundingBox;
import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.core.contents.Contents;
import mil.nga.geopackage.extension.scale.TileScaling;
import mil.nga.geopackage.io.GeoPackageProgress;
import mil.nga.mapcache.data.GeoPackageDatabase;
import mil.nga.mapcache.data.GeoPackageDatabases;
import mil.nga.mapcache.data.GeoPackageTable;
import mil.nga.mapcache.indexer.IIndexerTask;
import mil.nga.mapcache.repository.GeoPackageRepository;
import mil.nga.sf.GeometryType;


public class GeoPackageViewModel extends AndroidViewModel implements IIndexerTask {

    /**
     * Repository to access GeoPackages and provide data
     */
    private GeoPackageRepository repository;

    /**
     * List of active tables
     */
    private MutableLiveData<List<GeoPackageTable>> activeTables = new MutableLiveData<>();

    /**
     * List of GeoPackageTable objects organized by GeoPackage Name
     */
    private MutableLiveData<List<List<GeoPackageTable>>> geoPackageTables = new MutableLiveData<List<List<GeoPackageTable>>>();

    /**
     * List of (closed) GeoPackage objects
     */
    private MutableLiveData<List<GeoPackage>> geoPackages = new MutableLiveData<>();

    /**
     * geos is a GeoPackageDatabases object powered by the repository.  Contains a list of all
     * GeoPackageTables opened in this project
     */
    private MutableLiveData<GeoPackageDatabases> geos = new MutableLiveData<>();

    /**
     * active is a GeoPackageDatabases object powered by the repository.  Contains a list
     * ONLY the GeoPackageTables that are set to active
     */
    private MutableLiveData<GeoPackageDatabases> active = new MutableLiveData<>();


    /**
     * Constructor
     * @param application application
     */
    public GeoPackageViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * Init
     * Create our live data objects and generate the data for the first time
     */
    public void init() {
        repository = new GeoPackageRepository(getApplication());
        activeTables.setValue(new ArrayList<GeoPackageTable>());
        geos = getGeos();
        active = getActive();
//        generateGeoPackageList();
        regenerateGeoPackageTableList();
//        geoPackageTables.setValue(geoList);
//        geoPackages.setValue(geoPackageList);
    }


    /**
     * geos GeoPackageTables --------------
     */
    /**
     *  Get geos live data from repository
     */
    public MutableLiveData<GeoPackageDatabases> getGeos(){
        return repository.getGeos();
    }

    /**
     *  Returns the GeoPackage's size from the GeoPackageDatabases storage by accessing geos
     */
    public String getGeoPackageSize(String geoPackageName){
        String size = "0mb";
        if(geos.getValue().getDatabase(geoPackageName) != null) {
            size = geos.getValue().getDatabase(geoPackageName).getSize();
        }
        return size;
    }

    /**
     * Returns a GeoPackageDatabase from the geos list
     * @param geoPackageName The name of the geopackage to return
     * @return a GeoPackageDatabase object
     */
    public GeoPackageDatabase getGeoByName(String geoPackageName){
        return geos.getValue().getDatabase(geoPackageName);
    }

    /**
     * Returns the GeoPackage's featureTable size from the GeoPackageDatabases storage
     * @param geoPackageName Name of the geopackage to search for
     * @return count - int of the number of feature tables
     */
    public int getFeatureCount(String geoPackageName){
        int count = 0;
        if(geos.getValue().getDatabase(geoPackageName) != null) {
            count = geos.getValue().getDatabase(geoPackageName).getFeatureCount();
        }
        return count;
    }

    /**
     *  Returns the GeoPackage's tileTable size from the GeoPackageDatabases storage
     * @param geoPackageName Name of the geopackage to search for
     * @return count - int of the number of tile tables
     */
    public int getTileCount(String geoPackageName){
        int count = 0;
        if(geos.getValue().getDatabase(geoPackageName) != null) {
            count = geos.getValue().getDatabase(geoPackageName).getTileCount();
        }
        return count;
    }

    /**
     * Returns true if the given table name exists in the given geopackage name
     */
    public boolean tableExistsInGeoPackage(String geoName, String tableName){
        return repository.tableExistsInGeoPackage(geoName, tableName);
    }




    /**
     * Active GeoPackageTables --------------
     */
    /**
     *  Get active live data from repository
     */
    public MutableLiveData<GeoPackageDatabases> getActive(){
        return repository.getActive();
    }

    /**
     * Sets the layer's active state to the given value
     * @param table GeoPackageTable type
     */
    public boolean setLayerActive(GeoPackageTable table){
        return repository.setLayerActive(table);
    }

    /**
     * Sets all the layers active in the given geopackage
     * @param db GeoPackageDatabase to add
     * @param active should all layers be active or inactive
     * @return true if all layers are enabled
     */
    public boolean setAllLayersActive(boolean active, GeoPackageDatabase db){
        return repository.setAllLayersActive(active, db);
    }

    /**
     * Remove all active layers for the given database
     */
    public boolean removeActiveTableLayers(String geoPackageName){
        return repository.removeActiveForGeoPackage(geoPackageName);
    }

    /**
     * Search for the layer name in the GeoPackage and return true if it's found and deleted
     * @param geoPackageName Name of the GeoPackage to remove the active layer from
     * @param layerName Name of the layer to remove
     * @return true if the layer was found and deleted
     */
    public boolean removeActiveLayer(String geoPackageName, String layerName){
        return repository.removeActiveLayer(geoPackageName, layerName);
    }

    /**
     * Remove all active tables
     */
    public void clearAllActive(){
        repository.clearAllActive();
    }






    /**
     * List<List<GeoPackageTable>
     */
    public void setGeoPackageTables(List<List<GeoPackageTable>> newGeoPackageTables) {
        geoPackageTables.setValue(newGeoPackageTables);
    }
    public MutableLiveData<List<List<GeoPackageTable>>> getGeoPackageTables() {
        return geoPackageTables;
    }

    /**
     * List<GeoPackage>
     */
    public MutableLiveData<List<GeoPackage>> getGeoPackages() {
        return geoPackages;
    }
    public void setGeoPackages(List<GeoPackage> geoPackages) {
        this.geoPackages.setValue(geoPackages);
    }

    public MutableLiveData<List<GeoPackageTable>> getActiveTables() {
        return activeTables;
    }
    public void setActiveTables(List<GeoPackageTable> newTables){
        activeTables.setValue(newTables);
    }

    /**
     *  Return true if the given table is in the list of active tables
     * @param geoPackageName
     * @param tableName
     * @return
     */
    public boolean isTableActive(String geoPackageName, String tableName){
        if(getActiveTables().getValue() != null)
        {
            for(GeoPackageTable table : getActiveTables().getValue()){
                if(table.getDatabase().equalsIgnoreCase(geoPackageName) && table.getName().equalsIgnoreCase(tableName)){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Add the table to the activeTables list (used to enable a layer on the map)
     * @param newTable
     */
    public void addToTables(GeoPackageTable newTable){
        //List<GeoPackageTable> newTables = activeTables.getValue();
        //newTables.add(newTable);
        //activeTables.postValue(newTables);
    }

    /**
     * Find the given table in the table list, and add to activeTables if found
     * @param tableName
     * @param geoPackageName
     * @return true if the table was added
     */
    public boolean addTableByName(String tableName, String geoPackageName){
        // Use tableName and GeoPackageName to find the geoPackageTable in the livedata list
        for(List<GeoPackageTable> geoTableList : getGeoPackageTables().getValue()){
            if(geoTableList.size() > 0) {
              if(geoTableList.get(0).getDatabase().equalsIgnoreCase(geoPackageName)) {
                  for (GeoPackageTable table : geoTableList) {
                      if (table.getName().equalsIgnoreCase(tableName)){
                          // Save the geopackage with the layer as active
                          repository.getTableObject(geoPackageName, tableName, true);
                          // Add to our list of active tables
                          addToTables(table);
                          return true;
                      }
                  }
              }
            }
        }
        return false;
    }



    /**
     * Find the given table in the table list, and remove from activeTables if found
     * @param tableName
     * @param geoPackageName
     * @return true if the table was removed
     */
    public boolean removeActiveTableByName(String tableName, String geoPackageName){
        List<GeoPackageTable> currentTables = activeTables.getValue();
        if(currentTables != null && currentTables.size() > 0) {
            for (GeoPackageTable table : currentTables) {
                if(table.getName().equalsIgnoreCase(tableName) && table.getDatabase().equalsIgnoreCase(geoPackageName)){
                    currentTables.remove(table);
                    activeTables.postValue(currentTables);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Remove the given layer from a geopackage
     */
    public GeoPackageDatabase removeLayerFromGeo(String geoPackageName, String layerName){
        if(repository.removeLayerFromGeo(geoPackageName, layerName)) {
            GeoPackageDatabase db = repository.getDatabaseByName(geoPackageName);
            regenerateGeoPackageTableList();
            return db;
        }
        return null;
    }

    /**
     * Rename a layer in a geopackage
     */
    public GeoPackageDatabase renameLayer(String geoPackageName, String layerName, String newLayerName){
        if(repository.renameLayer(geoPackageName, layerName, newLayerName)){
            GeoPackageDatabase db = repository.getDatabaseByName(geoPackageName);
            regenerateGeoPackageTableList();
            return db;
        }
        return null;
    }

    /**
     * Enable all layers of the given geopackage name
     */
    public boolean enableAllLayers(String geoPackageName){
        List<GeoPackageTable> currentTables = activeTables.getValue();
        if(currentTables != null && currentTables.size() > 0) {
            Iterator<GeoPackageTable> tableIterator = currentTables.iterator();
            // First remove all layers from the list that match the given name
            while (tableIterator.hasNext()) {
                // Only delete if the geopackage name matches
                GeoPackageTable table = tableIterator.next();
                if (table.getDatabase().equalsIgnoreCase(geoPackageName)) {
                    tableIterator.remove();
                }
            }
        }

        // Then just add all layers to the active list
        for(List<GeoPackageTable> geoTableList : getGeoPackageTables().getValue()){
            if(geoTableList.size() > 0) {
                if(geoTableList.get(0).getDatabase().equalsIgnoreCase(geoPackageName)) {
                    for (GeoPackageTable table : geoTableList) {
                            currentTables.add(table);
                    }
                }
            }
        }


        activeTables.postValue(currentTables);
        return true;
    }





    /**
     * Get a single GeoPackage by name
     * @param name
     * @return
     */
    public GeoPackage getGeoPackageByName(String name){
        if(repository == null){
            repository = new GeoPackageRepository(getApplication());
        }
        return repository.getGeoPackageByName(name);
    }

    /**
     * Rename a GeoPackage, then find and change that old name in the activeTables list
     * @param oldName
     * @param newName
     * @return
     */
    public boolean setGeoPackageName(String oldName, String newName){
        if(repository == null){
            repository = new GeoPackageRepository(getApplication());
        }
        if(repository.setGeoPackageName(oldName, newName)) {
            regenerateGeoPackageTableList();
            return true;
        }
        return false;
    }


    /**
     * Iterate through the current list of active tables.  Find any table that matches the old
     * geopackage (database) name, and rename it to the new one
     * @param oldName
     * @param newName
     * @return
     */
    private boolean renameActiveGeoPackages(String oldName, String newName){
        boolean updated = false;
        if(getActiveTables().getValue() != null)
        {
            List<GeoPackageTable> activeGeos = getActiveTables().getValue();
            for(GeoPackageTable table : activeGeos){
                if(table.getDatabase().equalsIgnoreCase(oldName)){
                    updated = true;
                    table.setDatabase(newName);
                }
            }
            if(updated) {
                setActiveTables(activeGeos);
            }
            return updated;
        }
        return false;
    }

    /**
     * Update the List of GeoPackageTable by asking the repository to update
     */
    public void regenerateGeoPackageTableList(){
        List<List<GeoPackageTable>> databaseTables = repository.regenerateTableList();
         geoPackageTables.postValue(databaseTables);
//         generateGeoPackageList();
        for(List<GeoPackageTable> tableList : databaseTables){
            for(GeoPackageTable table : tableList){
                if(table.isActive()){
                    addToTables(table);
                }
            }

        }
        geoPackages.postValue(repository.getGeoPackages());
//        geos.postValue(repository.getGeos().getValue());
    }

    /**
     * Generate the list of geopackage objects
     */
    public void generateGeoPackageList(){
        if(repository == null){
            repository = new GeoPackageRepository(getApplication());
        }
        geoPackages.postValue(repository.getGeoPackages());

    }



    /**
     * Delete GeoPackage and regenerate the list of GeoPackages
     */
    public boolean deleteGeoPackage(String geoPackageName){
        if(repository.deleteGeoPackage(geoPackageName)){
            regenerateGeoPackageTableList();
            return true;
        }
        return false;
    }

    /**
     * Create GeoPackage and regenerate the list of GeoPackages
     */
    public boolean createGeoPackage(String geoPackageName){
        if(repository.createGeoPackage(geoPackageName)){
            //generateGeoPackageList();
            regenerateGeoPackageTableList();
            return true;
        }
        return false;
    }

    /**
     * import a geopackage from url.  GeoPackageProgress should be an instance of DownloadTask
     */
    public boolean importGeoPackage(String name, URL source, GeoPackageProgress progress){
        if(repository.importGeoPackage(name, source, progress)){
            regenerateGeoPackageTableList();
            return true;
        }
        return false;
    }


    /**
     * Copy GeoPackage and regenerate the list of GeoPackages
     */
    public boolean copyGeoPackage(String geoPackageName, String newName){
        if(repository.copyGeoPackage(geoPackageName, newName)){
            regenerateGeoPackageTableList();
            return true;
        }
        return false;
    }

    /**
     * Copy a layer and regenerate the list of GeoPackages
     */
    public boolean copyLayer(String geoPackageName, String currentLayer, String newLayerName){
        if(repository.copyLayer(geoPackageName, currentLayer, newLayerName)){
            regenerateGeoPackageTableList();
            return true;
        }
        return false;
    }


    /**
     *  Returns a database file
     */
    public File getDatabaseFile(String database){
        return repository.getDatabaseFile(database);
    }

    /**
     *  Returns true if it's an external db
     */
    public boolean isExternal(String database){
        return repository.isExternal(database);
    }

    /**
     *  Returns true if the db exists
     */
    public boolean exists(String database){
        return repository.exists(database);
    }

    /**
     * Import an GeoPackage as an external file link without copying locally
     *
     * @param path     full file path
     * @param database name to reference the database
     * @return true if imported successfully
     */
    public boolean importGeoPackageAsExternalLink(String path, String database){
        if(repository.importGeoPackageAsExternalLink(path, database)){
            regenerateGeoPackageTableList();
            return true;
        }
        return false;
    }

    /**
     * Import a GeoPackage stream
     *
     * @param database database name to save as
     * @param stream   GeoPackage stream to import
     * @param progress progress tracker
     * @return true if loaded
     */
    public boolean importGeoPackage(String database, InputStream stream,
                                    GeoPackageProgress progress){
        if(repository.importGeoPackage(database, stream, progress)){
//            // Then index any feature tables
//            List<String> newFeatures = repository.getFeatureTables(database);
//            if(!newFeatures.isEmpty()){
//                for(String tableName : newFeatures){
//                    indexFeatures(activity, database, tableName);
//
//                }
//            }
            regenerateGeoPackageTableList();
            return true;
        }
        return false;
    }

    /**
     *  Returns the list of tile tables for a geopackage
     */
    public List<String> getTileTables(String database){
        return repository.getTileTables(database);
    }

    /**
     *  Returns the list of feature tables for a geopackage
     */
    public List<String> getFeatureTables(String database){
        return repository.getFeatureTables(database);
    }

    /**
     * Get a GeoPackageTable object and set the active state
     */
    public GeoPackageTable getTableObjectActive(String gpName, String layerName){
        GeoPackageTable table = repository.getTableObject(gpName, layerName, null);
        table.setActive(isTableActive(gpName, layerName));
        return table;
    }

    /**
     * Get a GeoPackageTable object
     */
    public GeoPackageTable getTableObject(String gpName, String layerName){
        return repository.getTableObject(gpName, layerName, null);
    }

    public Contents getTableContents(String gpName, String tableName){
        return repository.getTableContents(gpName, tableName);
    }



    /** GET RID OF THE Activity context! **/
//    /**
//     * Index the given features table
//     */
//    public boolean indexFeatures(Activity activity, String database, String tableName){
//        IndexerTask.indexFeatures(activity, GeoPackageViewModel.this, database, tableName, FeatureIndexType.GEOPACKAGE);
//        return true;
//    }

    /**
     * Create feature table in the given geopackage
     */
    public boolean createFeatureTable(String gpName, BoundingBox boundingBox, GeometryType geometryType, String tableName){
        if(repository.createFeatureTable(gpName, boundingBox, geometryType, tableName)){
            regenerateGeoPackageTableList();
            return true;
        }
        return false;
    }

    /**
     * Create tile table in the given GeoPackage
     * @return
     */
    public boolean createTileTable(String gpName, BoundingBox boundingBox, long epsg, String tableName, TileScaling scaling){
        repository.createTileTable(gpName, boundingBox, epsg, tableName, scaling);
        regenerateGeoPackageTableList();
        return true;
    }


    /**
     * Get an alert dialog filled with a GeoPackage's details
     * @param geoPackageName
     * @param activity
     * @return
     */
    public AlertDialog getGeoPackageDetailDialog(String geoPackageName, Activity activity){
        return repository.getGeoPackageDetailDialog(geoPackageName, activity);
    }

    // Indexing functions
    @Override
    public void onIndexerCancelled(String result) {

    }
    @Override
    public void onIndexerPostExecute(String result) {

    }
}
