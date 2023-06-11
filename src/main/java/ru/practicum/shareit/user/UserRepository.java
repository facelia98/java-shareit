package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("update User u set u.email = ?2, u.name = ?3 where u.id = ?1")
    @Modifying
    void updateByNotNullFields(Long userId, String email, String name);
}
