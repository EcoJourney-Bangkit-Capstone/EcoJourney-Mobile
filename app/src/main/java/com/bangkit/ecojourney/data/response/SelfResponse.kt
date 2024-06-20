package com.bangkit.ecojourney.data.response

import com.google.gson.annotations.SerializedName

data class SelfResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class UserMetadata(

	@field:SerializedName("LastRefreshTimestamp")
	val lastRefreshTimestamp: Long? = null,

	@field:SerializedName("LastLogInTimestamp")
	val lastLogInTimestamp: Long? = null,

	@field:SerializedName("CreationTimestamp")
	val creationTimestamp: Long? = null
)

data class ProviderUserInfoItem(

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("displayName")
	val displayName: String? = null,

	@field:SerializedName("providerId")
	val providerId: String? = null,

	@field:SerializedName("rawId")
	val rawId: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class Data(

	@field:SerializedName("MultiFactor")
	val multiFactor: MultiFactor? = null,

	@field:SerializedName("displayName")
	val displayName: String? = null,

	@field:SerializedName("UserMetadata")
	val userMetadata: UserMetadata? = null,

	@field:SerializedName("CustomClaims")
	val customClaims: Any? = null,

	@field:SerializedName("EmailVerified")
	val emailVerified: Boolean? = null,

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("TenantID")
	val tenantID: String? = null,

	@field:SerializedName("providerId")
	val providerId: String? = null,

	@field:SerializedName("rawId")
	val rawId: String? = null,

	@field:SerializedName("ProviderUserInfo")
	val providerUserInfo: List<ProviderUserInfoItem?>? = null,

	@field:SerializedName("TokensValidAfterMillis")
	val tokensValidAfterMillis: Long? = null,

	@field:SerializedName("Disabled")
	val disabled: Boolean? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class MultiFactor(

	@field:SerializedName("EnrolledFactors")
	val enrolledFactors: Any? = null
)
