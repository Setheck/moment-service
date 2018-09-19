package com.setheck.momentservice.db;

import com.setheck.momentservice.api.*;
import io.dropwizard.hibernate.*;
import org.hibernate.*;

import java.util.*;

public class MomentDAO extends AbstractDAO<Moment> {
    public MomentDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Moment findById(Integer id) {
        return get(id);
    }

    public Moment findRandomApproved() {
        return currentSession()
                .createQuery("FROM Moment WHERE approved = true ORDER BY RAND()", Moment.class)
                .setMaxResults(1)
                .getSingleResult();
    }

    public List<Moment> findMomentsNeedingApproval() {
        return currentSession()
                .createQuery("FROM Moment WHERE approved = false", Moment.class)
                .getResultList();
    }

    public Integer create(Moment moment) {
        return persist(moment).getId();
    }

    public int updateSingleColumn(Set<Integer> idList, String column, Object value) {
        //TODO: sanitize column, or enforce it is a field of moment.
        return currentSession()
                .createQuery("update Moment m set "+ column +" = :value WHERE m.id in (:ids)")
//                .setParameter("column", column)
                .setParameter("value", value)
                .setParameter("ids", idList)
                .executeUpdate();
    }

    public void deleteById(Integer id) {
        currentSession()
                .createQuery("delete Moment WHERE id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
