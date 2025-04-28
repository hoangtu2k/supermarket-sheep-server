package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Role;
import com.example.supermarket.supermarketsheepserver.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    // Lấy tất cả trị trí
    public List<Role> getAllRoles() {
        return roleRepository.findAllRole();
    }

    // Lấy vị trí theo ID
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

}
