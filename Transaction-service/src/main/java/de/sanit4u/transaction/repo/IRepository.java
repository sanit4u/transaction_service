package de.sanit4u.transaction.repo;

import java.util.List;

/**
 * Interface providing the basic CRUD operations
 *
 * @param <T>
 */
public interface IRepository<T> {

	List<T> getAll();

	T save(T t);

}
