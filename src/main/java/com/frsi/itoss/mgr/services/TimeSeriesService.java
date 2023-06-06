package com.frsi.itoss.mgr.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
public class TimeSeriesService {

	private final DataSource dataSource;
	private final NamedParameterJdbcTemplate jdbcTimeseries;

	public TimeSeriesService(@Qualifier("timeseriesDataSource") DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTimeseries = new NamedParameterJdbcTemplate(this.dataSource);
	}

	// EXECUTE QUERIES
	public List<Map<String, Object>> query(String query) throws DataAccessException {
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();

		return jdbcTimeseries.queryForList(query, sqlParams);
	}

	public List<Map<String, Object>> query(String query, Map<String, Object> params) throws DataAccessException {
		MapSqlParameterSource sqlParams = new MapSqlParameterSource();
		sqlParams.addValues(params);

		return jdbcTimeseries.queryForList(query, sqlParams);
	}

	// GET SCHEMA FROM TABLE
	public List<Map<String, Object>> getSchemaFromTable(String tableName) throws DataAccessException {
		var query = """
				SELECT column_name, 
						data_type
					FROM information_schema.columns
					WHERE table_name = :tableName;
				""";
		Map<String, Object> params = Map.of("tableName", tableName);

		return this.query(query, params);
	}

	// GET SCHEMA FROM VIEW
	public List<Map<String, Object>> getSchemaFromView(String viewName) throws DataAccessException {
		var query = """
				SELECT attname as column_name, 
					   format_type(atttypid, atttypmod) AS data_type
				  FROM pg_class c
					   INNER JOIN pg_attribute a ON a.attrelid = c.oid
					   INNER JOIN pg_type t ON t.oid = a.atttypid
				    WHERE c.relkind = 'v'
					   AND c.relname = :viewName;
				""";
		Map<String, Object> params = Map.of("viewName", viewName);

		return this.query(query, params);
	}


}
