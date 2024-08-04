package pt.isel.ls.sports.data.comp.sports

import pt.isel.ls.sports.data.comp.MockData
import pt.isel.ls.sports.data.comp.StoredSport
import pt.isel.ls.sports.data.comp.getSublist
import pt.isel.ls.sports.data.comp.hasMore
import pt.isel.ls.sports.data.comp.toSport
import pt.isel.ls.sports.data.comp.transactions.Transaction
import pt.isel.ls.sports.domain.DataOutput
import pt.isel.ls.sports.domain.ListOfData
import pt.isel.ls.sports.domain.Sport
import pt.isel.ls.sports.domain.SportUpdateInput

class MemSportsData(private val data: MockData) : SportsData {

    /**
     * Get the next number for a new sport to use
     */
    private fun getNextSportNumber() = data.sports.last().number + 1

    override fun addSport(transaction: Transaction, userNumber: Int, name: String, description: String?): DataOutput {
        val nextNumber = getNextSportNumber()
        val newSport = StoredSport(nextNumber, name, description, userNumber)
        data.sports.add(newSport)
        return DataOutput(newSport.number)
    }

    override fun getSports(transaction: Transaction, limit: Int, skip: Int): ListOfData<Sport> {
        val sports = data.sports.map { it.toSport(data) }
        return ListOfData(getSublist(sports, limit, skip), hasMore(sports.size, limit, skip))
    }

    override fun getSportByNumber(transaction: Transaction, sportNumber: Int) =
        data.sports.find { it.number == sportNumber }?.toSport(data)

    override fun updateSport(transaction: Transaction, sportNumber: Int, updates: SportUpdateInput): DataOutput {
        data.sports.replaceAll {
            if (it.number == sportNumber)
                StoredSport(
                    number = it.number,
                    name = updates.name ?: it.name,
                    description = updates.description ?: it.description,
                    user = it.user
                )
            else it
        }
        return DataOutput(sportNumber)
    }

    override fun searchSports(transaction: Transaction, searchQuery: String, limit: Int, skip: Int): ListOfData<Sport> {
        val sports =
            data.sports
                .filter {
                    it.name.lowercase().contains(searchQuery.lowercase()) ||
                        it.description?.lowercase()?.contains(searchQuery.lowercase()) ?: false
                }
                .map { it.toSport(data) }
        return ListOfData(getSublist(sports, limit, skip), hasMore(sports.size, limit, skip))
    }
}
