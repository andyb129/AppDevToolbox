package uk.co.barbuzz.snippet.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "snippet_table")
@Parcelize
data class Snippet(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "abbrev") val abbreviation: String,
    @ColumnInfo(name = "snippet") val snippet: String
) : Parcelable
