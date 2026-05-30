package com.sanjay.splitwise.repository;

import com.sanjay.splitwise.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository
        extends JpaRepository<GroupMember, Long> {

    boolean existsByUser_IdAndGroup_Id(
            Long userId,
            Long groupId
    );

    boolean existsByUserIdAndGroupId(
            Long userId,
            Long groupId
    );
}