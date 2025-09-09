package com.server.manage.config;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * PostgreSQL TIMESTAMPTZ 到 LocalDateTime 的类型处理器
 */
@MappedJdbcTypes(JdbcType.TIMESTAMP_WITH_TIMEZONE)
public class PostgreSqlLocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
        // 将 LocalDateTime 转换为 TIMESTAMPTZ
        ps.setTimestamp(i, Timestamp.valueOf(parameter));
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object object = rs.getObject(columnName);
        return convertToLocalDateTime(object);
    }

    @Override
    public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object object = rs.getObject(columnIndex);
        return convertToLocalDateTime(object);
    }

    @Override
    public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object object = cs.getObject(columnIndex);
        return convertToLocalDateTime(object);
    }

    private LocalDateTime convertToLocalDateTime(Object object) {
        if (object == null) {
            return null;
        }
        
        if (object instanceof OffsetDateTime) {
            // PostgreSQL TIMESTAMPTZ 通常返回 OffsetDateTime
            return ((OffsetDateTime) object).toLocalDateTime();
        } else if (object instanceof Timestamp) {
            return ((Timestamp) object).toLocalDateTime();
        } else if (object instanceof LocalDateTime) {
            return (LocalDateTime) object;
        }
        
        return null;
    }
}
