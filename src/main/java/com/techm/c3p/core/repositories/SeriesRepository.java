package com.techm.c3p.core.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.c3p.core.entitybeans.Series;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Integer> {

	Set<Series> findBySeries(String series);

	long countBySeriesContains(String series);

}
