package com.jonadaly.brood.db.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity()
data class Brood(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var uuid: String = "",
    var name: String = "",
    var created: String = "",
    @Ignore var chickens: List<Chicken> = ArrayList()
)