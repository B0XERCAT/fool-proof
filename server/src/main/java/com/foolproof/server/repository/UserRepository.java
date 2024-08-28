package com.foolproof.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.foolproof.server.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
