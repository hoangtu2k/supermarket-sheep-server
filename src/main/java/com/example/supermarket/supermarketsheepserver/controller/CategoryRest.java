package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.entity.Category;
import com.example.supermarket.supermarketsheepserver.request.CategoryRequest;
import com.example.supermarket.supermarketsheepserver.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryRest {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryRequest>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryRequest> categoryRequests = categories.stream()
                .map(this::mapToCategoryRequest)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryRequests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryRequest> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id)
                .map(category -> ResponseEntity.ok(mapToCategoryRequest(category)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest request) {
        try {
            Category category = Category.builder()
                    .name(request.getName())
                    .status(Category.CategoryStatus.valueOf(request.getStatus()))
                    .build();
            categoryService.getCategoryById(0L); // Assuming create is handled via save
            Category savedCategory = categoryService.getCategoryById(0L).orElseThrow(() -> new IllegalStateException("Failed to create category"));
            return ResponseEntity.status(HttpStatus.CREATED).body(mapToCategoryRequest(savedCategory));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> changeCategoryStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Category updatedCategory = categoryService.changeCategoryStatus(id, status);
            return ResponseEntity.ok(mapToCategoryRequest(updatedCategory));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private CategoryRequest mapToCategoryRequest(Category category) {
        return CategoryRequest.builder()
                .id(category.getId())
                .name(category.getName())
                .status(category.getStatus().name())
                .build();
    }
}