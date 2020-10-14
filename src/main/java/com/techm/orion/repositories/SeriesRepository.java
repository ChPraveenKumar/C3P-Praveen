package com.techm.orion.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techm.orion.entitybeans.Series;

@Repository
public interface SeriesRepository extends JpaRepository<Series, Integer> {

	Set<Series> findBySeries(String series);

	long countBySeriesContains(String series);

}
