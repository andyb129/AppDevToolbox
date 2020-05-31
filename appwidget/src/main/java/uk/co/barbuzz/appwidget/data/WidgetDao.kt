package uk.co.barbuzz.appwidget.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface WidgetDao {

    @Query("SELECT * FROM widget")
    fun allWidgets(): Single<List<Widget>>

    @Query("SELECT * FROM widget WHERE appWidgetId = :appWidgetId")
    fun findWidgetById(appWidgetId: Int): Maybe<Widget>

    @Query("SELECT * from widget where appWidgetId IN (:appWidgetId)")
    fun findWidgetsById(appWidgetId: IntArray): Single<List<Widget>>

    @Insert(onConflict = ABORT)
    fun insertWidget(vararg widget: Widget): Completable

    @Update(onConflict = IGNORE)
    fun updateWidget(widget: Widget): Completable

    @Query("UPDATE widget SET appWidgetId = :appWidgetId WHERE appWidgetId = -1")
    fun updateTempWidgetId(appWidgetId: Int): Completable

    @Delete
    fun deleteWidgets(widgets: List<Widget>): Completable
}
