package com.wedding.invite.repository;

import com.wedding.invite.model.Blessing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlessingRepository extends JpaRepository<Blessing, Long> {
}