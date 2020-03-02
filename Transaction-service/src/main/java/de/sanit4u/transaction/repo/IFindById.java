package de.sanit4u.transaction.repo;

import java.util.Optional;

public interface IFindById<T> {
	Optional<T> findById(long id);
}
