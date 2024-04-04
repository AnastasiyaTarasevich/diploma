package com.example.diploma.repos;


import com.example.diploma.models.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepo extends CrudRepository<Image, Long> {
}
