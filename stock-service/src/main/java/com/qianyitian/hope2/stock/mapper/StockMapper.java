package com.qianyitian.hope2.stock.mapper;


import org.apache.ibatis.annotations.*;


public interface StockMapper {
	@Insert("INSERT INTO daily(code,kline) VALUES(#{code}, #{kline}) ON DUPLICATE KEY UPDATE `kline`= #{kline}")
	void insertDaily(@Param("code") String code, @Param("kline") String kline);

	@Insert("INSERT INTO daily_lite(code,kline) VALUES(#{code}, #{kline}) ON DUPLICATE KEY UPDATE `kline`= #{kline}")
	void insertDailyLite(@Param("code") String code, @Param("kline") String kline);

	@Insert("INSERT INTO weekly(code,kline) VALUES(#{code}, #{kline}) ON DUPLICATE KEY UPDATE `kline`= #{kline}")
	void insertWeekly(@Param("code") String code, @Param("kline") String kline);

	@Insert("INSERT INTO monthly(code,kline) VALUES(#{code}, #{kline}) ON DUPLICATE KEY UPDATE `kline`= #{kline}")
	void insertMonthly(@Param("code") String code, @Param("kline") String kline);

	@Select("SELECT kline FROM daily WHERE code = #{code}")
	String getDaily(@Param("code") String code);

	@Select("SELECT kline FROM daily_lite WHERE code = #{code}")
	String getDailyLite(@Param("code") String code);

	@Select("SELECT kline FROM weekly WHERE code = #{code}")
	String getWeekly(@Param("code") String code);
	@Select("SELECT kline FROM monthly WHERE code = #{code}")
	String getMonthly(@Param("code") String code);
}