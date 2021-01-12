package garg.digaant.Asset_Management.services;

import garg.digaant.Asset_Management.models.BasicEntity;

import java.util.Set;

public interface CrudService<T extends BasicEntity, ID> {

    Set<T> findAll();

    T findById(ID id);

    T save(T object);

    void delete(T object);

    void deleteById(ID id);
}