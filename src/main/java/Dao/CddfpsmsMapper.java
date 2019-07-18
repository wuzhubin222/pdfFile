package Dao;

import model.Cddfpsms;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * Created by zsxx001 on 2017/11/1.
 */
@Repository
public interface CddfpsmsMapper {

    int insertSms(Cddfpsms cddfpsms);

    int insertSmsMap(Map<String,Object> data);

}
