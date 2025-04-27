package es.pedrazamiguez.assessment.onlinebookstore.domain.service.book;

import es.pedrazamiguez.assessment.onlinebookstore.domain.exception.NotEnoughBookCopiesException;

public interface AvailableBookCopiesService {

  boolean check(Long bookId, Long requestedCopies);

  void assure(Long bookId, Long requestedCopies) throws NotEnoughBookCopiesException;
}
