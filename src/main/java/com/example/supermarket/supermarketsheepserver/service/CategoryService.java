package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Category;
import com.example.supermarket.supermarketsheepserver.entity.Category.CategoryStatus;
import com.example.supermarket.supermarketsheepserver.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findByStatusOrderByCreatedAtDesc(CategoryStatus.ACTIVE);
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public Category changeCategoryStatus(Long categoryId, String status) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        category.setStatus(Category.CategoryStatus.valueOf(status));
        return categoryRepository.save(category);
    }
}