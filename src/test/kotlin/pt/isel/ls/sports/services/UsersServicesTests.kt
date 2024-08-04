package pt.isel.ls.sports.services

import org.junit.Test
import pt.isel.ls.sports.data.DataMem
import pt.isel.ls.sports.domain.UserCredentials
import pt.isel.ls.sports.domain.UserInput
import pt.isel.ls.sports.services.comp.AppException
import pt.isel.ls.sports.services.comp.AppExceptionStatus
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UsersServicesTests {

    private val data = DataMem()
    private val services = Services(data).usersServices

    @Test
    fun create_user() {
        val newUser = UserInput("Fiona", "iloveshrek@gmail.com", "shrekinho")
        val userOutput = services.createUser(newUser)
        assertEquals(4, userOutput.number)
    }

    @Test
    fun create_user_with_repeated_email() {
        val appException =
            assertFailsWith<AppException> {
                val newUser = UserInput("Fiona", "iloveshrek@gmail.com", "shrekinho")
                repeat(2) {
                    services.createUser(newUser)
                }
            }
        assertEquals("The email iloveshrek@gmail.com is already in use", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun create_user_with_empty_name() {
        val appException =
            assertFailsWith<AppException> {
                val newUser = UserInput("", "noname@gmail.com", "noname")
                services.createUser(newUser)
            }
        assertEquals("Empty name", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun create_user_with_empty_email() {
        val appException =
            assertFailsWith<AppException> {
                val newUser = UserInput("No Email", "", "noemail")
                services.createUser(newUser)
            }
        assertEquals("Empty email", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun create_user_with_invalid_email() {
        val appException =
            assertFailsWith<AppException> {
                val newUser = UserInput("Invalid Email", "1234", "noemail")
                services.createUser(newUser)
            }
        assertEquals("Invalid email", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun create_user_with_empty_password() {
        val appException =
            assertFailsWith<AppException> {
                val newUser = UserInput("No Password", "nopassword@gmail.com", "")
                services.createUser(newUser)
            }
        assertEquals("Empty password", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_user_details() {
        val user = services.getUserDetails(2)
        assertEquals("Bob", user.name)
    }

    @Test
    fun get_user_details_of_non_existing_user() {
        val appException =
            assertFailsWith<AppException> {
                services.getUserDetails(10)
            }
        assertEquals("User doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun get_user_details_of_invalid_user_number() {
        val appException =
            assertFailsWith<AppException> {
                services.getUserDetails(-10)
            }
        assertEquals("Invalid user number", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_users() {
        val users = services.getUsers(2, 0)
        assertEquals(2, users.list.size)
        assertEquals("Shrek", users.list[0].name)
        assertEquals("Bob", users.list[1].name)
    }

    @Test
    fun get_users_skipping_1() {
        val users = services.getUsers(2, 1)
        assertEquals(2, users.list.size)
        assertEquals("Bob", users.list[0].name)
        assertEquals("Duck", users.list[1].name)
    }

    @Test
    fun get_users_with_invalid_limit() {
        val appException =
            assertFailsWith<AppException> {
                services.getUsers(-1, 0)
            }
        assertEquals("Invalid limit", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_users_with_invalid_skip() {
        val appException =
            assertFailsWith<AppException> {
                services.getUsers(4, -10)
            }
        assertEquals("Invalid skip", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_user_rankings() {
        val creators = services.getUserRankings(2, 1, 5, 0)
        assertEquals(2, creators.list.size)
        assertEquals("Bob", creators.list[0].name)
        assertEquals("Duck", creators.list[1].name)
    }

    @Test
    fun get_user_rankings_skipping_1() {
        val creators = services.getUserRankings(2, 1, 5, 1)
        assertEquals(1, creators.list.size)
        assertEquals("Duck", creators.list[0].name)
    }

    @Test
    fun get_user_rankings_with_invalid_limit() {
        val appException =
            assertFailsWith<AppException> {
                services.getUserRankings(2, 1, -1, 0)
            }
        assertEquals("Invalid limit", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_user_rankings_with_invalid_skip() {
        val appException =
            assertFailsWith<AppException> {
                services.getUserRankings(2, 1, 1, -10)
            }
        assertEquals("Invalid skip", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_session() {
        val session = services.getUserToken(UserCredentials("shrek@gmail.com", "ilovefiona"))
        assertEquals(1, session.number)
        assertEquals("Shrek", session.name)
        assertEquals("e2a6cf7c-7125-4a88-858a-2196d24e8ead", session.token)
    }

    @Test
    fun get_user_token_with_wrong_password() {
        val appException =
            assertFailsWith<AppException> {
                services.getUserToken(UserCredentials("shrek@gmail.com", "nope"))
            }
        assertEquals("Invalid credentials", appException.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, appException.status)
    }

    @Test
    fun get_user_token_with_wrong_email() {
        val appException =
            assertFailsWith<AppException> {
                services.getUserToken(UserCredentials("nope@gmail.com", "ilovefiona"))
            }
        assertEquals("Invalid credentials", appException.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, appException.status)
    }
}
