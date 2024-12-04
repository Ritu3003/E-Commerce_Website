package com.ecom.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	Page<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String title, String category,
			Pageable pageable);

	Page<Product> findByIsActiveTrue(Pageable pageable);

	Page<Product> findByCategory(Pageable pageable, String category);

	Page<Product> findByIsActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String title,
			String category, Pageable pageable);

	List<Product> findByIsActiveTrue();

	List<Product> findByCategory(String category);

	List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2);
}
