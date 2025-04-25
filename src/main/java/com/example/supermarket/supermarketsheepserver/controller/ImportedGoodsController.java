package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.entity.EntryForm;
import com.example.supermarket.supermarketsheepserver.request.EntryFormRequest;
import com.example.supermarket.supermarketsheepserver.service.ImportedGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/admin/imported-goods")
public class ImportedGoodsController {

    @Autowired
    private ImportedGoodsService importedGoodsService;

    @PostMapping("/create-entry-form")
    public ResponseEntity<EntryForm> taoPhieuNhap(
            @RequestParam Long supplierId,
            @RequestParam Long userId,
            @RequestParam(required = false) String note) {

        EntryForm entryForm = importedGoodsService.createEntryForm(supplierId, userId, note);
        return ResponseEntity.ok(entryForm);
    }

    @PostMapping("/add-product")
    public ResponseEntity<String> themSanPhamNhap(
            @RequestParam Long entryFormId,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam BigDecimal ImportPrice) {

        importedGoodsService.addImportedProducts(entryFormId, productId, quantity, ImportPrice);
        return ResponseEntity.ok("Đã thêm sản phẩm vào phiếu nhập");
    }

    @PostMapping("/confirm-entry/{entryFormId}")
    public ResponseEntity<String> xacNhanNhapHang(@PathVariable Long entryFormId) {
        importedGoodsService.xacNhanNhapHang(entryFormId);
        return ResponseEntity.ok("Đã xác nhận nhập hàng và cập nhật kho");
    }

    @GetMapping("/import-bill/{entryFormId}")
    public ResponseEntity<?> getEntryForm(@PathVariable String entryFormId) {
        try {
            EntryFormRequest dto = importedGoodsService.getEntryFormWithDetails(entryFormId);
            return ResponseEntity.ok(dto);
        } catch (ImportedGoodsService.ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

}
