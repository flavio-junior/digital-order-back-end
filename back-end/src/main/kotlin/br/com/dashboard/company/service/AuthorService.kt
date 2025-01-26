package br.com.dashboard.company.service

import br.com.dashboard.company.entities.fee.Author
import br.com.dashboard.company.exceptions.ResourceNotFoundException
import br.com.dashboard.company.repository.AuthorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class AuthorService {

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Transactional
    fun saveAuthor(
        author: Author
    ): Author {
        return authorRepository.save(author)
    }

    fun getAuthor(
        authorId: Long,
        feeId: Long
    ): Author {
        val authorSaved: Author? = authorRepository.findFeeById(authorId = authorId, feeId = feeId)
        if (authorSaved != null) {
            return authorSaved
        } else {
            throw ResourceNotFoundException(message = AUTHOR_NOT_FOUND)
        }
    }

    @Transactional
    fun deleteAuthor(
        authorId: Long,
        feeId: Long
    ) {
        val authorSaved = getAuthor(authorId = authorId, feeId = feeId)
        authorRepository.deleteFeeById(authorId = authorSaved.id, feeId = authorSaved.fee?.id ?: 0)
    }

    companion object {
        const val AUTHOR_NOT_FOUND = "Author not found!"
    }
}
