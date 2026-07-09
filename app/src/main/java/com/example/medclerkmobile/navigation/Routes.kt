package com.example.medclerkmobile.navigation

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val NEW_LOGBOOK_ENTRY = "logbook/new"

    const val SYSTEM_ID_ARG = "systemId"
    const val SIGN_ID_ARG = "signId"
    const val SKILL_ID_ARG = "skillId"

    const val SYSTEM_DETAIL = "library/system/{$SYSTEM_ID_ARG}"
    const val SIGN_DETAIL = "library/sign/{$SIGN_ID_ARG}"
    const val SKILL_DETAIL = "library/skill/{$SKILL_ID_ARG}"

    fun systemDetail(systemId: Int) = "library/system/$systemId"
    fun signDetail(signId: Int) = "library/sign/$signId"
    fun skillDetail(skillId: Int) = "library/skill/$skillId"
}
