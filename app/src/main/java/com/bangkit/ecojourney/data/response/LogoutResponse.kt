package com.bangkit.ecojourney.data.response

import com.google.gson.annotations.SerializedName

data class LogoutResponse(

	@field:SerializedName("data")
	val data: LogoutData,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class LogoutData(

	@field:SerializedName("user")
	val user: User
)

data class User(

	@field:SerializedName("uid")
	val uid: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("username")
	val username: String
)
