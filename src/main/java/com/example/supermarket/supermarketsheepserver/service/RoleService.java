package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.entity.Role;
import com.example.supermarket.supermarketsheepserver.entity.Role.RoleStatus;
import com.example.supermarket.supermarketsheepserver.repository.RoleRepository;
import com.example.supermarket.supermarketsheepserver.request.RoleRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
    }

    public Role createRole(RoleRequest request) {
        Role role = Role.builder()
                .name(request.getName())
                .status(RoleStatus.ACTIVE)
                .build();
        return roleRepository.save(role);
    }

    public Role updateRole(Long id, RoleRequest request) {
        Role role = getRoleById(id);
        role.setName(request.getName());
        return roleRepository.save(role);
    }

    @Transactional
    public Role changeRoleStatus(Long roleId, String status) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        role.setStatus(RoleStatus.valueOf(status));
        return roleRepository.save(role);
    }
}