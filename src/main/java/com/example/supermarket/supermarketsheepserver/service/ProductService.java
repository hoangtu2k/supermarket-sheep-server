package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Product;
import com.example.supermarket.supermarketsheepserver.entity.ProductPhoto;
import com.example.supermarket.supermarketsheepserver.repository.ProductPhotoRepository;
import com.example.supermarket.supermarketsheepserver.repository.ProductRepository;
import com.example.supermarket.supermarketsheepserver.request.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.Date;


@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductPhotoRepository productPhotoRepository;

    // Lấy tất cả sản phẩm
    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }

    // Lấy sản phẩm theo ID
    public Product getProductById(Long id) {
        return productRepository.getById(id);
    }

    // Tạo mới sản phẩm
    public Product createProduct(ProductRequest productRequest) {
        Product product = new Product();
        if (productRequest.getCode() == null) {
            // Generate a new code automatically if it's null
            String generatedCode = generateCode();
            product.setCode(generatedCode);
        } else {
            // Otherwise, set the code from the request
            product.setCode(productRequest.getCode());
        }
        product.setName(productRequest.getName());
        product.setWeight(productRequest.getWeight());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setQuantity(productRequest.getQuantity());
        product.setCreateDate(new Date());
        product.setStatus(1);

        return productRepository.save(product);
    }

    // Cập nhật sản phẩm
    public Product updateProduct(Long id, ProductRequest productRequest) {
        // Kiểm tra xem sản phẩm có tồn tại không
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        Product product = optionalProduct.get();
        // Cập nhật các thuộc tính sản phẩm
        if (productRequest.getCode() == null) {
            String generatedCode = generateCode();
            product.setCode(generatedCode);
        }
        else {
            product.setCode(productRequest.getCode());
        }
        product.setName(productRequest.getName());
        product.setPrice(productRequest.getPrice());
        product.setWeight(productRequest.getWeight());
        product.setDescription(productRequest.getDescription());
        product.setQuantity(productRequest.getQuantity());
        // Lưu sản phẩm đã cập nhật
        return productRepository.save(product);
    }

    // Thay đổi trạng thái sản phẩm (xóa hoặc khôi phục)
    public Product changeProductStatus(Long productId, Integer newStatus) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStatus(newStatus);
        return productRepository.save(product);
    }

    // import
    public void importExel(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
            for (Row row : sheet) {
                if (row.getRowNum() > 0) { // Skip header
                    String code = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : generateCode();
                    String name = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null;
                    String url = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : null;
                    Double price = row.getCell(3) != null ? row.getCell(3).getNumericCellValue() : null;
                    Double qty = row.getCell(4) != null ? row.getCell(4).getNumericCellValue() : null;
                    Double weight = row.getCell(5) != null ? row.getCell(5).getNumericCellValue() : null;
                    String description = row.getCell(6) != null ? row.getCell(6).getStringCellValue() : null;

                    if (name != null && url != null && price != null && qty != null && weight != null) {
                        Product product = new Product();
                        product.setCode(code);
                        product.setName(name);
                        product.setPrice(BigDecimal.valueOf(price));
                        product.setQuantity(qty.intValue());
                        product.setWeight(weight);
                        product.setDescription(description);
                        product.setStatus(1);
                        product.setCreateDate(new Date());
                        productRepository.save(product);

                        ProductPhoto productPhoto = new ProductPhoto();
                        productPhoto.setMainImage(true);
                        productPhoto.setImageUrl(url);
                        productPhoto.setProduct(product);
                        productPhotoRepository.save(productPhoto);
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Failed to import Excel file: " + e.getMessage());
        }
    }

    // Tạo code ramdom
    private String generateCode() {
        return UUID.randomUUID().toString();
    }

}
