package com.wedding.invite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.wedding.invite.model.Blessing;

@Repository
public interface BlessingRepository extends JpaRepository<Blessing, Long> {
}