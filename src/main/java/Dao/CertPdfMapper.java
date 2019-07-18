package Dao;


import model.CertPdf;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface CertPdfMapper {
    int deleteByPrimaryKey(String id);

    int insert(CertPdf record);

    int insertSelective(CertPdf record);

    CertPdf selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(CertPdf record);

    int updateByPrimaryKey(CertPdf record);

    List<HashMap<String,Object>> selectByNsrsbhTime(@Param("nsrsbh") String nsrsbh, @Param("startTime") String startTime, @Param("endTime") String endTime);


    CertPdf selectByParm2(String parm2);

    Integer updateByParm2(@Param("id") String id, @Param("parm3") String parm3, @Param("pdfUrl") String pdfUrl, @Param("parm5") String parm5);
}