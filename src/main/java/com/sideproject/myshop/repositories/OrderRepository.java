package com.sideproject.myshop.repositories;

import com.sideproject.myshop.auth.entities.User;
import com.sideproject.myshop.entities.Address;
import com.sideproject.myshop.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUser(User user);

    List<Order> findByAddress(Address address);
}
