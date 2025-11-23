package com.edufelip.meer.mapper

import com.edufelip.meer.domain.auth.AuthenticatedUser
import com.edufelip.meer.dto.UserDto

fun AuthenticatedUser.toDto() = UserDto(
    id = id.toString(),
    name = name,
    email = email
)
