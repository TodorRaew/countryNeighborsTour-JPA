package com.example.countryNeighborsTour.repository;

import com.example.countryNeighborsTour.model.Neighbor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NeighborRepository extends JpaRepository<Neighbor, Long> {

    List<Neighbor> findByStartingCountryName(String neighbor);
}
