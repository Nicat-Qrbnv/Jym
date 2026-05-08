package com.epam.jym.crm.repository;

import com.epam.jym.crm.entity.User;
import java.util.Optional;

public interface UserRepository<U extends User> extends BaseRepository<U> {

  Optional<U> findByUsername(String username);
}
