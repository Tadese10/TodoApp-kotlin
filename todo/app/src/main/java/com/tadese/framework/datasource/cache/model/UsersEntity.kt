package com.tadese.framework.datasource.cache.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tadese.business.domain.model.comment.Comment
import com.tadese.business.domain.model.login.UserAddress
import com.tadese.framework.datasource.cache.model.UsersEntity.Companion.table_name

@Entity(tableName = table_name)
data class UsersEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name="id")
    val id: Int,

    @ColumnInfo(name="name")
    val name : String,

    @ColumnInfo(name="username")
    val username: String,

    @ColumnInfo(name="email")
    val email: String,

    @ColumnInfo(name="phone")
    val phone : String,

    @ColumnInfo(name="website")
    val website : String,

    @ColumnInfo(name="address")
    val address: String
) {

    @Ignore
    val addressObject = convertStringUserAddressToJson(address)

    companion object {
        const val table_name = "users"

        fun convertUserAddressToString(address: UserAddress?) : String{
            return if(address == null){
                ""
            }else{
                Gson()
                    .toJson(
                        address,
                        object: TypeToken<UserAddress>() {}.type
                    )
            }
        }

        fun convertStringUserAddressToJson(comments: String) : UserAddress{
            return if(comments.isNullOrEmpty()){
                UserAddress(
                    "","","","",null
                )
            }else{
                Gson()
                    .fromJson(
                        comments,
                        object: TypeToken<UserAddress>() {}.type
                    )
            }
        }
    }
}