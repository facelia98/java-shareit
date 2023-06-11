package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(" select i from Item i " +
            "where i.available = true and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> search(String query);

    @Query("UPDATE Item i SET i.description = ?2, i.name = ?3, i.available = ?4 WHERE i.id = ?1")
    @Modifying
    void updateByNotNullFields(Long itemId, String description, String name, boolean isAvailable);

    @Query("SELECT i FROM Item i WHERE i.owner.id = ?1")
    List<Item> findAllByOwner_Id(Long ownerId);
}
