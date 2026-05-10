package com.sanjay.splitwise.repository;

import com.sanjay.splitwise.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

}