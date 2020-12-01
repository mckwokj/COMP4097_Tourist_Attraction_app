package edu.hkbu.comp.comp4097.comp4097project.data

import androidx.room.*

@Dao
interface PlaceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: PlaceInfo)
    @Query("Select * from PlaceInfo where xid = :xid")
    suspend fun findPlaceByXid(xid: String): PlaceInfo
    @Query("Select * from PlaceInfo where dist <= :dist")
    suspend fun findXidByDist(dist: Int): List<PlaceInfo>
    @Query("Select * from PlaceInfo")
    suspend fun findAllPlaces(): List<PlaceInfo>
    @Query("Delete from PlaceInfo")
    suspend fun deleteAllPlaces()
    @Delete
    suspend fun delete(vararg place: PlaceInfo)
    @Update
    suspend fun update(place: PlaceInfo)
}