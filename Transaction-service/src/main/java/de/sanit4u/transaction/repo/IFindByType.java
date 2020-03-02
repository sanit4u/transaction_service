package de.sanit4u.transaction.repo;

import java.util.List;

public interface IFindByType<T> {

	List<T> findByType(String type);
}
