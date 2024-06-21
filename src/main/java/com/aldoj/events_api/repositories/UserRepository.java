package com.aldoj.events_api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import com.aldoj.events_api.domain.users.User;

public interface UserRepository extends JpaRepository<User, UUID> {

    UserDetails findByEmail(String email);
}
