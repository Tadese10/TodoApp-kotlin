package com.tadese.framework.datasource.cache.model

import com.tadese.business.domain.model.login.LoginUser
import com.tadese.business.domain.util.EntityMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggedInUserCacheMapper
@Inject
    constructor(): EntityMapper<UsersEntity, LoginUser> {

    override fun mapFromEntity(entity: UsersEntity): LoginUser {
        return LoginUser(
            id = entity.id,
            name = entity.name,
            username = entity.username,
            email = entity.email,
            phone = entity.phone,
            website = entity.website,
            address = UsersEntity.convertStringUserAddressToJson(entity.address)
        )
    }

    override fun mapToEntity(domainModel: LoginUser): UsersEntity {
        return UsersEntity(
            id = domainModel.id,
            name = domainModel.name,
            username = domainModel.username,
            email = domainModel.email,
            phone = domainModel.phone,
            website = domainModel.website,
            address = UsersEntity.convertUserAddressToString(domainModel.address)
        )
    }


}