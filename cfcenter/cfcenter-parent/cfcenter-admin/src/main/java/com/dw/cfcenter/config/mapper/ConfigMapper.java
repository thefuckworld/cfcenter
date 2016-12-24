package com.dw.cfcenter.config.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.dw.cfcenter.config.vo.ConfigVO;

@Repository
public interface ConfigMapper {

	List<ConfigVO> getDataByProject (@Param("projectName")String projectName);
	
	void addDataSource(@Param("configVo") ConfigVO configVo);
}
