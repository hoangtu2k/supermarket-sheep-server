package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.*;
import com.example.supermarket.supermarketsheepserver.repository.*;
import com.example.supermarket.supermarketsheepserver.request.EntryDetailsRequest;
import com.example.supermarket.supermarketsheepserver.request.EntryFormRequest;
import com.example.supermarket.supermarketsheepserver.request.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImportedGoodsService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private EntryFormRepository entryFormRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTypeRepository productTypeRepository;

    // Lấy tất cả sản phẩm
    public List<EntryForm> getAllEntryForms() {
        return entryFormRepository.findAll();
    }

    public EntryForm createEntryForm(Long supplierId, Long userId, String note) {
        EntryForm entryForm = new EntryForm();
        entryForm.setEntry_form_code("PN" + System.currentTimeMillis()); // Tạo mã tự động
        entryForm.setEntry_date(LocalDateTime.now());

        Supplier supplier = supplierRepository.findById(supplierId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        entryForm.setSupplier(supplier);
        entryForm.setUser(user);
        entryForm.setNote(note);
        entryForm.setTotal(BigDecimal.ZERO);

        return entryFormRepository.save(entryForm);
    }

    public void addImportedProducts(Long entryFormId, Long productId, Integer quantity, BigDecimal importPrice) {
        EntryForm entryForm = entryFormRepository.findById(entryFormId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        EntryDetails entryDetails = new EntryDetails();
        entryDetails.setEntryform(entryForm);
        entryDetails.setProduct(product);
        entryDetails.setQuantity(quantity);
        entryDetails.setImport_price(importPrice);
        entryDetails.setPayment(importPrice.multiply(BigDecimal.valueOf(quantity)));

        // Thêm vào danh sách chi tiết
        entryForm.getEntryDetails().add(entryDetails);

        // Cập nhật tổng tiền
        BigDecimal tongTien = entryForm.getEntryDetails().stream()
                .map(EntryDetails::getPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        entryForm.setTotal(tongTien);

        entryFormRepository.save(entryForm);
    }

    public void xacNhanNhapHang(Long entryFormId) {
        EntryForm entryForm = entryFormRepository.findById(entryFormId).orElseThrow();

        // Cập nhật số lượng tồn kho cho từng sản phẩm
        for (EntryDetails entryDetails : entryForm.getEntryDetails()) {
            Product product = entryDetails.getProduct();
            int soLuongNhap = entryDetails.getQuantity();

            // Nếu sản phẩm chưa có trong kho (số lượng null)
            if (product.getQuantity() == null) {
                product.setQuantity(0);
            }

            // Cập nhật số lượng tồn
            product.setQuantity(product.getQuantity() + soLuongNhap);

            // Cập nhật giá bán (nếu cần)
            // sp.setGiaBan(chiTiet.getDonGiaNhap().multiply(BigDecimal.valueOf(1.1)) // Ví dụ +10%

            productRepository.save(product);
        }

        // Đánh dấu phiếu nhập đã hoàn thành (nếu cần)
        // phieuNhap.setTrangThai(true);
        // phieuNhapRepository.save(phieuNhap);
    }

    public void themSanPhamMoi(ProductRequest productRequest) {
        Product sp = new Product();
        sp.setCode("SP" + System.currentTimeMillis());
        sp.setName(productRequest.getName());
        sp.setDescription(productRequest.getDescription());
        sp.setPrice(productRequest.getPrice());
        sp.setQuantity(0); // Ban đầu = 0
        sp.setProductType(productTypeRepository.findById(productRequest.getProductTypeId()).orElseThrow());
        sp.setSupplier(supplierRepository.findById(productRequest.getSupplierId()).orElseThrow());
        sp.setStatus(1);

        productRepository.save(sp);
    }

    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public EntryFormRequest getEntryFormWithDetails(String entryFormId) {
        EntryForm entryForm = entryFormRepository.findByIdWithDetails(entryFormId)
                .orElseThrow(() -> new ResourceNotFoundException("Phiếu nhập không tồn tại"));

        EntryFormRequest dto = new EntryFormRequest();
        dto.setEntryFormId(entryForm.getId());
        dto.setEntryDate(entryForm.getEntry_date());
        dto.setUsername(entryForm.getUser().getName());
        dto.setSupplierName(entryForm.getSupplier().getName());
        dto.setTotal(entryForm.getTotal());

        List<EntryDetailsRequest> entryDetailsRequests = entryForm.getEntryDetails().stream()
                .map(ct -> {
                    EntryDetailsRequest ctDto = new EntryDetailsRequest();
                    ctDto.setProductId(ct.getProduct().getId());
                    ctDto.setProductName(ct.getProduct().getName());
                    ctDto.setQuantity(ct.getQuantity());
                    ctDto.setImportPrice(ct.getImport_price());
                    ctDto.setPayment(ct.getPayment());
                    return ctDto;
                })
                .collect(Collectors.toList());

        dto.setEntryDetaisRequests(entryDetailsRequests);

        return dto;
    }

}
