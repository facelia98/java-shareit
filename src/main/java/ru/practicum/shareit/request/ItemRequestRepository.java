package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Transactional
    List<ItemRequest> findAllByRequestor_Id(Long requestorId);

    @Transactional
    List<ItemRequest> findAllByRequestor_IdNot(Long requestorId, Pageable pageable);
}
