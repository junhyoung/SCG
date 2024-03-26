package com.example.gateway.entity;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TestMapper {
    @Select("SELECT uri FROM test WHERE client_id = #{clientId}")
    List<String> findByClientId(@Param("clientId") String clientId);

    @Insert("INSERT INTO test (client_id, uri) VALUES (#{clientId}, #{uri})")
    void insert(@Param("clientId") String clientId, @Param("uri") String uri);
}