package es.pedrazamiguez.assessment.onlinebookstore.domain.repository;

public interface BookCopyRepository {

  void addCopies(Long bookId, int copies);
}
