package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.ProductPhoto;
import com.example.supermarket.supermarketsheepserver.repository.ProductPhotoRepository;
import com.example.supermarket.supermarketsheepserver.repository.ProductRepository;
import com.example.supermarket.supermarketsheepserver.request.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductPhotoService {

    @Autowired
    private ProductPhotoRepository productPhotoRepository;

    private ProductRepository productRepository;

    public List<ProductPhoto> getAllProductPhoto() {
        return productPhotoRepository.getAllProductPhoto();
    }

    public ProductPhoto getProductPhotoById(Long id) {
        // Kiểm tra xem productPhoto có tồn tại không
        ProductPhoto productPhoto = productPhotoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productPhotoRepository.getById(id);
    }

    public ProductPhoto createProductPhoto(ProductRequest image) {
        // Kiểm tra xem sản phẩm đã có ảnh chính (main image) hay chưa
        if (image.getMainImage()) {
            // Kiểm tra xem đã có ảnh chính nào tồn tại trong cơ sở dữ liệu chưa
            boolean hasMainImage = productPhotoRepository.existsByProductIdAndMainImageTrue(image.getProductId());

            if (hasMainImage) {
                List<ProductPhoto> list = productPhotoRepository.getAllByIdSP(image.getProductId());
                for(ProductPhoto p : list){
                    productPhotoRepository.delete(p);
                    System.out.println("Xóa mainImage");
                }
            }

            if (!hasMainImage) {
                List<ProductPhoto> list = productPhotoRepository.getAllByIdSP1(image.getProductId());
                for(ProductPhoto p : list){
                    productPhotoRepository.delete(p);
                    System.out.println("Xóa mainImage phụ");
                }
            }

        }

        // Tạo đối tượng ProductPhoto mới
        ProductPhoto productImage = new ProductPhoto();
        productImage.setImageUrl(image.getImageUrl());
        productImage.setMainImage(image.getMainImage());
        productImage.setProduct(Product.builder().id(image.getProductId()).build());

        // Lưu vào cơ sở dữ liệu
        return productPhotoRepository.save(productImage);
    }

    public void deleteImg(Long IdProduct){
        List<ProductPhoto> list = productPhotoRepository.getAllByIdSP(IdProduct);
        for(ProductPhoto p : list){
            productPhotoRepository.delete(p);
        }
    }


}