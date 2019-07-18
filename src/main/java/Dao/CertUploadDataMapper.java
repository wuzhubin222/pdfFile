package Dao;

import model.CertUploadData;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertUploadDataMapper {
    List<CertUploadData> selectAll();

    List<CertUploadData> selectSelective(CertUploadData record);

    int insertSelective(CertUploadData record);

    int updateByPrimaryKeySelective(CertUploadData record);

    int updateByNsrsbh(CertUploadData certUploadData);
}