package pt.isel.ls.sports.services

import org.junit.Test
import pt.isel.ls.sports.data.DataMem
import pt.isel.ls.sports.domain.SportInput
import pt.isel.ls.sports.domain.SportUpdateInput
import pt.isel.ls.sports.services.comp.AppException
import pt.isel.ls.sports.services.comp.AppExceptionStatus
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SportsServicesTests {

    private val data = DataMem()
    private val services = Services(data).sportsServices
    private val tokenOfUser1 = "e2a6cf7c-7125-4a88-858a-2196d24e8ead"
    private val tokenOfUser2 = "ffc0b3b2-8684-4d16-bccf-331a93a982c2"

    @Test
    fun create_sport() {
        val newSport = SportInput("Andebol", "Futebol mas com a mão")
        val sportOutput = services.createSport(tokenOfUser1, newSport)
        assertEquals(3, sportOutput.number)
    }

    @Test
    fun create_sport_with_empty_token() {
        val appException =
            assertFailsWith<AppException> {
                val newSport = SportInput("Andebol", "Futebol mas com a mão")
                services.createSport("", newSport)
            }
        assertEquals("No token provided", appException.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, appException.status)
    }

    @Test
    fun create_sport_with_invalid_token() {
        val appException =
            assertFailsWith<AppException> {
                val newSport = SportInput("Andebol", "Futebol mas com a mão")
                services.createSport("1234", newSport)
            }
        assertEquals("Invalid token", appException.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, appException.status)
    }

    @Test
    fun create_sport_with_empty_name() {
        val appException =
            assertFailsWith<AppException> {
                val newSport = SportInput("", "Futebol mas com a mão")
                services.createSport(tokenOfUser1, newSport)
            }
        assertEquals("Empty sport name", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_sport_details() {
        val sport = services.getSportDetails(1)
        assertEquals("Voleibol", sport.name)
        assertEquals("Melhor desporto para engate", sport.description)
        assertEquals(1, sport.number)
        assertEquals(1, sport.user.number)
    }

    @Test
    fun get_sport_details_of_non_existing_sport() {
        val appException =
            assertFailsWith<AppException> {
                services.getSportDetails(10)
            }
        assertEquals("Sport doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun get_sport_details_of_invalid_sport_number() {
        val appException =
            assertFailsWith<AppException> {
                services.getSportDetails(-10)
            }
        assertEquals("Invalid sport number", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_sports() {
        val sports = services.getSports(2, 0)
        assertEquals(2, sports.list.size)
        assertEquals(1, sports.list[0].number)
        assertEquals(2, sports.list[1].number)
    }

    @Test
    fun get_sports_with_invalid_limit() {
        val appException =
            assertFailsWith<AppException> {
                services.getSports(-1, 0)
            }
        assertEquals("Invalid limit", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun get_sports_with_invalid_skip() {
        val appException =
            assertFailsWith<AppException> {
                services.getSports(4, -10)
            }
        assertEquals("Invalid skip", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun update_sport() {
        assertEquals("Voleibol", services.getSportDetails(1).name)
        assertEquals("Melhor desporto para engate", services.getSportDetails(1).description)
        services.updateSport(
            tokenOfUser1,
            1,
            SportUpdateInput("Voleibol on steroids", "O verdadeiro melhor desporo para engate")
        )
        assertEquals("Voleibol on steroids", services.getSportDetails(1).name)
        assertEquals("O verdadeiro melhor desporo para engate", services.getSportDetails(1).description)
    }

    @Test
    fun update_non_exisiting_sport() {
        val appException =
            assertFailsWith<AppException> {
                services.updateSport(tokenOfUser2, 10, SportUpdateInput("Failed"))
            }
        assertEquals("Sport doesn't exist", appException.message)
        assertEquals(AppExceptionStatus.NOT_FOUND, appException.status)
    }

    @Test
    fun update_invalid_sport_number() {
        val appException =
            assertFailsWith<AppException> {
                services.updateSport(tokenOfUser2, -10, SportUpdateInput("Failed"))
            }
        assertEquals("Invalid sport number", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun update_sport_that_is_not_yours() {
        val appException =
            assertFailsWith<AppException> {
                services.updateSport(tokenOfUser2, 1, SportUpdateInput("Failed"))
            }
        assertEquals("Sport is not yours to update", appException.message)
        assertEquals(AppExceptionStatus.UNAUTHORIZED, appException.status)
    }

    @Test
    fun search_sports() {
        val sports = services.searchSports("Badminton", 2, 0)
        assertEquals(1, sports.list.size)
        assertEquals(2, sports.list[0].number)
    }

    @Test
    fun search_sports_with_invalid_limit() {
        val appException =
            assertFailsWith<AppException> {
                services.searchSports("Badminton", -1, 0)
            }
        assertEquals("Invalid limit", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }

    @Test
    fun search_sports_with_invalid_skip() {
        val appException =
            assertFailsWith<AppException> {
                services.searchSports("Badminton", 1, -10)
            }
        assertEquals("Invalid skip", appException.message)
        assertEquals(AppExceptionStatus.BAD_REQUEST, appException.status)
    }
}
