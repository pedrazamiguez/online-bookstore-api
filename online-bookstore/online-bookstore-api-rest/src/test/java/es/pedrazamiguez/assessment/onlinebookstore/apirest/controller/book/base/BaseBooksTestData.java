package es.pedrazamiguez.assessment.onlinebookstore.apirest.controller.book.base;

import static org.instancio.Select.field;

import es.pedrazamiguez.assessment.onlinebookstore.domain.model.Book;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookDto;
import es.pedrazamiguez.assessment.onlinebookstore.openapi.model.BookRequestDto;
import org.instancio.Instancio;

public abstract class BaseBooksTestData {

  protected Book givenBook() {
    return Instancio.of(Book.class)
        .supply(field(Book::getId), gen -> gen.longRange(1L, 1000L))
        .supply(field(Book::getYearPublished), gen -> gen.intRange(1800, 2500))
        .create();
  }

  protected Book givenBook(final Long bookId) {
    return Instancio.of(Book.class)
        .supply(field(Book::getId), gen -> bookId)
        .supply(field(Book::getYearPublished), gen -> gen.intRange(1800, 2500))
        .create();
  }

  protected BookRequestDto givenBookRequestDto() {
    return Instancio.of(BookRequestDto.class)
        .supply(field(BookRequestDto::getYear), gen -> gen.intRange(1800, 2500))
        .create();
  }

  protected BookDto givenBookDto(final Long bookId) {
    return Instancio.of(BookDto.class)
        .supply(field(BookDto::getId), gen -> bookId)
        .supply(field(BookDto::getYear), gen -> gen.intRange(1800, 2500))
        .create();
  }
}
