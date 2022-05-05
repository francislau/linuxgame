package com.francin.linux.mapper;

import com.francin.linux.pojo.Level;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface LevelMapper {

    List<Level> queryLevelList();

    Level queryLevelById(int id);

    int addLevel(Level level);

    int updateLevel(Level level);

    int deleteLevel(int id);

    int batchDeleteLevel(String[] ids);
}
